import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

    public class TcpServer {

        private static final int port= 4567;
        private static final List<Socket> connections = new LinkedList<>();
        private static final int connectionTimeoutMinutes = 10;
        private static final ExecutorService threadPool = new ThreadPoolExecutor(1, 400,
                60L, TimeUnit.SECONDS, new SynchronousQueue<>());

        public static void main(String[] args) {
            System.out.println("Server running: port=" + port);
            try (ServerSocket serverSocket = new ServerSocket(port)){
                acceptConnections(serverSocket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private static void acceptConnections(ServerSocket serverSocket) {
            while (true) {
                try{
                    Socket accept = serverSocket.accept();
                    threadPool.execute(() -> newConnection(accept));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private static void newConnection(Socket accept) {
            try {
                accept.setSoTimeout(connectionTimeoutMinutes * 60 * 1000);
            } catch (SocketException e) {
                e.printStackTrace();
                return;
            }
            connections.add(accept);
            System.out.println("New connection from " + accept.getInetAddress().getHostAddress() + ":" + accept.getPort()
                    + " -> user id: " + getUserNumber(accept.getInetAddress(), accept.getPort()));
            try {
                PrintStream p = new PrintStream(accept.getOutputStream());
                try {
                    echoToAll(accept);
                } catch (SocketTimeoutException e) {
                    System.out.println("Connection from " + accept.getInetAddress().getHostAddress() + " timed out");
                } catch (SocketException e) {
                    System.out.println("Connection from " + accept.getInetAddress().getHostAddress() + " reset");
                } catch (Exception e) {
                    if(accept.isConnected()) {
                        p.println("Error: Something went wrong processing your input: " + e.getMessage());
                        p.flush();
                    }
                    e.printStackTrace();
                } finally {
                    accept.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private static void echoToAll(Socket accept) throws IOException {
            BufferedReader in = new BufferedReader(new InputStreamReader(accept.getInputStream()));
            String line;
            while((line = in.readLine()) != null) {
                String response = line;
                System.out.println(response);
                synchronized(connections) {
                    Iterator<Socket> it = connections.iterator();
                    while (it.hasNext()) {
                        Socket client = it.next();
                        if (client.isClosed()) {
                            it.remove();
                            continue;
                        }
                        if (client == accept)
                            continue;
                        //System.out.println("Response send to: " + client);
                        try {
                            PrintStream out = new PrintStream(client.getOutputStream());
                            out.println(response);
                        } catch (IOException e) {
                            System.out.println("Failure to send to " + client);
                        }
                    }
                }
            }
        }

        private static int getUserNumber(InetAddress address, int port) {
            return Math.abs((17 * address.hashCode() + 31 * port) % 1000);
        }
    }


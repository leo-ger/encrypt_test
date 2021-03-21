import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class ChatClient {

    private Socket socket = null;
    private ChatListener listener = null;
    private final PrintStream outputPrintStream;
    private final String hostname;
    private final int port;
    private Crypter crypter;

    public ChatClient(String hostname, int port, PrintStream outputPrintStream, Crypter crypter) {
        this.hostname = hostname;
        this.port = port;
        this.outputPrintStream = outputPrintStream;
        this.crypter = crypter;
    }

    public void connect() throws IOException {
        try {
            // TODO: Create new socket with target given in constructor arguments.
            socket = new Socket(InetAddress.getByName(hostname), port);
        } catch (IOException e) {
            throw new IOException("Could not connect to " + hostname + ":" + port + " : " + e.getMessage(), e);
        }

        outputPrintStream.println("Connected to " + socket.getInetAddress().getHostAddress() + " through port " + socket.getLocalPort());

        BufferedReader in;
        // TODO: Create BufferedReader on top of InputStreamReader on top of the input stream of the socket. Throw IOException if that fails
        try{
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch(Exception err) {
            throw new IOException();
        }

        // TODO: Create new ChatListener with BufferedReader 'in' and 'outputPrintStream'. Store in member variable 'listener'
        listener = new ChatListener(in, outputPrintStream, crypter);
        // TODO: Create and start a new Thread with this ChatListener
        Thread lThread = new Thread(listener);
        lThread.start();
    }

    public void disconnect() {
        if(!isConnected())
            return;
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        if(socket == null || listener == null)
            return false;
        return !socket.isClosed() && socket.isConnected() && listener.isConnected();
    }

    public void send(String message) throws IOException{
        // TODO: Throw IllegalStateException when client is not connected
        if(!isConnected()) {
            throw new IllegalStateException();
        }
        try {
            // TODO: Create a PrintWriter around the output stream of the socket and print the message on it with a line break at the end!
            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            writer.println(message);
            // Don't forget to flush (auto-flush is an option)!
            writer.flush();
        } catch (IOException e) {
            throw new IOException("Could not send message: " + e.getMessage(), e);
        }
    }

}

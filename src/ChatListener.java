import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;

// TODO: make Chatlistener implement interface Runnable
public class ChatListener implements Runnable {

    private final BufferedReader reader;
    private final PrintStream output;
    private Crypter crypter;

    private boolean connected = true;

    public ChatListener(BufferedReader reader, PrintStream output, Crypter crypter) {
        this.reader = reader;
        this.output = output;
        this.crypter = crypter;
    }

    public boolean isConnected() {
        return connected;
    }

    @Override
    public void run() {
        // TODO: listen continuosly for incoming messages and print them on to 'output'
        while(true) {
            try{
                String input = reader.readLine();
                if(input==null) {
                    break;
                } else {
                    String[] s_array = input.split(",");
                    byte[] b_array = new byte[s_array.length];

                    for(int i=0; i<s_array.length; i++) {
                        b_array[i] = Byte.decode(s_array[i]);
                    }

                    output.println(new String(crypter.decrypt(b_array)));
                }

            } catch(IOException err) {
                connected = false;
                break;
            }
        }
        // You can use the method readLine() in BufferedReader that blocks for input and returns a whole line that can be printed directly
        // readLine() returns null if the underlying stream has been closed and throws a SocketException when the connection has reset
        //      Use this to set 'connected' to false and end the run()-method when the connection has failed
    }
}

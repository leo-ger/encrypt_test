import javax.crypto.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class Main {

    public static void main(String[] args) {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");

            SecretKey key = keyGenerator.generateKey();

            Crypter crypter = new Crypter(key);

            ChatClient client = new ChatClient("localhost", 4567, System.out, crypter);
            // TODO: Make a chat client with given server address, port and System.out as output stream

            try {
                // TODO: Make client connect to server
                client.connect();
            } catch (IOException e) {
                System.err.println(e.getMessage());
                e.getCause().printStackTrace();
                return;
            }

            ChatClient client2 = new ChatClient("localhost", 4567, System.out, crypter);
            // TODO: Make a chat client with given server address, port and System.out as output stream

            try {
                // TODO: Make client connect to server
                client2.connect();
            } catch (IOException e) {
                System.err.println(e.getMessage());
                e.getCause().printStackTrace();
                return;
            }

            BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
            while(client.isConnected()) {
                try {
                    if(inputReader.ready()) {
                        String message = inputReader.readLine();
                        if(message.equals("exit")) {
                            client.disconnect();
                        }
                        else {
                            try {
                                String encrypted_message = "";
                                byte[] encrypted = crypter.encrypt(message.getBytes());
                                for(int i=0; i<encrypted.length; i++) {
                                    encrypted_message += Byte.toString(encrypted[i])+",";
                                }
                                client.send(encrypted_message);
                            } catch (IOException e) {
                                System.err.println(e.getMessage());
                                e.getCause().printStackTrace();
                            }
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Could not read input: " + e.getMessage());
                }
            }
            System.out.println("Chat ended");
        } catch(Exception err) {
            System.out.println("Error: Something went wrong!");
            System.out.println(err);
        }




























        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");

            SecretKey key = keyGenerator.generateKey();

            Crypter crypter = new Crypter(key);

            String s_raw = "Dies ist ein Test!";
            System.out.println(s_raw);

            byte[] b_raw = s_raw.getBytes();

            byte[] b_encrypted = crypter.encrypt(b_raw);

            String s_encrypted = new String(b_encrypted);
            System.out.println(s_encrypted);

            byte[] b_decrypted = crypter.decrypt(b_encrypted);

            String s_decrypted = new String(b_decrypted);
            System.out.println(s_decrypted);

        } catch(Exception err) {
            System.out.println("Error: Something went wrong!");
            System.out.println(err);
        }
    }
}

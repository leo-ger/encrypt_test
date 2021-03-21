import javax.crypto.*;

public class Crypter {

    private Cipher e_cipher;
    private Cipher d_cipher;
    private SecretKey key;

    public Crypter(SecretKey key) {
        this.key = key;

        try {
            e_cipher = Cipher.getInstance("AES");
            d_cipher = Cipher.getInstance("AES");
        } catch(Exception err) {
            System.out.println("Error: Could not create ciphers!");
            System.out.println(err);
        }

        try{
            e_cipher.init(Cipher.ENCRYPT_MODE, key);
            d_cipher.init(Cipher.DECRYPT_MODE, key);
        } catch(Exception err) {
            System.out.println("Error: Could not initialize ciphers!");
            System.out.println(err);
        }

    }

    public byte[] encrypt(byte[] raw_data) {
        try {
            byte[] encrypted = e_cipher.doFinal(raw_data);
            return encrypted;
        } catch(Exception err) {
            System.out.println("Error: Could not encrypt data!");
            System.out.println(err);
        }

        return null;
    }

    public byte[] decrypt(byte[] encrypted_data) {
        try {
            return d_cipher.doFinal(encrypted_data);
        } catch(Exception err) {
            System.out.println("Error: Could not decrypt data!");
            System.out.println(err);
        }

        return encrypted_data;
    }
}

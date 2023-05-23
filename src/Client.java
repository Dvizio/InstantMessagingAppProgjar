import java.io.*;
import java.security.KeyStore;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class Client {

    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 9000;

    public static void main(String[] args) {
        try {
            char[] keystorePassword = "change123".toCharArray();
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(new FileInputStream("key.jks"), keystorePassword);
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
            trustManagerFactory.init(keyStore);
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagerFactory.getTrustManagers(), null);
            SSLSocketFactory socketFactory = sslContext.getSocketFactory();
            SSLSocket socket = (SSLSocket) socketFactory.createSocket(SERVER_IP, SERVER_PORT);

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            System.out.print("Tolong Masukkan Username Anda: ");
            String username = reader.readLine();

            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

            oos.writeObject(username);
            oos.flush();

            Thread inputHandler = new inputHandler(reader, oos, username);
            inputHandler.start();

            Messages message;
            while ((message = (Messages) ois.readObject()) != null) {
                if (message.getSender().equals("server")) {
                    System.out.println("Online users: \n" + message.getMessageContent());
                } else {
                    System.out.println(message.getSender() + ": " + message.getMessageContent());
                }
            }

            ois.close();
            oos.close();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

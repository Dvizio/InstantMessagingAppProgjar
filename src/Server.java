import java.io.*;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.*;

public class Server {
    private static final int PORT = 9000;
    private static List<ClientHandler> clients = new ArrayList<>();

    public static void main(String[] args) {
        try {
            // SSL Setup
            char[] keystorePassword = "change123".toCharArray();
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(new FileInputStream("key.jks"), keystorePassword);
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(keyStore, keystorePassword);
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
            trustManagerFactory.init(keyStore);
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
            SSLServerSocketFactory socketFactory = sslContext.getServerSocketFactory();
            SSLServerSocket serverSocket = (SSLServerSocket) socketFactory.createServerSocket(PORT);

            System.out.println("Server mulai dalam port: " + PORT);

            while (true) {
                SSLSocket clientSocket = (SSLSocket) serverSocket.accept();
                System.out.println("New client connected");

                ClientHandler clientHandler = new ClientHandler(clientSocket, clients);
                synchronized (clients) {
                    clients.add(clientHandler);
                    clientHandler.start();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

import java.io.*;
import java.util.List;
import javax.net.ssl.*;

class ClientHandler extends Thread {
    private SSLSocket clientSocket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private String username;
    private List<ClientHandler> clients;

    public ClientHandler(SSLSocket socket, List<ClientHandler> clients) {
        this.clientSocket = socket;
        this.clients = clients;
    }

    public void run() {
        try {
            objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            objectInputStream = new ObjectInputStream(clientSocket.getInputStream());

            username = (String) objectInputStream.readObject();
            System.out.println("User " + username + " Memasuki Chat.");

            Messages message;
            do {
                message = (Messages) objectInputStream.readObject();

                if (message.getReceiver() == null) {
                    ClientInputHandler.broadcast(message, clients);
                } else if (message.getReceiver().equals("server") && message.getMessageContent().equals("getOnlineUsers")) {
                    List<String> onlineUsers = ClientInputHandler.getOnlineUsers(clients);
                    int userCount = 1;
                    String serverMessage = "";
                    for (String user : onlineUsers) {
                        System.out.println(user);
                        serverMessage = serverMessage.concat(Integer.toString(userCount) + "." + user + "\n");
                        userCount++;
                    }
                    System.out.println(serverMessage);
                    Messages onlineUsersMessage = new Messages("server", serverMessage);
                    sendMessage(onlineUsersMessage);
                } else {
                    // Private message
                    ClientInputHandler.sendPrivateMessage(username, message.getReceiver(), message, clients);
                }

            } while (!message.getMessageContent().equals("bye"));
            Messages terminationMessage = new Messages("server", username, "bye");
            sendMessage(terminationMessage);
            clients.remove(this);

            String terminationNotificationString = "User " + username + " meninggalkan chat.";
            System.out.println(terminationNotificationString);

            Messages terminationNotificationMessage = new Messages("server", terminationNotificationString);
            ClientInputHandler.broadcast(terminationNotificationMessage, clients);

            objectOutputStream.close();
            objectInputStream.close();
            clientSocket.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(Messages message) {
        try {
            objectOutputStream.writeObject(message);
            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUsername() {
        return username;
    }
}

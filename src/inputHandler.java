import java.io.*;

public class inputHandler extends Thread {
    private BufferedReader reader;
    private ObjectOutputStream oos;
    private String username;

    public inputHandler(BufferedReader reader, ObjectOutputStream oos, String username) {
        this.reader = reader;
        this.oos = oos;
        this.username = username;
    }

    @Override
    public void run() {
        try {
            while (true) {
                String input = reader.readLine();
                String[] inputParts = input.split(" ", 3);
                String command = inputParts[0].toLowerCase();

                if (command.equals("users")) {
                    Messages requestUsersMessage = new Messages(username, "server", "getOnlineUsers");
                    oos.writeObject(requestUsersMessage);
                    oos.flush();
                } else if (command.equals("private")) {
                    if (inputParts.length != 3) {
                        System.out.println("Invalid format. Usage: private <receiver> <message>");
                    } else {
                        String receiver = inputParts[1];
                        String content = inputParts[2];
                        Messages privateMessage = new Messages(username, receiver, content);
                        oos.writeObject(privateMessage);
                        oos.flush();
                    }
                } else if (command.equals("quit")) {
                    // remove user from online users
                    Messages exitMessage = new Messages(username, "server", "bye");
                    oos.writeObject(exitMessage);
                    oos.flush();
                    break;
                } else {
                    Messages broadcastMessage = new Messages(username, null, input);
                    oos.writeObject(broadcastMessage);
                    oos.flush();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class Server {

    private Set<ClientHandler> clients = new HashSet<>();
    private DatabaseManager db = new DatabaseManager();

    public static void main(String[] args) {
        new Server().start(); // starts server
    }

    public void start() {
        System.out.println("Server started...");

        try (ServerSocket serverSocket = new ServerSocket(5000)) {

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected.");

                ClientHandler handler = new ClientHandler(socket, this);

                synchronized (clients) {
                    clients.add(handler);
                }

                handler.start(); // starts client thread
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeClient(ClientHandler client) {
        synchronized (clients) {
            clients.remove(client);
        }
    }

    public void broadcastText(String message, ClientHandler sender) {// we are looping through every client to write the
                                                                     // message for them
        db.saveMessage(sender.getUsername(), message); // saving the message
        synchronized (clients) {
            for (ClientHandler client : clients) {
                client.sendText(message);
            }
        }
    }

    public void broadcastFile(String fileName, byte[] fileData, ClientHandler sender) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                client.sendFile(fileName, fileData);
            }
        }
    }

    public boolean authenticate(String username, String password) {
        return db.checkUser(username, password);
    }

    public boolean register(String username, String password) {
        return db.registerUser(username, password);
    }
}
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;

import javax.swing.JOptionPane;

public class Client {

    public static final int USERNAME = 0;
    public static final int TEXT = 1;
    public static final int FILE = 2;
    public static final int EXIT = 3;
    public static final int LOGIN = 4;
    public static final int AUTH_SUCCESS = 5;
    public static final int AUTH_FAIL = 6;
    public static final int REGISTER = 7;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    private ChatClientGUI gui;

    //  CONSTRUCTOR 
    public Client(String username, ChatClientGUI gui) {

        this.gui = gui;
        boolean authenticated = false;

        while (!authenticated) {

            try {
                //  new connection every attempt
                socket = new Socket("localhost", 5000);
                gui.appendMessage("Connected to server.");

                in = new DataInputStream(socket.getInputStream());
                out = new DataOutputStream(socket.getOutputStream());

                // OPTION DIALOG
                String[] options = {"Login", "Register"};
                int choice = JOptionPane.showOptionDialog(
                        null,
                        "Choose option",
                        "Authentication",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.INFORMATION_MESSAGE,
                        null,
                        options,
                        options[0]
                );

                //  User pressed close
                if (choice == -1) {
                    socket.close();
                    System.exit(0);
                }

                // PASSWORD INPUT
                String password = JOptionPane.showInputDialog("Enter password:");

                //  User pressed cancel
                if (password == null) {
                    socket.close();
                    System.exit(0);
                }

                //  empty password
                if (password.trim().isEmpty()) {
                    gui.appendMessage("Password cannot be empty.");
                    socket.close();
                    continue;
                }

                // SEND TYPE
                if (choice == 0) {
                    out.writeInt(LOGIN);
                } else {
                    out.writeInt(REGISTER);
                }

                out.writeUTF(username);
                out.writeUTF(password);
                out.flush();

                int response = in.readInt();

                if (response == AUTH_SUCCESS) {

                    if (choice == 1) {
                        gui.appendMessage("Registered successfully. Please login.");
                        socket.close();
                        continue;
                    }

                    gui.appendMessage("Login successful.");
                    authenticated = true;

                } else {
                    gui.appendMessage("Authentication failed. Try again.");
                    socket.close();
                }

            } catch (Exception e) {
                gui.appendMessage("Connection error. Retrying...");
            }
        }

        //  Start listening after successful login
        listenForMessages();
    }
    // END CONSTRUCTOR 


    //  SEND MESSAGE 
    public void sendMessage(String message) {
        try {
            if (socket == null || socket.isClosed()) return;

            out.writeInt(TEXT);
            out.writeUTF(message);
            out.flush();

        } catch (Exception e) {
            gui.appendMessage("Failed to send message.");
            e.printStackTrace();
        }
    }


    //  SEND FILE 
    public void sendFile(String path) {
        try {
            if (socket == null || socket.isClosed()) return;

            File file = new File(path);
            if (!file.exists()) {
                gui.appendMessage("File not found.");
                return;
            }

            FileInputStream fis = new FileInputStream(file);

            out.writeInt(FILE);
            out.writeUTF(file.getName());
            out.writeLong(file.length());

            byte[] buffer = new byte[4096];
            int read;

            while ((read = fis.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }

            out.flush();
            fis.close();

            gui.appendMessage("File sent: " + file.getName());

        } catch (Exception e) {
            gui.appendMessage("File sending failed.");
            e.printStackTrace();
        }
    }


    //  LISTEN =
    private void listenForMessages() {
        new Thread(() -> {
            try {
                while (true) {
                    int type = in.readInt();

                    if (type == TEXT) {
                        String message = in.readUTF();
                        gui.appendMessage(message);

                    } else if (type == FILE) {

                        String fileName = in.readUTF();
                        long fileSize = in.readLong();

                        byte[] fileData = new byte[(int) fileSize];
                        in.readFully(fileData);

                        java.io.FileOutputStream fos =
                                new java.io.FileOutputStream("received_" + fileName);

                        fos.write(fileData);
                        fos.close();

                        gui.appendMessage("File received: " + fileName);
                    }
                }

            } catch (Exception e) {
                gui.appendMessage("Disconnected from server.");
            }
        }).start();
    }


    //  DISCONNECT 
    public void disconnect() {
        try {
            if (socket != null && !socket.isClosed()) {
                out.writeInt(EXIT);
                out.flush();
                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
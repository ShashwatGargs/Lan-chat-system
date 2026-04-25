import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler extends Thread {
    private Socket socket;
    private Server server;
    private DataInputStream in;
    private DataOutputStream out;
    private String username;
    private static final int USERNAME = 0;
    private static final int TEXT = 1;
    private static final int FILE = 2;
    private static final int EXIT = 3;
    private static final int LOGIN = 4;
    private static final int AUTH_SUCCESS = 5;
    private static final int AUTH_FAIL = 6;
    private static final int REGISTER = 7;

    ClientHandler(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    public void run() {
        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            // Authentication
            int type = in.readInt();

            if (type == LOGIN) {
                String user = in.readUTF();
                String pass = in.readUTF();

                if (server.authenticate(user, pass)) {
                    username = user;
                    out.writeInt(AUTH_SUCCESS);
                    out.flush();

                    server.broadcastText("🔵 " + username + " joined the chat.", this);

                } else {
                    out.writeInt(AUTH_FAIL);
                    out.flush();
                    socket.close();
                    return;
                }
            }

            else if (type == REGISTER) {
                String user = in.readUTF();
                String pass = in.readUTF();

                if (server.register(user, pass)) {
                    out.writeInt(AUTH_SUCCESS);
                } else {
                    out.writeInt(AUTH_FAIL);
                }
                out.flush();

            }
  
            while (true) {
                type = in.readInt();
                if (type == TEXT) {
                    String message = in.readUTF();

                    server.broadcastText(username + ": " + message, this);

                } else if (type == FILE) {
                    String fileName = in.readUTF();
                    long fileSize = in.readLong();

                    byte[] fileData = new byte[(int) fileSize];
                    in.readFully(fileData);

                    server.broadcastFile(fileName, fileData, this);
                } else if (type == EXIT) {
                    break;
                }
            }

        } catch (Exception e) {
            server.broadcastText("🔴 " + username + " left the chat.", this);
        } finally {
            server.removeClient(this);

            try {

                socket.close();

            } catch (Exception ignored) {
            }

        }
    }

    public void sendText(String message) {
        try {
            out.writeInt(TEXT);
            out.writeUTF(message);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendFile(String fileName, byte[] fileData) {

        try {

            out.writeInt(FILE);
            out.writeUTF(fileName);
            out.writeLong(fileData.length);
            out.write(fileData);
            out.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getUsername() {
        return username;
    }

}

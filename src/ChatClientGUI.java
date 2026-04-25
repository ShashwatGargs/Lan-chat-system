import javax.swing.*;
import java.awt.*;
import java.io.File;

public class ChatClientGUI extends JFrame {

    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;
    private JButton fileButton;

    private Client client;

    public ChatClientGUI() {

        // Ask username before connecting
        String username = JOptionPane.showInputDialog(
                this,
                "Enter your username:",
                "Username",
                JOptionPane.PLAIN_MESSAGE);

        if (username == null || username.trim().isEmpty()) {
            System.exit(0);
        }

        setTitle("LAN Chat - " + username);
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // CHAT AREA
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);

        JScrollPane scrollPane = new JScrollPane(chatArea);

        add(scrollPane, BorderLayout.CENTER);

        // BOTTOM PANEL
        JPanel bottomPanel = new JPanel(new BorderLayout());

        messageField = new JTextField();
        sendButton = new JButton("Send");
        fileButton = new JButton("Send File");

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2));
        buttonPanel.add(sendButton);
        buttonPanel.add(fileButton);

        bottomPanel.add(messageField, BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);

        // BUTTON ACTIONS
        sendButton.addActionListener(e -> sendMessage());

        messageField.addActionListener(e -> sendMessage());

        fileButton.addActionListener(e -> sendFile());

        setVisible(true);

        // connect client AFTER GUI loads
        new Thread(() -> {
            client = new Client(username, this);
        }).start();
    }

    // SEND MESSAGE
    private void sendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            client.sendMessage(message); // in client class
            messageField.setText("");
        }
    }

    // SEND FILE
    private void sendFile() {

        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            client.sendFile(file.getAbsolutePath());
        }
    }

    // DISPLAY MESSAGE IN CHAT
    public void appendMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            chatArea.append(message + "\n");
            chatArea.setCaretPosition(chatArea.getDocument().getLength());

        });
    }

    // MAIN METHOD
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ChatClientGUI());

    }
}
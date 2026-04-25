import java.sql.*;

public class DatabaseManager {

    private static final String URL = "jdbc:mysql://localhost:3306/lan_chat?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "19072005";

    private Connection connection;

    public DatabaseManager() {

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            connection = DriverManager.getConnection(URL, USER, PASSWORD);

            System.out.println("Database connected.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveMessage(String username, String message) {

        String sql = "INSERT INTO messages(username, message) VALUES (?, ?)";

        try {

            PreparedStatement stmt = connection.prepareStatement(sql);

            stmt.setString(1, username);
            stmt.setString(2, message);

            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean checkUser(String username, String password) {

        String sql = "SELECT password FROM users WHERE username=?";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");
                return storedPassword.equals(password); //  plain compare
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean registerUser(String username, String password) {

        String sql = "INSERT INTO users(username, password) VALUES (?, ?)";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password); //  store plain

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            return false; // username already exists
        }
    }
}
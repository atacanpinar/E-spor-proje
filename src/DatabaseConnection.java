import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.CallableStatement;

public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/proje";
    private static final String USER = "postgres";
    private static final String PASSWORD = "3535";

    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.out.println("Bağlantı Hatası: " + e.getMessage());
        }
        return conn;
    }

    public static boolean transferPlayer(int playerId, int newTeamId) {
        String query = "{CALL transfer_player_proc(?, ?)}";
        try (Connection conn = connect();
             CallableStatement stmt = conn.prepareCall(query)) {

            stmt.setInt(1, playerId);
            stmt.setInt(2, newTeamId);
            stmt.execute();
            return true;
        } catch (SQLException e) {
            System.out.println("Hata: " + e.getMessage());
            return false;
        }
    }
    // -------------------------------------
}
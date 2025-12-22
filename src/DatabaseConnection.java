import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Veritabanı adı 'proje' olarak ayarlandı
    private static final String URL = "jdbc:postgresql://localhost:5432/proje";
    private static final String USER = "postgres";

    // BURAYI DÜZENLE: Yeni bilgisayardaki pgAdmin şifren neyse onu yaz!
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
}
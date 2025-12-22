import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class LeaderboardScreen extends JFrame {
    private JTable table;
    private DefaultTableModel model;

    public LeaderboardScreen() {
        setTitle("Lig Puan Durumu (Leaderboard)");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(new Color(240, 240, 240));

        // BAŞLIK
        JLabel lblTitle = new JLabel("PUAN DURUMU", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setForeground(new Color(0, 51, 102)); // Koyu Lacivert
        lblTitle.setBounds(50, 10, 400, 30);
        add(lblTitle);

        // TABLO
        model = new DefaultTableModel();
        model.addColumn("Sıra");
        model.addColumn("Takım Adı");
        model.addColumn("Puan");

        table = new JTable(model);
        table.setRowHeight(25);
        table.setFont(new Font("Arial", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(30, 60, 430, 280);
        add(scrollPane);

        loadLeaderboard();
    }

    private void loadLeaderboard() {
        model.setRowCount(0);
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             // ORDER BY league_points DESC -> En yüksek puanlı en üstte!
             ResultSet rs = stmt.executeQuery("SELECT team_name, league_points FROM teams ORDER BY league_points DESC")) {

            int rank = 1;
            while (rs.next()) {
                model.addRow(new Object[]{
                        rank++, // Sıralama (1., 2., 3...)
                        rs.getString("team_name"),
                        rs.getInt("league_points")
                });
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }
}

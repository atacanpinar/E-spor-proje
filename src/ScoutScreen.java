import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ScoutScreen extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private int selectedPlayerId = -1;

    // Transfer işlemini yapacak kişinin bilgileri
    private String userRole;
    private int userTeamId;

    public ScoutScreen(String role, int teamId) {
        this.userRole = role;
        this.userTeamId = teamId;

        setTitle("Scout & Transfer Pazarı - (Hoşgeldin " + role + ")");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Ana menüyü kapatmasın
        setLocationRelativeTo(null);
        setLayout(null);

        // Başlık ve Bilgi
        JLabel lblInfo = new JLabel("Boştaki Oyuncular Listesi (Free Agents)");
        lblInfo.setFont(new Font("Arial", Font.BOLD, 16));
        lblInfo.setBounds(20, 20, 400, 30);
        add(lblInfo);

        JLabel lblSubInfo = new JLabel("Takımınıza katmak istediğiniz oyuncuyu seçip butona basın.");
        lblSubInfo.setFont(new Font("Arial", Font.PLAIN, 12));
        lblSubInfo.setForeground(Color.GRAY);
        lblSubInfo.setBounds(20, 50, 400, 20);
        add(lblSubInfo);

        // TRANSFER BUTONU
        JButton btnTransfer = new JButton("Takımıma Kat (+)");
        btnTransfer.setBounds(500, 20, 160, 40);
        btnTransfer.setBackground(new Color(0, 153, 255)); // Mavi renk
        btnTransfer.setForeground(Color.WHITE);
        btnTransfer.setFont(new Font("Arial", Font.BOLD, 12));
        add(btnTransfer);

        // TABLO
        model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Nickname");
        model.addColumn("Rank Puanı");
        model.addColumn("Ad Soyad");

        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(20, 80, 640, 360);
        add(scrollPane);

        // Tablo Tıklama Olayı (Seçilen oyuncunun ID'sini al)
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    selectedPlayerId = Integer.parseInt(model.getValueAt(row, 0).toString());
                }
            }
        });

        // Buton İşlevi
        btnTransfer.addActionListener(e -> transferPlayer());

        // Ekran açılınca verileri getir
        loadFreeAgents();
    }

    // --- METOTLAR ---

    // 1. Boştaki Oyuncuları Listeleme
    private void loadFreeAgents() {
        model.setRowCount(0); // Tabloyu temizle
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             // Sadece team_id'si NULL olanları çekiyoruz
             ResultSet rs = stmt.executeQuery("SELECT * FROM players WHERE team_id IS NULL OR team_id = 0 ORDER BY rank_score DESC")) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("player_id"),
                        rs.getString("nickname"),
                        rs.getInt("rank_score"),
                        rs.getString("first_name") + " " + rs.getString("last_name")
                });
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    // 2. Transfer İşlemi
    private void transferPlayer() {
        if (selectedPlayerId == -1) {
            JOptionPane.showMessageDialog(this, "Lütfen listeden bir oyuncu seçin!", "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Yetki Kontrolü: Sadece Kaptanlar transfer yapabilir
        if (!"KAPTAN".equals(userRole)) {
            JOptionPane.showMessageDialog(this, "Transfer işlemini sadece Takım Kaptanları yapabilir.\nAdminler 'Oyuncu Yönetimi' ekranını kullanmalıdır.", "Yetki Hatası", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Onay Kutusu
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bu oyuncuyu takımınıza transfer etmek istiyor musunuz?",
                "Transfer Onayı", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.connect()) {
                // Oyuncunun team_id'sini, giriş yapan kaptanın team_id'si ile güncelliyoruz
                String sql = "UPDATE players SET team_id = ? WHERE player_id = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, userTeamId); // Kaptanın takımı
                pstmt.setInt(2, selectedPlayerId); // Seçilen oyuncu

                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "Hayırlı Olsun! Transfer Başarılı. 🎉");
                    loadFreeAgents(); // Listeyi yenile (Transfer olan listeden düşmeli)
                    selectedPlayerId = -1; // Seçimi sıfırla
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Hata: " + ex.getMessage());
            }
        }
    }
}
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.Random;

public class SimulationScreen extends JFrame {
    private JComboBox<TeamItem> cmbTeam1, cmbTeam2;
    private JLabel lblResult;
    private JButton btnSimulate;

    // ComboBox için yardımcı sınıf
    class TeamItem {
        int id;
        String name;
        public TeamItem(int id, String name) { this.id = id; this.name = name; }
        @Override
        public String toString() { return name; }
    }

    public SimulationScreen() {
        setTitle("Maç Simülasyonu (Espor Modu)");
        setSize(650, 480);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        // --- TASARIM (Koyu Tema) ---
        getContentPane().setBackground(new Color(30, 30, 40)); // Koyu Espor Teması

        // BAŞLIK
        JLabel lblTitle = new JLabel("VADİ KARŞILAŞMASI", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBounds(50, 20, 550, 30);
        add(lblTitle);

        // --- BLUE SIDE (SOL TARAF) ---
        JLabel lblT1 = new JLabel("BLUE SIDE 🔵");
        lblT1.setForeground(new Color(0, 191, 255)); // Derin Gökyüzü Mavisi
        lblT1.setFont(new Font("Arial", Font.BOLD, 16));
        lblT1.setBounds(50, 80, 200, 20);
        add(lblT1);

        cmbTeam1 = new JComboBox<>();
        cmbTeam1.setBounds(50, 105, 220, 35);
        add(cmbTeam1);

        // Blue Side Avantaj Bilgisi
        JLabel lblBlueBuff = new JLabel("(Draft Avantajı: +%5 Güç)");
        lblBlueBuff.setForeground(Color.GRAY);
        lblBlueBuff.setFont(new Font("Arial", Font.ITALIC, 10));
        lblBlueBuff.setBounds(50, 145, 200, 15);
        add(lblBlueBuff);

        // --- VS YAZISI ---
        JLabel lblVS = new JLabel("VS", SwingConstants.CENTER);
        lblVS.setFont(new Font("Arial", Font.BOLD, 40));
        lblVS.setForeground(new Color(200, 200, 200)); // Gri
        lblVS.setBounds(290, 100, 60, 40);
        add(lblVS);

        // --- RED SIDE (SAĞ TARAF) ---
        JLabel lblT2 = new JLabel("RED SIDE 🔴");
        lblT2.setForeground(new Color(255, 69, 0)); // Kırmızı-Turuncu
        lblT2.setFont(new Font("Arial", Font.BOLD, 16));
        lblT2.setBounds(360, 80, 200, 20);
        add(lblT2);

        cmbTeam2 = new JComboBox<>();
        cmbTeam2.setBounds(360, 105, 220, 35);
        add(cmbTeam2);

        JLabel lblRedBuff = new JLabel("(Counter Pick İmkanı)");
        lblRedBuff.setForeground(Color.GRAY);
        lblRedBuff.setFont(new Font("Arial", Font.ITALIC, 10));
        lblRedBuff.setBounds(360, 145, 200, 15);
        add(lblRedBuff);

        // MAÇI BAŞLAT BUTONU
        btnSimulate = new JButton("MAÇI BAŞLAT 🎮");
        btnSimulate.setBounds(225, 200, 200, 45);
        btnSimulate.setBackground(new Color(34, 139, 34)); // Orman Yeşili
        btnSimulate.setForeground(Color.WHITE);
        btnSimulate.setFont(new Font("Arial", Font.BOLD, 16));
        btnSimulate.setFocusPainted(false);
        add(btnSimulate);

        // SONUÇ ALANI
        lblResult = new JLabel("Vadi hazır, şampiyonlar bekleniyor...", SwingConstants.CENTER);
        lblResult.setFont(new Font("Arial", Font.BOLD, 15));
        lblResult.setForeground(new Color(255, 215, 0)); // Altın Sarısı
        lblResult.setBounds(20, 270, 600, 30);
        add(lblResult);

        // Detaylı Bilgi
        JLabel lblInfo = new JLabel("Not: Algoritmada Takım Gücü, Günlük Form, Blue Side Avantajı ve Kritik Şans etkilidir.", SwingConstants.CENTER);
        lblInfo.setFont(new Font("Arial", Font.PLAIN, 10));
        lblInfo.setForeground(Color.LIGHT_GRAY);
        lblInfo.setBounds(50, 400, 550, 20);
        add(lblInfo);

        // --- İŞLEMLER ---
        loadTeams();
        btnSimulate.addActionListener(e -> playMatch());
    }

    // --- METOTLAR ---

    // 1. Takımları Yükle
    private void loadTeams() {
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT team_id, team_name FROM teams")) {
            while (rs.next()) {
                TeamItem item = new TeamItem(rs.getInt("team_id"), rs.getString("team_name"));
                cmbTeam1.addItem(item);
                cmbTeam2.addItem(item);
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    // 2. Takım Gücünü Veritabanından Al (SQL SUM)
    private int getTeamPower(int teamId) {
        int power = 0;
        try (Connection conn = DatabaseConnection.connect()) {
            String sql = "SELECT SUM(rank_score) as total_power FROM players WHERE team_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, teamId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                power = rs.getInt("total_power");
            }
        } catch (Exception ex) { ex.printStackTrace(); }
        return power;
    }

    // 3. MAÇ ALGORİTMASI (ESPOR VERSİYONU)
    private void playMatch() {
        TeamItem t1 = (TeamItem) cmbTeam1.getSelectedItem();
        TeamItem t2 = (TeamItem) cmbTeam2.getSelectedItem();

        if (t1 == null || t2 == null) return;
        if (t1.id == t2.id) {
            JOptionPane.showMessageDialog(this, "Aynı takım kendisiyle maç yapamaz (Scrim modu kapalı)!", "Hata", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // A. Veritabanındaki HAM Güç
        int rawPower1 = getTeamPower(t1.id);
        int rawPower2 = getTeamPower(t2.id);

        if (rawPower1 == 0) rawPower1 = 100; // Boş takım koruması
        if (rawPower2 == 0) rawPower2 = 100;

        Random rand = new Random();

        // B. GÜNLÜK FORM (Mental Durum)
        // Esporcular robot değildir, o günkü mental durumları performansı %40 - %120 arası etkiler.
        double form1 = 0.4 + (0.8 * rand.nextDouble());
        double form2 = 0.4 + (0.8 * rand.nextDouble());

        int finalScore1 = (int) (rawPower1 * form1);
        int finalScore2 = (int) (rawPower2 * form2);

        // C. BLUE SIDE (DRAFT) AVANTAJI 🔵
        // İstatistiklere göre Blue Side %52-53 kazanma oranına sahiptir.
        // Bu yüzden Blue Side'a %5 stratejik avantaj puanı ekliyoruz.
        finalScore1 = (int) (finalScore1 * 1.05);

        System.out.println(">>> Blue Side (" + t1.name + ") First Pick avantajını kullandı: +%5 Güç");

        // D. KRİTİK AN / OUTPLAY (%10 Şans)
        // Zayıf takım bir "Teamfight"ı mükemmel oynayıp maçı çevirebilir.
        boolean crit1 = rand.nextInt(100) < 10;
        boolean crit2 = rand.nextInt(100) < 10;

        if (crit1) finalScore1 *= 3;
        if (crit2) finalScore2 *= 3;

        // E. SONUÇ
        String winnerText;
        String logText;

        if (finalScore1 > finalScore2) {
            winnerText = "VICTORY! " + t1.name + " KAZANDI! 🏆";
            logText = t1.name + " (Blue) yendi " + t2.name + " (Red)";
        } else if (finalScore2 > finalScore1) {
            winnerText = "VICTORY! " + t2.name + " KAZANDI! 🏆";
            logText = t2.name + " (Red) yendi " + t1.name + " (Blue)";
        } else {
            winnerText = "MAÇ BERABERE! (Base Race?)";
            logText = "Berabere: " + t1.name + " vs " + t2.name;
        }

        lblResult.setText(winnerText + " (Skor: " + finalScore1 + " - " + finalScore2 + ")");

        // Konsol Logları (Hocaya göstermelik)
        System.out.println("--- MAÇ DETAYI ---");
        System.out.println("BLUE: " + t1.name + " | Ham Güç: " + rawPower1 + " | Form: " + String.format("%.2f", form1) + " | Sonuç: " + finalScore1);
        System.out.println("RED : " + t2.name + " | Ham Güç: " + rawPower2 + " | Form: " + String.format("%.2f", form2) + " | Sonuç: " + finalScore2);

        // Veritabanı Log
        logMatchResult(logText + " [" + finalScore1 + "-" + finalScore2 + "]");
    }

    private void logMatchResult(String message) {
        try (Connection conn = DatabaseConnection.connect()) {
            String sql = "INSERT INTO activity_logs (message) VALUES (?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "Simülasyon: " + message);
            pstmt.executeUpdate();
        } catch (Exception ex) { ex.printStackTrace(); }
    }
}
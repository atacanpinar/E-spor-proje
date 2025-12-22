import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenu extends JFrame {

    // Giriş yapan kullanıcının bilgileri
    private String userRole;
    private int userTeamId;

    // Constructor
    public MainMenu(String role, int teamId) {
        this.userRole = role;
        this.userTeamId = teamId;

        // Pencere Ayarları
        setTitle("E-Spor Yönetim Paneli - " + role);
        setSize(500, 550); // Tüm butonlar sığsın diye boyutu ayarladık
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Ekranın ortasında açılır
        setLayout(null);

        // Başlık
        JLabel lblTitle = new JLabel("Hoşgeldiniz! (" + role + ")");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setBounds(100, 20, 300, 30);
        add(lblTitle);

        // 1. Takım İşlemleri Butonu
        JButton btnTeams = new JButton("Takım İşlemleri");
        btnTeams.setBounds(150, 70, 200, 40);
        add(btnTeams);

        // 2. Oyuncu İşlemleri Butonu
        JButton btnPlayers = new JButton("Oyuncu İşlemleri");
        btnPlayers.setBounds(150, 130, 200, 40);
        add(btnPlayers);

        // 3. Turnuva İşlemleri Butonu
        JButton btnTournaments = new JButton("Turnuva İşlemleri");
        btnTournaments.setBounds(150, 190, 200, 40);
        add(btnTournaments);

        // 4. Scout / Transfer Butonu (Mavi)
        JButton btnScout = new JButton("Scout / Transfer Pazarı");
        btnScout.setBounds(150, 250, 200, 40);
        btnScout.setBackground(new Color(0, 200, 255)); // Açık Mavi
        btnScout.setForeground(Color.BLACK);
        add(btnScout);

        // 5. Maç Simülasyonu Butonu (Turuncu)
        JButton btnSim = new JButton("Maç Simülasyonu 🎮");
        btnSim.setBounds(150, 310, 200, 40);
        btnSim.setBackground(new Color(255, 102, 0)); // Turuncu
        btnSim.setForeground(Color.WHITE);
        btnSim.setFont(new Font("Arial", Font.BOLD, 12));
        add(btnSim);

        // 6. Puan Durumu Butonu (Sarı)
        JButton btnLeaderboard = new JButton("Puan Durumu 🏆");
        btnLeaderboard.setBounds(150, 370, 200, 40);
        btnLeaderboard.setBackground(new Color(255, 215, 0)); // Altın Sarısı
        btnLeaderboard.setForeground(Color.BLACK);
        add(btnLeaderboard);

        // 7. Çıkış Yap Butonu (Kırmızı)
        JButton btnLogout = new JButton("Çıkış Yap");
        btnLogout.setBounds(150, 440, 200, 30);
        btnLogout.setBackground(Color.RED);
        btnLogout.setForeground(Color.WHITE);
        add(btnLogout);

        // --- ZİYARETÇİ KISITLAMASI ---
        if ("GUEST".equals(userRole)) {
            // Ziyaretçi transfer yapamaz, butonu kilitliyoruz
            btnScout.setEnabled(false);
            btnScout.setText("Scout (Yetkisiz)");
            btnScout.setBackground(Color.GRAY);
        }

        // --- BUTON AKSİYONLARI ---

        // Takım Ekranı (Rol bilgisini gönderiyoruz ki ziyaretçi ise kilitlensin)
        btnTeams.addActionListener(e -> new TeamScreen(userRole).setVisible(true));

        // Oyuncu Ekranı (Rol + Takım ID)
        btnPlayers.addActionListener(e -> new PlayerScreen(userRole, userTeamId).setVisible(true));

        // Turnuva Ekranı (Rol bilgisini gönderiyoruz - GÜNCELLENDİ)
        btnTournaments.addActionListener(e -> new TournamentScreen(userRole).setVisible(true));

        // Scout Ekranı (Transfer)
        btnScout.addActionListener(e -> new ScoutScreen(userRole, userTeamId).setVisible(true));

        // Simülasyon Ekranı
        btnSim.addActionListener(e -> new SimulationScreen().setVisible(true));

        // Puan Durumu Ekranı
        btnLeaderboard.addActionListener(e -> new LeaderboardScreen().setVisible(true));

        // Çıkış Yap
        btnLogout.addActionListener(e -> {
            dispose(); // Menüyü kapat
            new LoginScreen().setVisible(true); // Giriş ekranına dön
        });
    }
}
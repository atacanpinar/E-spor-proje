import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class PlayerScreen extends JFrame {
    private JTextField txtSearch;
    private JTextField txtFirstName, txtLastName, txtNickname, txtRank;
    private JComboBox<TeamItem> cmbTeams;
    private JTable table;
    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;

    private String userRole;
    private int userTeamId;

    private int selectedPlayerId = -1;
    private JButton btnAdd, btnUpdate, btnDelete;

    class TeamItem {
        int id; String name;
        public TeamItem(int id, String name) { this.id = id; this.name = name; }
        @Override public String toString() { return name; }
    }

    public PlayerScreen(String role, int teamId) {
        this.userRole = role;
        this.userTeamId = teamId;

        setTitle("Oyuncu Yönetimi - " + role);
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        // --- SOL TARAF (Girişler) ---
        JLabel lblAd = new JLabel("Ad:"); lblAd.setBounds(20, 30, 80, 25); add(lblAd);
        txtFirstName = new JTextField(); txtFirstName.setBounds(100, 30, 150, 25); add(txtFirstName);

        JLabel lblSoyad = new JLabel("Soyad:"); lblSoyad.setBounds(20, 70, 80, 25); add(lblSoyad);
        txtLastName = new JTextField(); txtLastName.setBounds(100, 70, 150, 25); add(txtLastName);

        JLabel lblNick = new JLabel("Nickname:"); lblNick.setBounds(20, 110, 80, 25); add(lblNick);
        txtNickname = new JTextField(); txtNickname.setBounds(100, 110, 150, 25); add(txtNickname);

        JLabel lblRank = new JLabel("Rank:"); lblRank.setBounds(20, 150, 80, 25); add(lblRank);
        txtRank = new JTextField(); txtRank.setBounds(100, 150, 150, 25); add(txtRank);

        JLabel lblTeam = new JLabel("Takımı:"); lblTeam.setBounds(20, 190, 80, 25); add(lblTeam);
        cmbTeams = new JComboBox<>();
        cmbTeams.setBounds(100, 190, 200, 25);
        add(cmbTeams);

        loadTeamsToCombo();

        // BUTONLAR
        btnAdd = new JButton("Ekle");
        btnAdd.setBounds(20, 250, 80, 30); btnAdd.setBackground(Color.GREEN); add(btnAdd);

        btnUpdate = new JButton("Güncelle");
        btnUpdate.setBounds(110, 250, 90, 30); btnUpdate.setBackground(Color.ORANGE); add(btnUpdate);

        btnDelete = new JButton("Sil");
        btnDelete.setBounds(210, 250, 80, 30); btnDelete.setBackground(Color.RED); btnDelete.setForeground(Color.WHITE); add(btnDelete);

        // --- SAĞ TARAF (ARAMA & TABLO) ---
        JLabel lblSearch = new JLabel("Oyuncu Ara 🔍:");
        lblSearch.setBounds(320, 20, 100, 25); add(lblSearch);

        txtSearch = new JTextField();
        txtSearch.setBounds(420, 20, 200, 25); add(txtSearch);

        model = new DefaultTableModel();
        model.addColumn("ID"); model.addColumn("Ad"); model.addColumn("Soyad");
        model.addColumn("Nickname"); model.addColumn("Rank"); model.addColumn("Takım");
        model.addColumn("TakımID");

        table = new JTable(model);
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        table.getColumnModel().getColumn(6).setMinWidth(0);
        table.getColumnModel().getColumn(6).setMaxWidth(0);
        table.getColumnModel().getColumn(6).setWidth(0);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(320, 55, 540, 480);
        add(scrollPane);

        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String text = txtSearch.getText();
                if (text.trim().length() == 0) sorter.setRowFilter(null);
                else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            }
        });

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int viewRow = table.getSelectedRow();
                if (viewRow == -1) return;
                int modelRow = table.convertRowIndexToModel(viewRow);

                selectedPlayerId = Integer.parseInt(model.getValueAt(modelRow, 0).toString());
                txtFirstName.setText(model.getValueAt(modelRow, 1).toString());
                txtLastName.setText(model.getValueAt(modelRow, 2).toString());
                txtNickname.setText(model.getValueAt(modelRow, 3).toString());
                txtRank.setText(model.getValueAt(modelRow, 4).toString());

                String teamIdStr = model.getValueAt(modelRow, 6).toString();
                int playerTeamId = teamIdStr.equals("0") ? 0 : Integer.parseInt(teamIdStr);

                // Yetki kontrolü her tıklamada çalışır
                checkPermissions(playerTeamId);
                setSelectedTeamInCombo(playerTeamId);
            }
        });

        // Buton İşlevleri
        btnAdd.addActionListener(e -> { addPlayer(); loadPlayers(); });
        btnUpdate.addActionListener(e -> { updatePlayer(); loadPlayers(); });
        btnDelete.addActionListener(e -> { deletePlayer(); loadPlayers(); });

        loadPlayers();

        // --- BAŞLANGIÇ KİLİTLERİ ---
        // Ekran ilk açıldığında Ziyaretçi ise "EKLE" butonunu hemen kapatıyoruz
        if ("GUEST".equals(userRole)) {
            btnAdd.setEnabled(false);    // KİLİTLENDİ 🔒
            btnUpdate.setEnabled(false); // KİLİTLENDİ 🔒
            btnDelete.setEnabled(false); // KİLİTLENDİ 🔒

            // Kullanıcıya hissettirmek için metin kutularını da kapatabilirsin (Opsiyonel)
            txtFirstName.setEditable(false);
            txtLastName.setEditable(false);
            txtNickname.setEditable(false);
            txtRank.setEditable(false);
            cmbTeams.setEnabled(false);

            setTitle(getTitle() + " - [SADECE İZLEME MODU]");
        }
        else if ("KAPTAN".equals(userRole)) {
            // Kaptan başta kimseyi seçmediği için sil/güncelle kapalı
            btnUpdate.setEnabled(false);
            btnDelete.setEnabled(false);
        }
    }

    private void setSelectedTeamInCombo(int teamId) {
        for (int i = 0; i < cmbTeams.getItemCount(); i++) {
            if (cmbTeams.getItemAt(i).id == teamId) {
                cmbTeams.setSelectedIndex(i);
                break;
            }
        }
    }

    // --- KRİTİK BÖLÜM: YETKİ KONTROLÜ ---
    private void checkPermissions(int playerTeamId) {
        // 1. ADMIN: Her şey serbest
        if ("ADMIN".equals(userRole)) {
            btnUpdate.setEnabled(true);
            btnDelete.setEnabled(true);
            btnAdd.setEnabled(true);
        }
        // 2. GUEST (ZİYARETÇİ): HER ŞEY YASAK ⛔
        else if ("GUEST".equals(userRole)) {
            btnAdd.setEnabled(false);
            btnUpdate.setEnabled(false);
            btnDelete.setEnabled(false);
        }
        // 3. KAPTAN: Sadece kendi takımı
        else if ("KAPTAN".equals(userRole)) {
            btnAdd.setEnabled(true);
            if (playerTeamId != 0 && userTeamId == playerTeamId) {
                btnUpdate.setEnabled(true);
                btnDelete.setEnabled(true);
            } else {
                btnUpdate.setEnabled(false);
                btnDelete.setEnabled(false);
            }
        }
    }

    private void loadTeamsToCombo() {
        cmbTeams.removeAllItems();
        cmbTeams.addItem(new TeamItem(0, "--- TAKIMSIZ (Serbest) ---"));
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT team_id, team_name FROM teams")) {
            while (rs.next()) cmbTeams.addItem(new TeamItem(rs.getInt("team_id"), rs.getString("team_name")));
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void loadPlayers() {
        model.setRowCount(0);
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT p.*, t.team_name, t.team_id as tid FROM players p LEFT JOIN teams t ON p.team_id = t.team_id ORDER BY p.player_id")) {
            while (rs.next()) {
                String teamName = rs.getString("team_name");
                if (teamName == null) teamName = "--- TAKIMSIZ ---";
                int tId = rs.getInt("tid");
                if (rs.wasNull()) tId = 0;
                model.addRow(new Object[]{rs.getInt("player_id"), rs.getString("first_name"), rs.getString("last_name"), rs.getString("nickname"), rs.getInt("rank_score"), teamName, tId});
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void addPlayer() {
        if ("GUEST".equals(userRole)) return; // Güvenlik önlemi
        TeamItem selectedTeam = (TeamItem) cmbTeams.getSelectedItem();
        if ("KAPTAN".equals(userRole) && selectedTeam.id != userTeamId) {
            JOptionPane.showMessageDialog(this, "Kaptanlar sadece kendi takımlarına oyuncu ekleyebilir!");
            return;
        }
        try (Connection conn = DatabaseConnection.connect()) {
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO players (first_name, last_name, nickname, rank_score, team_id) VALUES (?, ?, ?, ?, ?)");
            pstmt.setString(1, txtFirstName.getText()); pstmt.setString(2, txtLastName.getText());
            pstmt.setString(3, txtNickname.getText()); pstmt.setInt(4, Integer.parseInt(txtRank.getText()));
            if (selectedTeam.id == 0) pstmt.setNull(5, Types.INTEGER); else pstmt.setInt(5, selectedTeam.id);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Oyuncu Eklendi!");
            loadPlayers();
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Hata: " + ex.getMessage()); }
    }

    private void deletePlayer() {
        if ("GUEST".equals(userRole)) return;
        if (selectedPlayerId == -1) return;
        try (Connection conn = DatabaseConnection.connect()) {
            PreparedStatement pstmt = conn.prepareStatement("DELETE FROM players WHERE player_id = ?");
            pstmt.setInt(1, selectedPlayerId);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Silindi!");
            selectedPlayerId = -1;
            loadPlayers();
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void updatePlayer() {
        if ("GUEST".equals(userRole)) return;
        if (selectedPlayerId == -1) return;
        TeamItem selectedTeam = (TeamItem) cmbTeams.getSelectedItem();
        try (Connection conn = DatabaseConnection.connect()) {
            PreparedStatement pstmt = conn.prepareStatement("UPDATE players SET first_name=?, last_name=?, nickname=?, rank_score=?, team_id=? WHERE player_id=?");
            pstmt.setString(1, txtFirstName.getText()); pstmt.setString(2, txtLastName.getText());
            pstmt.setString(3, txtNickname.getText()); pstmt.setInt(4, Integer.parseInt(txtRank.getText()));
            if (selectedTeam.id == 0) pstmt.setNull(5, Types.INTEGER); else pstmt.setInt(5, selectedTeam.id);
            pstmt.setInt(6, selectedPlayerId);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Güncellendi!");
            selectedPlayerId = -1;
            loadPlayers();
        } catch (Exception ex) { ex.printStackTrace(); }
    }
}
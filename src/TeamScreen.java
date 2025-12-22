import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class TeamScreen extends JFrame {
    private JTextField txtTeamName;
    private JTable table;
    private DefaultTableModel model;
    private int selectedTeamId = -1;

    // Kullanıcı rolü
    private String userRole;

    public TeamScreen(String role) {
        this.userRole = role; // Rolü kaydet

        setTitle("Takım Yönetimi - " + role);
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        // --- GİRİŞ ALANLARI ---
        JLabel lblName = new JLabel("Takım Adı:");
        lblName.setBounds(20, 30, 80, 25);
        add(lblName);

        txtTeamName = new JTextField();
        txtTeamName.setBounds(100, 30, 150, 25);
        add(txtTeamName);

        // --- BUTONLAR ---
        JButton btnAdd = new JButton("Ekle");
        btnAdd.setBounds(20, 80, 70, 30);
        btnAdd.setBackground(Color.GREEN);
        add(btnAdd);

        JButton btnUpdate = new JButton("Güncelle");
        btnUpdate.setBounds(100, 80, 90, 30);
        btnUpdate.setBackground(Color.ORANGE);
        add(btnUpdate);

        JButton btnDelete = new JButton("Sil");
        btnDelete.setBounds(200, 80, 70, 30);
        btnDelete.setBackground(Color.RED);
        btnDelete.setForeground(Color.WHITE);
        add(btnDelete);

        // --- TABLO ---
        model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Takım Adı");
        model.addColumn("Puan");

        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(300, 20, 260, 320);
        add(scrollPane);

        // Tablo Tıklama
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                selectedTeamId = Integer.parseInt(model.getValueAt(row, 0).toString());
                txtTeamName.setText(model.getValueAt(row, 1).toString());
            }
        });

        // Buton Aksiyonları
        btnAdd.addActionListener(e -> addTeam());
        btnUpdate.addActionListener(e -> updateTeam());
        btnDelete.addActionListener(e -> deleteTeam());

        loadTeams();

        // --- ZİYARETÇİ KİLİDİ (GUEST MODE) ---
        if ("GUEST".equals(userRole)) {
            btnAdd.setEnabled(false);
            btnUpdate.setEnabled(false);
            btnDelete.setEnabled(false);
            txtTeamName.setEditable(false); // Yazı yazmayı da engelle
            setTitle(getTitle() + " [İZLEME MODU]");
        }
    }

    private void loadTeams() {
        model.setRowCount(0);
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM teams ORDER BY team_id")) {
            while (rs.next()) {
                model.addRow(new Object[]{rs.getInt("team_id"), rs.getString("team_name"), rs.getInt("league_points")});
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void addTeam() {
        if ("GUEST".equals(userRole)) return;
        try (Connection conn = DatabaseConnection.connect()) {
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO teams (team_name) VALUES (?)");
            pstmt.setString(1, txtTeamName.getText());
            pstmt.executeUpdate();
            loadTeams();
            txtTeamName.setText("");
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Hata: " + ex.getMessage()); }
    }

    private void updateTeam() {
        if ("GUEST".equals(userRole) || selectedTeamId == -1) return;
        try (Connection conn = DatabaseConnection.connect()) {
            PreparedStatement pstmt = conn.prepareStatement("UPDATE teams SET team_name=? WHERE team_id=?");
            pstmt.setString(1, txtTeamName.getText());
            pstmt.setInt(2, selectedTeamId);
            pstmt.executeUpdate();
            loadTeams();
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void deleteTeam() {
        if ("GUEST".equals(userRole) || selectedTeamId == -1) return;
        try (Connection conn = DatabaseConnection.connect()) {
            PreparedStatement pstmt = conn.prepareStatement("DELETE FROM teams WHERE team_id=?");
            pstmt.setInt(1, selectedTeamId);
            pstmt.executeUpdate();
            loadTeams();
            selectedTeamId = -1;
            txtTeamName.setText("");
        } catch (Exception ex) { ex.printStackTrace(); }
    }
}
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class TournamentScreen extends JFrame {
    private JTextField txtName, txtDate, txtPrize;
    private JTable table;
    private DefaultTableModel model;
    private int selectedTournId = -1;

    // Rol bilgisi
    private String userRole;

    public TournamentScreen(String role) {
        this.userRole = role; // Rolü al

        setTitle("Turnuva Yönetimi - " + role);
        setSize(700, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        // --- GİRİŞLER ---
        JLabel lblName = new JLabel("Turnuva Adı:"); lblName.setBounds(20, 30, 100, 25); add(lblName);
        txtName = new JTextField(); txtName.setBounds(120, 30, 150, 25); add(txtName);

        JLabel lblDate = new JLabel("Tarih (Y-A-G):"); lblDate.setBounds(20, 70, 100, 25); add(lblDate);
        txtDate = new JTextField(); txtDate.setBounds(120, 70, 150, 25); add(txtDate);

        JLabel lblPrize = new JLabel("Ödül Havuzu:"); lblPrize.setBounds(20, 110, 100, 25); add(lblPrize);
        txtPrize = new JTextField(); txtPrize.setBounds(120, 110, 150, 25); add(txtPrize);

        // --- BUTONLAR ---
        JButton btnAdd = new JButton("Ekle");
        btnAdd.setBounds(20, 160, 80, 30); btnAdd.setBackground(Color.GREEN); add(btnAdd);

        JButton btnDelete = new JButton("Sil");
        btnDelete.setBounds(110, 160, 80, 30); btnDelete.setBackground(Color.RED); btnDelete.setForeground(Color.WHITE); add(btnDelete);

        // --- TABLO ---
        model = new DefaultTableModel();
        model.addColumn("ID"); model.addColumn("Turnuva"); model.addColumn("Tarih"); model.addColumn("Ödül");

        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(300, 20, 360, 350);
        add(scrollPane);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                selectedTournId = Integer.parseInt(model.getValueAt(row, 0).toString());
                txtName.setText(model.getValueAt(row, 1).toString());
                txtDate.setText(model.getValueAt(row, 2).toString());
                txtPrize.setText(model.getValueAt(row, 3).toString());
            }
        });

        btnAdd.addActionListener(e -> addTournament());
        btnDelete.addActionListener(e -> deleteTournament());

        loadTournaments();

        // --- ZİYARETÇİ KİLİDİ ---
        if ("GUEST".equals(userRole)) {
            btnAdd.setEnabled(false);
            btnDelete.setEnabled(false);

            txtName.setEditable(false);
            txtDate.setEditable(false);
            txtPrize.setEditable(false);

            setTitle(getTitle() + " [İZLEME MODU]");
        }
    }

    private void loadTournaments() {
        model.setRowCount(0);
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM tournaments")) {
            while (rs.next()) {
                model.addRow(new Object[]{rs.getInt("tournament_id"), rs.getString("name"), rs.getDate("start_date"), rs.getInt("prize_pool")});
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void addTournament() {
        if ("GUEST".equals(userRole)) return;
        try (Connection conn = DatabaseConnection.connect()) {
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO tournaments (name, start_date, prize_pool) VALUES (?, CAST(? AS DATE), ?)");
            pstmt.setString(1, txtName.getText());
            pstmt.setString(2, txtDate.getText());
            pstmt.setInt(3, Integer.parseInt(txtPrize.getText()));
            pstmt.executeUpdate();
            loadTournaments();
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Hata (Tarih formatı YYYY-MM-DD olmalı): " + ex.getMessage()); }
    }

    private void deleteTournament() {
        if ("GUEST".equals(userRole) || selectedTournId == -1) return;
        try (Connection conn = DatabaseConnection.connect()) {
            PreparedStatement pstmt = conn.prepareStatement("DELETE FROM tournaments WHERE tournament_id=?");
            pstmt.setInt(1, selectedTournId);
            pstmt.executeUpdate();
            loadTournaments();
            selectedTournId = -1;
        } catch (Exception ex) { ex.printStackTrace(); }
    }
}
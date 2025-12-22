import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginScreen extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    public LoginScreen() {
        setTitle("E-Spor Sistemi - Giriş");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel lblUser = new JLabel("Kullanıcı Adı:");
        lblUser.setBounds(50, 50, 100, 30);
        add(lblUser);

        txtUsername = new JTextField();
        txtUsername.setBounds(150, 50, 150, 30);
        add(txtUsername);

        JLabel lblPass = new JLabel("Şifre:");
        lblPass.setBounds(50, 100, 100, 30);
        add(lblPass);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(150, 100, 150, 30);
        add(txtPassword);

        btnLogin = new JButton("Giriş Yap");
        btnLogin.setBounds(150, 160, 100, 30);
        add(btnLogin);

        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkLogin(txtUsername.getText(), new String(txtPassword.getPassword()));
            }
        });
    }

    private void checkLogin(String username, String password) {
        try (Connection conn = DatabaseConnection.connect()) {
            if(conn == null) {
                JOptionPane.showMessageDialog(this, "Veritabanına bağlanılamadı!");
                return;
            }

            // Kullanıcıyı sorguluyoruz
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Veritabanından ROL ve TAKIM ID bilgisini alıyoruz
                String role = rs.getString("role");
                int teamId = rs.getInt("team_id"); // Eklenen kısım burası

                JOptionPane.showMessageDialog(this, "Giriş Başarılı! Rol: " + role);

                // Ana Menüyü açarken hem rolü hem de takım ID'sini gönderiyoruz
                new MainMenu(role, teamId).setVisible(true);

                // Giriş ekranını kapatıyoruz
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Hatalı kullanıcı adı veya şifre!");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Bir hata oluştu: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginScreen().setVisible(true));
    }
}
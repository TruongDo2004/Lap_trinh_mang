package UDP;

import org.bson.Document;

import javax.swing.*;
import java.awt.*;

public class LoginGUI extends JFrame {
    private final JTextField tfUser = new JTextField();
    private final JPasswordField pfPass = new JPasswordField();

    public LoginGUI() {
        super("Đăng nhập");
        setSize(480, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        MongoDBHelper.connect();

        JPanel root = new JPanel(new BorderLayout(10,10));
        root.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        root.setBackground(new Color(245, 248, 250));

        JLabel title = new JLabel("Ứng dụng truyền file (UDP)", SwingConstants.CENTER);
        title.setOpaque(true);
        title.setBackground(new Color(25,118,210));
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        root.add(title, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setBackground(root.getBackground());
        center.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8,8,8,8);
        c.fill = GridBagConstraints.NONE;

        c.gridx=0; c.gridy=0; center.add(new JLabel("Username:"), c);
        tfUser.setPreferredSize(new Dimension(160,30)); // ~1/3 width
        c.gridx=1; center.add(tfUser, c);

        c.gridx=0; c.gridy=1; center.add(new JLabel("Password:"), c);
        pfPass.setPreferredSize(new Dimension(160,30)); // ~1/3 width
        c.gridx=1; center.add(pfPass, c);

        root.add(center, BorderLayout.CENTER);

        JPanel bot = new JPanel();
        bot.setBackground(root.getBackground());
        JButton btnLogin = new JButton("Đăng nhập");
        btnLogin.setBackground(new Color(67,160,71)); btnLogin.setForeground(Color.WHITE);
        btnLogin.addActionListener(e -> doLogin());
        JButton btnReg = new JButton("Đăng ký");
        btnReg.setBackground(new Color(255,167,38)); btnReg.setForeground(Color.WHITE);
        btnReg.addActionListener(e -> { new RegisterGUI().setVisible(true); dispose(); });
        bot.add(btnLogin); bot.add(btnReg);
        root.add(bot, BorderLayout.SOUTH);

        add(root);
        setVisible(true);
    }

    private void doLogin() {
        String u = tfUser.getText().trim();
        String p = new String(pfPass.getPassword());
        if (u.isEmpty() || p.isEmpty()) { JOptionPane.showMessageDialog(this, "Không được để trống"); return; }
        try {
            Document userDoc = MongoDBHelper.findUser(u);
            if (userDoc != null && p.equals(userDoc.getString("password"))) {
                String role = userDoc.getString("role");
                JOptionPane.showMessageDialog(this, "Đăng nhập thành công");
                if ("admin".equals(role)) {
                    new AdminPanelGUI().setVisible(true);
                } else {
                    new UserPanelGUI(u).setVisible(true);
                }
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Sai username hoặc password");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi kết nối DB: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginGUI::new);
    }
}

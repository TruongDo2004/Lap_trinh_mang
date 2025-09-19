package UDP;

import javax.swing.*;
import java.awt.*;

public class RegisterGUI extends JFrame {
    private final JTextField tfUser = new JTextField();
    private final JPasswordField pfPass = new JPasswordField();
    private final JPasswordField pfPass2 = new JPasswordField();

    public RegisterGUI() {
        super("Đăng ký");
        setSize(520, 340);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(10,10));
        root.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        root.setBackground(new Color(250, 246, 240));

        JLabel title = new JLabel("Tạo tài khoản", SwingConstants.CENTER);
        title.setOpaque(true);
        title.setBackground(new Color(244,81,30));
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        root.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(root.getBackground());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8,8,8,8);

        c.gridx=0; c.gridy=0; form.add(new JLabel("Username:"), c);
        tfUser.setPreferredSize(new Dimension(160,30)); c.gridx=1; form.add(tfUser, c);

        c.gridx=0; c.gridy=1; form.add(new JLabel("Password:"), c);
        pfPass.setPreferredSize(new Dimension(160,30)); c.gridx=1; form.add(pfPass, c);

        c.gridx=0; c.gridy=2; form.add(new JLabel("Re-type Password:"), c);
        pfPass2.setPreferredSize(new Dimension(160,30)); c.gridx=1; form.add(pfPass2, c);

        root.add(form, BorderLayout.CENTER);

        JPanel bot = new JPanel();
        bot.setBackground(root.getBackground());
        JButton btnReg = new JButton("Đăng ký");
        btnReg.setBackground(new Color(67,160,71)); btnReg.setForeground(Color.WHITE);
        btnReg.addActionListener(e -> doRegister());
        JButton btnBack = new JButton("Quay lại");
        btnBack.addActionListener(e -> { new LoginGUI().setVisible(true); dispose(); });
        bot.add(btnReg); bot.add(btnBack);
        root.add(bot, BorderLayout.SOUTH);

        add(root);
        setVisible(true);
    }

    private void doRegister() {
        String u = tfUser.getText().trim();
        String p1 = new String(pfPass.getPassword());
        String p2 = new String(pfPass2.getPassword());
        if (u.isEmpty() || p1.isEmpty() || p2.isEmpty()) { JOptionPane.showMessageDialog(this, "Không được để trống"); return; }
        if (!p1.equals(p2)) { JOptionPane.showMessageDialog(this, "Mật khẩu nhập lại không khớp"); return; }
        try {
            if (MongoDBHelper.findUser(u) != null) { JOptionPane.showMessageDialog(this, "Username đã tồn tại"); return; }
            MongoDBHelper.registerUser(u, p1, "user");
            JOptionPane.showMessageDialog(this, "Đăng ký thành công! Mời đăng nhập.");
            new LoginGUI().setVisible(true);
            dispose();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }
}

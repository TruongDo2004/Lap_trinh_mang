package UDP;

import org.bson.Document;
import org.bson.types.Binary;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class UserPanelGUI extends JFrame {
    private final String username;
    private JLabel lblClock;
    private JLabel lblSelected;
    private JProgressBar progressBar;
    private File selectedFile;
    private DefaultTableModel model;
    private JTextField hostField, portField;

    public UserPanelGUI(String username) {
        super("Gửi file - " + username);
        this.username = username;
        setSize(980, 640);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        initUI();
        startClock();
        loadHistory();
        setVisible(true);
    }

    private void initUI() {
        getContentPane().setBackground(new Color(235, 248, 255));
        setLayout(new BorderLayout(10,10));

        // Top bar
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(new Color(18, 115, 196));
        top.setBorder(new EmptyBorder(10,12,10,12));
        JLabel lblUser = new JLabel("Tài khoản: " + username);
        lblUser.setForeground(Color.WHITE); lblUser.setFont(new Font("Segoe UI", Font.BOLD, 16));
        top.add(lblUser, BorderLayout.WEST);

        JButton btnLogout = new JButton("Đăng xuất");
        btnLogout.setBackground(new Color(233,30,99)); btnLogout.setForeground(Color.WHITE);
        btnLogout.addActionListener(e -> { new LoginGUI().setVisible(true); dispose(); });
        top.add(btnLogout, BorderLayout.EAST);

        // Time panel below the top bar (aligned right)
        lblClock = new JLabel();
        lblClock.setForeground(Color.WHITE);
        lblClock.setHorizontalAlignment(SwingConstants.RIGHT);
        lblClock.setBorder(new EmptyBorder(6,6,6,6));
        JPanel timePanel = new JPanel(new BorderLayout());
        timePanel.setBackground(new Color(26, 137, 206));
        timePanel.add(lblClock, BorderLayout.EAST);

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(top, BorderLayout.NORTH);
        topContainer.add(timePanel, BorderLayout.SOUTH);
        add(topContainer, BorderLayout.NORTH);

        // Center area for controls
        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        center.setBorder(new EmptyBorder(12,12,12,12));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8,8,8,8);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx=0; c.gridy=0; center.add(new JLabel("Server host:"), c);
        hostField = new JTextField("localhost"); hostField.setPreferredSize(new Dimension(200,28));
        c.gridx=1; c.gridwidth=2; center.add(hostField, c);
        c.gridwidth=1; c.gridx=3; center.add(new JLabel("Port:"), c);
        portField = new JTextField("9002"); portField.setPreferredSize(new Dimension(100,28));
        c.gridx=4; center.add(portField, c);

        c.gridx=0; c.gridy=1; center.add(new JLabel("File:"), c);
        lblSelected = new JLabel("Chưa chọn file"); lblSelected.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        c.gridx=1; c.gridwidth=3; center.add(lblSelected, c);
        c.gridwidth=1; c.gridx=4;
        JButton btnChoose = new JButton("Chọn file");
        center.add(btnChoose, c);

        c.gridx=1; c.gridy=2; c.gridwidth=3;
        progressBar = new JProgressBar(0,100); progressBar.setStringPainted(true);
        progressBar.setVisible(false); // hidden until send
        progressBar.setPreferredSize(new Dimension(400,22));
        center.add(progressBar, c);
        c.gridwidth=1; c.gridx=4; c.gridy=2;
        JButton btnSend = new JButton("Gửi file");
        btnSend.setBackground(new Color(0,150,136)); btnSend.setForeground(Color.WHITE);
        center.add(btnSend, c);

        add(center, BorderLayout.CENTER);

        // Bottom file list - styled panel around table
        model = new DefaultTableModel(new Object[]{"STT","Tên file","Kích thước (KB)","Thời gian"}, 0) {
            public boolean isCellEditable(int r,int c){ return false; }
        };
        JTable table = new JTable(model);
        table.setRowHeight(28);
        table.getTableHeader().setBackground(new Color(100,181,246));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(200,230,255));

        JScrollPane scroll = new JScrollPane(table);
        JPanel tableWrap = new JPanel(new BorderLayout());
        tableWrap.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 220, 230), 1, true),
                BorderFactory.createEmptyBorder(6,6,6,6)
        ));
        tableWrap.setBackground(Color.WHITE);
        tableWrap.add(scroll, BorderLayout.CENTER);
        tableWrap.setPreferredSize(new Dimension(940, 260));
        add(tableWrap, BorderLayout.SOUTH);

        // Actions
        btnChoose.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                selectedFile = chooser.getSelectedFile();
                lblSelected.setText(selectedFile.getName());
            }
        });

        btnSend.addActionListener(e -> {
            if (selectedFile == null) { JOptionPane.showMessageDialog(this, "Vui lòng chọn file"); return; }
            String host = hostField.getText().trim();
            int port;
            try { port = Integer.parseInt(portField.getText().trim()); } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Port không hợp lệ"); return; }

            progressBar.setVisible(true);
            progressBar.setValue(0);

            UDPClient.sendFile(selectedFile, username, host, port, new UDPClient.ProgressListener() {
                @Override public void onProgress(long sent, long total) {
                    int p = (int)Math.round(sent * 100.0 / total);
                    SwingUtilities.invokeLater(() -> progressBar.setValue(p));
                }
                @Override public void onDone() {
                    SwingUtilities.invokeLater(() -> {
                        progressBar.setValue(100);
                        // wait short time for server to store into DB
                        new Timer(900, ev -> { loadHistory(); progressBar.setVisible(false); progressBar.setValue(0); lblSelected.setText("Chưa chọn file"); selectedFile = null; ((Timer)ev.getSource()).stop(); }).start();
                        JOptionPane.showMessageDialog(UserPanelGUI.this, "Gửi hoàn tất. Server đã lưu file vào MongoDB.");
                    });
                }
                @Override public void onError(Exception ex) {
                    SwingUtilities.invokeLater(() -> {
                        progressBar.setVisible(false);
                        JOptionPane.showMessageDialog(UserPanelGUI.this, "Lỗi gửi: " + ex.getMessage());
                    });
                }
            });
        });
    }

    private void startClock() {
        Timer t = new Timer(1000, e -> lblClock.setText(new SimpleDateFormat("HH:mm:ss dd/MM/yyyy").format(new Date())));
        t.start();
    }

    private void loadHistory() {
        SwingUtilities.invokeLater(() -> {
            model.setRowCount(0);
            try {
                java.util.List<Document> docs = MongoDBHelper.listFilesByUser(username);
                int idx = 1;
                for (Document d : docs) {
                    String fn = d.getString("filename");
                    Date t = d.getDate("created_at");
                    String ts = t == null ? "" : new SimpleDateFormat("HH:mm:ss dd/MM/yyyy").format(t);
                    int sizeKb = 0;
                    try { Binary bin = d.get("data", Binary.class); if (bin != null) sizeKb = Math.max(1, bin.getData().length / 1024); } catch (Exception ex) {}
                    model.addRow(new Object[]{idx++, fn, sizeKb, ts});
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }
}

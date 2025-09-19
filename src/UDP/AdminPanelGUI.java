package UDP;

import org.bson.Document;
import org.bson.types.ObjectId;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AdminPanelGUI extends JFrame {
    private final DefaultTableModel model;

    public AdminPanelGUI() {
        super("Admin - Quản lý files");
        setSize(1000, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        model = new DefaultTableModel(new Object[]{"STT","ID","Username","Filename","Thời gian"}, 0) {
            public boolean isCellEditable(int r,int c){ return false; }
        };
        JTable table = new JTable(model);
        table.setRowHeight(28);
        table.getTableHeader().setBackground(new Color(100,181,246));
        table.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        JButton btnDelete = new JButton("Xoá file đã chọn");
        btnDelete.setBackground(new Color(244,67,54)); btnDelete.setForeground(Color.WHITE);
        JButton btnLogout = new JButton("Đăng xuất");
        btnLogout.setBackground(new Color(33,150,243)); btnLogout.setForeground(Color.WHITE);
        bottom.add(btnDelete); bottom.add(btnLogout);
        add(bottom, BorderLayout.SOUTH);

        loadAll();

        btnDelete.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r >= 0) {
                String idHex = (String) model.getValueAt(r, 1);
                int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xoá file này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        MongoDBHelper.deleteFileById(new ObjectId(idHex));
                        model.removeRow(r);
                        JOptionPane.showMessageDialog(this, "Xoá thành công");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(this, "Xoá thất bại: " + ex.getMessage());
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Chọn 1 file để xoá.");
            }
        });

        btnLogout.addActionListener(e -> {
            new LoginGUI().setVisible(true);
            dispose();
        });

        setVisible(true);
    }

    private void loadAll() {
        SwingUtilities.invokeLater(() -> {
            model.setRowCount(0);
            try {
                List<Document> docs = MongoDBHelper.listAllFiles();
                int idx = 1;
                for (Document d : docs) {
                    ObjectId uid = d.getObjectId("user_id");
                    String uidStr = uid == null ? "" : uid.toHexString();
                    String username = d.getString("username");
                    String fname = d.getString("filename");
                    Date t = d.getDate("created_at");
                    String ts = t == null ? "" : new SimpleDateFormat("HH:mm:ss dd/MM/yyyy").format(t);
                    model.addRow(new Object[]{idx++, uidStr, username, fname, ts});
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }
}

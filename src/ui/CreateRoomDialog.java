package ui;

import model.Room;
import service.RoomServiceImpl;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CreateRoomDialog extends JDialog {
    private JCheckBox cbParking, cbLaundry, cbCleaning;
    private JTextField txtP, txtL, txtC; // Đưa ra ngoài để listener truy cập được

    public CreateRoomDialog(JFrame parent, RoomServiceImpl service, RoomPanel panel) {
        super(parent, "Thiết lập phòng mới", true);
        setSize(520, 680);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBorder(new EmptyBorder(20, 20, 20, 20));

        // --- 1. Thông tin cơ bản ---
        JPanel p1 = new JPanel(new GridLayout(2, 2, 10, 15));
        p1.setBorder(BorderFactory.createTitledBorder("Thông tin cơ bản"));
        JTextField txtCode = new JTextField();
        JTextField txtPrice = new JTextField();
        p1.add(new JLabel("Mã phòng:")); p1.add(txtCode);
        p1.add(new JLabel("Giá thuê:")); p1.add(txtPrice);

        // --- 2. Giá dịch vụ & Checkbox ---
        JPanel p2 = new JPanel(new GridBagLayout());
        p2.setBorder(BorderFactory.createTitledBorder("Đơn giá dịch vụ & Tiện ích"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField txtE = new JTextField("3500");
        JTextField txtW = new JTextField("15000");

        // Khởi tạo các ô nhập giá dịch vụ tùy chọn
        txtP = new JTextField("0");
        txtL = new JTextField("0");
        txtC = new JTextField("0");

        txtP.setEnabled(false);
        txtL.setEnabled(false);
        txtC.setEnabled(false);

        // Khởi tạo checkbox
        cbParking = new JCheckBox("Đăng ký");
        cbLaundry = new JCheckBox("Đăng ký");
        cbCleaning = new JCheckBox("Đăng ký");

        cbParking.addItemListener(e -> txtP.setEnabled(cbParking.isSelected()));
        cbLaundry.addItemListener(e -> txtL.setEnabled(cbLaundry.isSelected()));
        cbCleaning.addItemListener(e -> txtC.setEnabled(cbCleaning.isSelected()));

        addServiceRow(p2, gbc, 0, "Điện (kWh):", txtE, null);
        addServiceRow(p2, gbc, 1, "Nước (m3):", txtW, null);
        addServiceRow(p2, gbc, 2, "Gửi xe:", txtP, cbParking);
        addServiceRow(p2, gbc, 3, "Giặt giũ:", txtL, cbLaundry);
        addServiceRow(p2, gbc, 4, "Dọn dẹp:", txtC, cbCleaning);

        main.add(p1);
        main.add(Box.createVerticalStrut(20));
        main.add(p2);
        add(main, BorderLayout.CENTER);

        JButton btnSave = new JButton("LƯU PHÒNG");
        btnSave.setPreferredSize(new Dimension(0, 50));
        btnSave.setBackground(new Color(237, 77, 126));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 14));

        btnSave.addActionListener(e -> {
            try {
                Room r = new Room(txtCode.getText().trim(), "Standard", Double.parseDouble(txtPrice.getText().trim()));
                r.setElectricityPrice(Double.parseDouble(txtE.getText().trim()));
                r.setWaterPrice(Double.parseDouble(txtW.getText().trim()));

                r.setParkingPrice(cbParking.isSelected() ? Double.parseDouble(txtP.getText().trim()) : 0);
                r.setLaundryPrice(cbLaundry.isSelected() ? Double.parseDouble(txtL.getText().trim()) : 0);
                r.setCleaningPrice(cbCleaning.isSelected() ? Double.parseDouble(txtC.getText().trim()) : 0);

                List<String> selectedUtils = new ArrayList<>();
                if (cbParking.isSelected()) selectedUtils.add("Gửi xe");
                if (cbLaundry.isSelected()) selectedUtils.add("Giặt giũ");
                if (cbCleaning.isSelected()) selectedUtils.add("Dọn dẹp");

                r.setUtilities(selectedUtils);

                service.addRoom(r);
                panel.loadRooms(service.getRooms());
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: Vui lòng nhập đúng định dạng số!");
            }
        });
        add(btnSave, BorderLayout.SOUTH);
    }

    private void addServiceRow(JPanel panel, GridBagConstraints gbc, int row, String label, JTextField txt, JCheckBox cb) {
        gbc.gridy = row;
        gbc.weightx = 0.3; gbc.gridx = 0;
        panel.add(new JLabel(label), gbc);
        gbc.weightx = 0.4; gbc.gridx = 1;
        panel.add(txt, gbc);
        gbc.weightx = 0.3; gbc.gridx = 2;
        if (cb != null) panel.add(cb, gbc);
        else panel.add(new JLabel(""), gbc);
    }
}
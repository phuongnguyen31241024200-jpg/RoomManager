package ui;

import model.Room;
import service.RoomServiceImpl;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class UpdateRoomDialog extends JDialog {
    private JCheckBox cbParking, cbLaundry, cbCleaning;
    private JTextField txtCode, txtPrice, txtE, txtW, txtP, txtL, txtC;

    public UpdateRoomDialog(Window parent, RoomServiceImpl service, Room room, RoomPanel panel) {
        super(parent, "Chỉnh sửa thông tin phòng", ModalityType.APPLICATION_MODAL);
        setSize(520, 680);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBorder(new EmptyBorder(20, 20, 20, 20));

        // --- 1. Thông tin cơ bản ---
        JPanel p1 = new JPanel(new GridLayout(2, 2, 10, 15));
        p1.setBorder(BorderFactory.createTitledBorder("Thông tin cơ bản"));

        txtCode = new JTextField(room.getRoomCode());
        txtPrice = new JTextField(String.valueOf((int)room.getPrice()));
        p1.add(new JLabel("Mã phòng:")); p1.add(txtCode);
        p1.add(new JLabel("Giá thuê:")); p1.add(txtPrice);

        JPanel p2 = new JPanel(new GridBagLayout());
        p2.setBorder(BorderFactory.createTitledBorder("Đơn giá dịch vụ & Tiện ích"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        txtE = new JTextField(String.valueOf((int)room.getElectricityPrice()));
        txtW = new JTextField(String.valueOf((int)room.getWaterPrice()));
        txtP = new JTextField(String.valueOf((int)room.getParkingPrice()));
        txtL = new JTextField(String.valueOf((int)room.getLaundryPrice()));
        txtC = new JTextField(String.valueOf((int)room.getCleaningPrice()));

        cbParking = new JCheckBox("Đăng ký", room.getUtilities().contains("Gửi xe"));
        cbLaundry = new JCheckBox("Đăng ký", room.getUtilities().contains("Giặt giũ"));
        cbCleaning = new JCheckBox("Đăng ký", room.getUtilities().contains("Dọn dẹp"));

        // Thiết lập trạng thái enable dựa trên checkbox
        txtP.setEnabled(cbParking.isSelected());
        txtL.setEnabled(cbLaundry.isSelected());
        txtC.setEnabled(cbCleaning.isSelected());

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

        JButton btnUpdate = new JButton("CẬP NHẬT THÔNG TIN");
        btnUpdate.setPreferredSize(new Dimension(0, 50));
        btnUpdate.setBackground(new Color(41, 128, 185));
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.setFont(new Font("Segoe UI", Font.BOLD, 14));

        btnUpdate.addActionListener(e -> {
            try {
                // Cập nhật dữ liệu cho đối tượng room hiện tại
                room.setRoomCode(txtCode.getText().trim());
                room.setPrice(Double.parseDouble(txtPrice.getText().trim()));
                room.setElectricityPrice(Double.parseDouble(txtE.getText().trim()));
                room.setWaterPrice(Double.parseDouble(txtW.getText().trim()));

                room.setParkingPrice(cbParking.isSelected() ? Double.parseDouble(txtP.getText().trim()) : 0);
                room.setLaundryPrice(cbLaundry.isSelected() ? Double.parseDouble(txtL.getText().trim()) : 0);
                room.setCleaningPrice(cbCleaning.isSelected() ? Double.parseDouble(txtC.getText().trim()) : 0);

                List<String> selectedUtils = new ArrayList<>();
                if (cbParking.isSelected()) selectedUtils.add("Gửi xe");
                if (cbLaundry.isSelected()) selectedUtils.add("Giặt giũ");
                if (cbCleaning.isSelected()) selectedUtils.add("Dọn dẹp");
                room.setUtilities(selectedUtils);

                // Lưu vào file thông qua service
                service.saveData();
                panel.loadRooms(service.getRooms()); // Cập nhật lại giao diện thẻ
                JOptionPane.showMessageDialog(this, "Đã cập nhật thông tin phòng!");
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: Vui lòng kiểm tra định dạng số!");
            }
        });
        add(btnUpdate, BorderLayout.SOUTH);
    }

    private void addServiceRow(JPanel panel, GridBagConstraints gbc, int row, String label, JTextField txt, JCheckBox cb) {
        gbc.gridy = row; gbc.gridx = 0; panel.add(new JLabel(label), gbc);
        gbc.gridx = 1; panel.add(txt, gbc);
        gbc.gridx = 2; if (cb != null) panel.add(cb, gbc);
    }
}
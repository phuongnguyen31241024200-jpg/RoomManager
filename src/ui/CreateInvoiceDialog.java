package ui;

import model.Invoice;
import model.InvoiceStatus;
import service.InvoiceService;
import model.Room;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class CreateInvoiceDialog extends JDialog {

    public CreateInvoiceDialog(InvoiceService service, Room room, InvoicePanel invPanel, Runnable onDone) {
        if (room == null || room.getTenant() == null) {
            JOptionPane.showMessageDialog(null, "Phòng này hiện chưa có người thuê. Không thể tạo hóa đơn!");
            this.dispose();
            return;
        }

        setTitle("Tạo hóa đơn - Phòng " + room.getRoomCode());
        setSize(450, 600); // Tăng kích thước để chứa thêm các ô nhập
        setLocationRelativeTo(null);
        setModal(true);

        // Dùng JPanel chính để căn chỉnh cho đẹp
        JPanel mainPanel = new JPanel(new GridLayout(10, 2, 10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Các trường nhập liệu
        JTextField txtMonth = new JTextField(java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("MM/yyyy")));
        JTextField txtRoomPrice = new JTextField("0");
        JTextField txtElec = new JTextField("0");
        JTextField txtWater = new JTextField("0");
        JTextField txtService = new JTextField("0");

        String tName = room.getTenant().getFullName();
        String tId = room.getTenant().getAccountId();

        // Label thông tin cố định
        JLabel lblRoomCode = new JLabel(room.getRoomCode());
        JLabel lblTenantName = new JLabel(tName);
        JLabel lblTenantId = new JLabel(tId);

        JButton btnCreate = new JButton("Xác nhận & Gửi");
        btnCreate.setBackground(new Color(237, 77, 126));
        btnCreate.setForeground(Color.WHITE);
        btnCreate.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Thêm vào giao diện
        mainPanel.add(new JLabel(" Phòng:")); mainPanel.add(lblRoomCode);
        mainPanel.add(new JLabel(" ID Tài khoản:")); mainPanel.add(lblTenantId);
        mainPanel.add(new JLabel(" Khách thuê:")); mainPanel.add(lblTenantName);
        mainPanel.add(new JLabel(" Kỳ hóa đơn (Tháng):")); mainPanel.add(txtMonth);
        mainPanel.add(new JLabel(" Tiền phòng:")); mainPanel.add(txtRoomPrice);
        mainPanel.add(new JLabel(" Tiền điện:")); mainPanel.add(txtElec);
        mainPanel.add(new JLabel(" Tiền nước:")); mainPanel.add(txtWater);
        mainPanel.add(new JLabel(" Dịch vụ khác:")); mainPanel.add(txtService);
        mainPanel.add(new JLabel("")); mainPanel.add(new JLabel(""));
        mainPanel.add(new JLabel("")); mainPanel.add(btnCreate);

        add(mainPanel);

        btnCreate.addActionListener(e -> {
            try {
                String month = txtMonth.getText().trim();

                // Lấy dữ liệu số
                double rPrice = Double.parseDouble(txtRoomPrice.getText().trim());
                double eFee = Double.parseDouble(txtElec.getText().trim());
                double wFee = Double.parseDouble(txtWater.getText().trim());
                double sFee = Double.parseDouble(txtService.getText().trim());

                // Tính tổng tiền theo logic cũ nhưng tự động hơn
                double totalAmount = rPrice + eFee + wFee + sFee;

                if (month.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Vui lòng nhập kỳ hóa đơn!");
                    return;
                }

                // Khởi tạo hóa đơn với Constructor mới (Có đủ các loại tiền chi tiết)
                Invoice invoice = new Invoice(
                        room.getRoomCode(),
                        tName,
                        month,
                        totalAmount,
                        room.getRoomId(),
                        tId,
                        rPrice, // roomPrice
                        eFee,   // electricityFee
                        wFee,   // waterFee
                        sFee    // serviceFee
                );

                invoice.setStatus(InvoiceStatus.UNPAID);

                // Lưu xuống file (Logic cũ giữ nguyên)
                service.createInvoice(invoice);

                // Cập nhật giao diện Admin
                if (invPanel != null) {
                    invPanel.addInvoice(invoice);
                }

                if (onDone != null) onDone.run();

                JOptionPane.showMessageDialog(this, "Đã xuất hóa đơn thành công!\nTổng tiền: " + String.format("%,.0f đ", totalAmount));
                dispose();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập số tiền hợp lệ!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
    }
}
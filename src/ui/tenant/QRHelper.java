package ui.tenant;

import model.BankInfo;
import service.SettingService;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class QRHelper {

    public static void showQR(Component parent, String roomCode, double amount, String month) {
        // 1. Lấy thông tin ngân hàng từ hệ thống
        SettingService settingService = new SettingService();
        BankInfo bank = settingService.loadBankInfo();

        if (bank == null || bank.getAccountNo() == null || bank.getAccountNo().trim().isEmpty()) {
            JOptionPane.showMessageDialog(parent, "Chủ trọ chưa cài đặt thông tin ngân hàng nhận tiền!");
            return;
        }

        // 2. Tạo giao diện Dialog chờ tải
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), "Thanh toán qua mã QR", true);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(Color.WHITE);

        JLabel lblStatus = new JLabel("Đang tạo mã QR, vui lòng đợi...", SwingConstants.CENTER);
        lblStatus.setPreferredSize(new Dimension(400, 450));
        dialog.add(lblStatus, BorderLayout.CENTER);

        dialog.pack();
        dialog.setLocationRelativeTo(parent);

        // 3. Xử lý tải ảnh trong luồng phụ để không bị treo máy
        new Thread(() -> {
            try {
                // Mã hóa các chuỗi có dấu (Tháng, Tên chủ tài khoản) để tránh lỗi HTTP 400
                String description = "Thanh toan " + roomCode + " " + month;
                String encodedDesc = URLEncoder.encode(description, StandardCharsets.UTF_8.toString());
                String encodedName = URLEncoder.encode(bank.getAccountName(), StandardCharsets.UTF_8.toString());

                // Tạo URL link VietQR
                String qrUrl = String.format(
                        "https://img.vietqr.io/image/%s-%s-compact2.png?amount=%.0f&addInfo=%s&accountName=%s",
                        bank.getBankId(),
                        bank.getAccountNo().trim(),
                        amount,
                        encodedDesc,
                        encodedName
                );

                // Tải ảnh từ Internet
                URL url = new URL(qrUrl);
                BufferedImage image = ImageIO.read(url);

                if (image != null) {
                    // Cập nhật giao diện sau khi tải xong
                    SwingUtilities.invokeLater(() -> {
                        dialog.getContentPane().removeAll(); // Xóa chữ "Đang tải"

                        // Thu phóng ảnh cho đẹp
                        Image scaledImg = image.getScaledInstance(350, 350, Image.SCALE_SMOOTH);
                        JLabel lblImage = new JLabel(new ImageIcon(scaledImg));
                        lblImage.setHorizontalAlignment(SwingConstants.CENTER);

                        // Hiển thị thông tin văn bản bên dưới
                        String infoHtml = String.format(
                                "<html><center><div style='padding:10px;'>"
                                        + "<b style='font-size:14px; color:#e11d48;'>Số tiền: %,.0f VNĐ</b><br>"
                                        + "<span style='font-size:11px; color:#64748b;'>Người nhận: %s (%s)</span>"
                                        + "</div></center></html>",
                                amount, bank.getAccountName().toUpperCase(), bank.getBankId()
                        );
                        JLabel lblInfo = new JLabel(infoHtml, SwingConstants.CENTER);

                        dialog.add(lblImage, BorderLayout.CENTER);
                        dialog.add(lblInfo, BorderLayout.SOUTH);

                        dialog.revalidate();
                        dialog.repaint();
                        dialog.pack();
                        dialog.setLocationRelativeTo(parent);
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    lblStatus.setText("Lỗi: Không thể kết nối máy chủ VietQR!");
                });
            }
        }).start();

        dialog.setVisible(true);
    }
}
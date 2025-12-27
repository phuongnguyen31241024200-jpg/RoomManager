package ui.tenant;

import model.Invoice;
import model.InvoiceStatus;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class InvoiceDetailDialog extends JDialog {

    private final Color PINK_MAIN = new Color(237, 77, 126);

    public InvoiceDetailDialog(Frame parent, Invoice invoice) {
        super(parent, "Chi tiết hóa đơn", true);
        setSize(400, 550); // Tăng chiều cao để hiện đủ danh mục phí
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Color.WHITE);
        container.setBorder(new EmptyBorder(25, 25, 25, 25));

        // --- TIÊU ĐỀ ---
        JLabel lblHeader = new JLabel("CHI TIẾT HÓA ĐƠN", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblHeader.setForeground(PINK_MAIN);
        container.add(lblHeader, BorderLayout.NORTH);

        // --- NỘI DUNG ---
        boolean isPaid = (invoice.getStatus() == InvoiceStatus.PAID);
        String statusText = isPaid ? "ĐÃ THANH TOÁN" : "CHỜ THANH TOÁN";
        String statusColor = isPaid ? "#27AE60" : "#E74C3C";

        // HTML Đầy đủ các dòng phí
        String htmlContent = "<html><div style='font-family: Segoe UI; font-size: 13px; color: #444444;'>"
                + "<p style='margin-bottom:5px;'><b>Mã:</b> #" + invoice.getInvoiceId().substring(0, 8) + "</p>"
                + "<p style='margin-bottom:5px;'><b>Phòng:</b> " + invoice.getRoomCode() + "</p>"
                + "<p style='margin-bottom:5px;'><b>Người thuê:</b> " + invoice.getTenantName() + "</p>"
                + "<p style='margin-bottom:5px;'><b>Kỳ hạn:</b> " + invoice.getMonth() + "</p>"
                + "<hr style='border: 0.5px solid #eeeeee;'>"
                + "<table width='100%' style='margin-top: 10px;'>"
                + "<tr><td style='padding:5px 0;'>Tiền phòng:</td><td align='right'><b>" + formatMoney(invoice.getRoomPrice()) + "</b></td></tr>"
                + "<tr><td style='padding:5px 0;'>Tiền điện:</td><td align='right'><b>" + formatMoney(invoice.getElectricityFee()) + "</b></td></tr>"
                + "<tr><td style='padding:5px 0;'>Tiền nước:</td><td align='right'><b>" + formatMoney(invoice.getWaterFee()) + "</b></td></tr>"
                + "<tr><td style='padding:5px 0;'>Tiện ích/Dịch vụ:</td><td align='right'><b>" + formatMoney(invoice.getServiceFee()) + "</b></td></tr>"
                + "<tr><td colspan='2'><hr style='border: 0.5px dotted #cccccc;'></td></tr>"
                + "<tr style='font-size: 16px; color: #E74C3C;'>"
                + "<td style='padding:10px 0;'><b>TỔNG CỘNG:</b></td>"
                + "<td align='right'><b>" + formatMoney(invoice.getTotalAmount()) + "</b></td></tr>"
                + "</table>"
                + "<p style='margin-top: 25px; text-align: center; color: " + statusColor + ";'>"
                + "Trạng thái: <b style='font-size:14px;'>" + statusText + "</b></p>"
                + "</div></html>";

        JLabel lblBody = new JLabel(htmlContent);
        lblBody.setVerticalAlignment(SwingConstants.TOP);
        container.add(lblBody, BorderLayout.CENTER);

        // --- NÚT ĐÓNG ---
        JButton btnClose = new JButton("Đóng");
        btnClose.setFocusPainted(false);
        btnClose.setBackground(PINK_MAIN);
        btnClose.setForeground(Color.WHITE);
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnClose.setPreferredSize(new Dimension(0, 45));
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> dispose());

        container.add(btnClose, BorderLayout.SOUTH);
        add(container);
    }

    private String formatMoney(double amount) {
        return String.format("%,.0f đ", amount);
    }
}
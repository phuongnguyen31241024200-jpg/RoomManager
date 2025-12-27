package ui;

import model.Room;
import model.Invoice;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;
import java.util.Locale;

public class QuickInvoiceDialog extends JDialog {
    private JTextField txtE_Old, txtE_New, txtW_Old, txtW_New;
    private JLabel lblRoomPrice, lblElectricity, lblWater, lblTotal;
    private JPanel pnlUtilities;
    private Room room;
    private InvoicePanel invoicePanel;
    private Invoice invToEdit;
    private final NumberFormat formatter = NumberFormat.getInstance(Locale.GERMANY);

    public QuickInvoiceDialog(Room room, InvoicePanel invPanel, Invoice invToEdit) {
        this.room = room;
        this.invoicePanel = invPanel;
        this.invToEdit = invToEdit;

        setTitle(invToEdit == null ? "Lập hóa đơn chi tiết - " + room.getRoomCode() : "Chỉnh sửa hóa đơn - " + room.getRoomCode());
        setSize(480, 750);
        setLocationRelativeTo(null);
        setModal(true);

        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBorder(new EmptyBorder(25, 25, 25, 25));
        main.setBackground(Color.WHITE);

        setupUI(main);

        main.add(Box.createVerticalStrut(20));
        main.add(new JSeparator());
        main.add(Box.createVerticalStrut(15));

        lblRoomPrice = createDetailRow(main, " Tiền thuê phòng:", room.getPrice());
        lblElectricity = createDetailRow(main, " Tiền điện:", 0);
        lblWater = createDetailRow(main, " Tiền nước:", 0);

        main.add(createTitleLabel(" Các tiện ích:"));
        pnlUtilities = new JPanel();
        pnlUtilities.setLayout(new BoxLayout(pnlUtilities, BoxLayout.Y_AXIS));
        pnlUtilities.setOpaque(false);
        pnlUtilities.setAlignmentX(Component.LEFT_ALIGNMENT);
        main.add(pnlUtilities);

        main.add(Box.createVerticalStrut(15));
        main.add(new JSeparator());
        main.add(Box.createVerticalStrut(10));

        lblTotal = new JLabel("TỔNG CỘNG: 0 đ");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTotal.setForeground(new Color(237, 77, 126));
        lblTotal.setAlignmentX(Component.CENTER_ALIGNMENT);
        main.add(lblTotal);

        main.add(Box.createVerticalStrut(20));

        JButton btnConfirm = new JButton(invToEdit == null ? "XUẤT HÓA ĐƠN" : "CẬP NHẬT HÓA ĐƠN");
        btnConfirm.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btnConfirm.setBackground(invToEdit == null ? new Color(237, 77, 126) : new Color(52, 152, 219));
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnConfirm.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnConfirm.addActionListener(e -> processInvoice());

        main.add(btnConfirm);
        add(new JScrollPane(main));

        renderUtilities();

        if (invToEdit != null) {
            lblTotal.setText("TỔNG CŨ: " + formatter.format(invToEdit.getTotalAmount()) + " đ");
        }

        calculate();
    }

    private void renderUtilities() {
        pnlUtilities.removeAll();
        if (room.getUtilities() != null) {
            for (String util : room.getUtilities()) {
                double price = 0;
                if (util.equals("Gửi xe")) price = room.getParkingPrice();
                else if (util.equals("Giặt giũ")) price = room.getLaundryPrice();
                else if (util.equals("Dọn dẹp")) price = room.getCleaningPrice();
                createDetailRow(pnlUtilities, "   + " + util + ":", price);
            }
        }
        pnlUtilities.revalidate();
    }

    private JLabel createDetailRow(JPanel parent, String label, double value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        JLabel lblName = new JLabel(label);
        lblName.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JLabel lblValue = new JLabel(formatter.format(value) + " đ");
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 14));
        row.add(lblName, BorderLayout.WEST);
        row.add(lblValue, BorderLayout.EAST);
        parent.add(row);
        return lblValue;
    }

    private void calculate() {
        double eDiff = Math.max(0, parse(txtE_New.getText()) - parse(txtE_Old.getText()));
        double dienTotal = eDiff * room.getElectricityPrice();
        lblElectricity.setText(formatter.format(dienTotal) + " đ");

        double wDiff = Math.max(0, parse(txtW_New.getText()) - parse(txtW_Old.getText()));
        double nuocTotal = wDiff * room.getWaterPrice();
        lblWater.setText(formatter.format(nuocTotal) + " đ");

        double utilityTotal = 0;
        if (room.getUtilities() != null) {
            if (room.getUtilities().contains("Gửi xe")) utilityTotal += room.getParkingPrice();
            if (room.getUtilities().contains("Giặt giũ")) utilityTotal += room.getLaundryPrice();
            if (room.getUtilities().contains("Dọn dẹp")) utilityTotal += room.getCleaningPrice();
        }

        double finalTotal = room.getPrice() + dienTotal + nuocTotal + utilityTotal;
        lblTotal.setText("TỔNG CỘNG: " + formatter.format(finalTotal) + " đ");
    }

    private void setupUI(JPanel main) {
        main.add(createTitleLabel("⚡ CHỈ SỐ ĐIỆN (Cũ - Mới):"));
        JPanel pE = new JPanel(new GridLayout(1, 2, 10, 0));
        pE.setOpaque(false); pE.setMaximumSize(new Dimension(500, 40));
        txtE_Old = new JTextField("0"); txtE_New = new JTextField("0");
        pE.add(txtE_Old); pE.add(txtE_New);
        main.add(pE); main.add(Box.createVerticalStrut(15));

        main.add(createTitleLabel("💧 CHỈ SỐ NƯỚC (Cũ - Mới):"));
        JPanel pW = new JPanel(new GridLayout(1, 2, 10, 0));
        pW.setOpaque(false); pW.setMaximumSize(new Dimension(500, 40));
        txtW_Old = new JTextField("0"); txtW_New = new JTextField("0");
        pW.add(txtW_Old); pW.add(txtW_New);
        main.add(pW);

        KeyAdapter k = new KeyAdapter() { @Override public void keyReleased(KeyEvent e) { calculate(); } };
        txtE_Old.addKeyListener(k); txtE_New.addKeyListener(k);
        txtW_Old.addKeyListener(k); txtW_New.addKeyListener(k);
    }

    private JLabel createTitleLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        lbl.setBorder(new EmptyBorder(10, 0, 5, 0));
        return lbl;
    }

    private void processInvoice() {
        try {
            double eOld = parse(txtE_Old.getText());
            double eNew = parse(txtE_New.getText());
            double wOld = parse(txtW_Old.getText());
            double wNew = parse(txtW_New.getText());

            double dienTotal = Math.max(0, eNew - eOld) * room.getElectricityPrice();
            double nuocTotal = Math.max(0, wNew - wOld) * room.getWaterPrice();

            double utilityTotal = 0;
            if (room.getUtilities() != null) {
                if (room.getUtilities().contains("Gửi xe")) utilityTotal += room.getParkingPrice();
                if (room.getUtilities().contains("Giặt giũ")) utilityTotal += room.getLaundryPrice();
                if (room.getUtilities().contains("Dọn dẹp")) utilityTotal += room.getCleaningPrice();
            }

            double finalTotal = room.getPrice() + dienTotal + nuocTotal + utilityTotal;
            String currentMonth = "Tháng " + java.time.LocalDate.now().getMonthValue();

            if (invToEdit == null) {
                String tenantName = (room.getTenant() != null) ? room.getTenant().getTenantName() : "Khách vãng lai";

                String tenantId = (room.getTenant() != null) ? room.getTenant().getTenantId() : "";

                Invoice newInv = new Invoice(
                        room.getRoomCode(),     // 1. roomCode
                        tenantName,             // 2. tenantName
                        currentMonth,           // 3. month
                        finalTotal,             // 4. totalAmount
                        room.getRoomId(),       // 5. roomId
                        tenantId,               // 6. tenantId
                        room.getPrice(),        // 7. roomPrice (Tiền thuê phòng)
                        dienTotal,              // 8. electricityFee (Tiền điện)
                        nuocTotal,              // 9. waterFee (Tiền nước)
                        utilityTotal            // 10. serviceFee (Tiền tiện ích khác)
                );

                invoicePanel.addInvoice(newInv);
                JOptionPane.showMessageDialog(this, "Đã xuất hóa đơn thành công!");
            } else {
                // --- TRƯỜNG HỢP SỬA ---
                invToEdit.setTotalAmount(finalTotal);
                invToEdit.setMonth(currentMonth + " (Đã sửa)");

                invoicePanel.getInvoiceService().updateInvoice(invToEdit);

                // Gửi thông báo khi chỉnh sửa
                String updateMsg = "Hóa đơn " + currentMonth + " của phòng " + invToEdit.getRoomCode() +
                        " đã được điều chỉnh lại. Số tiền mới: " + formatter.format(finalTotal) + " đ";
                service.NotificationService.addNotification(updateMsg, "CHỈNH SỬA");

                invoicePanel.refreshTable();
                JOptionPane.showMessageDialog(this, "Đã cập nhật hóa đơn thành công!");
            }
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }

    private double parse(String s) {
        try { return Double.parseDouble(s.trim().replace(",", "")); } catch (Exception e) { return 0; }
    }
}
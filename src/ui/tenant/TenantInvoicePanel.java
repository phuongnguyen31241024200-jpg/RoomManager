package ui.tenant;

import model.Invoice;
import model.InvoiceStatus;
import service.InvoiceService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class TenantInvoicePanel extends JPanel {

    private final InvoiceService invoiceService;
    private final String roomCode;
    private final JTable table;
    private final DefaultTableModel model;

    public TenantInvoicePanel(InvoiceService invoiceService, String roomCode) {
        this.invoiceService = invoiceService;
        this.roomCode = roomCode;

        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(250, 250, 251));
        setBorder(new EmptyBorder(25, 25, 25, 25));

        // Tiêu đề
        JLabel lblTitle = new JLabel("HÓA ĐƠN PHÒNG " + roomCode.toUpperCase());
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        add(lblTitle, BorderLayout.NORTH);

        // Cấu hình bảng
        String[] cols = {"Kỳ hạn", "Số tiền (Bấm để xem)", "Trạng thái", "Ngày tạo", "Thao tác"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(50);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setCursor(new Cursor(Cursor.HAND_CURSOR));

        loadData();

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                int col = table.getSelectedColumn();
                if (row == -1) return;

                // Lấy thông tin hóa đơn từ list gốc để chính xác nhất
                List<Invoice> filtered = getFilteredInvoices();
                Invoice selectedInvoice = filtered.get(row);

                // 1. CLICK VÀO CỘT SỐ TIỀN (Cột 1) -> HIỆN CHI TIẾT
                if (col == 1) {
                    Window parentWindow = SwingUtilities.getWindowAncestor(TenantInvoicePanel.this);
                    InvoiceDetailDialog detailDialog = new InvoiceDetailDialog((Frame) parentWindow, selectedInvoice);
                    detailDialog.setVisible(true);
                }

                // 2. CLICK VÀO CỘT THAO TÁC (Cột 4) -> QUÉT MÃ QR
                if (col == 4) {
                    if (selectedInvoice.getStatus() == InvoiceStatus.UNPAID) {
                        QRHelper.showQR(TenantInvoicePanel.this, roomCode,
                                selectedInvoice.getTotalAmount(), selectedInvoice.getMonth());
                    } else {
                        JOptionPane.showMessageDialog(null, "Hóa đơn này đã được thanh toán!");
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        add(scrollPane, BorderLayout.CENTER);
    }

    private List<Invoice> getFilteredInvoices() {
        return invoiceService.getAllInvoices().stream()
                .filter(i -> i.getRoomCode().equalsIgnoreCase(roomCode))
                .collect(Collectors.toList());
    }

    private void loadData() {
        model.setRowCount(0);
        List<Invoice> filtered = getFilteredInvoices();
        NumberFormat fmt = NumberFormat.getInstance(Locale.GERMANY);

        for (Invoice i : filtered) {
            boolean isUnpaid = (i.getStatus() == InvoiceStatus.UNPAID);

            // Format cột trạng thái cho đẹp
            String status = isUnpaid ? " Chưa đóng" : " Đã đóng";

            // Cột số tiền có màu xanh để kích thích người dùng bấm vào
            String amountHtml = "<html><u style='color:#ED4D7E;'>" + fmt.format(i.getTotalAmount()) + " đ</u></html>";

            // Cột thao tác
            String action = isUnpaid ? "<html><b style='color:#2563EB;'>Quét mã QR</b></html>" : "---";

            model.addRow(new Object[]{
                    "Tháng " + i.getMonth(),
                    amountHtml,
                    status,
                    i.getCreatedAt(),
                    action
            });
        }
    }
}
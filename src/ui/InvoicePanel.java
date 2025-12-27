package ui;

import model.Invoice;
import model.InvoiceStatus;
import model.Room;
import service.InvoiceServiceImpl;
import service.NotificationService;
import service.RoomServiceImpl;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.List;
import java.util.stream.Collectors;

public class InvoicePanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtSearchRoom;
    private final NumberFormat fmt = NumberFormat.getInstance(Locale.GERMANY);
    private final SimpleDateFormat dateFmt = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    private RoomServiceImpl roomService;
    private InvoiceServiceImpl invoiceService;

    public InvoicePanel(RoomServiceImpl roomService, InvoiceServiceImpl invoiceService) {
        this.roomService = roomService;
        this.invoiceService = invoiceService;

        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(250, 250, 251));
        setBorder(new EmptyBorder(25, 25, 25, 25));

        setupHeader();

        String[] columns = {"Phòng", "Khách hàng", "Kỳ hạn", "Tổng tiền", "Trạng thái", "Ngày lập"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        setupTableUI();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        add(scrollPane, BorderLayout.CENTER);

        setupControlButtons();
        refreshTable();
    }

    private void setupHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Quản lý hoá đơn");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        headerPanel.add(lblTitle, BorderLayout.WEST);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        actionPanel.setOpaque(false);

        txtSearchRoom = new JTextField(12);
        txtSearchRoom.setPreferredSize(new Dimension(150, 30));

        txtSearchRoom.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                filterTable(txtSearchRoom.getText().trim());
            }
        });

        JButton btnCreateInvoice = new JButton("Tạo hóa đơn");
        btnCreateInvoice.setBackground(new Color(237, 77, 126));
        btnCreateInvoice.setForeground(Color.WHITE);
        btnCreateInvoice.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCreateInvoice.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCreateInvoice.addActionListener(e -> showCreateInvoiceFlow());

        actionPanel.add(new JLabel("Tìm phòng:"));
        actionPanel.add(txtSearchRoom);
        actionPanel.add(btnCreateInvoice);
        headerPanel.add(actionPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);
    }

    private void setupControlButtons() {
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        controlPanel.setOpaque(false);

        JButton btnEdit = new JButton("Sửa chi tiết");
        JButton btnDelete = new JButton("Xóa bỏ");
        JButton btnPay = new JButton("Thanh toán");

        btnEdit.setBackground(new Color(52, 152, 219)); btnEdit.setForeground(Color.WHITE);
        btnDelete.setBackground(new Color(231, 76, 60)); btnDelete.setForeground(Color.WHITE);
        btnPay.setBackground(new Color(46, 204, 113)); btnPay.setForeground(Color.WHITE);

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Chọn hóa đơn cần xóa!");
                return;
            }


            int confirm = JOptionPane.showConfirmDialog(this, "Xóa hóa đơn này sẽ không thể hoàn tác. Tiếp tục?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                Invoice inv = invoiceService.getAllInvoices().get(row);
                invoiceService.deleteInvoice(inv.getInvoiceId());

                NotificationService.addNotification("Hóa đơn " + inv.getMonth() + " của phòng " + inv.getRoomCode() + " đã được thu hồi/xóa.", "HỆ THỐNG");

                refreshTable();
            }
        });

        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Chọn hóa đơn cần chỉnh sửa!");
                return;
            }
            Invoice invToEdit = invoiceService.getAllInvoices().get(row);
            Room r = roomService.getRooms().stream()
                    .filter(room -> room.getRoomCode().equalsIgnoreCase(invToEdit.getRoomCode()))
                    .findFirst().orElse(null);

            if (r != null) {
                new QuickInvoiceDialog(r, this, invToEdit).setVisible(true);
            }
        });

        btnPay.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Chọn hóa đơn để thanh toán!");
                return;
            }

            Invoice inv = invoiceService.getAllInvoices().get(row);

            if (inv.getStatus() == InvoiceStatus.PAID) {
                JOptionPane.showMessageDialog(this, "Hóa đơn này đã được thanh toán trước đó.");
                return;
            }

            invoiceService.payInvoice(inv, null);

            String notifyMsg = String.format("Xác nhận: Hóa đơn phòng %s đã thanh toán thành công số tiền %s đ cho kỳ %s.",
                    inv.getRoomCode(),
                    fmt.format(inv.getTotalAmount()),
                    inv.getMonth());
            NotificationService.addNotification(notifyMsg, "THANH TOÁN");

            refreshTable();
            JOptionPane.showMessageDialog(this, "Thanh toán thành công! Tiền đã được cập nhật vào mục Thu - Chi.");
        });

        controlPanel.add(btnEdit);
        controlPanel.add(btnDelete);
        controlPanel.add(btnPay);
        add(controlPanel, BorderLayout.SOUTH);
    }

    public void refreshTable() {
        updateTableData(invoiceService.getAllInvoices());
    }

    private void filterTable(String query) {
        if (query.isEmpty()) {
            refreshTable();
            return;
        }
        List<Invoice> filtered = invoiceService.getAllInvoices().stream()
                .filter(i -> i.getRoomCode().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
        updateTableData(filtered);
    }

    private void updateTableData(List<Invoice> list) {
        model.setRowCount(0);
        for (Invoice inv : list) {
            String statusTxt = (inv.getStatus() == InvoiceStatus.UNPAID) ? " Chưa thu" : " Đã thu";
            model.addRow(new Object[]{
                    inv.getRoomCode(),
                    inv.getTenantName(),
                    inv.getMonth(),
                    fmt.format(inv.getTotalAmount()) + " đ",
                    statusTxt,
                    inv.getCreatedAt() != null ? dateFmt.format(inv.getCreatedAt()) : ""
            });
        }
    }

    private void showCreateInvoiceFlow() {
        String roomName = txtSearchRoom.getText().trim();
        if (roomName.isEmpty()) roomName = JOptionPane.showInputDialog(this, "Nhập tên phòng cần lập hóa đơn:");

        if (roomName != null && !roomName.isEmpty()) {
            final String finalName = roomName;
            Room r = roomService.getRooms().stream()
                    .filter(room -> room.getRoomCode().equalsIgnoreCase(finalName))
                    .findFirst().orElse(null);

            if (r != null && r.getTenant() != null) {
                new QuickInvoiceDialog(r, this, null).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Phòng không tồn tại hoặc chưa có người thuê!");
            }
        }
    }

    public void addInvoice(Invoice inv) {
        if (inv != null) {
            invoiceService.addInvoice(inv);
            String message = String.format("Bạn có hóa đơn mới tháng %s cho phòng %s. Số tiền cần đóng: %s đ",
                    inv.getMonth(), inv.getRoomCode(), fmt.format(inv.getTotalAmount()));
            NotificationService.addNotification(message, "HÓA ĐƠN");
            refreshTable();
        }
    }

    private void setupTableUI() {
        table.setRowHeight(45);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(240, 240, 240));
        table.setSelectionBackground(new Color(237, 77, 126, 40));
        table.setSelectionForeground(Color.BLACK);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
    }

    public InvoiceServiceImpl getInvoiceService() {
        return this.invoiceService;
    }
    // ===== UI ONLY – STYLE BUTTON =====
    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 16, 8, 16)); // bo tròn giả
        btn.setContentAreaFilled(true);
    }

}
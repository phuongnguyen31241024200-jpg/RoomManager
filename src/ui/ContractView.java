package ui;

import service.ContractService;
import model.Contract;
import model.Room;
import model.ContractStatus;
import service.RoomServiceImpl;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ContractView extends JPanel {

    private DefaultTableModel model;
    private JTable table;
    private RoomServiceImpl roomService;
    private ContractService contractService;
    private RoomPanel roomPanel;

    private final Color PINK_THEME = new Color(236, 72, 127);
    private final Color BG_LIGHT = new Color(249, 250, 251);
    private final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final Color BG_PASTEL = new Color(255, 245, 247);

    public ContractView(RoomServiceImpl roomService, ContractService contractService, RoomPanel roomPanel) {
        this.roomService = roomService;
        this.contractService = contractService;
        this.roomPanel = roomPanel;

        setLayout(new BorderLayout(0, 20));
        setBackground(BG_PASTEL);
        setBorder(new EmptyBorder(30, 40, 30, 40));

        // ===== HEADER =====
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel(
                "<html><b style='font-size:22px; color:#111827;'>Quản lý hợp đồng</b><br>" +
                        "<font color='#6B7280' style='font-size:12px;'>Danh sách hợp đồng thuê phòng</font></html>"
        );

        JButton btnCreate = createStyledButton("Tạo hợp đồng", PINK_THEME);
        btnCreate.addActionListener(e -> showContractPopup());

        header.add(title, BorderLayout.WEST);
        header.add(btnCreate, BorderLayout.EAST);

        // ===== TABLE SETUP =====
        String[] cols = {
                "Phòng", "Người thuê", "Số điện thoại", "Ngày bắt đầu", "Ngày kết thúc",
                "Tiền cọc", "Tiền phòng", "Trạng thái", "Thao tác"
        };

        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(model);
        styleTable(table);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());

                if (row != -1 && col == 8) { // Cột Thao tác
                    String roomCode = table.getValueAt(row, 0).toString();
                    if (confirmDelete(roomCode)) {
                        // 1. Xóa trong file .dat thông qua Service
                        Contract c = contractService.getContractByRoom(roomCode);
                        if (c != null) {
                            contractService.deleteContract(c.getContractId());
                        }

                        // 2. Giải phóng trạng thái phòng (đổi sang màu Trống)
                        Room room = roomService.findRoomByCode(roomCode);
                        if (room != null) {
                            roomService.releaseRoom(room.getRoomId());
                            if (roomPanel != null) roomPanel.refreshData();
                        }

                        // 3. Cập nhật lại giao diện bảng ngay lập tức
                        refreshData();
                        JOptionPane.showMessageDialog(null, "Đã xóa hợp đồng và giải phóng phòng " + roomCode);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(true);
        scrollPane.getViewport().setBackground(Color.WHITE);

// Card bo tròn
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(10, 10, 10, 10),
                new RoundedBorder(16, new Color(230, 230, 230))
        ));


        add(header, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Load dữ liệu lần đầu khi mở tab
        refreshData();
    }


    public void addContract(String room, String tenant, String phone, String start, String end, String deposit, String price) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            // Xử lý chuỗi số (loại bỏ dấu phẩy hoặc ký tự lạ nếu có)
            double depositVal = Double.parseDouble(deposit.replaceAll("[^0-9]", ""));
            double priceVal = Double.parseDouble(price.replaceAll("[^0-9]", ""));

            Contract newContract = new Contract(
                    "CT" + System.currentTimeMillis(),
                    room, tenant, phone,
                    sdf.parse(start), sdf.parse(end),
                    depositVal, priceVal,
                    ContractStatus.ACTIVE
            );

            contractService.addContract(newContract);

            refreshData();

            table.revalidate();
            table.repaint();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu hợp đồng: Định dạng ngày/số không đúng!");
        }
    }


    public void refreshData() {
        if (contractService == null || model == null) return;

        // Xóa sạch dòng cũ trước khi nạp
        model.setRowCount(0);

        List<Contract> list = contractService.getAllContracts();

        for (Contract c : list) {
            try {
                String startStr = DATE_FORMAT.format(c.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                String endStr = DATE_FORMAT.format(c.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

                model.addRow(new Object[]{
                        c.getRoomCode(),
                        c.getTenantId(),
                        c.getTenantPhone(),
                        startStr,
                        endStr,
                        String.format("%,.0f", c.getDeposit()),
                        String.format("%,.0f", c.getPrice()),
                        calculateStatus(endStr),
                        "Xóa"
                });
            } catch (Exception e) {
            }
        }
    }

    private String calculateStatus(String endDateStr) {
        try {
            LocalDate endDate = LocalDate.parse(endDateStr.trim(), DATE_FORMAT);
            return LocalDate.now().isAfter(endDate) ? "HẾT HẠN" : "CÒN HIỆU LỰC";
        } catch (Exception e) {
            return "LỖI NGÀY";
        }
    }

    private void styleTable(JTable t) {
        t.setRowHeight(60);
        t.setShowGrid(false);
        t.setIntercellSpacing(new Dimension(0, 0));
        JTableHeader header = t.getTableHeader();
        header.setPreferredSize(new Dimension(0, 45));
        header.setBackground(new Color(250, 250, 251));
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBorder(new EmptyBorder(0, 10, 0, 10));

        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                l.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(240, 240, 240)));
                l.setHorizontalAlignment(col == 0 ? LEFT : CENTER);

                if (col == 7) { // Trạng thái
                    String val = String.valueOf(value);
                    if ("CÒN HIỆU LỰC".equals(val)) {
                        l.setText("<html><div style='background:#DCFCE7;color:#16A34A;padding:3px 10px;border-radius:12px;font-weight:bold;'>Còn hiệu lực</div></html>");
                    } else if ("HẾT HẠN".equals(val)) {
                        l.setText("<html><div style='background:#FEE2E2;color:#EF4444;padding:3px 10px;border-radius:12px;font-weight:bold;'>Hết hạn</div></html>");
                    }
                }

                if (col == 8) { // Nút xóa
                    l.setText("<html><b style='color:#EF4444; cursor:hand;'>Xóa</b></html>");
                }

                l.setBackground(isSelected ? BG_LIGHT : Color.WHITE);
                l.setForeground(new Color(55, 65, 81));
                return l;
            }
        });
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton b = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? bg.brighter() : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(160, 40));
        return b;
    }


    private boolean confirmDelete(String room) {
        return JOptionPane.showConfirmDialog(this, "Xóa hợp đồng phòng " + room + "?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    private void showContractPopup() {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null) {
            new ContractDialog(window, this, roomService, contractService, roomPanel).setVisible(true);
        }
    }
}
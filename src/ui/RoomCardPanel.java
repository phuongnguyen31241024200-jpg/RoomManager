package ui;

import model.Room;
import model.RoomStatus;
import service.RoomServiceImpl;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.text.NumberFormat;
import java.util.Locale;

public class RoomCardPanel extends JPanel {
    private final Room room;
    private final RoomServiceImpl service;
    private final RoomPanel parentPanel;
    private final NumberFormat formatter = NumberFormat.getInstance(Locale.GERMANY);

    // --- THAY ĐỔI: Chuyển bảng màu sang tông Pastel ---
    private final Color COLOR_AVAILABLE = new Color(168, 230, 207);   // Mint Pastel
    private final Color COLOR_OCCUPIED = new Color(255, 170, 165);    // Pink Pastel
    private final Color COLOR_MAINTENANCE = new Color(255, 238, 173); // Yellow Pastel
    private final Color TEXT_DARK = new Color(72, 84, 96);           // Blue-Grey đậm (mềm hơn đen)
    private final Color UTILITY_BLUE = new Color(145, 190, 225);     // Sky Blue Pastel

    public RoomCardPanel(Room room, RoomServiceImpl service, RoomPanel parentPanel) {
        this.room = room;
        this.service = service;
        this.parentPanel = parentPanel;

        setPreferredSize(new Dimension(280, 360));
        setLayout(new BorderLayout());
        setOpaque(false); // THAY ĐỔI: Tắt opaque để hiển thị góc bo tròn của panel chính
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // --- 1. Header ---
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel lblCode = new JLabel("Phòng " + room.getRoomCode());
        lblCode.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblCode.setForeground(TEXT_DARK);

        // THAY ĐỔI: Custom JLabel để bo góc nhãn trạng thái
        JLabel lblStatus = new JLabel(getStatusText(room.getStatus())) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15); // Bo góc nhãn
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        lblStatus.setOpaque(false); // Tắt mặc định để paintComponent tự vẽ nền tròn
        lblStatus.setBackground(getStatusColor(room.getStatus()));
        lblStatus.setForeground(new Color(60, 60, 60)); // Chữ xám đậm cho dễ đọc trên nền pastel
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
        lblStatus.setBorder(new EmptyBorder(3, 10, 3, 10));

        header.add(lblCode, BorderLayout.WEST);
        header.add(lblStatus, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // --- 2. Body (Giữ nguyên logic logic lấy thông tin) ---
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setOpaque(false);
        body.add(Box.createVerticalStrut(15));

        String tenantName = "Trống";
        String tenantPhone = "---";

        if (room.getTenant() != null) {
            tenantName = room.getTenant().getTenantName();
            if (tenantName == null || tenantName.isEmpty()) {
                tenantName = room.getTenant().getFullName();
            }

            tenantPhone = room.getTenant().getPhone();
            if (tenantPhone == null || tenantPhone.equals("---") || tenantPhone.isEmpty()) {
                tenantPhone = room.getTenant().getPhoneNumber();
            }
            if (tenantPhone == null || tenantPhone.isEmpty()) {
                tenantPhone = room.getTenant().getTenantId();
            }
        }

        String utilsText = (room.getUtilities() == null || room.getUtilities().isEmpty())
                ? "Không có" : String.join(", ", room.getUtilities());

        body.add(createInfoRow(" Khách:", tenantName));
        body.add(Box.createVerticalStrut(8));
        body.add(createInfoRow(" SĐT:", tenantPhone));
        body.add(Box.createVerticalStrut(8));
        body.add(createUtilityRow(" Tiện ích:", utilsText));
        body.add(Box.createVerticalStrut(8));
        body.add(createInfoRow("Giá", formatter.format(room.getPrice()) + " đ"));

        add(body, BorderLayout.CENTER);

        // --- 3. Footer ---
        JPanel footer = new JPanel(new GridLayout(1, 2, 10, 0));
        footer.setOpaque(false);

        // THAY ĐỔI: Sử dụng màu Pastel cho các nút
        JButton btnDelete = createStyledButton("Xóa phòng", new Color(255, 158, 158));
        JButton btnEdit = createStyledButton("Chỉnh sửa", new Color(174, 214, 241));

        // Logic (Giữ nguyên)
        btnEdit.addActionListener(e -> {
            Window parentWindow = SwingUtilities.getWindowAncestor(this);
            UpdateRoomDialog dialog = new UpdateRoomDialog(parentWindow, service, room, parentPanel);
            dialog.setVisible(true);
        });

        btnDelete.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc muốn xóa " + room.getRoomCode() + "?",
                    "Xác nhận xóa", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                if (room.getStatus() == RoomStatus.OCCUPIED) {
                    JOptionPane.showMessageDialog(this, "Không thể xóa phòng đang có người ở!");
                } else {
                    service.deleteRoom(room.getRoomId());
                    parentPanel.loadRooms(service.getRooms());
                }
            }
        });

        footer.add(btnDelete);
        footer.add(btnEdit);
        add(footer, BorderLayout.SOUTH);
    }

    private JPanel createInfoRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setForeground(Color.GRAY);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.BOLD, 14));
        val.setForeground(TEXT_DARK);
        row.add(lbl, BorderLayout.WEST);
        row.add(val, BorderLayout.EAST);
        return row;
    }

    private JPanel createUtilityRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setForeground(Color.GRAY);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.ITALIC | Font.BOLD, 12));
        val.setForeground(UTILITY_BLUE);
        row.add(lbl, BorderLayout.WEST);
        row.add(val, BorderLayout.EAST);
        return row;
    }

    // THAY ĐỔI: Cập nhật hàm tạo Button để bo góc 20px
    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30); // Bo góc nút
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(new Color(60, 60, 60));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setContentAreaFilled(false); // Quan trọng: Tắt nền mặc định để hiện nền bo tròn
        btn.setBorderPainted(false);
        return btn;
    }

    private String getStatusText(RoomStatus status) {
        if (status == RoomStatus.AVAILABLE) return "TRỐNG";
        if (status == RoomStatus.OCCUPIED) return "ĐÃ THUÊ";
        return "BẢO TRÌ";
    }

    private Color getStatusColor(RoomStatus status) {
        if (status == RoomStatus.AVAILABLE) return COLOR_AVAILABLE;
        if (status == RoomStatus.OCCUPIED) return COLOR_OCCUPIED;
        return COLOR_MAINTENANCE;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        // Bật khử răng cưa để các đường cong mượt mà
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        // THAY ĐỔI: Vẽ khung Card chính bo góc 20
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
        g2.dispose();
    }
}
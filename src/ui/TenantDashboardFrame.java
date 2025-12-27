package ui;

import ui.tenant.*;
import model.Account;
import model.Tenant;
import service.IssueService;
import service.InvoiceService;
import service.RoomService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TenantDashboardFrame extends JFrame {

    private final JPanel contentArea;
    private final Account account;
    private final IssueService issueService;
    private final InvoiceService invoiceService;
    private final RoomService roomService;
    private final String roomCode;

    // Định nghĩa bảng màu và font chữ hiện đại
    private final Color PINK_MAIN = new Color(237, 77, 126);
    private final Color SIDEBAR_BG = Color.WHITE;
    private final Color CONTENT_BG = new Color(250, 250, 251);
    private final Color TEXT_GRAY = new Color(100, 100, 100);
    private final Color BORDER_COLOR = new Color(230, 230, 230); // Màu viền xám nhạt

    private JButton lastSelectedButton = null;

    public TenantDashboardFrame(
            Account account,
            IssueService issueService,
            InvoiceService invoiceService,
            RoomService roomService,
            String roomCode
    ) {
        this.account = account;
        this.issueService = issueService;
        this.invoiceService = invoiceService;
        this.roomService = roomService;
        this.roomCode = roomCode;

        // Thiết lập Frame chính
        setTitle("RoomManager - Người thuê (Phòng: " + roomCode + ")");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(CONTENT_BG);
        add(root);

        // ===== SIDEBAR (Thanh bên) =====
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(260, 0));
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(240, 240, 240)));

        // Brand/Logo Area
        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 30));
        brandPanel.setOpaque(false);
        brandPanel.add(createLogoIcon(""));

        JLabel brandName = new JLabel("RoomManager");
        brandName.setFont(new Font("Segoe UI", Font.BOLD, 20));
        brandPanel.add(brandName);

        // Navigation Menu
        JPanel navMenu = new JPanel();
        navMenu.setLayout(new BoxLayout(navMenu, BoxLayout.Y_AXIS));
        navMenu.setOpaque(false);
        navMenu.setBorder(new EmptyBorder(10, 20, 10, 20));

        JButton btnHome = createMenuButton("Trang chủ", "");
        JButton btnInvoice = createMenuButton("Hóa đơn", "");
        JButton btnIssue = createMenuButton("Sự cố", "️");
        JButton btnNotify = createMenuButton("Thông báo", "");

        navMenu.add(btnHome);
        navMenu.add(Box.createVerticalStrut(10));
        navMenu.add(btnInvoice);
        navMenu.add(Box.createVerticalStrut(10));
        navMenu.add(btnIssue);
        navMenu.add(Box.createVerticalStrut(10));
        navMenu.add(btnNotify);

        // Nút Đăng xuất
        JButton btnLogout = new JButton("   Đăng xuất");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogout.setForeground(new Color(231, 76, 60));
        btnLogout.setBorderPainted(false);
        btnLogout.setContentAreaFilled(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.setPreferredSize(new Dimension(260, 50));

        sidebar.add(brandPanel, BorderLayout.NORTH);
        sidebar.add(navMenu, BorderLayout.CENTER);
        sidebar.add(btnLogout, BorderLayout.SOUTH);

        root.add(sidebar, BorderLayout.WEST);

        // ===== CONTENT AREA (Khu vực hiển thị chính) =====
        contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(CONTENT_BG);
        contentArea.setBorder(new EmptyBorder(25, 25, 25, 25)); // Padding để lộ viền card
        root.add(contentArea, BorderLayout.CENTER);

        // ===== XỬ LÝ SỰ KIỆN =====
        btnHome.addActionListener(e -> { setActiveButton(btnHome); showHomePanel(); });
        btnInvoice.addActionListener(e -> { setActiveButton(btnInvoice); showInvoicePanel(); });
        btnIssue.addActionListener(e -> { setActiveButton(btnIssue); showIssuePanel(); });
        btnNotify.addActionListener(e -> { setActiveButton(btnNotify); showNotificationPanel(); });

        btnLogout.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn đăng xuất?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                this.dispose();
                new LoginFrame().setVisible(true);
            }
        });

        // Mặc định hiển thị Trang chủ
        setActiveButton(btnHome);
        showHomePanel();
    }

    /**
     * Hàm dùng chung để hiển thị các Panel chức năng vào khung bo tròn (Card)
     */
    private void showPage(JPanel panel) {
        contentArea.removeAll();

        // Tạo Panel bao quanh (Card)
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);

        // Sử dụng RoundedBorder đã tạo để tạo viền bo tròn
        card.setBorder(new RoundedBorder(25, BORDER_COLOR));

        card.add(panel, BorderLayout.CENTER);
        contentArea.add(card, BorderLayout.CENTER);

        refresh();
    }

    private void showHomePanel() {
        TenantHomePanel home = new TenantHomePanel(account, issueService, invoiceService, roomService);
        showPage(home);
        home.refreshData();
    }

    private void showInvoicePanel() {
        showPage(new TenantInvoicePanel(invoiceService, roomCode));
    }

    private void showIssuePanel() {
        if (account instanceof Tenant) {
            showPage(new TenantIssuePanel(issueService, (Tenant) account));
        }
    }

    private void showNotificationPanel() {
        showPage(new TenantNotificationPanel());
    }

    private void refresh() {
        contentArea.revalidate();
        contentArea.repaint();
    }

    // ===== UI HELPERS (Hỗ trợ giao diện) =====

    private JButton createMenuButton(String text, String icon) {
        JButton btn = new JButton("  " + icon + "    " + text);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btn.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 15));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setForeground(TEXT_GRAY);
        btn.setFocusPainted(false);
        return btn;
    }

    private void setActiveButton(JButton btn) {
        if (lastSelectedButton != null) {
            lastSelectedButton.setForeground(TEXT_GRAY);
        }
        btn.setForeground(PINK_MAIN);
        lastSelectedButton = btn;
    }

    private JLabel createLogoIcon(String icon) {
        JLabel label = new JLabel(icon, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PINK_MAIN);
                // Vẽ hình tròn cho logo nền hồng
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        label.setPreferredSize(new Dimension(45, 45));
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        return label;
    }
}
package ui;

import service.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import ui.NotificationPanel;

public class MainFrame extends JFrame {

    private final JPanel contentArea;
    private final JLabel lblNotificationCount;

    // ===== SERVICES =====
    private final RoomService roomService;
    private final FinanceService financeService;
    private final IssueService issueService;
    private final InvoiceService invoiceService;
    private final ContractService contractService;

    // ===== VIEW PANELS =====
    private final HomePanel homePanel;
    private final ContractView contractView;
    private final OwnerIssuePanel ownerIssuePanel;
    private final InvoicePanel invoicePanel;
    private final RoomPanel roomPanel;
    private final NotificationPanel notificationPanel;

    // ===== UI COLORS =====
    private final Color PINK_MAIN = new Color(237, 77, 126);
    private final Color SIDEBAR_BG = Color.WHITE;
    private final Color CONTENT_BG = new Color(250, 250, 251);
    private final Color TEXT_GRAY = new Color(100, 100, 100);
    private final Color LOGOUT_RED = new Color(231, 76, 60);

    private JButton lastSelectedButton = null;

    public MainFrame() {
        setTitle("RoomManager - Hệ thống quản lý nhà trọ");
        setSize(1300, 850);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // ===== 1. INIT SERVICES =====
        this.roomService = new RoomServiceImpl();
        this.financeService = new FinanceServiceImpl();
        this.invoiceService = new InvoiceServiceImpl(financeService);
        this.issueService = new IssueReportServiceImpl();
        this.contractService = new ContractServiceImpl();

        // ===== 2. INIT PANELS =====
        this.roomPanel = new RoomPanel((RoomServiceImpl) roomService);
        this.invoicePanel = new InvoicePanel((RoomServiceImpl) roomService, (InvoiceServiceImpl) invoiceService);
        this.contractView = new ContractView((RoomServiceImpl) roomService, contractService, roomPanel);
        this.homePanel = new HomePanel(roomService, issueService, financeService);
        this.ownerIssuePanel = new OwnerIssuePanel(issueService);
        this.notificationPanel = new NotificationPanel();

        // ===== 3. ROOT LAYOUT =====
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(CONTENT_BG);
        add(root);

        // ===== 4. SIDEBAR =====
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(280, 0));
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(240, 240, 240)));

        // Logo
        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 30));
        brandPanel.setOpaque(false);
        // Giữ lại logo nhỏ cho đẹp hoặc bạn có thể bỏ luôn dòng này
        brandPanel.add(createLogoIcon("🏠"));

        JLabel brandName = new JLabel("RoomManager");
        brandName.setFont(new Font("Segoe UI", Font.BOLD, 22));
        brandPanel.add(brandName);
        sidebar.add(brandPanel, BorderLayout.NORTH);

        // Navigation Menu
        JPanel navMenu = new JPanel();
        navMenu.setLayout(new BoxLayout(navMenu, BoxLayout.Y_AXIS));
        navMenu.setOpaque(false);
        navMenu.setBorder(new EmptyBorder(10, 20, 10, 20));

        // KHỞI TẠO CÁC NÚT (Đã bỏ icon)
        JButton btnHome = createMenuButton("Trang chủ");
        JButton btnRooms = createMenuButton("Quản lý phòng");
        JButton btnInvoices = createMenuButton("Hóa đơn");
        JButton btnContracts = createMenuButton("Hợp đồng");
        JButton btnFinance = createMenuButton("Thu - Chi");
        JButton btnIssues = createMenuButton("Sự cố");
        JButton btnNotifications = createMenuButton("Thông báo");
        JButton btnSettingQR = createMenuButton("Cài đặt QR");
        JButton btnLogout = createMenuButton("Đăng xuất");
        btnLogout.setForeground(LOGOUT_RED);

        // Thêm vào Menu
        navMenu.add(btnHome);
        navMenu.add(btnRooms);
        navMenu.add(btnInvoices);
        navMenu.add(btnContracts);
        navMenu.add(btnFinance);
        navMenu.add(btnIssues);
        navMenu.add(btnNotifications);
        navMenu.add(btnSettingQR);

        sidebar.add(navMenu, BorderLayout.CENTER);

        // Logout Panel
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(10, 20, 30, 20));
        bottomPanel.add(btnLogout, BorderLayout.SOUTH);
        sidebar.add(bottomPanel, BorderLayout.SOUTH);

        root.add(sidebar, BorderLayout.WEST);

        // ===== 5. MAIN CONTENT AREA =====
        JPanel mainContentWrapper = new JPanel(new BorderLayout());
        mainContentWrapper.setOpaque(false);

        // Header Bar
        JPanel headerBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 25, 15));
        headerBar.setOpaque(false);

        JPanel notifyBadge = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        notifyBadge.setOpaque(false);
        notifyBadge.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel bellIcon = new JLabel("🔔");
        bellIcon.setFont(new Font("Segoe UI", Font.PLAIN, 20));

        lblNotificationCount = new JLabel("0");
        lblNotificationCount.setForeground(Color.WHITE);
        lblNotificationCount.setOpaque(true);
        lblNotificationCount.setBackground(Color.RED);
        lblNotificationCount.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblNotificationCount.setBorder(BorderFactory.createEmptyBorder(1, 5, 1, 5));

        notifyBadge.add(bellIcon);
        notifyBadge.add(lblNotificationCount);
        notifyBadge.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                showNotificationDialog();
            }
        });

        headerBar.add(notifyBadge);
        mainContentWrapper.add(headerBar, BorderLayout.NORTH);

        contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(CONTENT_BG);
        mainContentWrapper.add(contentArea, BorderLayout.CENTER);

        root.add(mainContentWrapper, BorderLayout.CENTER);

        // ===== 6. LISTENERS =====
        btnHome.addActionListener(e -> { setActiveButton(btnHome); showHomePanel(); });
        btnRooms.addActionListener(e -> { setActiveButton(btnRooms); showRoomPanel(); });
        btnInvoices.addActionListener(e -> { setActiveButton(btnInvoices); showInvoicePanel(); });
        btnContracts.addActionListener(e -> { setActiveButton(btnContracts); showContractView(); });
        btnFinance.addActionListener(e -> { setActiveButton(btnFinance); showFinancePanel(); });
        btnIssues.addActionListener(e -> { setActiveButton(btnIssues); showIssuePanel(); });
        btnNotifications.addActionListener(e -> { setActiveButton(btnNotifications); showNotificationPanel(); });

        btnSettingQR.addActionListener(e -> {
            setActiveButton(btnSettingQR);
            SettingService service = new SettingService();
            new BankSettingDialog(this, service).setVisible(true);
        });

        btnLogout.addActionListener(e -> handleLogout());

        new Timer(3000, e -> updateNotificationUI()).start();

        setActiveButton(btnHome);
        showHomePanel();
        setVisible(true);
    }

    private void updateNotificationUI() {
        int count = NotificationService.getUnreadCount();
        lblNotificationCount.setText(String.valueOf(count));
        lblNotificationCount.setVisible(count > 0);
    }

    private void showNotificationDialog() {
        StringBuilder sb = new StringBuilder();
        if (NotificationService.getNotifications().isEmpty()) {
            sb.append("Không có thông báo nào.");
        } else {
            NotificationService.getNotifications().forEach(n -> {
                sb.append("• ").append(n.getContent()).append("\n\n");
            });
            NotificationService.markAllAsRead();
        }
        JOptionPane.showMessageDialog(this, new JScrollPane(new JTextArea(sb.toString(), 15, 30)), "Thông báo hệ thống", JOptionPane.INFORMATION_MESSAGE);
        updateNotificationUI();
    }

    private void handleLogout() {
        if (JOptionPane.showConfirmDialog(this, "Bạn chắc chắn muốn đăng xuất?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            this.dispose();
            // Đảm bảo có lệnh setVisible(true)
            new LoginFrame().setVisible(true);
        }
    }

    private void showHomePanel() {
        contentArea.removeAll();
        homePanel.refreshData();
        contentArea.add(homePanel);
        refresh();
    }

    private void showRoomPanel() {
        contentArea.removeAll();
        roomPanel.loadRooms(roomService.getRooms());
        contentArea.add(roomPanel);
        refresh();
    }

    private void showInvoicePanel() {
        contentArea.removeAll();
        invoicePanel.refreshTable();
        contentArea.add(invoicePanel);
        refresh();
    }

    private void showFinancePanel() {
        contentArea.removeAll();
        contentArea.add(new FinancePanel(financeService));
        refresh();
    }

    private void showIssuePanel() {
        contentArea.removeAll();
        ownerIssuePanel.reloadData();
        contentArea.add(ownerIssuePanel);
        refresh();
    }

    private void showContractView() {
        contentArea.removeAll();
        contractView.refreshData();
        contentArea.add(contractView);
        refresh();
    }

    private void showNotificationPanel() {
        contentArea.removeAll();
        notificationPanel.refreshData();
        contentArea.add(notificationPanel);
        NotificationService.markAllAsRead();
        updateNotificationUI();
        refresh();
    }

    private void refresh() {
        contentArea.revalidate();
        contentArea.repaint();
    }

    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        btn.setPreferredSize(new Dimension(240, 45));
        btn.setMaximumSize(new Dimension(240, 45));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setForeground(TEXT_GRAY);
        btn.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        return btn;
    }

    private void setActiveButton(JButton btn) {
        if (lastSelectedButton != null && lastSelectedButton.getForeground() != LOGOUT_RED) {
            lastSelectedButton.setForeground(TEXT_GRAY);
        }
        if (btn.getForeground() != LOGOUT_RED) {
            btn.setForeground(PINK_MAIN);
        }
        lastSelectedButton = btn;
    }

    private JLabel createLogoIcon(String icon) {
        JLabel label = new JLabel(icon, SwingConstants.CENTER);
        label.setPreferredSize(new Dimension(45, 45));
        label.setOpaque(true);
        label.setBackground(PINK_MAIN);
        label.setForeground(Color.WHITE);
        return label;
    }
}
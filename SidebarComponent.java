import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SidebarComponent extends JPanel {

    private Main main;
    private final List<JButton> menuButtons = new ArrayList<>();
    private final Map<String, String> menuRouteMap = new LinkedHashMap<>();

    private final Color PINK_THEME = new Color(236, 72, 127);
    private final Color TEXT_GRAY = new Color(107, 114, 128);
    private final Color SIDEBAR_BG = Color.WHITE;

    public SidebarComponent(Main main, String activePage) {
        this.main = main;
        setLayout(new BorderLayout());
        setBackground(SIDEBAR_BG);
        setPreferredSize(new Dimension(240, 0));
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(243, 244, 246)));

        /* ================= TOP ================= */
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);
        topPanel.setBorder(new EmptyBorder(25, 20, 30, 20));

        JLabel logo = new JLabel("<html><b style='font-size:16px;color:#1f2937'>RoomManager</b><br>" +
                "<span style='font-size:9px;color:gray'>Chủ trọ</span></html>");
        topPanel.add(logo);
        add(topPanel, BorderLayout.NORTH);

        /* ================= ROUTE MAP ================= */
        menuRouteMap.put("Trang chủ", "TrangChu");
        menuRouteMap.put("Quản lý phòng", "Phong");
        menuRouteMap.put("Hợp đồng", "HopDong");
        menuRouteMap.put("Hóa đơn", "HoaDon");
        menuRouteMap.put("Thu - Chi", "ThuChi");
        menuRouteMap.put("Sự cố", "SuCo");
        menuRouteMap.put("Thông báo", "ThongBao");

        /* ================= CENTER MENU ================= */
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setOpaque(false);
        menuPanel.setBorder(new EmptyBorder(0, 10, 0, 10));

        String[][] menuItems = {
                {"Trang chủ", "🏠"},
                {"Quản lý phòng", "🏢"},
                {"Hợp đồng", "📝"},
                {"Hóa đơn", "📄"},
                {"Thu - Chi", "💰"},
                {"Sự cố", "⚠️"},
                {"Thông báo", "🔔"}
        };

        for (String[] item : menuItems) {
            JButton btn = createMenuButton(item[0], item[1], item[0].equals(activePage));
            menuButtons.add(btn);
            menuPanel.add(btn);
            menuPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        }

        add(menuPanel, BorderLayout.CENTER);

        /* ================= BOTTOM ================= */
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(20, 15, 25, 15));

        bottomPanel.add(new JSeparator());
        bottomPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        JLabel user = new JLabel("<html><b>Nguyễn Văn A</b><br><span style='font-size:10px;color:gray'>0987654321</span></html>");
        bottomPanel.add(user);

        JButton logout = new JButton("Đăng xuất");
        logout.setForeground(new Color(220, 50, 50));
        logout.setBorderPainted(false);
        logout.setContentAreaFilled(false);
        logout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logout.addActionListener(e -> System.exit(0));

        bottomPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        bottomPanel.add(logout);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    /* ================= MENU BUTTON ================= */
    private JButton createMenuButton(String text, String icon, boolean isActive) {

        JButton btn = new JButton(icon + "   " + text) {
            @Override
            protected void paintComponent(Graphics g) {
                if (getBackground().equals(PINK_THEME)) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(PINK_THEME);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };

        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(220, 42));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMargin(new Insets(0, 15, 0, 0));

        setActiveStyle(btn, isActive);

        btn.addActionListener(e -> {
            String route = menuRouteMap.get(text);
            if (route != null) {
                main.showView(route);
            }
            updateActiveButton(btn);
        });

        return btn;
    }

    /* ================= UI HELPERS ================= */
    private void updateActiveButton(JButton activeBtn) {
        for (JButton b : menuButtons) {
            setActiveStyle(b, false);
        }
        setActiveStyle(activeBtn, true);
        repaint();
    }

    private void setActiveStyle(JButton btn, boolean active) {
        btn.setFont(new Font("SansSerif", active ? Font.BOLD : Font.PLAIN, 14));
        btn.setBackground(active ? PINK_THEME : SIDEBAR_BG);
        btn.setForeground(active ? Color.WHITE : TEXT_GRAY);
    }
}

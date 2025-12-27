package ui;

import service.RoomService;
import service.IssueService;
import service.FinanceService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.text.NumberFormat;
import java.util.Locale;

public class HomePanel extends JPanel {

    private final RoomService roomService;
    private final IssueService issueService;
    private final FinanceService financeService;

    private JLabel lblTotalRooms, lblRented, lblEmpty, lblIssues;
    private JLabel lblIncome, lblExpense;

    // ===== COLORS (Dựa trên mã màu chuẩn của ảnh) =====
    private final Color COLOR_BG = new Color(252, 252, 252);
    private final Color COLOR_TEXT_MAIN = new Color(33, 37, 41);
    private final Color COLOR_TEXT_SUB = new Color(158, 158, 158);
    private final Color COLOR_GREEN = new Color(40, 167, 69);
    private final Color COLOR_RED = new Color(220, 53, 69);
    private final Color COLOR_BORDER = new Color(240, 240, 240); // Viền rất nhạt

    public HomePanel(RoomService roomService, IssueService issueService, FinanceService financeService) {
        this.roomService = roomService;
        this.issueService = issueService;
        this.financeService = financeService;

        setBackground(COLOR_BG);
        setLayout(new BorderLayout(0, 30));
        setBorder(new EmptyBorder(30, 40, 30, 40));

        // ===== HEADER =====
        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 0, 8));
        headerPanel.setOpaque(false);

        JLabel title = new JLabel("Xin chào, Chủ trọ!");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(COLOR_TEXT_MAIN);

        JLabel subtitle = new JLabel("Tổng quan hoạt động hôm nay");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(COLOR_TEXT_SUB);

        headerPanel.add(title);
        headerPanel.add(subtitle);
        add(headerPanel, BorderLayout.NORTH);

        // ===== MAIN CONTENT =====
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setOpaque(false);

        // ===== STATS ROW (4 cards nhỏ) =====
        JPanel statsGrid = new JPanel(new GridLayout(1, 4, 15, 0));
        statsGrid.setOpaque(false);
        statsGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        lblTotalRooms = new JLabel("0");
        lblRented = new JLabel("0");
        lblEmpty = new JLabel("0");
        lblIssues = new JLabel("0");

        statsGrid.add(createStatCard("Tổng phòng", lblTotalRooms, "🏢", new Color(255, 245, 248)));
        statsGrid.add(createStatCard("Đang thuê", lblRented, "👥", new Color(240, 253, 244)));
        statsGrid.add(createStatCard("Còn trống", lblEmpty, "🔓", new Color(239, 246, 255)));
        statsGrid.add(createStatCard("Sự cố chờ", lblIssues, "⚠️", new Color(255, 251, 235)));

        // ===== FINANCE ROW (2 cards lớn) =====
        JPanel financeGrid = new JPanel(new GridLayout(1, 2, 20, 0));
        financeGrid.setOpaque(false);
        financeGrid.setBorder(new EmptyBorder(25, 0, 0, 0));
        financeGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

        lblIncome = new JLabel("0 đ");
        lblExpense = new JLabel("0 đ");

        financeGrid.add(createFinanceCard("Tổng thu", lblIncome, COLOR_GREEN, "📈", new Color(232, 245, 233)));
        financeGrid.add(createFinanceCard("Tổng chi", lblExpense, COLOR_RED, "📉", new Color(255, 235, 238)));

        mainContent.add(statsGrid);
        mainContent.add(financeGrid);

        add(mainContent, BorderLayout.CENTER);
        refreshData();
    }

    private JPanel createStatCard(String title, JLabel valueLabel, String icon, Color iconBg) {
        RoundedPanel card = new RoundedPanel(20, Color.WHITE);
        card.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 18));

        // Icon tròn
        JLabel lblIcon = new JLabel(icon, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new Ellipse2D.Double(0, 0, getWidth(), getHeight()));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        lblIcon.setBackground(iconBg);
        lblIcon.setPreferredSize(new Dimension(45, 45));

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        textPanel.setOpaque(false);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        valueLabel.setForeground(COLOR_TEXT_MAIN);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titleLabel.setForeground(COLOR_TEXT_SUB);

        textPanel.add(valueLabel);
        textPanel.add(titleLabel);

        card.add(lblIcon);
        card.add(textPanel);
        return card;
    }

    private JPanel createFinanceCard(String title, JLabel valueLabel, Color color, String icon, Color iconBg) {
        RoundedPanel card = new RoundedPanel(20, Color.WHITE);
        card.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 25));

        JLabel lblIcon = new JLabel(icon, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new Ellipse2D.Double(0, 0, getWidth(), getHeight()));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lblIcon.setBackground(iconBg);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        lblIcon.setPreferredSize(new Dimension(55, 55));

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        textPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(COLOR_TEXT_SUB);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(color);

        textPanel.add(titleLabel);
        textPanel.add(valueLabel);

        card.add(lblIcon);
        card.add(textPanel);
        return card;
    }

    public void refreshData() {
        if (roomService != null) {
            lblTotalRooms.setText(String.valueOf(roomService.getTotalRooms()));
            lblRented.setText(String.valueOf(roomService.getOccupiedRoomCount()));
            lblEmpty.setText(String.valueOf(roomService.getAvailableRoomCount()));
        }
        if (issueService != null) {
            lblIssues.setText(String.valueOf(issueService.getOpenIssueCount()));
        }
        if (financeService != null) {
            lblIncome.setText(formatMoney(financeService.getIncomeThisMonth()));
            lblExpense.setText(formatMoney(financeService.getExpenseThisMonth()));
        }
    }

    private String formatMoney(double value) {
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        return nf.format(value) + " đ";
    }

    private static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color bg;

        public RoundedPanel(int radius, Color bg) {
            this.radius = radius;
            this.bg = bg;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(0, 0, 0, 10)); // Màu shadow cực nhẹ
            g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, radius, radius);

            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);

            g2.setColor(new Color(230, 230, 230)); // Viền mảnh
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g2.dispose();
        }
    }
}
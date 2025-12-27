package ui.tenant;

import model.IssueReport;
import model.IssueStatus;
import model.Tenant;
import service.IssueService;
import service.NotificationService; // Đảm bảo import đúng service

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TenantIssuePanel extends JPanel {

    private final IssueService issueService;
    private final Tenant currentTenant;

    // ===== STYLE =====
    private final Color PRIMARY_PINK = new Color(255, 110, 157);
    private final Color BG_COLOR = new Color(252, 252, 252);
    private final Color BORDER_GRAY = new Color(231, 233, 237);
    private final Color STATUS_GRAY_BG = new Color(243, 244, 246);
    private final Color STATUS_GRAY_FG = new Color(107, 114, 128);
    private final String CUTE_FONT = "Segoe UI";
    private final Color BG_PASTEL = new Color(255, 245, 247);

    private JPanel mainListPanel;

    public TenantIssuePanel(IssueService issueService, Tenant tenant) {
        this.issueService = issueService;
        this.currentTenant = tenant;
        initComponents();
        loadExistingIssues();
    }

    private void initComponents() {
        setLayout(new BorderLayout(20, 15));
        setBackground(BG_PASTEL);
        setBorder(new EmptyBorder(30, 40, 30, 40));

        add(createHeader(), BorderLayout.NORTH);

        mainListPanel = new JPanel();
        mainListPanel.setLayout(new BoxLayout(mainListPanel, BoxLayout.Y_AXIS));
        mainListPanel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(mainListPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);
    }

    public void loadExistingIssues() {
        mainListPanel.removeAll();
        List<IssueReport> allIssues = issueService.getAllIssues();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm - dd/MM/yyyy");

        if (allIssues != null && currentTenant != null) {
            for (IssueReport issue : allIssues) {
                // Lọc sự cố theo username của khách thuê hiện tại
                if (issue.getTenant() != null &&
                        currentTenant.getUsername() != null &&
                        currentTenant.getUsername().equals(issue.getTenant().getUsername())) {

                    String timeStr = issue.getCreatedAt() != null
                            ? issue.getCreatedAt().format(formatter)
                            : "Vừa xong";

                    addNewIssueCard(issue, timeStr);
                }
            }
        }
        mainListPanel.revalidate();
        mainListPanel.repaint();
    }

    private void addNewIssueCard(IssueReport issue, String timeStr) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(0, 0, 10, 0));
        wrapper.setMaximumSize(new Dimension(1200, 105));

        wrapper.add(createIssueCard(issue, timeStr));
        mainListPanel.add(wrapper, 0);
    }

    private JPanel createIssueCard(IssueReport issue, String time) {
        JPanel card = new JPanel(new BorderLayout(10, 2)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BORDER_GRAY);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 14, 14);
                g2.dispose();
            }
        };

        card.setOpaque(false);
        card.setBorder(new EmptyBorder(12, 18, 12, 18));

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);

        JLabel lblType = new JLabel(issue.getIssueType().toUpperCase());
        lblType.setFont(new Font(CUTE_FONT, Font.BOLD, 11));
        lblType.setForeground(PRIMARY_PINK);

        JPanel badgeContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        badgeContainer.setOpaque(false);
        badgeContainer.add(createBadge(issue.getUrgencyLevel(), new Color(236, 253, 245), new Color(16, 185, 129)));

        // Hiển thị trạng thái dựa trên Enum
        String statusText = "Chờ xử lý";
        Color stBg = STATUS_GRAY_BG;
        Color stFg = STATUS_GRAY_FG;

        if (issue.getStatus() == IssueStatus.PROCESSING) {
            statusText = "Đang xử lý";
            stBg = new Color(254, 243, 199);
            stFg = new Color(180, 83, 9);
        } else if (issue.getStatus() == IssueStatus.DONE) {
            statusText = "Hoàn thành";
            stBg = new Color(220, 252, 231);
            stFg = new Color(21, 128, 61);
        }

        badgeContainer.add(createBadge(statusText, stBg, stFg));
        topRow.add(lblType, BorderLayout.WEST);
        topRow.add(badgeContainer, BorderLayout.EAST);

        JLabel lblDesc = new JLabel("<html><body style='width:650px'><b>" + issue.getDescription() + "</b></body></html>");
        lblDesc.setFont(new Font(CUTE_FONT, Font.PLAIN, 13));
        lblDesc.setForeground(new Color(31, 41, 55));

        JLabel lblTime = new JLabel(time);
        lblTime.setFont(new Font(CUTE_FONT, Font.PLAIN, 10));
        lblTime.setForeground(new Color(156, 163, 175));

        card.add(topRow, BorderLayout.NORTH);
        card.add(lblDesc, BorderLayout.CENTER);
        card.add(lblTime, BorderLayout.SOUTH);

        return card;
    }

    private JLabel createBadge(String text, Color bg, Color fg) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        lbl.setFont(new Font(CUTE_FONT, Font.BOLD, 10));
        lbl.setForeground(fg);
        lbl.setBorder(new EmptyBorder(2, 10, 2, 10));
        return lbl;
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 10, 0));

        JPanel titleGroup = new JPanel();
        titleGroup.setLayout(new BoxLayout(titleGroup, BoxLayout.Y_AXIS));
        titleGroup.setOpaque(false);

        JLabel lblTitle = new JLabel("Sự cố của tôi");
        lblTitle.setFont(new Font(CUTE_FONT, Font.BOLD, 28));
        lblTitle.setForeground(new Color(17, 24, 39));

        JLabel lblSub = new JLabel("Danh sách báo cáo kỹ thuật tại phòng");
        lblSub.setFont(new Font(CUTE_FONT, Font.PLAIN, 14));
        lblSub.setForeground(STATUS_GRAY_FG);

        titleGroup.add(lblTitle);
        titleGroup.add(Box.createVerticalStrut(4));
        titleGroup.add(lblSub);

        JButton btnAdd = createAddButton("Báo cáo sự cố");
        btnAdd.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(this);
            new ReportIssueDialog(window, data -> {
                // 1. Khởi tạo đối tượng sự cố
                IssueReport issue = new IssueReport(data.type, data.desc, data.level, currentTenant);

                issue.setStatus(IssueStatus.PENDING);

                // 2. Lưu vào service chung
                issueService.addIssue(issue);

                // 3. Tạo thông báo cho chủ trọ
                String sender = (currentTenant.getTenantName() != null && !currentTenant.getTenantName().isEmpty())
                        ? currentTenant.getTenantName()
                        : currentTenant.getUsername();

                String thongBao = String.format("SỰ CỐ MỚI từ [%s]: %s - %s",
                        sender, data.type, data.desc);

                NotificationService.addNotification(thongBao, "ISSUE");

                // 4. Làm mới danh sách tại chỗ
                loadExistingIssues();

                JOptionPane.showMessageDialog(this, "Gửi báo cáo sự cố thành công!");
            }).setVisible(true);
        });

        header.add(titleGroup, BorderLayout.WEST);
        header.add(btnAdd, BorderLayout.EAST);
        return header;
    }

    private JButton createAddButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PRIMARY_PINK);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font(CUTE_FONT, Font.BOLD, 13));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(170, 42));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
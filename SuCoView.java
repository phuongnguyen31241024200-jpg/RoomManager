import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SuCoView extends JPanel {
    private Main main;
    private final List<Issue> allIssues = new ArrayList<>();
    private JPanel issueListContainer;
    private JPanel filterBar;
    private JTextField searchField;
    private String currentStatusFilter = "Tất cả";
    private final Color PINK_THEME = new Color(236, 72, 127);

    public SuCoView(Main main) {
        this.main = main;
        initData();
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(30, 40, 30, 40));

        // --- HEADER ---
        JPanel header = new JPanel(new BorderLayout(0, 20));
        header.setOpaque(false);
        JLabel title = new JLabel("<html><b style='font-size:20px;'>Quản lý sự cố</b><br>" +
                "<font color='gray' style='font-size:12px;'>Theo dõi và xử lý các vấn đề kỹ thuật phát sinh từ người thuê</font></html>");

        // Tìm kiếm và Filter gom nhóm
        JPanel topAction = new JPanel(new BorderLayout(20, 0));
        topAction.setOpaque(false);

        searchField = new JTextField("🔍 Tìm kiếm sự cố...");
        searchField.setPreferredSize(new Dimension(0, 45));
        searchField.setForeground(Color.GRAY);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(25, new Color(230, 230, 230)), // Bo tròn cực đại
                new EmptyBorder(0, 15, 0, 15)));

        filterBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filterBar.setOpaque(false);

        topAction.add(searchField, BorderLayout.CENTER);
        topAction.add(filterBar, BorderLayout.EAST);

        header.add(title, BorderLayout.NORTH);
        header.add(topAction, BorderLayout.SOUTH);

        issueListContainer = new JPanel();
        issueListContainer.setLayout(new BoxLayout(issueListContainer, BoxLayout.Y_AXIS));
        issueListContainer.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(issueListContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(header, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        updateIssueDisplay();
    }

    private void updateIssueDisplay() {
        // Cập nhật Filter Buttons
        filterBar.removeAll();
        String[] filters = {"Tất cả", "Chờ xử lý", "Đang xử lý", "Hoàn thành"};
        for (String f : filters) filterBar.add(createFilterButton(f));

        // Cập nhật Danh sách
        issueListContainer.removeAll();
        String search = searchField.getText().toLowerCase().replace("🔍 tìm kiếm sự cố...", "");
        List<Issue> filtered = allIssues.stream()
                .filter(i -> currentStatusFilter.equals("Tất cả") || i.status.equals(currentStatusFilter))
                .filter(i -> i.room.toLowerCase().contains(search) || i.description.toLowerCase().contains(search))
                .collect(Collectors.toList());

        issueListContainer.removeAll();
        issueListContainer.add(Box.createVerticalStrut(10));

        for (Issue issue : filtered) {
            issueListContainer.add(createIssueCard(issue));
            issueListContainer.add(Box.createRigidArea(new Dimension(0, 15)));
        }

        filterBar.revalidate(); filterBar.repaint();
        issueListContainer.revalidate(); issueListContainer.repaint();
    }

    private JPanel createIssueCard(Issue issue) {
        // Thay đổi layout sang BorderLayout để chia 3 phần: Icon - Info - Status
        JPanel card = new JPanel(new BorderLayout(20, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30); // Bo tròn 30
                g2.setColor(new Color(245, 245, 245));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 30, 30);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBackground(Color.WHITE);
        card.setMaximumSize(new Dimension(2000, 100));
        card.setBorder(new EmptyBorder(15, 20, 15, 20));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 1. Bên trái: Icon tròn (Tự động theo loại)
        String icon = issue.description.contains("đèn") ? "⚡" : (issue.description.contains("nước") ? "💧" : "⚙️");
        JLabel lbIconCircle = new JLabel(icon, SwingConstants.CENTER);
        lbIconCircle.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        lbIconCircle.setPreferredSize(new Dimension(55, 55));
        lbIconCircle.setOpaque(true);
        lbIconCircle.setBackground(new Color(249, 250, 251));
        // Bo tròn icon label bằng code vẽ lại

        // 2. Ở giữa: Thông tin phòng, trạng thái, mô tả
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        centerPanel.setOpaque(false);

        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topRow.setOpaque(false);
        JLabel lbRoom = new JLabel(issue.room);
        lbRoom.setFont(new Font("SansSerif", Font.BOLD, 16));
        topRow.add(lbRoom);
        topRow.add(createStatusTag(issue.status));
        topRow.add(createLevelTag(issue.level));

        JLabel lbDesc = new JLabel(issue.description);
        lbDesc.setForeground(new Color(107, 114, 128));
        lbDesc.setFont(new Font("SansSerif", Font.PLAIN, 13));

        centerPanel.add(topRow);
        centerPanel.add(lbDesc);

        // 3. Bên phải: Thời gian và Tick
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);
        JLabel lbTime = new JLabel(issue.reportTime);
        lbTime.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lbTime.setForeground(new Color(156, 163, 175));

        JLabel lbCheck = new JLabel(issue.status.equals("Hoàn thành") ? "✔️" : "🕒");
        lbCheck.setHorizontalAlignment(SwingConstants.RIGHT);
        lbCheck.setForeground(issue.status.equals("Hoàn thành") ? new Color(34, 197, 94) : PINK_THEME);

        rightPanel.add(lbTime, BorderLayout.NORTH);
        rightPanel.add(lbCheck, BorderLayout.CENTER);

        card.add(lbIconCircle, BorderLayout.WEST);
        card.add(centerPanel, BorderLayout.CENTER);
        card.add(rightPanel, BorderLayout.EAST);

        // Hover Effect
        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { card.setBackground(new Color(252, 252, 253)); }
            @Override public void mouseExited(MouseEvent e) { card.setBackground(Color.WHITE); }
        });

        return card;
    }

    private JButton createFilterButton(String text) {
        boolean isSelected = text.equals(currentStatusFilter);
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isSelected ? PINK_THEME : Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                if(!isSelected) {
                    g2.setColor(new Color(229, 231, 235));
                    g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 25, 25);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setOpaque(false); btn.setContentAreaFilled(false); btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(110, 38));
        btn.setForeground(isSelected ? Color.WHITE : new Color(75, 85, 99));
        btn.setFont(new Font("SansSerif", isSelected ? Font.BOLD : Font.PLAIN, 13));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> { currentStatusFilter = text; updateIssueDisplay(); });
        return btn;
    }

    private JLabel createRoundedLabel(String text, Color bg, Color fg) {
        JLabel label = new JLabel(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        label.setOpaque(false); label.setBackground(bg); label.setForeground(fg);
        label.setFont(new Font("SansSerif", Font.BOLD, 10));
        label.setBorder(new EmptyBorder(3, 10, 3, 10));
        return label;
    }

    // --- GIỮ NGUYÊN DỮ LIỆU ---
    private void initData() {
        allIssues.add(new Issue("Phòng A01", "Bóng đèn bị hỏng, cần thay", "Chờ xử lý", "Trung bình"));
        allIssues.add(new Issue("Phòng B01", "Vòi nước bồn rửa bị rỉ", "Đang xử lý", "Khẩn cấp"));
        allIssues.add(new Issue("Phòng C01", "Khóa cửa bị kẹt", "Hoàn thành", "Thấp"));
    }

    private JLabel createStatusTag(String status) {
        Color bg = status.equals("Chờ xử lý") ? new Color(254, 243, 199) :
                status.equals("Đang xử lý") ? new Color(219, 234, 254) : new Color(209, 250, 229);
        Color fg = status.equals("Chờ xử lý") ? new Color(245, 158, 11) :
                status.equals("Đang xử lý") ? new Color(59, 130, 246) : new Color(16, 185, 129);
        return createRoundedLabel(status, bg, fg);
    }

    private JLabel createLevelTag(String level) {
        Color bg = level.equals("Khẩn cấp") ? new Color(254, 226, 226) : new Color(243, 244, 246);
        Color fg = level.equals("Khẩn cấp") ? new Color(239, 68, 68) : new Color(107, 114, 128);
        return createRoundedLabel(level, bg, fg);
    }

    private static class Issue {
        String room, description, status, level, reportTime;
        Issue(String r, String d, String s, String l) {
            room=r; description=d; status=s; level=l;
            reportTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy"));
        }
    }
}
// Copy đoạn này dán vào cuối class SuCoView
class RoundedBorder implements javax.swing.border.Border {
    private int r;
    private Color c;

    RoundedBorder(int r, Color c) {
        this.r = r;
        this.c = c;
    }

    public Insets getBorderInsets(Component c) {
        return new Insets(10, 20, 10, 20);
    }

    public boolean isBorderOpaque() {
        return false;
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(this.c);
        // Vẽ đường viền bo tròn theo bán kính r
        g2.drawRoundRect(x, y, width - 1, height - 1, r, r);
        g2.dispose();
    }
}

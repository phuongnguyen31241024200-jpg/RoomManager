package ui;

import model.IssueReport;
import model.IssueStatus;
import service.IssueService;
import ui.components.IssueCardPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SuCoView extends JPanel {

    private final IssueService issueService;
    private JPanel issueListContainer;
    private JTextField searchField;
    private final Color PRIMARY_PINK = new Color(240, 84, 131);
    private final String PLACEHOLDER = " Tìm kiếm sự cố...";

    private IssueStatus currentFilterStatus = null;
    private JPanel filterBar;

    public SuCoView(IssueService issueService) {
        this.issueService = issueService;

        setLayout(new BorderLayout());
        setBackground(new Color(252, 252, 252));
        setBorder(new EmptyBorder(30, 40, 30, 40));


        // Khởi tạo Filter Bar (Cần tạo TRƯỚC khi add vào headerWrapper)
        filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        filterBar.setOpaque(false);
        filterBar.setBorder(new EmptyBorder(25, 0, 25, 0));

        // Khởi tạo Search Field bo tròn viên thuốc
        searchField = new JTextField(PLACEHOLDER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, getHeight(), getHeight());
                super.paintComponent(g);
                g2.dispose();
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isFocusOwner() ? PRIMARY_PINK : new Color(220, 220, 220));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, getHeight(), getHeight());
                g2.dispose();
            }
        };
        searchField.setPreferredSize(new Dimension(350, 42));
        searchField.setOpaque(false);
        searchField.setBorder(new EmptyBorder(0, 20, 0, 20));
        searchField.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (searchField.getText().equals(PLACEHOLDER)) searchField.setText("");
                repaint();
            }
            @Override public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) searchField.setText(PLACEHOLDER);
                repaint();
            }
        });


        JPanel headerWrapper = new JPanel(new BorderLayout());
        headerWrapper.setOpaque(false);

        // Panel chứa chữ (Tiêu đề + Phụ)
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel title = new JLabel("Quản lý sự cố");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(33, 37, 41));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subTitle = new JLabel("Theo dõi và xử lý các vấn đề kỹ thuật phát sinh từ người thuê");
        subTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subTitle.setForeground(new Color(150, 150, 150));
        subTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        textPanel.add(title);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(subTitle);

        // Đưa chữ về bên trái, filter bar nằm dưới
        headerWrapper.add(textPanel, BorderLayout.WEST);
        headerWrapper.add(filterBar, BorderLayout.SOUTH);

        add(headerWrapper, BorderLayout.NORTH);

        issueListContainer = new JPanel();
        issueListContainer.setLayout(new BoxLayout(issueListContainer, BoxLayout.Y_AXIS));
        issueListContainer.setOpaque(false);

        JScrollPane scroll = new JScrollPane(issueListContainer);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        renderFilterButtons();
        reloadData();
    }

    private void renderFilterButtons() {
        filterBar.removeAll();
        filterBar.add(searchField);
        filterBar.add(createFilterButton("Tất cả", currentFilterStatus == null, null));
        filterBar.add(createFilterButton("Chờ xử lý", currentFilterStatus == IssueStatus.PENDING, IssueStatus.PENDING));
        filterBar.add(createFilterButton("Đang xử lý", currentFilterStatus == IssueStatus.PROCESSING, IssueStatus.PROCESSING));
        filterBar.add(createFilterButton("Hoàn thành", currentFilterStatus == IssueStatus.DONE, IssueStatus.DONE));
        filterBar.revalidate();
        filterBar.repaint();
    }

    private JButton createFilterButton(String text, boolean isActive, IssueStatus status) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(120, 40));
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setForeground(isActive ? Color.WHITE : new Color(110, 110, 110));

        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isActive ? PRIMARY_PINK : Color.WHITE);
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), c.getHeight(), c.getHeight());
                if (!isActive) {
                    g2.setColor(new Color(220, 220, 220));
                    g2.drawRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, c.getHeight(), c.getHeight());
                }
                super.paint(g2, c);
                g2.dispose();
            }
        });

        btn.addActionListener(e -> {
            currentFilterStatus = status;
            renderFilterButtons();
            reloadData();
        });
        return btn;
    }

    public void reloadData() {
        issueListContainer.removeAll();
        List<IssueReport> allIssues = issueService.getAllIssues();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm - dd/MM/yyyy");

        if (allIssues != null) {
            for (IssueReport issue : allIssues) {
                if (currentFilterStatus != null && issue.getStatus() != currentFilterStatus) continue;

                IssueCardPanel card = new IssueCardPanel(
                        issue.getIssueType(),
                        issue.getDescription(),
                        issue.getUrgencyLevel(),
                        (issue.getCreatedAt() != null) ? issue.getCreatedAt().format(formatter) : "Vừa xong",
                        issue.getStatus()
                );
                card.addClickListener(() -> {
                    Window parent = SwingUtilities.getWindowAncestor(this);
                    if (parent instanceof JFrame) {
                        new IssueDetailDialog((JFrame) parent, issue, this::reloadData).setVisible(true);
                    }
                });
                issueListContainer.add(card);
                issueListContainer.add(Box.createVerticalStrut(15));
            }
        }
        revalidate();
        repaint();
    }
}
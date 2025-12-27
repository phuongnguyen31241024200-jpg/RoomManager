package ui.tenant;

import model.Notification;
import service.NotificationService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class TenantNotificationPanel extends JPanel {

    private JPanel listContainer;
    private final Color BG_COLOR = new Color(250, 250, 251);
    private final Color BG_PASTEL = new Color(255, 245, 247);

    public TenantNotificationPanel() {
        setLayout(new BorderLayout());
        setBackground(BG_COLOR);
        setBorder(new EmptyBorder(25, 30, 25, 30));

        // --- HEADER ---
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel lblTitle = new JLabel("Thông báo của bạn");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        header.add(lblTitle, BorderLayout.WEST);


        add(header, BorderLayout.NORTH);

        // --- LIST CONTAINER ---
        listContainer = new JPanel();
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        listContainer.setBackground(BG_COLOR);

        JScrollPane scrollPane = new JScrollPane(listContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(15);
        scrollPane.getViewport().setBackground(BG_COLOR);

        add(scrollPane, BorderLayout.CENTER);

        // Load dữ liệu lần đầu
        refreshNotifications();
    }

    public void refreshNotifications() {
        listContainer.removeAll();
        List<Notification> notifications = NotificationService.getNotifications();

        if (notifications.isEmpty()) {
            showEmptyState();
        } else {
            for (Notification n : notifications) {
                listContainer.add(createNotificationItem(n));
                listContainer.add(Box.createVerticalStrut(15));
            }
        }

        listContainer.revalidate();
        listContainer.repaint();
    }

    private JPanel createNotificationItem(Notification n) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 232), 1, true),
                new EmptyBorder(15, 20, 15, 20)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        // Icon & Màu sắc dựa trên loại thông báo
        JLabel iconLabel = new JLabel();
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        Color typeColor;

        switch (n.getType()) {
            case "HÓA ĐƠN":
                typeColor = new Color(52, 152, 219); // Blue
                break;
            case "THANH TOÁN":
                typeColor = new Color(46, 204, 113); // Green
                break;
            case "CHỈNH SỬA":
                typeColor = new Color(241, 194, 50); // Yellow
                break;
            default:
                typeColor = new Color(237, 77, 126); // Pink
                break;
        }

        // Nội dung
        JPanel contentPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        contentPanel.setOpaque(false);

        JLabel lblType = new JLabel(n.getType());
        lblType.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblType.setForeground(typeColor);

        JLabel lblText = new JLabel(n.getContent());
        lblText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblText.setForeground(new Color(50, 50, 50));

        contentPanel.add(lblType);
        contentPanel.add(lblText);

        // Đánh dấu chưa đọc bằng một dấu chấm nhỏ
        if (!n.isRead()) {
            JPanel dot = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.setColor(new Color(237, 77, 126));
                    g.fillOval(0, 0, 8, 8);
                }
            };
            dot.setPreferredSize(new Dimension(8, 8));
            dot.setOpaque(false);
            card.add(dot, BorderLayout.EAST);
        }

        card.add(iconLabel, BorderLayout.WEST);
        card.add(contentPanel, BorderLayout.CENTER);

        return card;
    }

    private void showEmptyState() {
        JLabel empty = new JLabel("Chưa có thông báo nào dành cho bạn.", SwingConstants.CENTER);
        empty.setForeground(Color.GRAY);
        empty.setBorder(new EmptyBorder(50, 0, 0, 0));
        listContainer.add(empty);
    }

    private void styleSecondaryButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(new Color(100, 100, 100));
        setBackground(BG_PASTEL);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}
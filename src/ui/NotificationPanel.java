package ui;

import model.Notification;
import service.NotificationService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;

public class NotificationPanel extends JPanel {
    private JPanel listPanel;
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm - dd/MM/yyyy");

    public NotificationPanel() {
        // Thiết lập layout và màu sắc
        setLayout(new BorderLayout());
        setBackground(new Color(250, 250, 251));
        setBorder(new EmptyBorder(25, 25, 25, 25));

        // Tiêu đề
        JLabel title = new JLabel("Thống báo hệ thống");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setBorder(new EmptyBorder(0, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        // listPanel: Nơi chứa danh sách các dòng thông báo
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);

        // Thanh cuộn
        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }

    // Hàm cập nhật dữ liệu (đã được gọi trong MainFrame)
    public void refreshData() {
        listPanel.removeAll();
        java.util.List<Notification> list = NotificationService.getNotifications();

        if (list == null || list.isEmpty()) {
            JLabel emptyMsg = new JLabel(" Hiện tại chưa có thông báo nào mới.");
            emptyMsg.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            emptyMsg.setForeground(Color.GRAY);
            emptyMsg.setBorder(new EmptyBorder(20, 20, 0, 0));
            listPanel.add(emptyMsg);
        } else {
            // Hiển thị thông báo mới nhất lên đầu
            for (int i = list.size() - 1; i >= 0; i--) {
                listPanel.add(createNotificationItem(list.get(i)));
                listPanel.add(Box.createVerticalStrut(10));
            }
        }
        listPanel.revalidate();
        listPanel.repaint();
    }

    // Thiết kế từng dòng thông báo
    private JPanel createNotificationItem(Notification n) {
        JPanel p = new JPanel(new BorderLayout(15, 0));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(245, 245, 245)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
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


        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        centerPanel.setOpaque(false);

        JLabel contentLabel = new JLabel(n.getContent());
        contentLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));

        JLabel timeLabel = new JLabel(n.getTimestamp().format(dtf));
        timeLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        timeLabel.setForeground(Color.GRAY);

        centerPanel.add(contentLabel);
        centerPanel.add(timeLabel);

        p.add(iconLabel, BorderLayout.WEST);
        p.add(centerPanel, BorderLayout.CENTER);

        return p;
    }
}
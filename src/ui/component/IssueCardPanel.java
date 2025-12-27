package ui.components;

import model.IssueStatus;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class IssueCardPanel extends JPanel {

    private final Color PRIMARY_PINK = new Color(255, 110, 157);
    private final String CUTE_FONT = "Segoe UI";
    private Color currentBg = Color.WHITE;

    public IssueCardPanel(String type, String content, String level, String time, IssueStatus status) {
        setLayout(new BorderLayout(15, 8));
        setOpaque(false);
        setBorder(new EmptyBorder(15, 20, 15, 20));
        setMaximumSize(new Dimension(1000, 150));
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);

        JLabel lblType = new JLabel(type.toUpperCase());
        lblType.setFont(new Font(CUTE_FONT, Font.BOLD, 14));
        lblType.setForeground(PRIMARY_PINK);

        JPanel badgeContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        badgeContainer.setOpaque(false);

        // Badge Mức độ
        badgeContainer.add(createBadge(level, getLevelColor(level), getLevelBg(level)));

        // Badge Trạng thái mặc định (Chờ xử lý / Đang xử lý / Hoàn thành)
        badgeContainer.add(createStatusBadge(status));

        topRow.add(lblType, BorderLayout.WEST);
        topRow.add(badgeContainer, BorderLayout.EAST);

        JLabel lblDesc = new JLabel("<html><body style='width:600px; font-family:" + CUTE_FONT + "; font-size:15pt; color:#444;'>" + content + "</body></html>");

        JLabel lblTime = new JLabel(time);
        lblTime.setFont(new Font(CUTE_FONT, Font.PLAIN, 14));
        lblTime.setForeground(new Color(180, 180, 180));

        add(topRow, BorderLayout.NORTH);
        add(lblDesc, BorderLayout.CENTER);
        add(lblTime, BorderLayout.SOUTH);

        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { currentBg = new Color(250, 250, 250); repaint(); }
            @Override public void mouseExited(MouseEvent e) { currentBg = Color.WHITE; repaint(); }
        });
    }

    private JPanel createStatusBadge(IssueStatus status) {
        String text; Color fg, bg;
        if (status == IssueStatus.PROCESSING) {
            text = "Đang xử lý"; fg = new Color(59, 130, 246); bg = new Color(219, 234, 254);
        } else if (status == IssueStatus.DONE) {
            text = "Hoàn thành"; fg = new Color(34, 197, 94); bg = new Color(220, 252, 231);
        } else {
            text = "Chờ xử lý"; fg = new Color(107, 114, 128); bg = new Color(243, 244, 246);
        }
        return createBadge(text, fg, bg);
    }

    private JPanel createBadge(String text, Color foreground, Color background) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(CUTE_FONT, Font.BOLD, 12));
        label.setForeground(foreground);
        JPanel badge = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(background);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15); // Bo tròn badge
                g2.dispose();
            }
        };
        badge.setOpaque(false);
        badge.setBorder(new EmptyBorder(4, 12, 4, 12));
        badge.add(label);
        return badge;
    }

    private Color getLevelColor(String l) { return l.equals("Khẩn cấp") ? new Color(220, 60, 60) : l.equals("Trung bình") ? new Color(220, 140, 0) : new Color(60, 160, 90); }
    private Color getLevelBg(String l) { return l.equals("Khẩn cấp") ? new Color(255, 220, 220) : l.equals("Trung bình") ? new Color(255, 235, 200) : new Color(220, 245, 230); }

    public void addClickListener(Runnable action) {
        addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { action.run(); }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(currentBg);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25); // Bo tròn khung thẻ
        g2.setColor(new Color(235, 235, 235));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 25, 25);
        g2.dispose();
    }
}
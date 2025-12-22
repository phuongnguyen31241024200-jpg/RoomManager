import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class ThongBaoView extends JPanel {

    private Main main;

    private final Color BG = Color.WHITE;
    private final Color CARD_BORDER = new Color(243, 244, 246);
    private final Color TEXT_MAIN = new Color(17, 24, 39);
    private final Color TEXT_SUB = new Color(107, 114, 128);

    private List<NotifyCard> cards = new ArrayList<>();

    public ThongBaoView(Main main) {
        this.main = main;

        setLayout(new BorderLayout(0, 25));
        setBackground(BG);
        setBorder(new EmptyBorder(30, 40, 30, 40));

        // ===== HEADER =====
        JLabel title = new JLabel(
                "<html><b style='font-size:22px;'>Thông báo</b><br>" +
                        "<span style='font-size:13px;color:#6B7280;'>Tất cả đã đọc</span></html>"
        );
        title.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        add(title, BorderLayout.NORTH);

        // ===== LIST =====
        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setOpaque(false);

        cards.add(new NotifyCard(
                NotifyType.ALERT,
                "Sự cố mới",
                "Phòng A01 báo cáo sự cố về điện",
                "10/12/2024",
                () -> main.showView("SuCo")
        ));

        cards.add(new NotifyCard(
                NotifyType.SUCCESS,
                "Thanh toán thành công",
                "Phòng B01 đã thanh toán hóa đơn tháng 12",
                "05/12/2024",
                () -> main.showView("HopDong")
        ));

        cards.add(new NotifyCard(
                NotifyType.INFO,
                "Hóa đơn mới",
                "Hóa đơn tháng 12/2024 đã được tạo",
                "01/12/2024",
                () -> main.showView("HopDong")
        ));

        for (NotifyCard c : cards) {
            list.add(c);
            list.add(Box.createVerticalStrut(18));
        }

        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        add(scroll, BorderLayout.CENTER);
    }

    // ================= CARD =================
    class NotifyCard extends JPanel {

        NotifyCard(NotifyType type, String title, String desc, String date, Runnable onClick) {

            setLayout(new BorderLayout(18, 0));
            setBackground(Color.WHITE);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(CARD_BORDER),
                    new EmptyBorder(18, 22, 18, 22)
            ));

            // ICON
            add(new IconBadge(type), BorderLayout.WEST);

            // TEXT
            JPanel textWrap = new JPanel();
            textWrap.setLayout(new BoxLayout(textWrap, BoxLayout.Y_AXIS));
            textWrap.setOpaque(false);

            JLabel lblTitle = new JLabel(title);
            lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
            lblTitle.setForeground(TEXT_MAIN);

            JLabel lblDesc = new JLabel(desc);
            lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            lblDesc.setForeground(TEXT_SUB);

            JLabel lblDate = new JLabel(date);
            lblDate.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lblDate.setForeground(TEXT_SUB);

            textWrap.add(lblTitle);
            textWrap.add(Box.createVerticalStrut(6));
            textWrap.add(lblDesc);
            textWrap.add(Box.createVerticalStrut(10));
            textWrap.add(lblDate);

            add(textWrap, BorderLayout.CENTER);

            // HOVER + CLICK
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) {
                    setBackground(new Color(249, 250, 251));
                }
                @Override public void mouseExited(MouseEvent e) {
                    setBackground(Color.WHITE);
                }
                @Override public void mouseClicked(MouseEvent e) {
                    onClick.run();
                }
            });
        }
    }

    // ================= ICON =================
    enum NotifyType { ALERT, SUCCESS, INFO }

    class IconBadge extends JPanel {
        private NotifyType type;

        IconBadge(NotifyType type) {
            this.type = type;
            setPreferredSize(new Dimension(46, 46));
            setOpaque(false);
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color bg, icon;
            switch (type) {
                case ALERT -> {
                    bg = new Color(255, 237, 213);
                    icon = new Color(251, 146, 60);
                }
                case SUCCESS -> {
                    bg = new Color(220, 252, 231);
                    icon = new Color(34, 197, 94);
                }
                default -> {
                    bg = new Color(219, 234, 254);
                    icon = new Color(59, 130, 246);
                }
            }

            g2.setColor(bg);
            g2.fillOval(0, 0, 46, 46);

            g2.setColor(icon);
            g2.setStroke(new BasicStroke(2));

            // icon shape
            if (type == NotifyType.ALERT) {
                g2.drawLine(23, 12, 23, 26);
                g2.fillOval(21, 30, 4, 4);
            } else if (type == NotifyType.SUCCESS) {
                g2.drawLine(15, 24, 21, 30);
                g2.drawLine(21, 30, 31, 18);
            } else {
                g2.drawRect(16, 14, 14, 18);
                g2.drawLine(16, 20, 30, 20);
            }

            g2.dispose();
        }
    }
}

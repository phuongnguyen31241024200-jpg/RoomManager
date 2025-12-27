package ui.tenant;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;
import java.util.function.Consumer;

public class ReportIssueDialog extends JDialog {

    private final Color PRIMARY_PINK = new Color(255, 110, 157);
    private final Color SOFT_PINK_BG = new Color(255, 242, 246);
    private final String CUTE_FONT = "Segoe UI";

    public ReportIssueDialog(Window parent,
                             Consumer<IssueData> onSubmit) {

        super(parent, "Báo cáo sự cố", ModalityType.APPLICATION_MODAL);
        setSize(560, 680);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(Color.WHITE);

        JPanel rootPanel = new JPanel();
        rootPanel.setLayout(new BoxLayout(rootPanel, BoxLayout.Y_AXIS));
        rootPanel.setBackground(Color.WHITE);
        rootPanel.setBorder(new EmptyBorder(25, 40, 25, 40));

        JLabel popTitle = new JLabel("Báo cáo sự cố mới");
        popTitle.setFont(new Font(CUTE_FONT, Font.BOLD, 22));
        popTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        rootPanel.add(popTitle);
        rootPanel.add(Box.createVerticalStrut(25));

        // ===== Loại sự cố =====
        addFormLabel(rootPanel, "Loại sự cố");
        ButtonGroup typeGroup = new ButtonGroup();
        rootPanel.add(createOptionGroup(
                new String[]{"Điện", "Nước", "Cửa", "Khác"},
                typeGroup, 95));

        rootPanel.add(Box.createVerticalStrut(20));

        // ===== Mức độ =====
        addFormLabel(rootPanel, "Mức độ khẩn cấp");
        ButtonGroup levelGroup = new ButtonGroup();
        rootPanel.add(createOptionGroup(
                new String[]{"Thấp", "Trung bình", "Khẩn cấp"},
                levelGroup, 120));

        rootPanel.add(Box.createVerticalStrut(20));

        // ===== Mô tả =====
        addFormLabel(rootPanel, "Mô tả chi tiết");
        JTextArea txtDescription = new JTextArea(5, 20);
        txtDescription.setFont(new Font(CUTE_FONT, Font.PLAIN, 15));
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);

        JScrollPane textScroll = new JScrollPane(txtDescription);
        textScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        textScroll.setBorder(
                BorderFactory.createLineBorder(new Color(235, 235, 235), 1, true)
        );

        rootPanel.add(textScroll);
        rootPanel.add(Box.createVerticalStrut(30));

        JButton btnSend = createActionButton("Gửi báo cáo");
        btnSend.addActionListener(e -> {
            if (txtDescription.getText().trim().isEmpty()) return;

            IssueData data = new IssueData(
                    getSelectedButtonText(typeGroup),
                    txtDescription.getText(),
                    getSelectedButtonText(levelGroup),
                    LocalDateTime.now().format(
                            DateTimeFormatter.ofPattern("HH:mm - dd/MM/yyyy"))
            );

            onSubmit.accept(data);
            dispose();
        });

        rootPanel.add(btnSend);
        add(rootPanel);
    }

    // ===== DTO nhỏ gọn =====
    public static class IssueData {
        public final String type;
        public final String desc;
        public final String level;
        public final String time;

        public IssueData(String type, String desc, String level, String time) {
            this.type = type;
            this.desc = desc;
            this.level = level;
            this.time = time;
        }
    }

    // ===== Helpers (copy từ panel sang) =====

    private JPanel createOptionGroup(String[] options, ButtonGroup group, int width) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        for (int i = 0; i < options.length; i++) {
            JToggleButton btn = createOptionButton(options[i], width);
            group.add(btn);
            panel.add(btn);
            if (i == 0) btn.setSelected(true);
        }
        return panel;
    }

    private JToggleButton createOptionButton(String text, int width) {
        JToggleButton btn = new JToggleButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(isSelected() ? SOFT_PINK_BG : Color.WHITE);
                g2.fillRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 12, 12);

                g2.setColor(isSelected() ? PRIMARY_PINK : new Color(230, 230, 230));
                g2.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 12, 12);

                g2.setColor(isSelected() ? PRIMARY_PINK : Color.DARK_GRAY);
                g2.setFont(new Font(CUTE_FONT, Font.BOLD, 14));

                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                        (getWidth() - fm.stringWidth(getText())) / 2,
                        (getHeight() + fm.getAscent()) / 2 - 2);

                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(width, 40));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        return btn;
    }

    private JButton createActionButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(PRIMARY_PINK);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                g2.setColor(Color.WHITE);
                g2.setFont(new Font(CUTE_FONT, Font.BOLD, 16));

                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                        (getWidth() - fm.stringWidth(getText())) / 2,
                        (getHeight() + fm.getAscent()) / 2 - 2);

                g2.dispose();
            }
        };
        btn.setMaximumSize(new Dimension(500, 50));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        return btn;
    }

    private void addFormLabel(JPanel container, String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(CUTE_FONT, Font.BOLD, 15));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(label);
        container.add(Box.createVerticalStrut(8));
    }

    private String getSelectedButtonText(ButtonGroup group) {
        for (Enumeration<AbstractButton> e = group.getElements(); e.hasMoreElements();) {
            AbstractButton b = e.nextElement();
            if (b.isSelected()) return b.getText();
        }
        return "";
    }
}
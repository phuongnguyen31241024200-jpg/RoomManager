package ui;

import model.IssueReport;

import model.IssueStatus;



import javax.swing.*;

import javax.swing.border.EmptyBorder;

import java.awt.*;

import java.awt.geom.RoundRectangle2D;



public class IssueDetailDialog extends JDialog {



    private final Color PINK = new Color(236, 72, 127);

    private final Color GREEN = new Color(34, 197, 94);

    private final Color BG_GRAY = new Color(245, 245, 245);



    public IssueDetailDialog(JFrame parent,

                             IssueReport issue,

                             Runnable onUpdate) {



        super(parent, true);



        // ===== BO TRÒN TOÀN BỘ DIALOG =====

        setUndecorated(true);

        setSize(420, 380);

        setLocationRelativeTo(parent);

        setBackground(new Color(0, 0, 0, 0));

        setShape(new RoundRectangle2D.Double(0, 0, 420, 380, 40, 40));

        setLayout(new BorderLayout());



        // ===== ROOT PANEL =====

        RoundedPanel root = new RoundedPanel(40, Color.WHITE);

        root.setLayout(new BorderLayout());

        add(root);



        // ===== CONTENT =====

        JPanel content = new JPanel();

        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        content.setBorder(new EmptyBorder(20, 20, 20, 20));

        content.setOpaque(false);



        JLabel title = new JLabel("Chi tiết sự cố");

        title.setFont(new Font("SansSerif", Font.BOLD, 18));

        content.add(title);

        content.add(Box.createVerticalStrut(14));



        // ===== DESCRIPTION =====

        RoundedPanel descPanel = new RoundedPanel(22, BG_GRAY);

        descPanel.setLayout(new BorderLayout());

        descPanel.setBorder(new EmptyBorder(12, 14, 12, 14));



        JLabel lbDesc = new JLabel("<html>" + issue.getDescription() + "</html>");

        lbDesc.setFont(new Font("SansSerif", Font.PLAIN, 14));

        descPanel.add(lbDesc, BorderLayout.CENTER);



        content.add(descPanel);

        content.add(Box.createVerticalStrut(18));



        // ===== HISTORY =====

        JLabel historyLabel = new JLabel("Lịch sử xử lý");

        historyLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

        content.add(historyLabel);

        content.add(Box.createVerticalStrut(10));



        JTextArea historyArea = new JTextArea();

        historyArea.setEditable(false);

        historyArea.setFont(new Font("SansSerif", Font.PLAIN, 13));

        historyArea.setOpaque(false);



        for (String h : issue.getHistory()) {

            historyArea.append("• " + h + "\n");

        }



        JScrollPane scroll = new JScrollPane(historyArea);

        scroll.setBorder(null);

        scroll.setOpaque(false);

        scroll.getViewport().setOpaque(false);



        RoundedPanel historyPanel = new RoundedPanel(22, BG_GRAY);

        historyPanel.setLayout(new BorderLayout());

        historyPanel.setBorder(new EmptyBorder(8, 8, 8, 8));

        historyPanel.setPreferredSize(new Dimension(360, 120));

        historyPanel.add(scroll, BorderLayout.CENTER);



        content.add(historyPanel);

        root.add(content, BorderLayout.CENTER);



        // ===== BUTTONS =====

        JPanel buttons = new JPanel(new GridLayout(1, 2, 14, 0));

        buttons.setBorder(new EmptyBorder(14, 20, 16, 20));

        buttons.setOpaque(false);



        JButton btnProcessing = createPillButton("⏳ Đang xử lý", PINK);

        JButton btnDone = createPillButton("✔ Hoàn thành", GREEN);



        btnProcessing.addActionListener(e -> {

            issue.addHistory("Chuyển sang Đang xử lý");

            issue.setStatus(IssueStatus.PROCESSING);

            onUpdate.run();

            dispose();

        });



        btnDone.addActionListener(e -> {

            issue.addHistory("Đã hoàn thành");

            issue.setStatus(IssueStatus.DONE);

            onUpdate.run();

            dispose();

        });



        buttons.add(btnProcessing);

        buttons.add(btnDone);

        root.add(buttons, BorderLayout.SOUTH);

    }



    // ===== BUTTON =====

    private JButton createPillButton(String text, Color bg) {

        JButton btn = new JButton(text);

        btn.setForeground(Color.WHITE);

        btn.setFont(new Font("SansSerif", Font.BOLD, 13));

        btn.setContentAreaFilled(false);

        btn.setBorder(new EmptyBorder(10, 0, 10, 0));

        btn.setFocusPainted(false);



        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {

            @Override

            public void paint(Graphics g, JComponent c) {

                Graphics2D g2 = (Graphics2D) g;

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(bg);

                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 40, 40);

                super.paint(g, c);

            }

        });

        return btn;

    }



    // ===== ROUNDED PANEL =====

    static class RoundedPanel extends JPanel {

        private final int radius;

        private final Color bg;



        public RoundedPanel(int radius, Color bg) {

            this.radius = radius;

            this.bg = bg;

            setOpaque(false);

        }



        @Override

        protected void paintComponent(Graphics g) {

            Graphics2D g2 = (Graphics2D) g;

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(bg);

            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

        }

    }

}
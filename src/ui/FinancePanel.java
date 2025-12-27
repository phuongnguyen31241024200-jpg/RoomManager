package ui;

import model.Income;
import model.Expense;
import service.FinanceService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FinancePanel extends JPanel {

    private final FinanceService financeService;
    private final SummaryCard cardIncome;
    private final SummaryCard cardExpense;
    private final SummaryCard cardBalance;
    private final JPanel transactionListContainer;

    private final Color PINK_MAIN = new Color(237, 77, 126);
    private final Color GREEN_TEXT = new Color(40, 167, 69);
    private final Color RED_TEXT = new Color(220, 53, 69);
    private final Color BG_PASTEL = new Color(255, 245, 247);

    public FinancePanel(FinanceService financeService) {
        this.financeService = financeService;

        setLayout(new BorderLayout(0, 30));
        setBackground(BG_PASTEL);
        setBorder(new EmptyBorder(30, 40, 30, 40));

        // ================= HEADER =================
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JPanel titleGroup = new JPanel(new GridLayout(2, 1, 0, 5));
        titleGroup.setOpaque(false);

        JLabel lblTitle = new JLabel("Thu – Chi");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(new Color(50, 50, 50));

        JLabel lblSub = new JLabel("Quản lý tài chính cá nhân");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSub.setForeground(new Color(140, 140, 140));

        titleGroup.add(lblTitle);
        titleGroup.add(lblSub);

        // Nút thêm giao dịch với hiệu ứng bo tròn custom
        JButton btnAdd = new JButton("Thêm giao dịch") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
                g2.setColor(getForeground());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth() - fm.stringWidth(getText())) / 2, (getHeight() + fm.getAscent()) / 2 - 2);
                g2.dispose();
            }
        };
        btnAdd.setBackground(PINK_MAIN);
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAdd.setPreferredSize(new Dimension(160, 42));
        btnAdd.setBorder(null);
        btnAdd.setContentAreaFilled(false);
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnAdd.addActionListener(e -> new AddTransactionDialog(financeService, this).setVisible(true));

        headerPanel.add(titleGroup, BorderLayout.WEST);
        headerPanel.add(btnAdd, BorderLayout.EAST);

        // ================= SUMMARY CARDS =================
        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        summaryPanel.setOpaque(false);

        cardIncome = new SummaryCard("Tổng thu", "📈", GREEN_TEXT, new Color(240, 255, 240));
        cardExpense = new SummaryCard("Tổng chi", "📉", RED_TEXT, new Color(255, 240, 240));
        cardBalance = new SummaryCard("Số dư", "👛", PINK_MAIN, new Color(255, 240, 245));

        summaryPanel.add(cardIncome);
        summaryPanel.add(cardExpense);
        summaryPanel.add(cardBalance);

        // ================= TRANSACTION LIST =================
        RoundedPanel listWrapper = new RoundedPanel(40, Color.WHITE);
        listWrapper.setLayout(new BorderLayout());

        transactionListContainer = new JPanel();
        transactionListContainer.setLayout(new BoxLayout(transactionListContainer, BoxLayout.Y_AXIS));
        transactionListContainer.setBackground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(transactionListContainer);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getViewport().setBackground(Color.WHITE);

        listWrapper.add(scroll, BorderLayout.CENTER);

        JPanel northContainer = new JPanel(new BorderLayout(0, 25));
        northContainer.setOpaque(false);
        northContainer.add(headerPanel, BorderLayout.NORTH);
        northContainer.add(summaryPanel, BorderLayout.CENTER);

        add(northContainer, BorderLayout.NORTH);
        add(listWrapper, BorderLayout.CENTER);

        reloadData();
    }

    public void refreshData() {
        reloadData();
    }

    public void reloadData() {
        // Cập nhật giá trị 3 thẻ Summary
        cardIncome.setValue(financeService.getTotalIncome());
        cardExpense.setValue(financeService.getTotalExpense());
        cardBalance.setValue(financeService.getBalance());

        transactionListContainer.removeAll();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        // Hiển thị khoản thu (Income)
        List<Income> incomes = financeService.getIncomes();
        if (incomes != null) {
            for (int i = 0; i < incomes.size(); i++) {
                Income item = incomes.get(i);
                String dateStr = (item.getDate() != null) ? sdf.format(item.getDate()) : sdf.format(new Date());
                transactionListContainer.add(createTransactionItem(item.getDescription(), "Thu nhập", dateStr, item.getAmount(), true, i));
            }
        }

        // Hiển thị khoản chi (Expense)
        List<Expense> expenses = financeService.getExpenses();
        if (expenses != null) {
            for (int i = 0; i < expenses.size(); i++) {
                Expense item = expenses.get(i);
                String dateStr = (item.getDate() != null) ? sdf.format(item.getDate()) : sdf.format(new Date());
                transactionListContainer.add(createTransactionItem(item.getDescription(), "Chi tiêu", dateStr, item.getAmount(), false, i));
            }
        }

        transactionListContainer.add(Box.createVerticalGlue());
        transactionListContainer.revalidate();
        transactionListContainer.repaint();
    }

    private JPanel createTransactionItem(String title, String type, String date, double amount, boolean income, final int index) {
        JPanel p = new JPanel(new BorderLayout(20, 0));
        p.setBackground(Color.WHITE);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        // Icon Column
        JPanel iconContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        iconContainer.setOpaque(false);
        CirclePanel iconWrap = new CirclePanel(income ? new Color(240, 253, 244) : new Color(254, 242, 242));
        iconWrap.setPreferredSize(new Dimension(45, 45));
        JLabel icon = new JLabel(income ? "📈" : "📉", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI", Font.BOLD, 18));
        icon.setForeground(income ? GREEN_TEXT : RED_TEXT);
        iconWrap.add(icon);
        iconContainer.add(iconWrap);

        // Text Column
        JPanel textGroup = new JPanel();
        textGroup.setLayout(new BoxLayout(textGroup, BoxLayout.Y_AXIS));
        textGroup.setOpaque(false);

        JLabel lblItemTitle = new JLabel(title);
        lblItemTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblItemTitle.setForeground(new Color(40, 40, 40));

        JLabel lblSub = new JLabel(type + " • " + date);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(new Color(150, 150, 150));

        textGroup.add(Box.createVerticalGlue());
        textGroup.add(lblItemTitle);
        textGroup.add(Box.createVerticalStrut(3));
        textGroup.add(lblSub);
        textGroup.add(Box.createVerticalGlue());

        // Amount & Delete Button Column
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);

        JLabel lblAmount = new JLabel((income ? "+" : "-") + String.format("%,.0f ₫", amount));
        lblAmount.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblAmount.setForeground(income ? GREEN_TEXT : RED_TEXT);

        // Nút xóa giao dịch (Dấu X nhỏ)
        JButton btnDelete = new JButton(" X ");
        btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnDelete.setForeground(new Color(200, 200, 200));
        btnDelete.setBorderPainted(false);
        btnDelete.setContentAreaFilled(false);
        btnDelete.setFocusPainted(false);
        btnDelete.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnDelete.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btnDelete.setForeground(RED_TEXT); }
            public void mouseExited(java.awt.event.MouseEvent e) { btnDelete.setForeground(new Color(200, 200, 200)); }
        });

        btnDelete.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc chắn muốn xóa giao dịch này?", "Xác nhận xóa",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                if (income) {
                    financeService.deleteIncome(index);
                } else {
                    financeService.deleteExpense(index);
                }
                reloadData(); // Cập nhật lại UI và số dư ngay lập tức
            }
        });

        rightPanel.add(lblAmount);
        rightPanel.add(btnDelete);

        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(245, 245, 245)),
                new EmptyBorder(12, 25, 12, 25)
        ));

        p.add(iconContainer, BorderLayout.WEST);
        p.add(textGroup, BorderLayout.CENTER);
        p.add(rightPanel, BorderLayout.EAST);

        return p;
    }

    // ================= CUSTOM COMPONENTS =================

    private class SummaryCard extends RoundedPanel {
        private final JLabel lblValue;

        public SummaryCard(String title, String icon, Color accent, Color iconBg) {
            super(40, Color.WHITE);
            setLayout(new BorderLayout(15, 0));
            setBorder(new EmptyBorder(20, 20, 20, 20));

            CirclePanel iconWrap = new CirclePanel(iconBg);
            iconWrap.setPreferredSize(new Dimension(50, 50));
            JLabel lblIcon = new JLabel(icon, SwingConstants.CENTER);
            lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
            lblIcon.setForeground(accent);
            iconWrap.add(lblIcon);

            JPanel text = new JPanel(new GridLayout(2, 1));
            text.setOpaque(false);
            JLabel lblT = new JLabel(title);
            lblT.setForeground(new Color(140, 140, 140));
            lblValue = new JLabel("0 ₫");
            lblValue.setFont(new Font("Segoe UI", Font.BOLD, 22));
            lblValue.setForeground(accent);

            text.add(lblT);
            text.add(lblValue);
            add(iconWrap, BorderLayout.WEST);
            add(text, BorderLayout.CENTER);
        }

        public void setValue(double val) {
            lblValue.setText(String.format("%,.0f ₫", val));
        }
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
            g2.setColor(bg);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), radius, radius));
            g2.setColor(new Color(235, 235, 235));
            g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, radius, radius));
            g2.dispose();
        }
    }

    private static class CirclePanel extends JPanel {
        private final Color bg;
        public CirclePanel(Color bg) {
            this.bg = bg;
            setOpaque(false);
            setLayout(new BorderLayout());
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fillOval(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    }
}
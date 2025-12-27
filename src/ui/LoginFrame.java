package ui;

import model.Account;
import service.*;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.basic.BasicToggleButtonUI;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;

public class LoginFrame extends JFrame {

    // ===== SERVICES =====
    private final AuthService authService = new AuthServiceImpl();
    private final IssueService issueService = new IssueReportServiceImpl();
    private final FinanceService financeService = new FinanceServiceImpl();
    private final InvoiceServiceImpl invoiceService = new InvoiceServiceImpl(financeService);

    // ===== UI CONSTANTS =====
    private static final Color APP_BG = new Color(255, 245, 248);
    private static final Color PRIMARY_COLOR = new Color(244, 93, 145);
    private static final Color ACTIVE_BG = new Color(255, 230, 238);
    private static final Color BORDER_COLOR = new Color(230, 230, 230); // Màu xám mờ cho viền
    private static final int ROUNDED_RADIUS = 20; // Chỉnh lên 20 theo ý bạn

    private JToggleButton landlordBtn;
    private JToggleButton tenantBtn;
    private JPasswordField passField;
    private JTextField phoneField;

    private static final String DEFAULT_PHONE = "Nhập số điện thoại";
    private static final String DEFAULT_PASSWORD = "Nhập mật khẩu";

    private final AbstractBorder defaultBorder = new RoundedInputBorder(10, BORDER_COLOR);
    private final AbstractBorder focusBorder = new RoundedInputBorder(10, PRIMARY_COLOR);

    public LoginFrame() {
        setTitle("RoomManager - Đăng nhập");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel root = new JPanel();
        root.setBackground(APP_BG);
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.add(Box.createVerticalStrut(30));

        // --- LOGO & TITLE ---
        JPanel logoPanel = createLogoPanel(90);
        logoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        root.add(logoPanel);
        root.add(Box.createVerticalStrut(10));

        JLabel title = new JLabel("RoomManager");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        root.add(title);

        JLabel subtitle = new JLabel("Quản lý phòng trọ thông minh");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(Color.GRAY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        root.add(subtitle);

        root.add(Box.createVerticalStrut(30));

        // --- FORM CARD ---
        JPanel cardWrapper = new JPanel();
        cardWrapper.setOpaque(false);
        cardWrapper.setMaximumSize(new Dimension(360, 520));
        cardWrapper.setLayout(new BorderLayout());

        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedCardBorder(ROUNDED_RADIUS),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        JLabel loginTitle = new JLabel("Đăng nhập");
        loginTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        loginTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(loginTitle);
        card.add(Box.createVerticalStrut(20));

        // --- ROLE SELECTION (CHỦ TRỌ / NGƯỜI THUÊ) ---
        JPanel rolePanel = new JPanel(new GridLayout(1, 2, 15, 0));
        rolePanel.setOpaque(false);
        landlordBtn = createRoleToggle("Chủ trọ");
        tenantBtn = createRoleToggle("Người thuê");
        ButtonGroup group = new ButtonGroup();
        group.add(landlordBtn); group.add(tenantBtn);
        tenantBtn.setSelected(true);
        rolePanel.add(landlordBtn); rolePanel.add(tenantBtn);
        card.add(rolePanel);

        card.add(Box.createVerticalStrut(25));

        // --- INPUT FIELDS ---
        card.add(createLabelPanel("Số điện thoại"));
        phoneField = new JTextField(DEFAULT_PHONE);
        card.add(setupInputPanel(phoneField, defaultBorder));

        card.add(Box.createVerticalStrut(20));

        card.add(createLabelPanel("Mật khẩu"));
        passField = new JPasswordField(DEFAULT_PASSWORD);
        card.add(createPasswordFieldWithIcon(defaultBorder));

        card.add(Box.createVerticalStrut(25));

        // --- LOGIN BUTTON ---
        JButton loginBtn = createStyledButton("Đăng nhập");
        card.add(loginBtn);


        cardWrapper.add(card);
        root.add(cardWrapper);
        root.add(Box.createVerticalGlue());

        addLoginActionListener(loginBtn);

        add(root);
        pack();
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(420, 720));
        setVisible(true);
    }

    private void addLoginActionListener(JButton loginBtn) {
        loginBtn.addActionListener(e -> {
            String phone = phoneField.getText().trim();
            String password = String.valueOf(passField.getPassword()).trim();

            if (phone.equals(DEFAULT_PHONE) || password.equals(DEFAULT_PASSWORD)) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Account acc = authService.login(phone, password);
            if (acc == null) {
                JOptionPane.showMessageDialog(this, "Sai số điện thoại hoặc mật khẩu", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if ("OWNER".equalsIgnoreCase(acc.getRole())) {
                new MainFrame();
            } else {
                new EnterRoomCodeFrame(acc, issueService, invoiceService);
            }
            this.dispose();
        });
    }

    // ================= UI HELPERS =================

    private JPanel createLabelPanel(String text) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        p.add(l, BorderLayout.WEST);
        return p;
    }

    private JPanel setupInputPanel(JTextComponent field, AbstractBorder border) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(border);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        panel.setPreferredSize(new Dimension(0, 48));

        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setOpaque(false);
        field.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 10));
        field.setForeground(Color.GRAY);

        if (field instanceof JPasswordField) ((JPasswordField) field).setEchoChar((char) 0);

        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                panel.setBorder(focusBorder);
                if (field.getText().equals(DEFAULT_PHONE) || field.getText().equals(DEFAULT_PASSWORD)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                    if (field instanceof JPasswordField) ((JPasswordField) field).setEchoChar('•');
                }
            }
            public void focusLost(FocusEvent e) {
                panel.setBorder(defaultBorder);
                if (field.getText().isEmpty()) {
                    field.setText(field instanceof JPasswordField ? DEFAULT_PASSWORD : DEFAULT_PHONE);
                    field.setForeground(Color.GRAY);
                    if (field instanceof JPasswordField) ((JPasswordField) field).setEchoChar((char) 0);
                }
            }
        });
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createPasswordFieldWithIcon(AbstractBorder border) {
        JPanel panel = setupInputPanel(passField, border);
        JLabel eyeIcon = new JLabel("👁 ");
        eyeIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));
        eyeIcon.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (passField.getEchoChar() == '•') passField.setEchoChar((char) 0);
                else if (!fieldIsEmpty(passField)) passField.setEchoChar('•');
            }
        });
        panel.add(eyeIcon, BorderLayout.EAST);
        return panel;
    }

    private boolean fieldIsEmpty(JTextComponent f) {
        return f.getText().equals(DEFAULT_PASSWORD) || f.getText().isEmpty();
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setForeground(Color.WHITE);
        btn.setBackground(PRIMARY_COLOR);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        return btn;
    }

    private JToggleButton createRoleToggle(String text) {
        JToggleButton btn = new JToggleButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setUI(new RoleToggleUI());
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // QUAN TRỌNG: Tắt các hiệu ứng viền mặc định
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        return btn;
    }



    private JLabel createLink(String text) {
        JLabel link = new JLabel(text);
        link.setForeground(PRIMARY_COLOR);
        link.setAlignmentX(Component.CENTER_ALIGNMENT);
        link.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return link;
    }

    private JPanel createLogoPanel(int size) {
        JPanel p = new JPanel(new GridBagLayout()) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PRIMARY_COLOR);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setPreferredSize(new Dimension(size, size));
        p.setMaximumSize(new Dimension(size, size));
        JLabel l = new JLabel("RM");
        l.setForeground(Color.WHITE);
        l.setFont(new Font("Segoe UI", Font.BOLD, 32));
        p.add(l);
        return p;
    }

    // --- BORDERS & UI CUSTOMS ---
    static class RoundedInputBorder extends AbstractBorder {
        private final int radius; private final Color color;
        RoundedInputBorder(int radius, Color color) { this.radius = radius; this.color = color; }
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, w - 1, h - 1, radius, radius);
        }
    }

    static class RoundedCardBorder extends AbstractBorder {
        private final int radius;
        RoundedCardBorder(int radius) { this.radius = radius; }
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0, 0, 0, 15)); // Đổ bóng mờ hơn
            g2.drawRoundRect(x, y, w - 1, h - 1, radius, radius);
            g2.dispose();
        }
    }

    class RoleToggleUI extends BasicToggleButtonUI {
        @Override
        public void paint(Graphics g, JComponent c) {
            JToggleButton btn = (JToggleButton) c;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = btn.getWidth();
            int h = btn.getHeight();
            int arc = 20; // Bo tròn 20px

            if (btn.isSelected()) {
                // Khi được chọn: Nền hồng nhạt, Viền hồng đậm
                g2.setColor(ACTIVE_BG);
                g2.fillRoundRect(0, 0, w-1, h-1, arc, arc);
                g2.setColor(PRIMARY_COLOR);
                g2.drawRoundRect(0, 0, w-1, h-1, arc, arc);
                btn.setForeground(PRIMARY_COLOR);
            } else {
                // Khi không được chọn: Nền trắng, Viền xám mờ (BORDER_COLOR)
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, w-1, h-1, arc, arc);
                g2.setColor(BORDER_COLOR);
                g2.drawRoundRect(0, 0, w-1, h-1, arc, arc);
                btn.setForeground(Color.BLACK);
            }

            super.paint(g2, c);
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginFrame::new);
    }
}
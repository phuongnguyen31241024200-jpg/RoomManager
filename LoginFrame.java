package ui;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.basic.BasicToggleButtonUI;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

public class LoginFrame extends JFrame {

    // ===== HẰNG SỐ MÀU SẮC & THIẾT KẾ =====
    private static final Color APP_BG = new Color(255, 245, 248); // Màu nền chính (Hồng nhạt)
    private static final Color PRIMARY_COLOR = new Color(244, 93, 145); // Màu chủ đạo (Hồng đậm)
    private static final Color ACTIVE_BG = new Color(255, 230, 238); // Màu nền khi nút được chọn
    private static final Color BORDER_COLOR = new Color(230, 230, 230); // Màu viền Xám nhạt
    private static final int ROUNDED_RADIUS = 18; // Độ cong góc chung

    private JToggleButton landlordBtn;
    private JToggleButton tenantBtn;
    private JPasswordField passField;
    private JTextField phoneField;

    private static final String DEFAULT_PHONE = "Nhập số điện thoại";
    private static final String DEFAULT_PASSWORD = "Nhập mật khẩu";

    // Khai báo các đối tượng Border để tái sử dụng
    private final AbstractBorder defaultBorder = new RoundedInputBorder(ROUNDED_RADIUS / 2, BORDER_COLOR);
    private final AbstractBorder focusBorder = new RoundedInputBorder(ROUNDED_RADIUS / 2, PRIMARY_COLOR);


    public LoginFrame() {
        setTitle("RoomManager - Đăng nhập");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // ===== ROOT PANEL (Chứa mọi thứ) =====
        JPanel root = new JPanel();
        root.setBackground(APP_BG);
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));

        root.add(Box.createVerticalStrut(30));

        // ===== LOGO VÀ TEXT TRÊN CÙNG =====
        JPanel logoPanel = createLogoPanel("/images/logo.png", 90);
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

        // ===== CARD CHỨA FORM =====
        JPanel cardWrapper = new JPanel();
        cardWrapper.setOpaque(false);
        cardWrapper.setMaximumSize(new Dimension(360, Integer.MAX_VALUE));
        cardWrapper.setLayout(new BoxLayout(cardWrapper, BoxLayout.X_AXIS));

        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedCardBorder(ROUNDED_RADIUS),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Tiêu đề & Subtitle
        JLabel loginTitle = new JLabel("Đăng nhập");
        loginTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        loginTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(loginTitle);

        JLabel roleLabel = new JLabel("Chọn vai trò và đăng nhập");
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        roleLabel.setForeground(Color.GRAY);
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(roleLabel);

        card.add(Box.createVerticalStrut(20));

        // ===== ROLE TOGGLE BUTTONS (CHỌN VAI TRÒ) =====
        JPanel rolePanel = new JPanel(new GridLayout(1, 2, 15, 0));
        rolePanel.setOpaque(false);
        rolePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        landlordBtn = createRoleToggle("Chủ trọ", "/images/icon_landlord.png");
        tenantBtn = createRoleToggle("Người thuê", "/images/icon_tenant.png");

        ButtonGroup group = new ButtonGroup();
        group.add(landlordBtn);
        group.add(tenantBtn);
        tenantBtn.setSelected(true);

        rolePanel.add(landlordBtn);
        rolePanel.add(tenantBtn);
        card.add(rolePanel);

        card.add(Box.createVerticalStrut(25));

        // ===== SỐ ĐIỆN THOẠI (Dùng defaultBorder) =====
        JPanel phoneLabelPanel = new JPanel(new BorderLayout());
        phoneLabelPanel.setOpaque(false);
        phoneLabelPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));

        JLabel phoneLabel = new JLabel("Số điện thoại");
        phoneLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        phoneLabel.setHorizontalAlignment(SwingConstants.LEFT);

        phoneLabelPanel.add(phoneLabel, BorderLayout.WEST);
        card.add(phoneLabelPanel);


        // Khởi tạo với viền XÁM MẶC ĐỊNH
        JPanel phonePanel = createPhoneFieldWithBorder(DEFAULT_PHONE, defaultBorder);
        card.add(phonePanel);

        card.add(Box.createVerticalStrut(20));

        // ===== MẬT KHẨU (Dùng defaultBorder) =====
        JPanel passLabelPanel = new JPanel(new BorderLayout());
        passLabelPanel.setOpaque(false);
        passLabelPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));

        JLabel passLabel = new JLabel("Mật khẩu");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        passLabel.setHorizontalAlignment(SwingConstants.LEFT);

        passLabelPanel.add(passLabel, BorderLayout.WEST);
        card.add(passLabelPanel);


        // Khởi tạo với viền XÁM MẶC ĐỊNH
        JPanel passwordPanel = createPasswordFieldWithIcon(DEFAULT_PASSWORD, "/images/icon_eye.png", defaultBorder);
        card.add(passwordPanel);

        card.add(Box.createVerticalStrut(25));

        // ===== NÚT ĐĂNG NHẬP =====
        JPanel loginPanel = new JPanel(new BorderLayout());
        loginPanel.setOpaque(false);
        loginPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        loginPanel.setPreferredSize(new Dimension(0, 48));

        JButton loginBtn = new JButton("Đăng nhập") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), ROUNDED_RADIUS / 2, ROUNDED_RADIUS / 2);

                super.paintComponent(g2);
                g2.dispose();
            }
        };

        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setBackground(PRIMARY_COLOR);

        loginBtn.setFocusPainted(false);
        loginBtn.setBorderPainted(false);
        loginBtn.setContentAreaFilled(false); // ⭐ để mình tự vẽ
        loginBtn.setOpaque(false);

        loginPanel.add(loginBtn, BorderLayout.CENTER);
        card.add(loginPanel);

        addLoginActionListener(loginBtn);

        card.add(Box.createVerticalStrut(15));

        // ===== FOOTER LINK QUÊN MẬT KHẨU =====
        JLabel forgotPass = createLink("Bạn quên mật khẩu?");
        forgotPass.setForeground(PRIMARY_COLOR);
        forgotPass.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        card.add(forgotPass);

        card.add(Box.createVerticalStrut(5));

        JLabel registerLabel = new JLabel(
                "<html>Bạn chưa có tài khoản? <span style='color:#F45D91; font-weight:600;'>Đăng ký ngay!</span></html>"
        );
        registerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        registerLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

// ⭐ QUAN TRỌNG
        registerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerLabel.setHorizontalAlignment(SwingConstants.CENTER);

        registerLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(LoginFrame.this, "Đi tới màn hình đăng ký");
            }
        });

        card.add(registerLabel);



        cardWrapper.add(card);
        root.add(cardWrapper);

        root.add(Box.createVerticalGlue());

        add(root);

        pack();
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(420, 700));
        setVisible(true);
    }

    // =======================================================
    // ===== PHƯƠNG THỨC HỖ TRỢ & CUSTOM UI IMPLEMENTATIONS =====
    // =======================================================

    private JPanel createLogoPanel(String iconPath, int size) {
        // ... (Không thay đổi)
        JPanel logoPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PRIMARY_COLOR);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), ROUNDED_RADIUS, ROUNDED_RADIUS);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        logoPanel.setOpaque(false);
        logoPanel.setPreferredSize(new Dimension(size, size));
        logoPanel.setMaximumSize(new Dimension(size, size));
        logoPanel.setLayout(new GridBagLayout());

        URL logoUrl = getClass().getResource(iconPath);
        if (logoUrl != null) {
            ImageIcon icon = new ImageIcon(logoUrl);
            Image img = icon.getImage().getScaledInstance(size * 4 / 5, size * 4 / 5, Image.SCALE_SMOOTH);
            JLabel logo = new JLabel(new ImageIcon(img));
            logoPanel.add(logo);
        }
        return logoPanel;
    }

    // Phương thức tạo JTextField với custom border và Focus Listener
    private JPanel createPhoneFieldWithBorder(String defaultText, AbstractBorder defaultBorder) {
        phoneField = new JTextField(defaultText);
        JPanel panel = setupInputPanel(phoneField, defaultBorder);

        // Gán Focus Listener cho Phone Field
        addFocusBorderListener(phoneField, panel);

        return panel;
    }

    // Phương thức tạo JPasswordField với custom border và Focus Listener
    private JPanel createPasswordFieldWithIcon(String defaultText, String iconPath, AbstractBorder defaultBorder) {
        passField = new JPasswordField(defaultText);
        JPanel panel = setupInputPanel(passField, defaultBorder);

        // Gán Focus Listener cho Password Field
        addFocusBorderListener(passField, panel);


        // Thêm Icon Mắt (không thay đổi)
        JLabel eyeIcon = new JLabel();
        URL iconUrl = getClass().getResource(iconPath);
        if (iconUrl != null) {
            ImageIcon icon = new ImageIcon(iconUrl);
            Image img = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            eyeIcon.setIcon(new ImageIcon(img));
        } else {
            eyeIcon.setText("👁");
            eyeIcon.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        }

        eyeIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));
        eyeIcon.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 10));

        eyeIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (passField.getEchoChar() == 0) {
                    passField.setEchoChar('•'); // Ẩn
                } else {
                    passField.setEchoChar((char) 0); // Hiện
                }
            }
        });

        passField.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 5)); // Cần padding nhỏ hơn bên phải
        panel.add(eyeIcon, BorderLayout.EAST);

        return panel;
    }

    // Hàm chung thiết lập cơ bản cho cả JTextField và JPasswordField
    private JPanel setupInputPanel(JTextComponent field, AbstractBorder border) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(true);
        panel.setBackground(Color.WHITE);

        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        panel.setPreferredSize(new Dimension(0, 48));

        // ✅ DÒNG QUYẾT ĐỊNH – NẾU THIẾU → TRẮNG
        panel.setBorder(border);

        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setBackground(Color.WHITE);
        field.setCaretColor(Color.BLACK);
        field.setOpaque(false);
        field.setForeground(Color.GRAY); // ⭐ placeholder màu xám

        // Padding cho JTextField
        if (field instanceof JTextField) {
            field.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 10));
        }

        // Password
        if (field instanceof JPasswordField) {
            ((JPasswordField) field).setEchoChar((char) 0); // ⭐ placeholder KHÔNG che
        }


        styleFieldFocus(
                field,
                (field instanceof JPasswordField) ? DEFAULT_PASSWORD : DEFAULT_PHONE
        );

        panel.add(field, BorderLayout.CENTER);
        return panel;
    }


    // LẮNG NGHE SỰ KIỆN FOCUS ĐỂ THAY ĐỔI VIỀN
    private void addFocusBorderListener(
            JTextComponent field,
            JPanel containerPanel
    ) {
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                containerPanel.setBorder(focusBorder); // 🌸 HỒNG
            }

            @Override
            public void focusLost(FocusEvent e) {
                containerPanel.setBorder(defaultBorder); // ⬜ XÁM
            }
        });
    }


    // Logic Focus/Placeholder chung (Không thay đổi)
    private void styleFieldFocus(JTextComponent field, String defaultText) {
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field instanceof JPasswordField) {
                    if (String.valueOf(((JPasswordField) field).getPassword()).equals(defaultText)) {
                        ((JPasswordField) field).setText("");
                    }
                } else if (field instanceof JTextField) {
                    if (field.getText().equals(defaultText)) {
                        field.setText("");
                    }
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (field instanceof JPasswordField) {
                    if (String.valueOf(((JPasswordField) field).getPassword()).isEmpty()) {
                        ((JPasswordField) field).setText(defaultText);
                    }
                } else if (field instanceof JTextField) {
                    if (field.getText().isEmpty()) {
                        field.setText(defaultText);
                    }
                }
            }
        });
    }

    private JToggleButton createRoleToggle(String text, String iconPath) {
        // ... (Không thay đổi)
        JToggleButton btn = new JToggleButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(0, 75));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        URL iconUrl = getClass().getResource(iconPath);
        if (iconUrl != null) {
            ImageIcon icon = new ImageIcon(iconUrl);
            Image img = icon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
            btn.setIcon(new ImageIcon(img));
            btn.setHorizontalTextPosition(SwingConstants.CENTER);
            btn.setVerticalTextPosition(SwingConstants.BOTTOM);
        }

        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);

        btn.setUI(new RoleToggleUI());

        return btn;
    }

    private JLabel createLink(String text) {
        // ... (Không thay đổi)
        JLabel link = new JLabel(text);
        link.setAlignmentX(Component.CENTER_ALIGNMENT);
        link.setCursor(new Cursor(Cursor.HAND_CURSOR));
        link.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(LoginFrame.this, "Đã click vào: " + text);
            }
        });
        return link;
    }

    private void addLoginActionListener(JButton loginBtn) {
        loginBtn.addActionListener(e -> {
            String phone = phoneField.getText().trim();
            String password = String.valueOf(passField.getPassword()).trim();

            boolean isPhoneValid = phone.matches("\\d{10}"); // chỉ chấp nhận 10 số
            boolean isPasswordValid = !password.isEmpty() && !password.equals(DEFAULT_PASSWORD);

            if (!isPhoneValid || !isPasswordValid) {
                JOptionPane.showMessageDialog(
                        this,
                        "Vui lòng nhập đúng thông tin",
                        "Lỗi đăng nhập",
                        JOptionPane.WARNING_MESSAGE
                );
            } else {
                // Mở DashboardFrame
                MainFrame dashboard = new MainFrame();
                dashboard.setVisible(true);

                // Đóng LoginFrame
                this.dispose();
            }
        });
    }

    // ===== CUSTOM BORDER CLASSES (Đã gộp lại) =====

    // Border chung có thể tùy biến màu
    static class RoundedInputBorder extends AbstractBorder {
        private final int radius;
        private final Color color;
        private static final BasicStroke STROKE = new BasicStroke(1); // Độ dày 1px

        RoundedInputBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(STROKE);
            g2.drawRoundRect(x, y, w - 1, h - 1, radius, radius);
        }
    }

    // Border cho Card (Bóng nhẹ)
    static class RoundedCardBorder extends AbstractBorder {
        private final int radius;
        RoundedCardBorder(int radius) { this.radius = radius; }
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            for (int i = 0; i < 4; i++) {
                g2.setColor(new Color(0, 0, 0, 12 - i * 3));
                g2.drawRoundRect(x + i, y + i, w - 1 - 2 * i, h - 1 - 2 * i, radius, radius);
            }
            g2.dispose();
        }
    }

    // Border cho Button (Fill nền)
    static class RoundedButtonBorder extends AbstractBorder {
        private final int radius;
        RoundedButtonBorder(int radius) { this.radius = radius; }
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(c.getBackground());
            g2.fillRoundRect(x, y, w, h, radius, radius);
            g2.dispose();
        }
    }

    // Custom UI cho JToggleButton Vai trò
    class RoleToggleUI extends BasicToggleButtonUI {
        @Override
        public void paint(Graphics g, JComponent c) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            JToggleButton btn = (JToggleButton) c;
            int arc = ROUNDED_RADIUS / 2;

            if (btn.isSelected()) {
                g2.setColor(ACTIVE_BG);
                g2.fillRoundRect(0, 0, btn.getWidth(), btn.getHeight(), arc, arc);
                g2.setColor(PRIMARY_COLOR);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(0, 0, btn.getWidth() - 1, btn.getHeight() - 1, arc, arc);
                btn.setForeground(PRIMARY_COLOR);
            } else {
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, btn.getWidth(), btn.getHeight(), arc, arc);
                g2.setColor(BORDER_COLOR);
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, btn.getWidth() - 1, btn.getHeight() - 1, arc, arc);
                btn.setForeground(Color.BLACK);
            }

            super.paint(g2, c);
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(LoginFrame::new);
    }
}
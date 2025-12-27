package ui;

import model.Account;
import service.IssueService;
import service.InvoiceServiceImpl;
import service.RoomService;
import service.RoomServiceImpl;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class EnterRoomCodeFrame extends JFrame {
    private final Account tenantAccount;
    private final IssueService issueService;
    private final InvoiceServiceImpl invoiceService;
    private final RoomService roomService; //
    private JTextField roomCodeField;

    private final Color PINK_MAIN = new Color(237, 77, 126);
    private final Color BG_LIGHT = new Color(252, 252, 252);
    private final Color TEXT_GRAY = new Color(130, 130, 130);

    public EnterRoomCodeFrame(Account tenantAccount, IssueService issueService, InvoiceServiceImpl invoiceService) {
        this.tenantAccount = tenantAccount;
        this.issueService = issueService;
        this.invoiceService = invoiceService;
        this.roomService = new RoomServiceImpl();

        setTitle("RoomManager - Nhập mã phòng");
        setSize(500, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBackground(BG_LIGHT);
        root.setBorder(new EmptyBorder(40, 50, 40, 50));

        // Nút quay lại
        JButton btnBack = new JButton("←  Quay lại đăng nhập");
        btnBack.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnBack.setForeground(TEXT_GRAY);
        btnBack.setBorderPainted(false);
        btnBack.setContentAreaFilled(false);
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBack.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
        root.add(btnBack);
        root.add(Box.createVerticalStrut(30));

        // Logo thương hiệu (Giữ nguyên phần vẽ bo góc của bạn)
        JLabel logoLabel = new JLabel("🏠", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PINK_MAIN);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                g2.setPaint(Color.WHITE);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        logoLabel.setPreferredSize(new Dimension(80, 80));
        logoLabel.setMaximumSize(new Dimension(80, 80));
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 35));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        root.add(logoLabel);

        root.add(Box.createVerticalStrut(40));

        // Card nhập liệu
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(40, 30, 40, 30));

        JLabel cardTitle = new JLabel("Nhập mã phòng");
        cardTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        cardTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(cardTitle);

        card.add(Box.createVerticalStrut(10));
        JLabel cardSub = new JLabel("<html><center>Vui lòng nhập mã phòng được cấp bởi chủ trọ để tiếp tục</center></html>");
        cardSub.setHorizontalAlignment(SwingConstants.CENTER);
        cardSub.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(cardSub);

        card.add(Box.createVerticalStrut(30));

        roomCodeField = new JTextField("Nhập mã phòng");
        roomCodeField.setHorizontalAlignment(JTextField.CENTER);
        roomCodeField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        // Xóa text gợi ý khi click vào
        roomCodeField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (roomCodeField.getText().equals("Nhập mã phòng")) roomCodeField.setText("");
            }
        });
        card.add(roomCodeField);

        card.add(Box.createVerticalStrut(25));

        JButton confirmBtn = new JButton("Xác nhận");
        confirmBtn.setBackground(PINK_MAIN);
        confirmBtn.setForeground(Color.WHITE);
        confirmBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        confirmBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        confirmBtn.addActionListener(e -> checkRoomCode());
        card.add(confirmBtn);

        root.add(card);
        add(root);
        setVisible(true);
    }

    private void checkRoomCode() {
        String inputCode = roomCodeField.getText().trim();
        if (inputCode.isEmpty() || inputCode.equals("Nhập mã phòng")) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã phòng");
            return;
        }

        new TenantDashboardFrame(
                tenantAccount,
                issueService,
                invoiceService,
                roomService,  // Tham số mới
                inputCode     // roomCode
        ).setVisible(true);

        dispose();
    }
}
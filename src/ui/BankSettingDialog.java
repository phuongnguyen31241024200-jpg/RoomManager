package ui;

import model.BankInfo;
import service.SettingService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class BankSettingDialog extends JDialog {
    private JComboBox<String> cbBank;
    private JTextField txtAccountNo, txtAccountName;
    private SettingService settingService;

    public BankSettingDialog(Frame parent, SettingService service) {
        super(parent, "Thiết lập QR Thanh Toán", true);
        this.settingService = service;

        // Cấu hình chung cho Dialog
        setLayout(new BorderLayout());
        setSize(450, 350);
        setLocationRelativeTo(parent);
        setResizable(false);

        // --- Tiêu đề ---
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(236, 72, 127));
        JLabel lblTitle = new JLabel("CÀI ĐẶT TÀI KHOẢN NHẬN TIỀN");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setBorder(new EmptyBorder(15, 0, 15, 0));
        headerPanel.add(lblTitle);
        add(headerPanel, BorderLayout.NORTH);

        // --- Nội dung nhập liệu ---
        JPanel mainPanel = new JPanel(new GridLayout(3, 2, 15, 20));
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        mainPanel.setBackground(Color.WHITE);

        Font labelFont = new Font("Segoe UI", Font.PLAIN, 14);
        Font inputFont = new Font("Segoe UI", Font.PLAIN, 14);

        // 1. Ngân hàng
        JLabel lblBank = new JLabel("Ngân hàng:");
        lblBank.setFont(labelFont);
        String[] banks = {"MB", "VCB", "TCB", "ICB", "BIDV", "VBA"};
        cbBank = new JComboBox<>(banks);
        cbBank.setFont(inputFont);

        // 2. Số tài khoản
        JLabel lblAccNo = new JLabel("Số tài khoản:");
        lblAccNo.setFont(labelFont);
        txtAccountNo = new JTextField();
        txtAccountNo.setFont(inputFont);

        // 3. Chủ tài khoản
        JLabel lblAccName = new JLabel("Chủ tài khoản:");
        lblAccName.setFont(labelFont);
        txtAccountName = new JTextField();
        txtAccountName.setFont(inputFont);

        mainPanel.add(lblBank);
        mainPanel.add(cbBank);
        mainPanel.add(lblAccNo);
        mainPanel.add(txtAccountNo);
        mainPanel.add(lblAccName);
        mainPanel.add(txtAccountName);

        add(mainPanel, BorderLayout.CENTER);

        // --- Nút bấm ---
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
        footerPanel.setBackground(Color.WHITE);

        JButton btnSave = new JButton("LƯU THÔNG TIN");
        btnSave.setPreferredSize(new Dimension(200, 40));
        btnSave.setBackground(new Color(34, 197, 94)); // Màu xanh lá cho thân thiện
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSave.setFocusPainted(false);
        btnSave.setBorderPainted(false);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnSave.addActionListener(e -> handleSave());
        footerPanel.add(btnSave);
        add(footerPanel, BorderLayout.SOUTH);

        // Load dữ liệu cũ lên
        loadExistingData();
    }

    private void loadExistingData() {
        BankInfo info = settingService.loadBankInfo();
        if (info != null) {
            cbBank.setSelectedItem(info.getBankId());
            txtAccountNo.setText(info.getAccountNo());
            txtAccountName.setText(info.getAccountName());
        }
    }

    private void handleSave() {
        String bankId = cbBank.getSelectedItem().toString();
        String accNo = txtAccountNo.getText().trim();
        String accName = txtAccountName.getText().trim();

        // Kiểm tra dữ liệu đầu vào
        if (accNo.isEmpty() || accName.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập đầy đủ Số tài khoản và Tên chủ tài khoản!",
                    "Thiếu thông tin",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        BankInfo newInfo = new BankInfo(bankId, accNo, accName);
        settingService.saveBankInfo(newInfo);

        JOptionPane.showMessageDialog(this, "Đã cập nhật thông tin thanh toán thành công!");
        dispose();
    }
}
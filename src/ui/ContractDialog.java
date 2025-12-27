package ui;

import service.ContractService;
import service.RoomServiceImpl;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class ContractDialog extends JDialog {

    private final Color PINK = new Color(190, 48, 105);
    private final Color BORDER_COLOR = new Color(209, 213, 219);
    private final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    private JTextField tfRoom, tfTenant, tfPhone, tfStart, tfEnd, tfDeposit, tfPrice;

    private RoomServiceImpl roomService;
    private ContractService contractService; //
    private RoomPanel roomPanel;
    private ContractView contractView;

    public ContractDialog(Window parent, ContractView contractView, RoomServiceImpl roomService, ContractService contractService, RoomPanel roomPanel) {
        super(parent, "Tạo hợp đồng mới", ModalityType.APPLICATION_MODAL);
        this.contractView = contractView;
        this.roomService = roomService;
        this.contractService = contractService;
        this.roomPanel = roomPanel;

        setUndecorated(true);
        setSize(550, 650);
        setLocationRelativeTo(parent);
        setResizable(false);
        setBackground(new Color(0, 0, 0, 0));
        setShape(new RoundRectangle2D.Double(0, 0, 550, 650, 40, 40));

        JPanel root = new JPanel(new BorderLayout(0, 20));
        root.setBorder(new EmptyBorder(30, 40, 30, 40));
        root.setBackground(Color.WHITE);
        add(root);

        // ===== TITLE =====
        JLabel title = new JLabel("Tạo hợp đồng mới");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(17, 24, 39));
        root.add(title, BorderLayout.NORTH);

        // ===== FORM (Phần UI giữ nguyên như cũ của bạn) =====
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.weightx = 1;

        gbc.gridy = 0; tfRoom = createSingleField(form, gbc, "Mã phòng (Nhập đúng mã phòng đã có)");
        gbc.gridy = 1; tfTenant = createSingleField(form, gbc, "Tên người thuê");
        gbc.gridy = 2; tfPhone = createSingleField(form, gbc, "Số điện thoại người thuê");

        gbc.gridy = 3;
        tfStart = createFieldOnly(); tfEnd = createFieldOnly();
        JPanel rowDate = new JPanel(new GridLayout(1, 2, 20, 0));
        rowDate.setOpaque(false);
        rowDate.add(wrapWithLabel("Ngày bắt đầu (dd/MM/yyyy)", tfStart));
        rowDate.add(wrapWithLabel("Ngày kết thúc (dd/MM/yyyy)", tfEnd));
        form.add(rowDate, gbc);

        gbc.gridy = 4;
        tfDeposit = createFieldOnly(); tfPrice = createFieldOnly();
        JPanel rowMoney = new JPanel(new GridLayout(1, 2, 20, 0));
        rowMoney.setOpaque(false);
        rowMoney.add(wrapWithLabel("Tiền cọc (VNĐ)", tfDeposit));
        rowMoney.add(wrapWithLabel("Tiền phòng / tháng (VNĐ)", tfPrice));
        form.add(rowMoney, gbc);

        root.add(form, BorderLayout.CENTER);

        // ===== ACTIONS =====
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        actions.setOpaque(false);
        JButton btnCancel = new JButton("Hủy");
        styleCancelButton(btnCancel);
        JButton btnCreate = new JButton("Tạo hợp đồng");
        stylePrimaryButton(btnCreate);

        actions.add(btnCancel);
        actions.add(btnCreate);
        root.add(actions, BorderLayout.SOUTH);

        btnCancel.addActionListener(e -> dispose());

        // ===== LOGIC QUAN TRỌNG NHẤT =====
        btnCreate.addActionListener(e -> {
            String roomCode = tfRoom.getText().trim();
            String tenantNameInput = tfTenant.getText().trim();
            String phoneInput = tfPhone.getText().trim();

            if(roomCode.isEmpty() || tenantNameInput.isEmpty() || phoneInput.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đủ Mã phòng, Tên và SĐT!");
                return;
            }

            // Hàm này bây giờ sẽ gọi Service để lưu file .dat (Nếu bạn đã sửa ContractView như tôi chỉ ở trên)
            contractView.addContract(roomCode, tenantNameInput, phoneInput, tfStart.getText(), tfEnd.getText(), tfDeposit.getText(), tfPrice.getText());

            // BƯỚC 2: Cập nhật sơ đồ phòng (Đổi màu đỏ)
            boolean found = false;
            for (model.Room r : roomService.getRooms()) {
                if (r.getRoomCode().equalsIgnoreCase(roomCode)) {
                    model.Tenant newTenant = new model.Tenant(phoneInput, "123456", tenantNameInput);
                    roomService.assignTenant(r.getRoomId(), newTenant);
                    found = true;
                    break;
                }
            }

            if (found) {
                if(roomPanel != null) roomPanel.loadRooms(roomService.getRooms());
                JOptionPane.showMessageDialog(this, "Tạo thành công và dữ liệu đã được ghi xuống file!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Hợp đồng đã lưu nhưng không tìm thấy mã phòng để đổi màu sơ đồ!");
                dispose();
            }
        });
    }

    // --- Giữ nguyên các hàm style cũ của bạn ---
    private JTextField createSingleField(JPanel form, GridBagConstraints gbc, String labelText) {
        JTextField field = createFieldOnly();
        form.add(wrapWithLabel(labelText, field), gbc);
        return field;
    }

    private JTextField createFieldOnly() {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(0, 44));
        field.setFont(MAIN_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(new RoundBorder(40, BORDER_COLOR), new EmptyBorder(0, 14, 0, 14)));
        return field;
    }

    private JPanel wrapWithLabel(String labelText, JTextField field) {
        JPanel group = new JPanel(new BorderLayout(0, 6)); group.setOpaque(false);
        JLabel label = new JLabel(labelText); label.setFont(MAIN_FONT);
        label.setForeground(new Color(55, 65, 81));
        group.add(label, BorderLayout.NORTH); group.add(field, BorderLayout.CENTER);
        return group;
    }

    private void styleCancelButton(JButton b) {
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setPreferredSize(new Dimension(90, 44));
        b.setBackground(new Color(243, 244, 246));
        b.setBorder(new RoundBorder(40, BORDER_COLOR));
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void stylePrimaryButton(JButton b) {
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setPreferredSize(new Dimension(170, 44));
        b.setBackground(PINK); b.setForeground(Color.WHITE);
        b.setFocusPainted(false); b.setContentAreaFilled(false);
        b.setBorderPainted(false); b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(c.getBackground());
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 40, 40);
                g2.dispose();
                super.paint(g, c);
            }
        });
    }

    class RoundBorder extends javax.swing.border.AbstractBorder {
        private final int r; private final Color c;
        RoundBorder(int r, Color c) { this.r = r; this.c = c; }
        @Override
        public void paintBorder(Component comp, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(c); g2.drawRoundRect(x, y, w - 1, h - 1, r, r); g2.dispose();
        }
    }
}
package ui;

import model.Income;
import model.Expense;
import service.FinanceService;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddTransactionDialog extends JDialog {

    private final Color PINK_MAIN = new Color(237, 77, 126);
    private final Color TEXT_DARK = new Color(50, 50, 50);
    private final Color BORDER_GRAY = new Color(230, 230, 230);
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private final int ARC = 40;

    public AddTransactionDialog(FinanceService financeService, FinancePanel panel) {
        setUndecorated(true);
        setSize(400, 550);
        setLocationRelativeTo(null);
        setModal(true);
        setResizable(false);

        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), ARC, ARC));

        JPanel wrapper = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), ARC, ARC);
                g2.dispose();
            }
        };
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(25, 30, 30, 30));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel lblTitle = new JLabel("Thêm giao dịch mới");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(TEXT_DARK);

        JButton btnClose = new JButton("X");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnClose.setForeground(Color.GRAY);
        btnClose.setFocusPainted(false);
        btnClose.setBorderPainted(false);
        btnClose.setContentAreaFilled(false);
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> dispose());

        header.add(lblTitle, BorderLayout.WEST);
        header.add(btnClose, BorderLayout.EAST);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        JComboBox<String> typeBox = new JComboBox<>(new String[]{"Thu nhập", "Chi tiêu"});
        styleComponent(typeBox);

        JTextField txtDesc = new JTextField();
        styleComponent(txtDesc);

        JTextField txtAmount = new JTextField();
        styleComponent(txtAmount);

        JTextField txtDate = new JTextField(sdf.format(new Date()));
        styleComponent(txtDate);

        contentPanel.add(createLabel("Loại giao dịch:"));
        contentPanel.add(typeBox);
        contentPanel.add(Box.createVerticalStrut(12));

        contentPanel.add(createLabel("Mô tả:"));
        contentPanel.add(txtDesc);
        contentPanel.add(Box.createVerticalStrut(12));

        contentPanel.add(createLabel("Số tiền (VNĐ):"));
        contentPanel.add(txtAmount);
        contentPanel.add(Box.createVerticalStrut(12));

        contentPanel.add(createLabel("Ngày (dd/MM/yyyy):"));
        contentPanel.add(txtDate);
        contentPanel.add(Box.createVerticalStrut(25));

        JButton btnSave = new JButton("Xác nhận lưu") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PINK_MAIN);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), ARC, ARC);
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };

        btnSave.setPreferredSize(new Dimension(Integer.MAX_VALUE, 45));
        btnSave.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnSave.setContentAreaFilled(false);
        btnSave.setBorderPainted(false);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnSave.addActionListener(e -> {
            try {
                String type = (String) typeBox.getSelectedItem();
                String desc = txtDesc.getText().trim();
                String amountStr = txtAmount.getText().trim();
                String dateStr = txtDate.getText().trim();

                if (desc.isEmpty() || amountStr.isEmpty()) {
                    showError("Vui lòng nhập mô tả và số tiền!");
                    return;
                }

                double amount = Double.parseDouble(amountStr);
                Date selectedDate = sdf.parse(dateStr);

                // Gửi dữ liệu vào Service
                if ("Thu nhập".equals(type)) {
                    financeService.addIncome(new Income(desc, amount, selectedDate));
                } else {
                    financeService.addExpense(new Expense(desc, amount, selectedDate));
                }

                // Cập nhật lại bảng ở FinancePanel
                if (panel != null) {
                    panel.refreshData();
                }

                dispose(); // Đóng dialog
            } catch (NumberFormatException ex) {
                showError("Số tiền phải là số hợp lệ!");
            } catch (ParseException ex) {
                showError("Ngày không đúng định dạng dd/MM/yyyy!");
            } catch (Exception ex) {
                showError("Lỗi: " + ex.getMessage());
            }
        });

        contentPanel.add(btnSave);
        wrapper.add(header, BorderLayout.NORTH);
        wrapper.add(contentPanel, BorderLayout.CENTER);
        add(wrapper);
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(TEXT_DARK);
        lbl.setBorder(new EmptyBorder(0, 10, 5, 0));
        return lbl;
    }

    private void styleComponent(JComponent comp) {
        comp.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comp.setPreferredSize(new Dimension(Integer.MAX_VALUE, 42));
        comp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        comp.setBackground(Color.WHITE);
        comp.setBorder(new RoundedBorder(ARC, BORDER_GRAY));
        if (comp instanceof JComboBox) comp.setOpaque(false);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    class RoundedBorder extends AbstractBorder {
        private final int radius;
        private final Color color;
        RoundedBorder(int radius, Color color) { this.radius = radius; this.color = color; }
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }
        @Override
        public Insets getBorderInsets(Component c) { return new Insets(5, 15, 5, 15); }
    }
}
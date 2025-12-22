import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ContractView extends JPanel {
    private Main main;
    private DefaultTableModel model;
    private JTable table;
    private final Color PINK_THEME = new Color(236, 72, 127);
    private final Color TEXT_DARK = new Color(55, 65, 81);
    private final Color BG_LIGHT = new Color(249, 250, 251);

    public ContractView(Main main) {
        this.main = main;
        setLayout(new BorderLayout(0, 20));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(30, 40, 30, 40));

        // --- HEADER ---
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("<html><b style='font-size:20px; color:#111827;'>Quản lý hợp đồng</b><br>" +
                "<font color='#6B7280' style='font-size:12px;'>Danh sách hợp đồng thuê phòng</font></html>");

        JButton btnCreate = createStyledButton("+ Tạo hợp đồng", PINK_THEME);
        btnCreate.addActionListener(e -> showContractPopup(null));

        header.add(title, BorderLayout.WEST);
        header.add(btnCreate, BorderLayout.EAST);

        // --- DỮ LIỆU BẢNG ---
        String[] cols = {"Phòng", "Người thuê", "Ngày bắt đầu", "Ngày kết thúc", "Tiền cọc", "Tiền phòng", "Trạng thái", "Thao tác"};
        model = new DefaultTableModel(new Object[][]{
                {"Phòng 101", "Trần Thị Lan", "1/1/2024", "1/1/2025", "7.000.000 đ", "3.500.000 đ", "Hết hạn", ""},
                {"Phòng 102", "Lê Hoàng Nam", "15/3/2024", "15/3/2025", "6.400.000 đ", "3.200.000 đ", "Hết hạn", ""},
                {"Phòng 103", "Phạm Minh Tuấn", "1/6/2024", "1/12/2024", "7.600.000 đ", "3.800.000 đ", "Hết hạn", ""}
        }, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        styleTableComponent(table);

        // Click Sửa/Xóa
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (col == 7 && row != -1) {
                    Rectangle rect = table.getCellRect(row, col, false);
                    int localX = e.getX() - rect.x;
                    if (localX < rect.width / 2) showContractPopup(row);
                    else if (confirmDelete()) model.removeRow(row);
                }
            }
        });

        // --- BO VIỀN TRÒN CHO BẢNG ---
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(Color.WHITE);

        // Panel bao quanh tạo hiệu ứng bo tròn góc 15px
        JPanel tableWrapper = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(243, 244, 246)); // Màu viền xám cực nhạt
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                g2.dispose();
            }
        };
        tableWrapper.setOpaque(false);
        tableWrapper.add(sp);
        tableWrapper.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

        add(header, BorderLayout.NORTH);
        add(tableWrapper, BorderLayout.CENTER);
    }

    private void styleTableComponent(JTable t) {
        t.setRowHeight(65);
        t.setShowGrid(false);
        t.setIntercellSpacing(new Dimension(0, 0));
        t.setSelectionBackground(new Color(249, 250, 251));

        // Header bảng
        JTableHeader header = t.getTableHeader();
        header.setPreferredSize(new Dimension(0, 50));
        header.setBackground(new Color(252, 253, 254));
        header.setForeground(new Color(156, 163, 175));
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));

        // Tắt viền mặc định của header
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                l.setBackground(new Color(252, 253, 254));
                l.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(243, 244, 246)));
                l.setHorizontalAlignment(column == 0 || column == 1 ? LEFT : CENTER);
                if(column == 0) l.setText("<html><div style='padding-left:15px;'>"+value+"</div></html>");
                return l;
            }
        });

        // Renderer ô dữ liệu
        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hasF, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, isS, hasF, r, c);
                l.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(243, 244, 246)));
                l.setForeground(new Color(75, 85, 99));
                l.setFont(new Font("Segoe UI", Font.PLAIN, 13));

                // Căn chỉnh
                if (c == 0) { // Cột Phòng
                    l.setHorizontalAlignment(LEFT);
                    l.setText("<html><div style='padding-left:15px;'><font color='#9CA3AF'>📄</font> &nbsp;<b>" + v + "</b></div></html>");
                } else if (c == 1) { // Người thuê
                    l.setHorizontalAlignment(LEFT);
                    l.setText("<html><div style='padding-left:5px;'>" + v + "</div></html>");
                } else if (c == 6) { // Trạng thái
                    l.setHorizontalAlignment(CENTER);
                    l.setText("<html><div style='background-color:#FEE2E2; color:#EF4444; padding:3px 10px; border-radius:12px; font-weight:bold; font-size:10px;'>Hết hạn</div></html>");
                } else if (c == 7) { // Thao tác
                    l.setHorizontalAlignment(CENTER);
                    l.setText("<html><font color='#9CA3AF' size='5'>✎</font> &nbsp;&nbsp;&nbsp; <font color='#F87171' size='5'>🗑</font></html>");
                } else {
                    l.setHorizontalAlignment(CENTER);
                }

                l.setBackground(isS ? new Color(249, 250, 251) : Color.WHITE);
                return l;
            }
        });
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(160, 40));
        return b;
    }

    private boolean confirmDelete() {
        return JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    private void showContractPopup(Integer row) {
        // Code mở popup sửa/tạo ở đây
    }
}

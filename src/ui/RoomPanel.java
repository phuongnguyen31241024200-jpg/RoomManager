package ui;

import model.Room;
import service.RoomServiceImpl;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.List;
import java.util.stream.Collectors;

public class RoomPanel extends JPanel {

    private final RoomServiceImpl service;
    private final JPanel container;
    private JTextField txtSearch;
    private JLabel subtitle;
    private List<Room> allRooms;

    // ===== UI CONST =====
    private static final int RADIUS = 20;
    private static final Color BG = new Color(255, 245, 247);
    private static final Color BORDER = new Color(230, 230, 230);
    private static final Color PRIMARY = new Color(244, 93, 145);

    private static final String SEARCH_HINT =
            "Tìm kiếm phòng, mã phòng, người thuê...";

    public RoomPanel(RoomServiceImpl service) {
        this.service = service;
        this.allRooms = service.getRooms();

        setLayout(new BorderLayout());
        setBackground(BG);

        // ===== HEADER =====
        JPanel headerArea = new JPanel();
        headerArea.setLayout(new BoxLayout(headerArea, BoxLayout.Y_AXIS));
        headerArea.setOpaque(false);
        headerArea.setBorder(new EmptyBorder(25, 30, 15, 30));

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);

        JPanel titleGroup = new JPanel();
        titleGroup.setLayout(new BoxLayout(titleGroup, BoxLayout.Y_AXIS));
        titleGroup.setOpaque(false);

        JLabel title = new JLabel("Quản lý phòng");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(100, 80, 85));

        subtitle = new JLabel("Danh sách các phòng");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(Color.GRAY);
        subtitle.setBorder(new EmptyBorder(5, 0, 15, 0));

        titleGroup.add(title);
        titleGroup.add(subtitle);

        JButton btnAdd = createPastelButton("Thêm phòng", new Color(237, 77, 126));
        btnAdd.addActionListener(e ->
                new CreateRoomDialog(
                        (JFrame) SwingUtilities.getWindowAncestor(this),
                        service, this
                ).setVisible(true)
        );

        topRow.add(titleGroup, BorderLayout.WEST);
        topRow.add(btnAdd, BorderLayout.EAST);

        headerArea.add(topRow);
        headerArea.add(Box.createVerticalStrut(10));
        headerArea.add(createSearchBar());

        add(headerArea, BorderLayout.NORTH);

        // ===== LIST =====
        container = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 25));
        container.setOpaque(false);

        JScrollPane scroll = new JScrollPane(container);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        add(scroll, BorderLayout.CENTER);

        loadRooms(allRooms);
    }


    private JPanel createSearchBar() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setMaximumSize(new Dimension(820, 48));

        JPanel searchBox = new JPanel(new BorderLayout());
        searchBox.setBackground(Color.WHITE);
        searchBox.setBorder(new RoundedBorder(RADIUS, BORDER));
        searchBox.setPreferredSize(new Dimension(820, 48));

        JLabel icon = new JLabel("");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        icon.setBorder(new EmptyBorder(0, 15, 0, 8));
        icon.setForeground(Color.GRAY);

        txtSearch = new JTextField();
        txtSearch.setBorder(new EmptyBorder(0, 5, 0, 15));
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtSearch.setOpaque(false);

        // ===== PLACEHOLDER =====
        txtSearch.setText(SEARCH_HINT);
        txtSearch.setForeground(Color.LIGHT_GRAY);

        txtSearch.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                searchBox.setBorder(new RoundedBorder(RADIUS, PRIMARY));
                if (txtSearch.getText().equals(SEARCH_HINT)) {
                    txtSearch.setText("");
                    txtSearch.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                searchBox.setBorder(new RoundedBorder(RADIUS, BORDER));
                if (txtSearch.getText().isEmpty()) {
                    txtSearch.setText(SEARCH_HINT);
                    txtSearch.setForeground(Color.LIGHT_GRAY);
                }
            }
        });

        // ===== FILTER LOGIC (REALTIME) =====
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }

            private void filter() {
                String q = txtSearch.getText().trim().toLowerCase();

                if (q.isEmpty() || q.equals(SEARCH_HINT.toLowerCase())) {
                    loadRooms(allRooms);
                    return;
                }

                List<Room> filtered = allRooms.stream()
                        .filter(r ->
                                r.getRoomCode().toLowerCase().contains(q)
                                        || (r.getTenant() != null
                                        && r.getTenant().getTenantName()
                                        .toLowerCase().contains(q))
                        )
                        .collect(Collectors.toList());

                loadRooms(filtered);
            }
        });

        searchBox.add(icon, BorderLayout.WEST);
        searchBox.add(txtSearch, BorderLayout.CENTER);
        wrapper.add(searchBox, BorderLayout.WEST);

        return wrapper;
    }

    private JButton createPastelButton(String text, Color bg) {
        JButton btn = new JButton(text) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? bg.brighter() : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(160, 45));
        return btn;
    }


    public void refreshData() {
        allRooms = service.getRooms();
        subtitle.setText("Danh sách các phòng");
        loadRooms(allRooms);
    }

    public void loadRooms(List<Room> rooms) {
        container.removeAll();
        if (rooms != null) {
            for (Room r : rooms) {
                container.add(new RoomCardPanel(r, service, this));
            }
        }
        container.revalidate();
        container.repaint();
    }

    static class RoundedBorder extends AbstractBorder {
        private final int radius;
        private final Color color;

        RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }

        public void paintBorder(Component c, Graphics g,
                                int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, w - 1, h - 1, radius, radius);
        }
    }
}

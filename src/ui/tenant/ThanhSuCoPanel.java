package ui.tenant;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ThanhSuCoPanel extends JPanel {

    public ThanhSuCoPanel(JComponent content) {
        setLayout(new BorderLayout());
        setOpaque(false);

        // Tạo khoảng cách (margin) giữa viền xám và card nội dung bên trong
        // 10px là con số chuẩn để lộ phần nền trắng của card bên trong đẹp nhất
        setBorder(new EmptyBorder(10, 12, 10, 12));

        add(content, BorderLayout.CENTER);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 1. Màu viền xám cực nhạt giúp card trông nổi khối tự nhiên
        g2.setColor(new Color(238, 238, 238));

        // 2. Độ dày mảnh (1.2f) tạo cảm giác hiện đại (Modern UI)
        g2.setStroke(new BasicStroke(1.2f));

        // 3. Vẽ viền bo góc (Radius 25 để khớp với các thiết kế bo tròn mạnh)
        g2.drawRoundRect(
                1,
                1,
                getWidth() - 3, // Trừ 3 để đường line không bị sát mép panel
                getHeight() - 3,
                25,
                25
        );

        g2.dispose();
    }
}
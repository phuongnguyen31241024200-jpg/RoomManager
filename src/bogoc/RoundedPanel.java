package bogoc;

import javax.swing.*;
import java.awt.*;

public class RoundedPanel extends JPanel {
    private final int radius;
    private final Color bg;

    public RoundedPanel(int radius, Color bg) {
        this.radius = radius;
        this.bg = bg;
        setOpaque(false); // Bắt buộc để thấy được góc bo phía sau
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create(); // Sử dụng bản sao để không ảnh hưởng các component khác
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(bg);
        // Vẽ hình chữ nhật bo góc, trừ đi 1 pixel ở width/height để viền không bị cắt
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);

        g2.dispose(); // Giải phóng tài nguyên đồ họa

        // Không gọi super.paintComponent(g) sau khi vẽ vì JPanel mặc định sẽ đè màu nền vuông lên
        // super.paintComponent(g);
    }
}
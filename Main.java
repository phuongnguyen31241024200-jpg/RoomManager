import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {

    private JPanel cardPanel;
    private CardLayout cardLayout;
    private SidebarComponent sidebar;

    // Các view
    private SuCoView suCoView;
    private ContractView contractView;
    private ThongBaoView thongBaoView;

    public Main() {
        setTitle("Hệ thống Quản lý Phòng trọ");
        setSize(1300, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // ===== 1. CardLayout cho nội dung =====
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // ===== 2. Khởi tạo các màn hình =====
        suCoView = new SuCoView(this);
        contractView = new ContractView(this);
        thongBaoView = new ThongBaoView(this);

        // ===== 3. Add vào CardPanel (ID phải khớp Sidebar) =====
        cardPanel.add(suCoView, "SuCo");
        cardPanel.add(contractView, "HopDong");
        cardPanel.add(thongBaoView, "ThongBao");

        // ===== 4. Sidebar (mặc định đang ở Sự cố) =====
        sidebar = new SidebarComponent(this, "Sự cố");

        // ===== 5. Layout tổng =====
        setLayout(new BorderLayout());
        add(sidebar, BorderLayout.WEST);
        add(cardPanel, BorderLayout.CENTER);

        // ===== 6. View mặc định =====
        showView("SuCo");
    }

    // ===== Hàm chuyển trang =====
    public void showView(String viewName) {
        cardLayout.show(cardPanel, viewName);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}

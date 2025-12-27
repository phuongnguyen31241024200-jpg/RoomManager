package ui.tenant;

import model.Account;
import model.Tenant;
import service.IssueService;
import service.InvoiceService;
import service.RoomService;
import ui.RoundedBorder;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TenantHomePanel extends JPanel {

    // ===== COLORS =====
    private final Color PINK_MAIN = new Color(237, 77, 126);
    private final Color PINK_PASTEL = new Color(255, 235, 240);
    private final Color PINK_BORDER = new Color(255, 180, 200);
    private final Color LIGHT_BG = new Color(250, 250, 251);

    private final IssueService issueService;
    private final InvoiceService invoiceService;
    private final Account tenantAccount;

    private JLabel lblIssueSummary;
    private JLabel lblPaymentAmount;
    private JLabel lblPaymentStatus;
    private JLabel lblWelcomeRoom;
    private JLabel lblHello;

    public TenantHomePanel(Account account, IssueService issueService,
                           InvoiceService invoiceService, RoomService roomService) {

        this.tenantAccount = account;
        this.issueService = issueService;
        this.invoiceService = invoiceService;

        setLayout(new BorderLayout());
        setBackground(LIGHT_BG);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setOpaque(false);

        main.add(createWelcomeCard());
        main.add(Box.createVerticalStrut(20));
        main.add(createPaymentCard());
        main.add(Box.createVerticalStrut(20));
        main.add(createIssueSummaryCard());

        add(main, BorderLayout.NORTH);
        refreshData();
    }

    // ================= LOGIC GIỮ NGUYÊN =================
    public void refreshData() {
        if (tenantAccount == null) return;

        String roomCode = "";
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window instanceof JFrame) {
            String title = ((JFrame) window).getTitle();
            if (title.contains("Phòng:")) {
                try {
                    int start = title.indexOf("Phòng:") + 6;
                    int end = title.contains(")") ? title.indexOf(")") : title.length();
                    roomCode = title.substring(start, end).trim();
                } catch (Exception ignored) {}
            }
        }

        if (!roomCode.isEmpty()) {
            lblHello.setText("Xin chào, khách thuê phòng " + roomCode);
            lblWelcomeRoom.setText("Phòng " + roomCode + " • Đang trực tuyến");
        } else if (tenantAccount instanceof Tenant) {
            lblHello.setText("Xin chào, " + ((Tenant) tenantAccount).getUsername());
            lblWelcomeRoom.setText("Khách thuê • Đang trực tuyến");
        }

        if (invoiceService != null && !roomCode.isEmpty()) {
            double totalUnpaid = invoiceService.getTotalUnpaidAmount(roomCode);
            lblPaymentAmount.setText(formatMoney(totalUnpaid));

            if (totalUnpaid > 0) {
                lblPaymentStatus.setText("Bạn có hóa đơn chưa thanh toán");
                lblPaymentStatus.setForeground(new Color(231, 76, 60));
            } else {
                lblPaymentStatus.setText("Đã hoàn thành nghĩa vụ thanh toán");
                lblPaymentStatus.setForeground(new Color(46, 204, 113));
            }
        }

        if (issueService != null && lblIssueSummary != null && tenantAccount instanceof Tenant) {
            String username = ((Tenant) tenantAccount).getUsername();
            long count = issueService.getAllIssues().stream()
                    .filter(i -> i.getTenant() != null &&
                            username.equals(i.getTenant().getUsername()))
                    .count();
            lblIssueSummary.setText("Sự cố đã gửi: " + count);
        }
    }

    // ================= UI ONLY =================

    private JPanel createWelcomeCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(PINK_MAIN);
        card.setBorder(new RoundedBorder(24, PINK_MAIN));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        card.setOpaque(true);

        lblHello = new JLabel("Đang tải...");
        lblHello.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblHello.setForeground(Color.WHITE);

        lblWelcomeRoom = new JLabel("Vui lòng chờ...");
        lblWelcomeRoom.setForeground(new Color(255, 255, 255, 210));

        JPanel text = new JPanel();
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.setOpaque(false);
        text.setBorder(new EmptyBorder(20, 25, 20, 25));
        text.add(lblHello);
        text.add(lblWelcomeRoom);

        card.add(text, BorderLayout.WEST);
        return card;
    }

    private JPanel createPaymentCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(PINK_PASTEL);
        card.setBorder(new RoundedBorder(20, PINK_BORDER));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        card.setOpaque(true);

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);
        info.setBorder(new EmptyBorder(18, 22, 18, 22));

        JLabel title = new JLabel("Số dư cần thanh toán");
        title.setForeground(new Color(120, 80, 100));

        lblPaymentAmount = new JLabel("0 đ");
        lblPaymentAmount.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblPaymentAmount.setForeground(PINK_MAIN);

        lblPaymentStatus = new JLabel("Đang kiểm tra...");

        info.add(title);
        info.add(Box.createVerticalStrut(4));
        info.add(lblPaymentAmount);
        info.add(lblPaymentStatus);

        card.add(info, BorderLayout.CENTER);
        return card;
    }

    private JPanel createIssueSummaryCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(PINK_PASTEL);
        card.setBorder(new RoundedBorder(18, PINK_BORDER));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));
        card.setOpaque(true);

        lblIssueSummary = new JLabel("Sự cố đã gửi: 0");
        lblIssueSummary.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblIssueSummary.setBorder(new EmptyBorder(15, 22, 15, 22));
        lblIssueSummary.setForeground(new Color(90, 60, 80));

        card.add(lblIssueSummary, BorderLayout.WEST);
        return card;
    }

    private String formatMoney(double amount) {
        return String.format("%,.0f đ", amount);
    }
}

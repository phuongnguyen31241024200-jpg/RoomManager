package model;

public enum InvoiceStatus {
    UNPAID("Chưa đóng"),
    PAID("Đã thanh toán"),
    OVERDUE("Quá hạn");

    private final String display;

    InvoiceStatus(String display) {
        this.display = display;
    }

    public String getDisplay() {
        return display;
    }
}
package model;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class Invoice implements Serializable {
    private static final long serialVersionUID = 1L;

    private String invoiceId;
    private String month;
    private double totalAmount;
    private InvoiceStatus status;
    private Date createdAt;
    private String roomId;
    private String roomCode;
    private String tenantName;
    private String tenantId;

    // --- CÁC BIẾN MỚI THÊM VÀO ĐỂ HIỆN CHI TIẾT ---
    private double roomPrice;      // Tiền phòng
    private double electricityFee; // Tiền điện
    private double waterFee;       // Tiền nước
    private double serviceFee;     // Tiền dịch vụ (rác, wifi...)

    public Invoice() {
        this.invoiceId = UUID.randomUUID().toString();
        this.status = InvoiceStatus.UNPAID;
        this.createdAt = new Date();
    }

    // Constructor đầy đủ để dùng trong CreateInvoiceDialog
    public Invoice(String roomCode, String tenantName, String month, double totalAmount,
                   String roomId, String tenantId, double roomPrice,
                   double electricityFee, double waterFee, double serviceFee) {
        this();
        this.roomCode = roomCode;
        this.tenantName = tenantName;
        this.month = month;
        this.totalAmount = totalAmount;
        this.roomId = roomId;
        this.tenantId = tenantId;
        this.roomPrice = roomPrice;
        this.electricityFee = electricityFee;
        this.waterFee = waterFee;
        this.serviceFee = serviceFee;
    }

    public void markAsPaid(Payment payment) {
        this.status = InvoiceStatus.PAID;
    }

    // --- CÁC HÀM GETTER/SETTER CŨ ---
    public String getInvoiceId() { return invoiceId; }
    public void setInvoiceId(String invoiceId) { this.invoiceId = invoiceId; }
    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
    public String getRoomCode() { return roomCode; }
    public void setRoomCode(String roomCode) { this.roomCode = roomCode; }
    public String getTenantName() { return tenantName; }
    public void setTenantName(String tenantName) { this.tenantName = tenantName; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public InvoiceStatus getStatus() { return status; }
    public void setStatus(InvoiceStatus status) { this.status = status; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    // --- CÁC HÀM GETTER/SETTER MỚI (Để Dialog gọi không bị lỗi) ---
    public double getRoomPrice() { return roomPrice; }
    public void setRoomPrice(double roomPrice) { this.roomPrice = roomPrice; }
    public double getElectricityFee() { return electricityFee; }
    public void setElectricityFee(double electricityFee) { this.electricityFee = electricityFee; }
    public double getWaterFee() { return waterFee; }
    public void setWaterFee(double waterFee) { this.waterFee = waterFee; }
    public double getServiceFee() { return serviceFee; }
    public void setServiceFee(double serviceFee) { this.serviceFee = serviceFee; }
}
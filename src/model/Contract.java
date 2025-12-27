package model;

import java.io.Serializable;
import java.util.Date;

public class Contract implements Serializable {
    private static final long serialVersionUID = 1L;

    private String contractId;
    private String roomCode;     // Mã phòng
    private String tenantId;     // Tên người thuê (Dùng để hiển thị)
    private String tenantPhone;  // Số điện thoại
    private Date startDate;
    private Date endDate;
    private double deposit;
    private double price;        // Tiền phòng
    private ContractStatus status;

    // Constructor đầy đủ tham số để khớp với bảng hiển thị
    public Contract(String contractId, String roomCode, String tenantId, String tenantPhone,
                    Date startDate, Date endDate, double deposit, double price, ContractStatus status) {
        this.contractId = contractId;
        this.roomCode = roomCode;
        this.tenantId = tenantId;
        this.tenantPhone = tenantPhone;
        this.startDate = startDate;
        this.endDate = endDate;
        this.deposit = deposit;
        this.price = price;
        this.status = status;
    }

    //  Các hàm logic
    public boolean isExpired() {
        if (endDate == null) return false;
        return new Date().after(endDate);
    }

    //  Getters và Setters
    public String getRoomCode() { return roomCode; }
    public void setRoomCode(String roomCode) { this.roomCode = roomCode; }

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

    public String getTenantPhone() { return tenantPhone; }
    public void setTenantPhone(String tenantPhone) { this.tenantPhone = tenantPhone; }

    public String getContractId() { return contractId; }
    public void setContractId(String contractId) { this.contractId = contractId; }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }

    public double getDeposit() { return deposit; }
    public void setDeposit(double deposit) { this.deposit = deposit; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public ContractStatus getStatus() { return status; }
    public void setStatus(ContractStatus status) { this.status = status; }
}
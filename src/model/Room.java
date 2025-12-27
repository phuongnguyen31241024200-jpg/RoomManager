package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;


public class Room implements Serializable {
    private static final long serialVersionUID = 1L;

    private String roomId;
    private String roomCode;
    private double price;
    private RoomStatus status;

    //  Các đối tượng liên quan
    private Tenant tenant;     // Người thuê
    private Contract contract; // Hợp đồng (Bắt buộc để check ngày hết hạn)

    //  Danh sách tiện ích đã chọn
    private List<String> utilities = new ArrayList<>();

    //  Đơn giá dịch vụ mặc định
    private double electricityPrice = 3500;
    private double waterPrice = 15000;
    private double parkingPrice = 100000;
    private double laundryPrice = 0;
    private double cleaningPrice = 20000;

    // 1. Constructor mặc định
    public Room() {
        this.roomId = UUID.randomUUID().toString();
        this.status = RoomStatus.AVAILABLE;
        this.utilities = new ArrayList<>();
    }

    // 2. Constructor 2 tham số (Dùng cho một số logic cũ)
    public Room(String roomCode, double price) {
        this();
        this.roomCode = roomCode;
        this.price = price;
    }


    public Room(String roomCode, String type, double price) {
        this();
        this.roomCode = roomCode;
        // Nếu ông có biến 'type' thì gán vào, nếu không thì cứ để vậy để tránh lỗi compile
        this.price = price;
    }

    public boolean isExpired() {
        if (status == RoomStatus.AVAILABLE || contract == null || contract.getEndDate() == null) {
            return false;
        }
        return new Date().after(contract.getEndDate());
    }

    public Contract getContract() { return contract; }
    public void setContract(Contract contract) { this.contract = contract; }

    public String getRoomId() { return roomId; }
    public String getRoomCode() { return roomCode; }
    public void setRoomCode(String roomCode) { this.roomCode = roomCode; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public RoomStatus getStatus() { return status; }
    public void setStatus(RoomStatus status) { this.status = status; }
    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }

    public List<String> getUtilities() {
        if (utilities == null) utilities = new ArrayList<>();
        return utilities;
    }
    public void setUtilities(List<String> utilities) { this.utilities = utilities; }

    public double getElectricityPrice() { return electricityPrice; }
    public void setElectricityPrice(double e) { this.electricityPrice = e; }
    public double getWaterPrice() { return waterPrice; }
    public void setWaterPrice(double w) { this.waterPrice = w; }
    public double getParkingPrice() { return parkingPrice; }
    public void setParkingPrice(double p) { this.parkingPrice = p; }
    public double getLaundryPrice() { return laundryPrice; }
    public void setLaundryPrice(double l) { this.laundryPrice = l; }
    public double getCleaningPrice() { return cleaningPrice; }
    public void setCleaningPrice(double c) { this.cleaningPrice = c; }

    public String getTenantNameDisplay() {
        return (tenant != null) ? tenant.getTenantName() : "Trống";
    }

    public String getRoomName() { return roomCode; }

    public boolean hasUtility(String utilityName) {
        return getUtilities().contains(utilityName);
    }

    public void updateStatus(RoomStatus s) { this.status = s; }

    @Override
    public String toString() {
        return "Room{" + "code='" + roomCode + '\'' + ", status=" + status + '}';
    }
}
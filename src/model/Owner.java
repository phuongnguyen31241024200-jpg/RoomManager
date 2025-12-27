package model;

import java.util.ArrayList;
import java.util.List;

public class Owner extends Account {
    private String ownerName;
    private String email;
    private List<Room> managedRooms;

    //  Constructor mặc định
    public Owner() {
        super();
        this.managedRooms = new ArrayList<>();
        setRole("OWNER");
    }

    //  Constructor 2 tham số
    public Owner(String phoneNumber, String password) {
        super(phoneNumber, password, "OWNER");
        this.ownerName = "Chủ trọ mới"; // Giá trị mặc định
        this.email = "";
        this.managedRooms = new ArrayList<>();
    }

    //  Constructor 4 tham số (Dùng khi đăng ký đầy đủ)
    public Owner(String phoneNumber, String password, String ownerName, String email) {
        super(phoneNumber, password, "OWNER");
        this.ownerName = ownerName;
        this.email = email;
        this.managedRooms = new ArrayList<>();
    }

    //  Constructor 5 tham số (Dùng khi load từ Database/File)
    public Owner(String accountId, String phoneNumber, String password, String ownerName, String email) {
        super(accountId, phoneNumber, password, "OWNER");
        this.ownerName = ownerName;
        this.email = email;
        this.managedRooms = new ArrayList<>();
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Room> getManagedRooms() {
        return managedRooms;
    }

    public void setManagedRooms(List<Room> managedRooms) {
        this.managedRooms = managedRooms;
    }

    // Hàm hỗ trợ thêm nhanh phòng vào danh sách
    public void addRoom(Room room) {
        if (this.managedRooms == null) {
            this.managedRooms = new ArrayList<>();
        }
        this.managedRooms.add(room);
    }
}
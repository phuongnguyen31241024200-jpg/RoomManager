package model;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

// Thêm implements Serializable để có thể lưu vào file .dat
public class Finance implements Serializable {

    // Một ID định danh phiên bản của class để tránh lỗi khi đọc file
    private static final long serialVersionUID = 1L;

    private String financeId;
    private String type; // INCOME / EXPENSE
    private double amount;
    private Date date;
    private String description;
    private Owner owner;

    public Finance() {
        this.financeId = UUID.randomUUID().toString();
        this.date = new Date();
    }

    public Finance(String type, double amount, Date date, String description, Owner owner) {
        this.financeId = UUID.randomUUID().toString();
        this.type = type;
        this.amount = amount;
        this.date = date;
        this.description = description;
        this.owner = owner;
    }

    // GETTERS & SETTERS

    public String getFinanceId() {
        return financeId;
    }

    public void setFinanceId(String financeId) {
        this.financeId = financeId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }
}
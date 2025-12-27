package model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Transaction implements Serializable {
    private double amount;
    private String type; // "INCOME" hoặc "EXPENSE"
    private String description;
    private LocalDateTime date;

    public Transaction(double amount, String type, String description, LocalDateTime date) {
        this.amount = amount;
        this.type = type;
        this.description = description;
        this.date = date;
    }

    // Getter và Setter
    public double getAmount() { return amount; }
    public String getType() { return type; }
    public String getDescription() { return description; }
    public LocalDateTime getDate() { return date; }
}
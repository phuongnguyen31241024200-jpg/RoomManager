package model;

import java.util.UUID;

public class Account {
    private String accountId;
    private String phoneNumber;
    private String password;
    private String role;

    public Account() {
        this.accountId = UUID.randomUUID().toString();
    }

    public Account(String phoneNumber, String password, String role) {
        this.accountId = UUID.randomUUID().toString();
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.role = role;
    }

    public Account(String accountId, String phoneNumber, String password, String role) {
        this.accountId = accountId;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.role = role;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}

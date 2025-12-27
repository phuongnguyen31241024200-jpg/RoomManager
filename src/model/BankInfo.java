package model;

import java.io.Serializable;

public class BankInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String bankId;      // Mã ngân hàng (VCB, MB...)
    private String accountNo;   // Số tài khoản
    private String accountName; // Tên chủ tài khoản

    // Constructor (Hàm khởi tạo)
    public BankInfo(String bankId, String accountNo, String accountName) {
        this.bankId = bankId;
        this.accountNo = accountNo;
        this.accountName = accountName;
    }

    // CÁC HÀM GETTER

    public String getBankId() {
        return bankId;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public String getAccountName() {
        return accountName;
    }

    // CÁC HÀM SETTER (thay đổi dữ liệu)

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }
}
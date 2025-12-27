package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Tenant extends Account implements Serializable {
    private static final long serialVersionUID = 1L;
    private String tenantId;
    private String tenantName;
    private List<IssueReport> issues = new ArrayList<>();

    // 1. Constructor không tham số
    public Tenant() {
        super();
        this.tenantId = "TENANT-" + getAccountId();
        this.setRole("TENANT");
    }

    // 2. Constructor 2 tham số
    // Constructor 2 tham số
    public Tenant(String phoneNumber, String password) {
        super(phoneNumber, password, "TENANT");
        this.tenantId = phoneNumber;
    }

    // Constructor 3 tham số
    public Tenant(String phoneNumber, String password, String tenantName) {
        super(phoneNumber, password, "TENANT");
        this.tenantId = phoneNumber;
        this.tenantName = tenantName;
    }


    public String getFullName() {
        return (tenantName == null || tenantName.isEmpty()) ? "Khách thuê" : tenantName;
    }

    public String getUsername() {
        return this.getPhoneNumber();
    }

    public String getTenantName() {
        return tenantName;
    }

    public String getName() {
        return (tenantName == null || tenantName.isEmpty()) ? "Chưa cập nhật" : tenantName;
    }

    public String getPhone() {
        return this.getPhoneNumber();
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public List<IssueReport> getIssues() { return issues; }

    public void addIssue(IssueReport issue) {
        if (issue != null) issues.add(issue);
    }
}
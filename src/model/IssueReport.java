package model;



import java.io.Serializable;

import java.time.LocalDateTime;

import java.util.ArrayList;

import java.util.List;



public class IssueReport implements Serializable {

    private static final long serialVersionUID = 1L;



    private String issueType;

    private String description;

    private String urgencyLevel;

    private Tenant tenant;

    private IssueStatus status;

    private LocalDateTime createdAt;

    private List<String> history = new ArrayList<>();



    public IssueReport(String issueType, String description, String urgencyLevel, Tenant tenant) {

        this.issueType = issueType;

        this.description = description;

        this.urgencyLevel = urgencyLevel;

        this.tenant = tenant;

        this.status = IssueStatus.PENDING; // Mặc định là PENDING (Chờ xử lý)

        this.createdAt = LocalDateTime.now();

        this.history = new ArrayList<>(); // Khởi tạo list để tránh NullPointerException

        this.history.add("Sự cố được tạo vào lúc " + LocalDateTime.now());

    }




    // Hàm này giúp lấy ID của người gửi từ đối tượng Tenant bên trong

    public String getTenantId() {

        if (tenant != null) {

            return tenant.getTenantId();

        }

        return "N/A"; // Trả về mặc định nếu không có thông tin người thuê

    }



    //  Hàm quan trọng để giao diện lấy chữ hiển thị trạng thái tiếng Việt

    public String getStatusDisplay() {

        if (status == null) return "Chờ xử lý";

        switch (status) {

            case PENDING: return "Chờ xử lý";

            case PROCESSING: return "Đang xử lý";

            case DONE: return "Hoàn thành";

            default: return "Chờ xử lý";

        }

    }



    public List<String> getHistory() {

        if (history == null) history = new ArrayList<>();

        return history;

    }



    public void addHistory(String note) {

        if (history == null) history = new ArrayList<>();

        this.history.add(note + " (" + LocalDateTime.now() + ")");

    }



    public String getIssueType() {

        return issueType;

    }



    public String getDescription() {

        return description;

    }



    public String getUrgencyLevel() {

        return urgencyLevel;

    }



    public Tenant getTenant() {

        return tenant;

    }



    public IssueStatus getStatus() {

        return status;

    }



    public void setStatus(IssueStatus status) {

        this.status = status;

    }



    public LocalDateTime getCreatedAt() {

        return createdAt;

    }

}
package model;

public enum IssueStatus {
    PENDING("Chờ xử lý"),
    PROCESSING("Đang xử lý"),
    DONE("Hoàn thành");

    private final String label;

    IssueStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}

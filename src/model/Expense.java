package model;

import java.io.Serializable;
import java.util.Date;

//  Thêm implements Serializable để đồng bộ với Finance và Income
public class Expense extends Finance implements Serializable {

    // Mã định danh phiên bản dữ liệu
    private static final long serialVersionUID = 1L;

    // Constructor rỗng
    public Expense() {
        super();
        setType("EXPENSE");
    }

    // Constructor 3 tham số để khớp với AddTransactionDialog
    public Expense(String description, double amount, Date date) {
        // Gọi lên class cha Finance với Type là EXPENSE và Owner là null
        super("EXPENSE", amount, date, description, null);
    }

    //  Constructor đầy đủ 4 tham số (giữ lại để không lỗi code cũ)
    public Expense(double amount, Date date, String description, Owner owner) {
        super("EXPENSE", amount, date, description, owner);
    }
}
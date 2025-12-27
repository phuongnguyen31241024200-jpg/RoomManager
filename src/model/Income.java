package model;

import java.io.Serializable;
import java.util.Date;

public class Income extends Finance implements Serializable {

    private static final long serialVersionUID = 1L;

    public Income() {
        // Gọi constructor rỗng của Finance
        super();
        setType("INCOME");
    }

    // Constructor 3 tham số để khớp với AddTransactionDialog
    public Income(String description, double amount, Date date) {
        // Truyền dữ liệu lên class cha Finance
        super("INCOME", amount, date, description, null);
    }

    // Constructor 4 tham số
    public Income(double amount, Date date, String description, Owner owner) {
        super("INCOME", amount, date, description, owner);
    }
}
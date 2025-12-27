package service;

import model.Account;
import model.Owner;
import model.Tenant;

import java.util.HashMap;
import java.util.Map;

public class AuthServiceImpl implements AuthService {

    private final Map<String, Account> accounts = new HashMap<>();
    private Account currentUser;

    public AuthServiceImpl() {
        // Tài khoản mẫu
        accounts.put("0987654321", new Owner("0987654321", "123"));
        accounts.put("0123456789", new Tenant("0123456789", "123"));
    }

    @Override
    public Account login(String phoneNumber, String password) {
        // 1. Kiểm tra 10 số
        if (phoneNumber == null || !phoneNumber.matches("^\\d{10}$")) {
            return null;
        }

        // 2. Kiểm tra mật khẩu không trống
        if (password == null || password.isEmpty()) {
            return null;
        }

        // 3. Xử lý logic đăng nhập
        Account acc = accounts.get(phoneNumber);

        if (acc == null) {
            // Quy ước: bắt đầu bằng 01 là Người thuê, còn lại là Chủ trọ
            if (phoneNumber.startsWith("01")) {
                acc = new Tenant(phoneNumber, password);
            } else {
                acc = new Owner(phoneNumber, password);
            }
            accounts.put(phoneNumber, acc);
        }

        this.currentUser = acc;
        return acc;
    }

    @Override
    public void logout() {
        this.currentUser = null;
    }

    @Override
    public void forgotPassword(String phoneNumber) {
        // Để trống xử lý sau
    }

    @Override
    public void register(String phoneNumber, String password) {
        if (phoneNumber != null && phoneNumber.matches("^\\d{10}$")) {
            Account acc = new Tenant(phoneNumber, password);
            accounts.put(phoneNumber, acc);
        }
    }

    @Override
    public Account getCurrentUser() {
        return currentUser;
    }
}
package service;

import model.Account;

public interface AuthService {
    Account login(String phoneNumber, String password);
    void logout();
    void forgotPassword(String phoneNumber);
    void register(String phoneNumber, String password);

    Account getCurrentUser();
}
package service;

import model.BankInfo;
import java.io.*;

public class SettingService {
    private final String FILE_PATH = "data_bank_settings.dat";

    public void saveBankInfo(BankInfo info) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(info);
            System.out.println("Lưu thành công: " + info.getAccountNo()); // In ra để kiểm tra
        } catch (IOException e) {
            System.err.println("Lỗi khi lưu file: " + e.getMessage());
        }
    }

    public BankInfo loadBankInfo() {
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            System.out.println("File chưa tồn tại, trả về mặc định trống.");
            return new BankInfo("MB", "", "Chưa cài đặt");
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            BankInfo info = (BankInfo) ois.readObject();
            if (info == null) return new BankInfo("MB", "", "");
            return info;
        } catch (Exception e) {
            // In ra lỗi thật sự nếu file bị hỏng hoặc sai phiên bản
            System.err.println("Lỗi khi đọc file cài đặt: " + e.getMessage());
            return new BankInfo("MB", "", "");
        }
    }
}
package service;

import model.Notification;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NotificationService {
    private static List<Notification> notifications = new ArrayList<>();
    private static final String FILE_PATH = "data_notifications.dat";

    // Khối static để load dữ liệu ngay khi ứng dụng khởi động
    static {
        loadData();
    }

    public static void addNotification(String content, String type) {
        // Thêm vào vị trí 0 để thông báo mới luôn ở trên cùng
        notifications.add(0, new Notification(content, type));
        saveData();
    }

    public static List<Notification> getNotifications() {
        return notifications;
    }

    public static int getUnreadCount() {
        return (int) notifications.stream().filter(n -> !n.isRead()).count();
    }

    public static void markAllAsRead() {
        for (Notification n : notifications) {
            n.setRead(true);
        }
        saveData();
    }


    private static void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(notifications);
        } catch (IOException e) {
            System.err.println("Lỗi lưu thông báo: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private static void loadData() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            notifications = (List<Notification>) ois.readObject();
        } catch (Exception e) {
            notifications = new ArrayList<>();
        }
    }
}
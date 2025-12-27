package service;

import model.Room;
import model.Tenant;
import java.util.List;

public interface RoomService {
    Room getRoomByTenantId(String tenantId);

    List<Room> searchRoom(String keyword);

    void addRoom(Room room);

    List<Room> getRooms();

    //   Hàm xóa phòng dựa trên ID
    void deleteRoom(String roomId);

    //   Hàm tìm phòng chính xác theo tên (Dùng cho Tab Hóa đơn)
    Room findRoomByCode(String roomCode);


    void assignTenant(String roomId, Tenant tenant);

    void releaseRoom(String roomId);

    int getTotalRooms();

    int getOccupiedRoomCount();

    int getAvailableRoomCount();
}
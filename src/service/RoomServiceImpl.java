package service;


import model.Room;

import model.Tenant;

import model.RoomStatus;



import java.io.*;

import java.util.ArrayList;

import java.util.Date;

import java.util.List;



public class RoomServiceImpl implements RoomService {



    private List<Room> rooms;

    private final String FILE_PATH = "data_rooms.dat";



    public RoomServiceImpl() {

        this.rooms = loadData();

        checkAllContracts();

    }




    @Override

    public Room getRoomByTenantId(String tenantId) {

        if (tenantId == null) return null;

        // Luôn load dữ liệu mới nhất trước khi tìm

        this.rooms = loadData();

        return rooms.stream()

                .filter(r -> r.getTenant() != null && tenantId.equals(r.getTenant().getTenantId()))

                .findFirst()

                .orElse(null);

    }



    @SuppressWarnings("unchecked")

    private List<Room> loadData() {

        File file = new File(FILE_PATH);

        if (!file.exists()) return new ArrayList<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {

            return (List<Room>) ois.readObject();

        } catch (Exception e) {

            return new ArrayList<>();

        }

    }



    // Sửa trong RoomServiceImpl.java

    public void saveData() {

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {

            oos.writeObject(new ArrayList<>(rooms));

        } catch (IOException e) {

            e.printStackTrace();

        }

    }



    public void checkAllContracts() {

        boolean hasChange = false;

        Date today = new Date();

        for (Room room : rooms) {

            if (room.getStatus() == RoomStatus.OCCUPIED && room.getContract() != null) {

                Date expiryDate = room.getContract().getEndDate();

                if (expiryDate != null && today.after(expiryDate)) {

                    room.setTenant(null);

                    room.setContract(null);

                    room.setStatus(RoomStatus.AVAILABLE);

                    hasChange = true;

                }

            }

        }

        if (hasChange) saveData();

    }



    @Override

    public List<Room> getRooms() {

        this.rooms = loadData();

        checkAllContracts();

        return rooms;

    }



    @Override

    public void addRoom(Room room) {

        this.rooms = loadData();

        rooms.add(room);

        saveData();

    }



    @Override

    public void deleteRoom(String roomId) {

        this.rooms = loadData();

        rooms.removeIf(room -> room.getRoomId().equals(roomId));

        saveData();

    }



    @Override

    public Room findRoomByCode(String roomCode) {

        if (roomCode == null) return null;

        this.rooms = loadData();

        return rooms.stream()

                .filter(r -> r.getRoomCode().equalsIgnoreCase(roomCode)

                        || r.getRoomCode().equalsIgnoreCase("Phòng " + roomCode))

                .findFirst()

                .orElse(null);

    }



    @Override

    public void assignTenant(String roomId, Tenant tenant) {

        this.rooms = loadData();

        for (Room room : rooms) {

            if (room.getRoomId().equals(roomId)) {


                room.setTenant(tenant);




                // room.setPhoneNumber(tenant.getPhoneNumber());



                room.setStatus(RoomStatus.OCCUPIED);




                saveData();

                System.out.println("Đã gán khách thuê: " + tenant.getTenantName() + " - SĐT: " + tenant.getPhoneNumber());

                break;

            }

        }

    }



    @Override

    public void releaseRoom(String roomId) {

        this.rooms = loadData();

        for (Room room : rooms) {

            if (room.getRoomId().equals(roomId)) {

                room.setTenant(null);

                room.setContract(null);

                room.setStatus(RoomStatus.AVAILABLE);

                saveData();

                break;

            }

        }

    }



    @Override

    public List<Room> searchRoom(String keyword) {

        this.rooms = loadData();

        checkAllContracts();

        if (keyword == null || keyword.isBlank()) return rooms;

        List<Room> result = new ArrayList<>();

        String kw = keyword.toLowerCase();

        for (Room room : rooms) {

            if (room.getRoomCode().toLowerCase().contains(kw)) {

                result.add(room);

            }

        }

        return result;

    }



    @Override

    public int getTotalRooms() { return loadData().size(); }



    @Override

    public int getOccupiedRoomCount() {

        return (int) loadData().stream().filter(r -> r.getStatus() == RoomStatus.OCCUPIED).count();

    }



    @Override

    public int getAvailableRoomCount() {

        return (int) loadData().stream().filter(r -> r.getStatus() == RoomStatus.AVAILABLE).count();

    }

}
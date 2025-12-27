package service;

import model.Contract;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ContractServiceImpl implements ContractService {
    private List<Contract> contracts;
    private final String FILE_PATH = "data_contracts.dat";

    public ContractServiceImpl() {
        this.contracts = loadData();
        // Log để kiểm tra xem lúc khởi động có đọc được gì không
        System.out.println("ContractService: Đã nạp " + contracts.size() + " hợp đồng từ file.");
    }

    @Override
    public synchronized void addContract(Contract contract) {
        // Luôn nạp mới dữ liệu từ file để tránh mất dữ liệu cũ
        this.contracts = loadData();

        // Xóa hợp đồng cũ của phòng này (nếu có)
        contracts.removeIf(c -> c.getRoomCode().equalsIgnoreCase(contract.getRoomCode()));

        // Thêm hợp đồng mới
        contracts.add(contract);

        saveData();
    }

    @Override
    public List<Contract> getAllContracts() {
        return loadData();
    }

    @Override
    public Contract getContractByRoom(String roomCode) {
        if (roomCode == null) return null;
        return loadData().stream()
                .filter(c -> roomCode.equalsIgnoreCase(c.getRoomCode()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void deleteContract(String contractId) {
        this.contracts = loadData();
        if (contracts.removeIf(c -> c.getContractId().equals(contractId))) {
            saveData();
        }
    }

    private synchronized void saveData() {
        // Dùng đường dẫn tuyệt đối để chắc chắn file nằm ở đâu
        File file = new File(FILE_PATH);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(new ArrayList<>(contracts));
            oos.flush(); // Ép dữ liệu xuống đĩa
            System.out.println("ContractService: Đã lưu file thành công tại: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("ContractService LỖI GHI FILE: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private List<Contract> loadData() {
        File file = new File(FILE_PATH);
        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object data = ois.readObject();
            if (data instanceof List) {
                return (List<Contract>) data;
            }
        } catch (Exception e) {
            System.err.println("ContractService LỖI ĐỌC FILE: " + e.getMessage());
        }
        return new ArrayList<>();
    }
}
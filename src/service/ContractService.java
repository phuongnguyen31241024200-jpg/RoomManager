package service;

import model.Contract;
import java.util.List;

public interface ContractService {
    void addContract(Contract contract);
    List<Contract> getAllContracts();
    Contract getContractByRoom(String roomCode); // Quan trọng để khách thuê tìm đúng hợp đồng
    void deleteContract(String contractId);
}
package service;

import model.Invoice;
import model.InvoiceStatus;
import model.Payment;
import model.Income;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InvoiceServiceImpl implements InvoiceService {
    private List<Invoice> invoices;
    private final FinanceService financeService;
    private final String FILE_PATH = "data_invoices.dat";

    public InvoiceServiceImpl(FinanceService financeService) {
        this.financeService = financeService;
        this.invoices = loadData();
    }

    // --- BỔ SUNG HÀM NÀY ĐỂ HIỆN CHI TIẾT ---
    @Override
    public Invoice getInvoiceById(String id) {
        if (id == null) return null;
        this.invoices = loadData(); // Luôn load mới để đảm bảo dữ liệu chính xác
        return invoices.stream()
                .filter(inv -> inv.getInvoiceId().equals(id)) // Dùng getInvoiceId() khớp với model của bạn
                .findFirst()
                .orElse(null);
    }

    @Override
    public void addInvoice(Invoice invoice) {
        if (invoice != null) {
            this.invoices = loadData();
            invoices.add(invoice);
            saveData();
        }
    }

    @Override
    public double getTotalUnpaidAmount(String roomCode) {
        if (roomCode == null || roomCode.isEmpty()) return 0;
        this.invoices = loadData();
        return invoices.stream()
                .filter(inv -> inv.getStatus() == InvoiceStatus.UNPAID)
                .filter(inv -> inv.getRoomCode() != null && inv.getRoomCode().trim().equalsIgnoreCase(roomCode.trim()))
                .mapToDouble(Invoice::getTotalAmount)
                .sum();
    }

    @Override
    public List<Invoice> getAllInvoices() {
        this.invoices = loadData();
        return new ArrayList<>(invoices);
    }

    @Override
    public void createInvoice(Invoice invoice) { addInvoice(invoice); }

    @Override
    public List<Invoice> getInvoices() { return getAllInvoices(); }

    @Override
    public void payInvoice(Invoice invoice, Payment payment) {
        if (invoice == null) return;
        this.invoices = loadData();
        for (Invoice inv : invoices) {
            if (inv.getInvoiceId().equals(invoice.getInvoiceId())) {
                inv.setStatus(InvoiceStatus.PAID);
                if (financeService != null) {
                    financeService.addIncome(new Income("Thu tiền phòng " + inv.getRoomCode(), inv.getTotalAmount(), new java.util.Date()));
                }
                break;
            }
        }
        saveData();
    }

    @Override
    public void updateInvoice(Invoice updatedInvoice) {
        this.invoices = loadData();
        for (int i = 0; i < invoices.size(); i++) {
            if (invoices.get(i).getInvoiceId().equals(updatedInvoice.getInvoiceId())) {
                invoices.set(i, updatedInvoice);
                saveData();
                break;
            }
        }
    }

    @Override
    public void deleteInvoice(String invoiceId) {
        this.invoices = loadData();
        if (invoices.removeIf(inv -> inv.getInvoiceId().equals(invoiceId))) saveData();
    }

    @Override
    public List<Invoice> getInvoicesByTenant(String roomCode) {
        this.invoices = loadData();
        return invoices.stream()
                .filter(inv -> inv.getRoomCode() != null && inv.getRoomCode().trim().equalsIgnoreCase(roomCode.trim()))
                .collect(Collectors.toList());
    }

    private synchronized void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(new ArrayList<>(invoices));
        } catch (IOException e) { e.printStackTrace(); }
    }

    private List<Invoice> loadData() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Invoice>) ois.readObject();
        } catch (Exception e) { return new ArrayList<>(); }
    }
}
package service;

import model.Invoice;
import model.Payment;
import java.util.List;

public interface InvoiceService {

    void addInvoice(Invoice invoice);

    List<Invoice> getAllInvoices();

    // Thêm hàm này để lấy chi tiết hóa đơn khi click
    Invoice getInvoiceById(String id);

    void createInvoice(Invoice invoice);

    List<Invoice> getInvoices();

    void payInvoice(Invoice invoice, Payment payment);

    void updateInvoice(Invoice updatedInvoice);

    void deleteInvoice(String invoiceId);

    List<Invoice> getInvoicesByTenant(String tenantId);

    double getTotalUnpaidAmount(String tenantId);
}
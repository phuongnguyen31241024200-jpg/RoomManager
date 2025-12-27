package model;

import java.util.Date;
import java.util.UUID;

public class Payment {
    private String paymentId;
    private double amount;
    private Date paymentDate;
    private String paymentMethod;
    private boolean processed;
    private String invoiceId;

    public Payment() {
        this.paymentId = UUID.randomUUID().toString();
        this.processed = false;
    }

    public Payment(double amount, String paymentMethod, String invoiceId) {
        this.paymentId = UUID.randomUUID().toString();
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.invoiceId = invoiceId;
        this.processed = false;
    }

    public Payment(String paymentId, double amount, Date paymentDate, String paymentMethod, boolean processed, String invoiceId) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
        this.processed = processed;
        this.invoiceId = invoiceId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public void processPayment() {
        this.paymentDate = new Date();
        this.processed = true;
    }
}
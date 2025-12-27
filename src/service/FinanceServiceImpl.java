package service;

import model.Income;
import model.Expense;

import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FinanceServiceImpl implements FinanceService {

    private List<Income> incomes;
    private List<Expense> expenses;
    private final String FILE_PATH = "finance_data.dat";

    public FinanceServiceImpl() {
        loadData();
    }

    // Hàm lưu dữ liệu dùng chung (Đã có sẵn trong code của bạn)
    private void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            List<Object> allData = new ArrayList<>();
            allData.add(incomes);
            allData.add(expenses);
            oos.writeObject(allData);
        } catch (IOException e) {
            System.err.println("Lỗi lưu file thu chi: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void loadData() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            incomes = new ArrayList<>();
            expenses = new ArrayList<>();
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            List<Object> allData = (List<Object>) ois.readObject();
            incomes = (List<Income>) allData.get(0);
            expenses = (List<Expense>) allData.get(1);
        } catch (Exception e) {
            incomes = new ArrayList<>();
            expenses = new ArrayList<>();
        }
    }

    @Override
    public void addIncome(Income income) {
        incomes.add(income);
        saveData();
    }

    @Override
    public void addExpense(Expense expense) {
        expenses.add(expense);
        saveData();
    }

    @Override
    public List<Income> getIncomes() {
        return incomes;
    }

    @Override
    public List<Expense> getExpenses() {
        return expenses;
    }

    @Override
    public double getTotalIncome() {
        return incomes.stream().mapToDouble(Income::getAmount).sum();
    }

    @Override
    public double getTotalExpense() {
        return expenses.stream().mapToDouble(Expense::getAmount).sum();
    }

    @Override
    public double getBalance() {
        return getTotalIncome() - getTotalExpense();
    }

    @Override
    public void deleteIncome(int index) {
        if (index >= 0 && index < incomes.size()) {
            incomes.remove(index);
            saveData(); // Gọi hàm lưu chung của bạn
        }
    }

    @Override
    public void deleteExpense(int index) {
        if (index >= 0 && index < expenses.size()) {
            expenses.remove(index);
            saveData(); // Gọi hàm lưu chung của bạn
        }
    }

    @Override
    public double getIncomeThisMonth() {
        Calendar now = Calendar.getInstance();
        int month = now.get(Calendar.MONTH);
        int year = now.get(Calendar.YEAR);

        return incomes.stream().filter(i -> {
            Calendar c = Calendar.getInstance();
            c.setTime(i.getDate());
            return c.get(Calendar.MONTH) == month && c.get(Calendar.YEAR) == year;
        }).mapToDouble(Income::getAmount).sum();
    }

    @Override
    public double getExpenseThisMonth() {
        Calendar now = Calendar.getInstance();
        int month = now.get(Calendar.MONTH);
        int year = now.get(Calendar.YEAR);

        return expenses.stream().filter(e -> {
            Calendar c = Calendar.getInstance();
            c.setTime(e.getDate());
            return c.get(Calendar.MONTH) == month && c.get(Calendar.YEAR) == year;
        }).mapToDouble(Expense::getAmount).sum();
    }

    // Hàm này bổ trợ cho việc hiển thị, dùng chung với getBalance
    public double getTotalBalance() {
        return getBalance();
    }
}
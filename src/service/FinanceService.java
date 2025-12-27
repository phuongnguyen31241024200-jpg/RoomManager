package service;

import model.Income;
import model.Expense;
import java.util.List;

public interface FinanceService {

    void addIncome(Income income);

    void addExpense(Expense expense);

    List<Income> getIncomes();

    List<Expense> getExpenses();

    double getTotalIncome();

    double getTotalExpense();

    double getBalance();

    double getIncomeThisMonth();

    double getExpenseThisMonth();

    void deleteIncome(int index);

    void deleteExpense(int index);
}
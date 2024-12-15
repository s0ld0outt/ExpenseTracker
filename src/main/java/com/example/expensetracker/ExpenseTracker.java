package com.example.expensetracker;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpenseTracker {
    private List<Expense> expenses;
    private List<String> categories;

    public ExpenseTracker() {
        expenses = new ArrayList<>();
        categories = new ArrayList<>(List.of("Еда", "Транспорт", "Жильё", "Коммунальные услуги", "Развлечения", "Здравоохранение", "Образование", "Одежда", "Путешествия", "Разное"));
    }

    public void addExpense(double amount, String category, LocalDate date) {
        expenses.add(new Expense(amount, category, date));
    }

    public double getTotalExpenses() {
        return expenses.stream().mapToDouble(Expense::getAmount).sum();
    }

    public Map<String, Double> getExpensesByCategory() {
        Map<String, Double> expensesByCategory = new HashMap<>();
        for (Expense expense : expenses) {
            expensesByCategory.merge(expense.getCategory(), expense.getAmount(), Double::sum);
        }
        return expensesByCategory;
    }

    public double getExpenseForCategory(String category) {
        return expenses.stream()
                .filter(e -> e.getCategory().equals(category))
                .mapToDouble(Expense::getAmount)
                .sum();
    }

    public Map<String, Double> getExpensePercentages() {
        Map<String, Double> percentages = new HashMap<>();
        double total = getTotalExpenses();
        Map<String, Double> expensesByCategory = getExpensesByCategory();

        for (Map.Entry<String, Double> entry : expensesByCategory.entrySet()) {
            percentages.put(entry.getKey(), (entry.getValue() / total) * 100);
        }
        return percentages;
    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    public List<String> getCategories() {
        return categories;
    }
}
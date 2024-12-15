package com.example.expensetracker;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class ExpenseTrackerController {
    @FXML private TextField amountField;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private DatePicker datePicker;
    @FXML private TextArea statisticsArea;
    @FXML private ComboBox<String> statisticsCategoryComboBox;

    private ExpenseTracker expenseTracker;

    @FXML
    public void initialize() {
        expenseTracker = new ExpenseTracker();
        categoryComboBox.getItems().addAll(expenseTracker.getCategories());
        statisticsCategoryComboBox.getItems().addAll(expenseTracker.getCategories());
        statisticsCategoryComboBox.getItems().add("Все категории");

        datePicker.setValue(LocalDate.now());
    }

    @FXML
    private void addExpense() {
        try {
            double amount = Double.parseDouble(amountField.getText());

            if (amount <= 0) {
                showAlert("Ошибка", "Сумма расхода должна быть положительным числом.");
                return;
            }

            String category = categoryComboBox.getValue();
            LocalDate date = datePicker.getValue();

            if (category == null || date == null) {
                showAlert("Ошибка", "Пожалуйста, выберите категорию и дату.");
                return;
            }

            expenseTracker.addExpense(amount, category, date);
            clearInputFields();
            showAlert("Успех", "Расход успешно добавлен.");
        } catch (NumberFormatException e) {
            showAlert("Ошибка", "Пожалуйста, введите корректную сумму.");
        }
    }

    @FXML
    private void showStatistics() {
        try {
            String selectedCategory = statisticsCategoryComboBox.getValue();
            StringBuilder stats = new StringBuilder();

            double totalExpenses = expenseTracker.getTotalExpenses();
            stats.append(String.format("Общая сумма расходов: %.2f ₽\n\n", totalExpenses));

            Map<String, Double> expensePercentages = expenseTracker.getExpensePercentages();

            if ("Все категории".equals(selectedCategory) || selectedCategory == null) {
                stats.append("Расходы по категориям:\n");
                for (String category : expenseTracker.getCategories()) {
                    double amount = expenseTracker.getExpenseForCategory(category);
                    double percentage = expensePercentages.getOrDefault(category, 0.0);
                    stats.append(String.format("%s: %.2f ₽ (%.2f%%)\n", category, amount, percentage));
                }
            } else {
                double amount = expenseTracker.getExpenseForCategory(selectedCategory);
                double percentage = expensePercentages.getOrDefault(selectedCategory, 0.0);
                stats.append(String.format("Расходы на %s: %.2f ₽ (%.2f%%)", selectedCategory, amount, percentage));
            }

            statisticsArea.setText(stats.toString());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка", "Произошла ошибка при отображении статистики: " + e.getMessage());
        }
    }

    @FXML
    private void exportToExcel() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить Excel файл");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel файлы", "*.xlsx"));
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Расходы");

                CellStyle currencyStyle = workbook.createCellStyle();
                DataFormat format = workbook.createDataFormat();
                currencyStyle.setDataFormat(format.getFormat("#,##0.00\\ ₽"));

                Row headerRow = sheet.createRow(0);
                headerRow.createCell(0).setCellValue("Дата");
                headerRow.createCell(1).setCellValue("Категория");
                headerRow.createCell(2).setCellValue("Сумма");

                int rowNum = 1;
                for (Expense expense : expenseTracker.getExpenses()) {
                    Row row = sheet.createRow(rowNum++);

                    Cell dateCell = row.createCell(0);
                    dateCell.setCellValue(expense.getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));

                    row.createCell(1).setCellValue(expense.getCategory());

                    Cell amountCell = row.createCell(2);
                    amountCell.setCellValue(expense.getAmount());
                    amountCell.setCellStyle(currencyStyle);
                }

                for (int i = 0; i < 3; i++) {
                    sheet.autoSizeColumn(i);
                }

                try (FileOutputStream outputStream = new FileOutputStream(file)) {
                    workbook.write(outputStream);
                }

                showAlert("Успех", "Расходы успешно экспортированы в Excel.");
            } catch (IOException e) {
                showAlert("Ошибка", "Не удалось экспортировать расходы в Excel: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void clearInputFields() {
        amountField.clear();
        categoryComboBox.setValue(null);
        datePicker.setValue(null);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
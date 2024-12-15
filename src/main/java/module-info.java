module com.example.expensetracker {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;

    opens com.example.expensetracker to javafx.fxml;
    exports com.example.expensetracker;
}
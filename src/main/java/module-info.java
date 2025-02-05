module com.example.project {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens com.example.project to javafx.fxml;

    exports com.example.project;
}
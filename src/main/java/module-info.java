module com.example.banker {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens com.example.banker to javafx.fxml;
    exports com.example.banker;
}
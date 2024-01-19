module com.apple163.control_panel {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.github.oshi;


    opens com.apple163.control_panel to javafx.fxml;
    exports com.apple163.control_panel;
}
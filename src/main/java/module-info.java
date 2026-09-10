module ni.edu.uam.fact_app {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    exports ni.edu.uam.fact_app.application to javafx.graphics;
    opens ni.edu.uam.fact_app.controller to javafx.fxml;
    exports ni.edu.uam.fact_app;
}
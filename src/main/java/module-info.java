module ni.edu.uam.fact_app {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    exports ni.edu.uam.fact_app.application to javafx.graphics;
    opens ni.edu.uam.fact_app.application to javafx.fxml;
    opens ni.edu.uam.fact_app.controller to javafx.fxml;
    opens ni.edu.uam.fact_app.model to javafx.base;
    opens ni.edu.uam.fact_app.icons to javafx.graphics, javafx.fxml, java.compiler;
    opens ni.edu.uam.fact_app.images to javafx.graphics, javafx.fxml, java.compiler;
    exports ni.edu.uam.fact_app;
}
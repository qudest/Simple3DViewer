module com.cgvsu {
    requires javafx.controls;
    requires javafx.fxml;
    requires junit;


    opens com.cgvsu to javafx.fxml;
    exports com.cgvsu;
    exports com.cgvsu.render_engine.graphicConveyor to junit;
    exports com.cgvsu.Math.Coordinate to junit;
}
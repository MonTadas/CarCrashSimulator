module com.carcrashsimulator {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires java.desktop;
    requires javafx.base;

    opens com.carcrashsimulator to javafx.fxml;
    exports com.carcrashsimulator;
    exports com.carcrashsimulator.fxControllers;
    opens com.carcrashsimulator.fxControllers to javafx.fxml;
    exports com.carcrashsimulator.fxUtils;
    opens com.carcrashsimulator.fxUtils to javafx.fxml;
    exports com.carcrashsimulator.models;
    opens com.carcrashsimulator.models to javafx.fxml;
}
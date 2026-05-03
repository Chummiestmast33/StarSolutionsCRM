module com.starsolutions.starsolutionscrm {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires java.sql;

    opens com.starsolutions.starsolutionscrm to javafx.fxml;
    opens com.starsolutions.starsolutionscrm.controller.base to javafx.fxml;
    opens com.starsolutions.starsolutionscrm.controller.compras to javafx.fxml;
    opens com.starsolutions.starsolutionscrm.controller.inventario to javafx.fxml;
    opens com.starsolutions.starsolutionscrm.controller.produccion to javafx.fxml;
    opens com.starsolutions.starsolutionscrm.controller.rrhh to javafx.fxml;
    opens com.starsolutions.starsolutionscrm.controller.ventas to javafx.fxml;
    exports com.starsolutions.starsolutionscrm;
    exports com.starsolutions.starsolutionscrm.database;
}
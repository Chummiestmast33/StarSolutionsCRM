module com.starsolutions.starsolutionscrm {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires java.sql;

    // Permisos para los Controladores (Para que JavaFX inyecte los @FXML y eventos)
    opens com.starsolutions.starsolutionscrm to javafx.fxml;
    opens com.starsolutions.starsolutionscrm.controller.base to javafx.fxml;
    opens com.starsolutions.starsolutionscrm.controller.compras to javafx.fxml;
    opens com.starsolutions.starsolutionscrm.controller.inventario to javafx.fxml;
    opens com.starsolutions.starsolutionscrm.controller.produccion to javafx.fxml;
    opens com.starsolutions.starsolutionscrm.controller.rrhh to javafx.fxml;
    opens com.starsolutions.starsolutionscrm.controller.ventas to javafx.fxml;

    opens com.starsolutions.starsolutionscrm.model.crm to javafx.base;
    opens com.starsolutions.starsolutionscrm.model.ventas to javafx.base;
    opens com.starsolutions.starsolutionscrm.model.rrhh to javafx.base;
    opens com.starsolutions.starsolutionscrm.model.produccion to javafx.base;
    opens com.starsolutions.starsolutionscrm.model.compras to javafx.base;
    opens com.starsolutions.starsolutionscrm.model.inventario to javafx.base;

    // Aperturas necesarias para que las pruebas puedan acceder por reflexión a DAOs y modelos
    opens com.starsolutions.starsolutionscrm.dao.impl;
    opens com.starsolutions.starsolutionscrm.dao.interfaces;
    opens com.starsolutions.starsolutionscrm.database;

    exports com.starsolutions.starsolutionscrm;
    exports com.starsolutions.starsolutionscrm.database;
}
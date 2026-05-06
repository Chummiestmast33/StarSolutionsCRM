package com.starsolutions.starsolutionscrm;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import atlantafx.base.theme.PrimerLight; // Import AtlantaFX Theme

import java.io.IOException;

public class CRMApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // Set Global Windows 11 / AtlantaFX Theme
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());

        FXMLLoader fxmlLoader = new FXMLLoader(
                CRMApplication.class.getResource(
                        "fxml/base/login.fxml"   // <-- abre login, no main
                )
        );
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("Star Solutions CRM — Login");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }
}
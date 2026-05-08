package com.starsolutions.starsolutionscrm;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
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
        stage.setTitle("Flowdesk — Login");
        stage.setResizable(false);
        stage.getIcons().add(new Image(CRMApplication.class.getResourceAsStream("/com/starsolutions/starsolutionscrm/img/FlowDeskIcon.png")));
        stage.setScene(scene);
        stage.show();
    }
}
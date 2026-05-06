package com.starsolutions.starsolutionscrm.controller.base;

import com.starsolutions.starsolutionscrm.dao.impl.EmpleadoDAOImpl;
import com.starsolutions.starsolutionscrm.dao.interfaces.IEmpleadoDAO;
import com.starsolutions.starsolutionscrm.model.rrhh.Empleado;
import com.starsolutions.starsolutionscrm.util.SessionManager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController {

    @FXML private TextField txtNumero;
    @FXML private PasswordField txtContrasena;
    @FXML private Label lblError;

    private final IEmpleadoDAO empleadoDAO = new EmpleadoDAOImpl();

    @FXML
    public void onLogin() {
        String numTexto    = txtNumero.getText().trim();
        String contrasena  = txtContrasena.getText().trim();

        // Validar que los campos no estén vacíos
        if (numTexto.isEmpty() || contrasena.isEmpty()) {
            mostrarError("Por favor ingresa tu número y contraseña.");
            return;
        }

        // Validar que el número sea entero
        int num;
        try {
            num = Integer.parseInt(numTexto);
        } catch (NumberFormatException e) {
            mostrarError("El número de empleado debe ser un número entero.");
            return;
        }

        // Intentar login contra la BD
        try {
            Empleado empleado = empleadoDAO.login(num, contrasena);

            if (empleado == null) {
                mostrarError("Número o contraseña incorrectos.");
                return;
            }

            // Guardar sesión
            SessionManager.getInstance().iniciarSesion(empleado);

            // Abrir pantalla principal
            abrirMain();

        } catch (SQLException e) {
            mostrarError("Error de conexión: " + e.getMessage());
        }
    }

    private void abrirMain() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/com/starsolutions/starsolutionscrm/fxml/base/main.fxml"
                    )
            );
            Stage stage = (Stage) txtNumero.getScene().getWindow();
            stage.setScene(new Scene(loader.load(), 1280, 720));
            stage.setTitle("Star Solutions CRM");
        } catch (IOException e) {
            mostrarError("No se pudo abrir la pantalla principal.");
            e.printStackTrace();
        }
    }

    private void mostrarError(String mensaje) {
        lblError.setText(mensaje);
        lblError.setVisible(true);
    }
}
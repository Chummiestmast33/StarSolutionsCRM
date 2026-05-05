package com.starsolutions.starsolutionscrm.database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;
    private static final String CONFIG_PATH = "/com/starsolutions/starsolutionscrm/database.local.properties";
    private static final String DEFAULT_TEST_URL = "jdbc:h2:mem:starsolutionscrm;MODE=MySQL;DB_CLOSE_DELAY=-1";

    private DatabaseConnection() throws SQLException {
        Properties props = new Properties();
        try (InputStream inputStream = DatabaseConnection.class.getResourceAsStream(CONFIG_PATH)) {
            if (inputStream != null) {
                props.load(inputStream);
            }
        } catch (IOException exception) {
            throw new SQLException("No se pudo leer la configuracion de base de datos", exception);
        }

        String url = getConfiguredValue("db.url", props.getProperty("db.url"), DEFAULT_TEST_URL);
        String user = getConfiguredValue("db.user", props.getProperty("db.user"), "");
        String password = getConfiguredValue("db.password", props.getProperty("db.password"), "");

        if (!user.isBlank()) {
            props.setProperty("user", user);
        }
        if (!password.isBlank()) {
            props.setProperty("password", password);
        }

        props.remove("db.url");
        props.remove("db.user");
        props.remove("db.password");

        connection = DriverManager.getConnection(url, props);
    }

    public static DatabaseConnection getInstance() throws SQLException {
        if (instance == null || instance.connection == null || instance.connection.isClosed()) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    private static String getConfiguredValue(String key, String defaultValue, String fallbackValue) {
        String systemProperty = System.getProperty(key);
        if (systemProperty != null && !systemProperty.isBlank()) {
            return systemProperty;
        }

        String environmentKey = key.toUpperCase().replace('.', '_');
        String environmentValue = System.getenv(environmentKey);
        if (environmentValue != null && !environmentValue.isBlank()) {
            return environmentValue;
        }

        if (defaultValue != null && !defaultValue.isBlank()) {
            return defaultValue;
        }

        return fallbackValue;
    }

    public Connection getConnection() {
        return connection;
    }
}

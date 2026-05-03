package com.starsolutions.starsolutionscrm.database;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DatabaseConnectionTest {

    @BeforeEach
    void setUp() throws Exception {
        System.setProperty("db.url", "jdbc:h2:mem:starsolutionscrm_test;MODE=MySQL;DB_CLOSE_DELAY=-1");
        System.clearProperty("db.user");
        System.clearProperty("db.password");
    }

    @AfterEach
    void tearDown() throws Exception {
        System.clearProperty("db.url");
    }

    @Test
    void deberiaConectarseCorrectamente() throws Exception {
        Connection connection = DatabaseConnection.getInstance().getConnection();

        assertNotNull(connection);
        assertFalse(connection.isClosed());

        try (PreparedStatement stmt = connection.prepareStatement("SELECT 1");
             ResultSet rs = stmt.executeQuery()) {

            assertTrue(rs.next());
            assertEquals(1, rs.getInt(1));
        }
    }
}
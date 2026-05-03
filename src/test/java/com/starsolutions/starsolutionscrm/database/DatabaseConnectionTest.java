package com.starsolutions.starsolutionscrm.database;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

class DatabaseConnectionTest {

    @BeforeMethod
    void setUp() throws Exception {
        System.setProperty("db.url", "jdbc:h2:mem:starsolutionscrm_test;MODE=MySQL;DB_CLOSE_DELAY=-1");
        System.clearProperty("db.user");
        System.clearProperty("db.password");
    }

    @AfterMethod
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
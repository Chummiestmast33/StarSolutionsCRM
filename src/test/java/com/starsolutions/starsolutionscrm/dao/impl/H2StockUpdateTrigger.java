package com.starsolutions.starsolutionscrm.dao.impl;

import org.h2.api.Trigger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Trigger H2 que replica el comportamiento de trg_actualiza_stock sobre inv_stock.
 */
public class H2StockUpdateTrigger implements Trigger {

    @Override
    public void init(Connection conn, String schemaName, String triggerName, String tableName, boolean before, int type) {
        // No initialization required
    }

    @Override
    public void fire(Connection conn, Object[] oldRow, Object[] newRow) throws SQLException {
        Integer idStock = ((Number) newRow[1]).intValue();
        String tipo = String.valueOf(newRow[3]);
        int cantidad = ((Number) newRow[4]).intValue();

        if ("ENTRADA".equalsIgnoreCase(tipo)) {
            try (PreparedStatement ps = conn.prepareStatement("UPDATE inv_stock SET cantidad_actual = cantidad_actual + ? WHERE id_stock = ?")) {
                ps.setInt(1, cantidad);
                ps.setInt(2, idStock);
                ps.executeUpdate();
            }
        } else if ("SALIDA".equalsIgnoreCase(tipo)) {
            try (PreparedStatement ps = conn.prepareStatement("UPDATE inv_stock SET cantidad_actual = cantidad_actual - ? WHERE id_stock = ?")) {
                ps.setInt(1, cantidad);
                ps.setInt(2, idStock);
                ps.executeUpdate();
            }
        }
    }

    @Override
    public void close() {
        // No-op
    }

    @Override
    public void remove() {
        // No-op
    }
}


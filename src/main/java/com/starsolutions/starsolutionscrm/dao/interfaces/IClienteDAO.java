package com.starsolutions.starsolutionscrm.dao.interfaces;

import com.starsolutions.starsolutionscrm.model.crm.Cliente;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public interface IClienteDAO {
    List<Cliente> listarActivos() throws SQLException;
    Cliente buscarPorId(int idCliente) throws SQLException;
    BigDecimal obtenerDescuentoCliente(int idCliente) throws SQLException;
    boolean crear(Cliente cliente) throws SQLException;
    boolean actualizar(Cliente cliente) throws SQLException;
    boolean desactivar(int idCliente) throws SQLException;
}
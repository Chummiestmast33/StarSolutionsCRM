package com.starsolutions.starsolutionscrm.dao.interfaces;

import com.starsolutions.starsolutionscrm.model.crm.ClienteDescuento;
import java.sql.SQLException;

public interface IClienteDescuentoDAO {
    // Busca si un cliente ya tiene un descuento VIP asignado
    ClienteDescuento obtenerPorCliente(int idCliente) throws SQLException;

    // Inserta un nuevo descuento o actualiza el existente
    boolean guardar(ClienteDescuento descuento) throws SQLException;

    // Desactiva el descuento de un cliente
    boolean desactivar(int idDescuento) throws SQLException;
}
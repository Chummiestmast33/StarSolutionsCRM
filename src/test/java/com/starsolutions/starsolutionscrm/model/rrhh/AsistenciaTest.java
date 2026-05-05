package com.starsolutions.starsolutionscrm.model.rrhh;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class AsistenciaTest {

    @Test
    void constructorVacioDeberiaCrearInstanciaConValoresPredeterminados() {
        Asistencia a = new Asistencia();
        assertEquals(0, a.getIdAsistencia());
        assertEquals(0, a.getIdEmpleado());
        assertNull(a.getFecha());
        assertNull(a.getHoraEntrada());
        assertNull(a.getHoraSalida());
    }

    @Test
    void constructorCompletoDeberiaAsignarTodosLosCampos() {
        LocalDate fecha = LocalDate.of(2026, 5, 5);
        LocalTime entrada = LocalTime.of(8, 0);
        LocalTime salida = LocalTime.of(17, 0);
        Asistencia a = new Asistencia(1, 3, fecha, entrada, salida);

        assertEquals(1, a.getIdAsistencia());
        assertEquals(3, a.getIdEmpleado());
        assertEquals(fecha, a.getFecha());
        assertEquals(entrada, a.getHoraEntrada());
        assertEquals(salida, a.getHoraSalida());
    }

    @Test
    void constructorDeberiaAdmitirHorasNulas() {
        LocalDate fecha = LocalDate.now();
        Asistencia a = new Asistencia(2, 4, fecha, null, null);

        assertNull(a.getHoraEntrada());
        assertNull(a.getHoraSalida());
    }

    @Test
    void settersDeberianActualizarCamposCorrectamente() {
        Asistencia a = new Asistencia();
        LocalDate fecha = LocalDate.of(2026, 3, 10);
        LocalTime entrada = LocalTime.of(9, 30);
        LocalTime salida = LocalTime.of(18, 30);

        a.setIdAsistencia(5);
        a.setIdEmpleado(7);
        a.setFecha(fecha);
        a.setHoraEntrada(entrada);
        a.setHoraSalida(salida);

        assertEquals(5, a.getIdAsistencia());
        assertEquals(7, a.getIdEmpleado());
        assertEquals(fecha, a.getFecha());
        assertEquals(entrada, a.getHoraEntrada());
        assertEquals(salida, a.getHoraSalida());
    }

    @Test
    void toStringDeberiaContenercamposClave() {
        LocalDate fecha = LocalDate.of(2026, 5, 5);
        Asistencia a = new Asistencia(1, 2, fecha, LocalTime.of(8, 0), null);
        String s = a.toString();
        assertTrue(s.contains("1"));
        assertTrue(s.contains("2"));
        assertTrue(s.contains("2026-05-05"));
    }
}

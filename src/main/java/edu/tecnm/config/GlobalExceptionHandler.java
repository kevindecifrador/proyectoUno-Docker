package edu.tecnm.config;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Este método se activa automáticamente cuando ocurre un error de base de datos
     * (como intentar borrar un Cliente que tiene Pedidos o Reservaciones).
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public String handleDataIntegrityViolation(DataIntegrityViolationException ex, Model model) {
        model.addAttribute("titulo", "No se puede realizar la acción");
        model.addAttribute("mensaje", "Este registro no se puede eliminar o modificar porque está siendo usado en otra parte del sistema (por ejemplo, un Cliente con Pedidos o Reservaciones asociadas).");
        model.addAttribute("errorTecnico", ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage());
        return "error/errorGeneral";
    }

    /**
     * Captura cualquier otro error inesperado.
     */
    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception ex, Model model) {
        model.addAttribute("titulo", "Ocurrió un error inesperado");
        model.addAttribute("mensaje", "Algo salió mal en la operación. Por favor intenta de nuevo o contacta al administrador.");
        model.addAttribute("errorTecnico", ex.getMessage());
        return "error/errorGeneral";
    }
}
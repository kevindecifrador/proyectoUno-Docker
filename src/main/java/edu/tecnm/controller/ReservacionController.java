package edu.tecnm.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.springframework.ui.Model;

import edu.tecnm.model.Cliente;
import edu.tecnm.model.EstatusReservacion;
import edu.tecnm.model.Reservacion;
import edu.tecnm.service.IClienteService;
import edu.tecnm.service.IMesaService;
import edu.tecnm.service.IReservacionService;

@Controller
@RequestMapping("/reservaciones")
public class ReservacionController {

    @Autowired private IReservacionService serviceReservacion;
    @Autowired private IClienteService serviceCliente;
    @Autowired private IMesaService serviceMesa;

    @GetMapping
    public String listarReservaciones(Model model,
            @RequestParam(value="fechaInicio", required=false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(value="fechaFin", required=false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(value="capacidad", required=false) Integer capacidad) {
        
        // 1. Obtener quién está conectado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        // Verificamos si tiene el rol CLIENTE
        boolean esCliente = auth.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("CLIENTE"));
        
        List<Reservacion> reservaciones = new ArrayList<>();
        
        // 2. Lógica de Seguridad
        if (esCliente) {
            // CASO A: ES CLIENTE -> Solo ve SUS reservaciones
            Cliente clienteLogueado = serviceCliente.buscarPorUsuario(username);
            if (clienteLogueado != null) {
                // Nota: Debes tener este método en serviceReservacion
                reservaciones = serviceReservacion.buscarPorClienteId(clienteLogueado.getId()); 
            }
        } else {
            // CASO B: ES ADMIN O CAJERO -> Ve todo y puede filtrar
            if (fechaInicio != null && fechaFin != null) {
                reservaciones = serviceReservacion.buscarPorRangoDeFechas(fechaInicio, fechaFin);
            } else if (fechaInicio != null) {
                reservaciones = serviceReservacion.buscarPorFechaDesde(fechaInicio);
            } else if (fechaFin != null) {
                reservaciones = serviceReservacion.buscarPorFechaFin(fechaFin);
            } else if (capacidad != null) {
                reservaciones = serviceReservacion.buscarPorCapacidad(capacidad);
            } else {
                reservaciones = serviceReservacion.buscarTodas();
            }
        }
        
        model.addAttribute("reservaciones", reservaciones);
        model.addAttribute("hoy", LocalDate.now());
        return "reservacion/listaReservaciones";
    }

    @GetMapping("/crear")
    public String crearReservacion(Model model) {
        Reservacion nuevaReservacion = new Reservacion();
        nuevaReservacion.setEstatus(EstatusReservacion.PENDIENTE); // Establece el estatus por defecto
        model.addAttribute("reservacion", nuevaReservacion);
        model.addAttribute("clientes", serviceCliente.buscarTodosClientes());
        model.addAttribute("mesas", serviceMesa.buscarTodas());
        model.addAttribute("hoy", LocalDate.now().toString()); // Pasa la fecha de hoy
        return "reservacion/formReservacion";
    }

    @PostMapping("/guardar")
    public String guardarReservacion(Reservacion reservacion, RedirectAttributes attributes) {
        // VALIDACIÓN DE FECHA EN SERVIDOR
        LocalDate hoy = LocalDate.now();
        // Comprueba si la fecha de la reserva es anterior a hoy
        if (reservacion.getFecha() != null && reservacion.getFecha().isBefore(hoy)) {
            attributes.addFlashAttribute("error", "Error: No se pueden crear ni modificar reservaciones para fechas pasadas.");
            // Si es una modificación, redirige de nuevo al formulario de modificación
            if (reservacion.getIdServicio() != null) {
                 return "redirect:/reservaciones/modificar/" + reservacion.getIdServicio();
            }
            // Si es una creación, redirige al formulario de creación (o a la lista, como prefieras)
            return "redirect:/reservaciones/crear"; 
        }
        
        // Solo validamos conflicto si es una NUEVA reserva O si la fecha/hora/mesa cambiaron en una MODIFICACION
        boolean esNueva = reservacion.getIdServicio() == null;
        if (esNueva || /* logica para detectar si cambiaron los datos clave */ true) {
            if (serviceReservacion.existeConflicto(reservacion.getMesa(), reservacion.getFecha(), reservacion.getHora())) {
                attributes.addFlashAttribute("error", "Error: La mesa seleccionada ya está reservada para esa fecha y hora.");
                // Decide a dónde redirigir (crear o modificar)
                return "redirect:" + (esNueva ? "/reservaciones/crear" : "/reservaciones/modificar/" + reservacion.getIdServicio());
            }
        }

        // Si la fecha es válida, procede a guardar
        // Asegurarse de que el estatus no se pierda al editar (si no se estableció por defecto)
        if (reservacion.getIdServicio() != null && reservacion.getEstatus() == null) {
             Reservacion existente = serviceReservacion.buscarPorId(reservacion.getIdServicio());
             if(existente != null) reservacion.setEstatus(existente.getEstatus());
        } else if (reservacion.getIdServicio() == null) {
             reservacion.setEstatus(EstatusReservacion.PENDIENTE); // Asegura PENDIENTE al crear
        }

        serviceReservacion.guardar(reservacion);
        attributes.addFlashAttribute("success", "Reservación guardada con éxito.");
        return "redirect:/reservaciones";
    }
    
    @GetMapping("/modificar/{id}")
    public String modificarReservacion(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("reservacion", serviceReservacion.buscarPorId(id));
        model.addAttribute("clientes", serviceCliente.buscarTodosClientes());
        model.addAttribute("mesas", serviceMesa.buscarTodas());
        model.addAttribute("hoy", LocalDate.now().toString());
        return "reservacion/formReservacion";
    }

    @GetMapping("/confirmar/{id}")
    public String confirmarReservacion(@PathVariable("id") Integer id, RedirectAttributes attributes) {
        Reservacion reservacion = serviceReservacion.buscarPorId(id);
        
        if (reservacion != null) {
            // VALIDACIÓN DE FECHA
            LocalDate hoy = LocalDate.now();
            if (reservacion.getFecha().isEqual(hoy)) {
                // Si la fecha coincide, se confirma
                serviceReservacion.confirmar(id); // Llama al metodo del servicio que cambia el estatus
                attributes.addFlashAttribute("success", "Reservación #" + id + " confirmada con éxito.");
            } else {
                // Si la fecha no es hoy, se manda un error
                attributes.addFlashAttribute("error", "Error: La reservación #" + id + " solo puede confirmarse el día " + reservacion.getFecha() + ".");
            }

        } else {
            attributes.addFlashAttribute("error", "Reservación no encontrada.");
        }
        return "redirect:/reservaciones";
    }

    @GetMapping("/cancelar/{id}")
    public String cancelarReservacion(@PathVariable("id") Integer id, RedirectAttributes attributes) {
        serviceReservacion.eliminar(id);
        attributes.addFlashAttribute("success", "Reservación cancelada/eliminada.");
        return "redirect:/reservaciones";
    }
}
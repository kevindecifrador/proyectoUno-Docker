package edu.tecnm.controller;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import edu.tecnm.model.Empleado;
import edu.tecnm.model.Pedido;
import edu.tecnm.service.IEmpleadoService;
import edu.tecnm.service.IPedidoService;

@Controller
@RequestMapping("/atender")
public class AtencionController {

    @Autowired
    private IPedidoService servicePedido;
    
    @Autowired
    private IEmpleadoService serviceEmpleado;

    @GetMapping
    public String listarAsignaciones(Model model, @RequestParam(value="q", required=false) String consulta) {
        List<Pedido> pedidos;
        if (consulta != null && !consulta.isEmpty()) {
            pedidos = servicePedido.buscarPorNombreDeEmpleado(consulta);
        } else {
            pedidos = servicePedido.buscarTodos();
        }
        model.addAttribute("pedidos", pedidos);
        
     // MODAL
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        model.addAttribute("objectMapper", objectMapper);
        
        return "atender/listaAtencion";
    }
    
    @GetMapping("/asignar/{idPedido}")
    public String mostrarFormularioAsignacion(@PathVariable("idPedido") Integer idPedido, Model model) {
        Pedido pedido = servicePedido.buscarPorId(idPedido);
        List<Empleado> empleados = serviceEmpleado.buscarTodos();
        
        model.addAttribute("pedido", pedido);
        model.addAttribute("empleados", empleados);
        model.addAttribute("empleadosAsignados", pedido != null ? pedido.getEmpleadosQueAtienden() : Set.of()); 
        
        return "atender/formAsignarEmpleado";
    }
    
    /**
     * Procesa el formulario y AÑADE la asignación de UN empleado al pedido.
     * Si el empleado ya estaba, no hace nada.
     */
    @PostMapping("/guardar")
    public String guardarAsignacion(@RequestParam("idPedido") Integer idPedido, 
                                     @RequestParam("claveEmpleado") String claveEmpleado, 
                                     RedirectAttributes attributes) {
        
        Pedido pedido = servicePedido.buscarPorId(idPedido);
        Empleado empleado = serviceEmpleado.buscarPorClave(claveEmpleado);
        
        if (pedido != null && empleado != null) {
            pedido.getEmpleadosQueAtienden().add(empleado); 

            servicePedido.guardar(pedido); // Guardamos el pedido con el empleado añadido al Set
            attributes.addFlashAttribute("success", "Empleado " + empleado.getNombreCompleto() + " asignado al Pedido #" + idPedido);
        } else {
            attributes.addFlashAttribute("error", "No se pudo realizar la asignación. Pedido o empleado no encontrado.");
        }
        
        return "redirect:/atender";
    }
    
    /**
     * Elimina TODAS las asignaciones de empleados a un pedido específico.
     * Podríamos hacerlo más específico para quitar solo UN empleado si tuviéramos su clave.
     */
    @GetMapping("/cancelar/{idPedido}")
    public String cancelarAsignacion(@PathVariable("idPedido") Integer idPedido, RedirectAttributes attributes) {
        Pedido pedido = servicePedido.buscarPorId(idPedido);
        if (pedido != null) {
           
            pedido.getEmpleadosQueAtienden().clear(); // Quitamos todos los empleados
         
            servicePedido.guardar(pedido); // Guardamos el pedido sin empleados asignados
            attributes.addFlashAttribute("success", "Todas las asignaciones canceladas para el Pedido #" + idPedido);
        } else {
            attributes.addFlashAttribute("error", "No se encontró el pedido.");
        }
        return "redirect:/atender";
    }
    
    /* * OPCIONAL: Si quisieras un botón para quitar UN empleado específico de un pedido
     * Necesitarías pasar tanto el idPedido como la claveEmpleado.
     */
    @GetMapping("/quitar/{idPedido}/{claveEmpleado}")
    public String quitarEmpleadoDePedido(@PathVariable("idPedido") Integer idPedido,
                                        @PathVariable("claveEmpleado") String claveEmpleado, 
                                        RedirectAttributes attributes) {
        Pedido pedido = servicePedido.buscarPorId(idPedido);
        Empleado empleado = serviceEmpleado.buscarPorClave(claveEmpleado);

        if (pedido != null && empleado != null) {
            boolean removed = pedido.getEmpleadosQueAtienden().remove(empleado); // Intenta quitar el empleado
            if (removed) {
                servicePedido.guardar(pedido);
                attributes.addFlashAttribute("success", "Empleado " + empleado.getNombreCompleto() + " quitado del Pedido #" + idPedido);
            } else {
                 attributes.addFlashAttribute("warning", "El empleado no estaba asignado a este pedido.");
            }
        } else {
             attributes.addFlashAttribute("error", "Pedido o empleado no encontrado.");
        }
        return "redirect:/atender";
    }
}
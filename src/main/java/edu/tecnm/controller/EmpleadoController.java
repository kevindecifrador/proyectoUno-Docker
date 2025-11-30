package edu.tecnm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.Authentication;


import edu.tecnm.model.Empleado;
import edu.tecnm.model.Usuario;
import edu.tecnm.service.IEmpleadoService;
import edu.tecnm.service.IUsuarioService;

@Controller
@RequestMapping("/empleados")
public class EmpleadoController {

    @Autowired
    private IEmpleadoService serviceEmpleado;
    
    @Autowired
    private IUsuarioService usuarioService;

    @GetMapping
    public String listarEmpleados(Model model, @RequestParam(value="q", required=false) String consulta) {
        List<Empleado> empleados;
        if (consulta != null && !consulta.isEmpty()) {
            empleados = serviceEmpleado.buscarPorNombre(consulta);
        } else {
            empleados = serviceEmpleado.buscarTodos();
        }
        model.addAttribute("listaEmpleados", empleados);
        return "empleado/listaEmpleados";
    }

    @GetMapping("/crear")
    public String crearEmpleado(Model model) {
        model.addAttribute("empleado", new Empleado());
        return "empleado/formEmpleado";
    }

    @PostMapping("/guardar")
    public String guardarEmpleado(@ModelAttribute("empleado") Empleado empleado,
                                  @RequestParam(value = "fromPerfil", required = false) Boolean fromPerfil,
                                  Authentication authentication,
                                  RedirectAttributes attributes) {

        // Si viene desde MI PERFIL, forzamos que el empleado quede ligado al usuario en sesión
        if (Boolean.TRUE.equals(fromPerfil)) {
            String username = authentication.getName();
            Usuario usuario = usuarioService.buscarPorUsername(username);
            empleado.setUsuario(usuario);
        }

        serviceEmpleado.guardar(empleado);
        attributes.addFlashAttribute("success", "Empleado guardado con éxito.");

        if (Boolean.TRUE.equals(fromPerfil)) {
            return "redirect:/productos";
        }
        return "redirect:/empleados";
    }
    
 // METODO PARA MODIFICAR
    @GetMapping("/modificar/{clave}")
    public String modificarEmpleado(@PathVariable("clave") String clave, Model model) {
        Empleado empleado = serviceEmpleado.buscarPorClave(clave);
        model.addAttribute("empleado", empleado);
        return "empleado/formEmpleado";
    }

    // METODO PARA ELIMINAR
    @GetMapping("/eliminar/{clave}")
    public String eliminarEmpleado(@PathVariable("clave") String clave, RedirectAttributes attributes) {
        serviceEmpleado.eliminar(clave);
        attributes.addFlashAttribute("success", "Empleado eliminado con éxito.");
        return "redirect:/empleados";
    }
}
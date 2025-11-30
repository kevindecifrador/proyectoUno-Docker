package edu.tecnm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.tecnm.model.Cliente;
import edu.tecnm.model.Empleado;
import edu.tecnm.model.Usuario;
import edu.tecnm.service.IClienteService;
import edu.tecnm.service.IEmpleadoService;
import edu.tecnm.service.IUsuarioService;

@Controller
@RequestMapping("/perfil")
public class PerfilController {

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private IClienteService clienteService;

    @Autowired
    private IEmpleadoService empleadoService;

    @GetMapping("/mi-perfil")
    public String miPerfil(Authentication authentication, Model model) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String username = authentication.getName();
        Usuario usuario = usuarioService.buscarPorUsername(username);
        Integer idUsuario = usuario.getId();

        // 1) ¿YA ES CLIENTE?
        Cliente cliente = clienteService.buscarPorUsuario(idUsuario);
        if (cliente != null) {
            model.addAttribute("cliente", cliente);
            model.addAttribute("fromPerfil", true);
            return "cliente/detalles";
        }

        // 2) ¿YA ES EMPLEADO?
        Empleado empleado = empleadoService.buscarPorUsuario(idUsuario);
        if (empleado != null) {
            model.addAttribute("empleado", empleado);
            model.addAttribute("fromPerfil", true);
            return "empleado/formEmpleado";
        }

        // 3) NO TIENE REGISTRO
        boolean esCliente = usuario.getPerfiles().stream()
                .anyMatch(p -> "CLIENTE".equalsIgnoreCase(p.getPerfil()));

        if (esCliente) {
            // Crear nuevo cliente para este usuario
            Cliente nuevo = new Cliente();
            nuevo.setUsuario(usuario);
            model.addAttribute("cliente", nuevo);
            model.addAttribute("fromPerfil", true);
            return "cliente/formCliente";
        } else {
            // Cualquier otro rol -> lo tratamos como empleado (CAJERO, MESERO, etc.)
            Empleado nuevo = new Empleado();
            nuevo.setUsuario(usuario);
            model.addAttribute("empleado", nuevo);
            model.addAttribute("fromPerfil", true);
            return "empleado/formEmpleado";
        }
    }
}

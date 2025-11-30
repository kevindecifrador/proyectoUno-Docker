package edu.tecnm.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.util.StringUtils;
import org.springframework.security.core.Authentication;


import edu.tecnm.model.Cliente;
import edu.tecnm.model.Usuario;
import edu.tecnm.service.CloudinaryService;
import edu.tecnm.service.IClienteService;
import edu.tecnm.service.IUsuarioService;

@Controller
public class ClienteController {

    @Autowired
    private IClienteService serviceCliente;
    
    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private CloudinaryService cloudinaryService;


    @GetMapping("/lista")
    public String mostrarListaClientes(Model model) {
        List<Cliente> lista = serviceCliente.buscarTodosClientes();
        model.addAttribute("clienteLista", lista);
        return "listaClientes";
    }

    @GetMapping("/ver/{id}")
    public String verDetalleCliente(@PathVariable("id") int idCliente, Model model) {
        Cliente cliente = serviceCliente.buscarPorIdCliente(idCliente);
        model.addAttribute("cliente", cliente);
        return "cliente/detalles";
    }

    @GetMapping("/crear")
    public String crearCliente(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "cliente/formCliente";
    }

    @PostMapping("/guardar")
    public String guardarCliente(@ModelAttribute("cliente") Cliente cliente,
                                 @RequestParam("imagenFile") MultipartFile file,
                                 RedirectAttributes attributes,
                                 Authentication authentication) {

    	/*
        // 1. Validar ID
        if (cliente.getId() == null) {
            attributes.addFlashAttribute("error", "Debes capturar un ID para el cliente antes de guardar.");
            return "redirect:/crear";
        }
        */

        // 2. Lógica de Seguridad
        // Verificamos si es CLIENTE para ligarlo a su usuario
        boolean esCliente = authentication != null &&
                authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("CLIENTE"));

        if (esCliente) {
            String username = authentication.getName();
            Usuario usuario = usuarioService.buscarPorUsername(username);
            cliente.setUsuario(usuario);
        }

        // 3. Manejo de imagen
        if (!file.isEmpty()) {
            // A. Si hay foto nueva -> Cloudinary
            String urlImagen = cloudinaryService.subirArchivo(file);
            if (urlImagen != null) {
                cliente.setFotocliente(urlImagen);
            }
        } else {
            // B. Si no hay foto nueva -> Mantener la anterior (si es edición)
        	// Primero verificamos si es una EDICIÓN (tiene ID)
            if (cliente.getId() != null) {
                Cliente clienteExistente = serviceCliente.buscarPorIdCliente(cliente.getId());
                if (clienteExistente != null) {
                    // Si existe, recuperamos su foto vieja
                    cliente.setFotocliente(clienteExistente.getFotocliente());
                }
            }
        }
        // 4. Guardar cliente
        serviceCliente.guardarCliente(cliente);

        // 5. Redirección según rol
        if (esCliente) {
            attributes.addFlashAttribute("success", "Tu perfil de cliente se guardó correctamente.");

            return "redirect:/productos"; 
        } else {
            attributes.addFlashAttribute("success", "Cliente guardado correctamente.");
            return "redirect:/lista";
        }
    }

    @GetMapping("/modificar/{id}")
    public String modificarCliente(@PathVariable("id") Integer idCliente, Model model) {
        Cliente cliente = serviceCliente.buscarPorIdCliente(idCliente);
        model.addAttribute("cliente", cliente);
        return "cliente/formCliente";
    }
    
    @GetMapping("/eliminar/{id}")
    public String eliminarCliente(@PathVariable("id") Integer idCliente, RedirectAttributes redirectAttributes) {
        serviceCliente.eliminarCliente(idCliente);
        redirectAttributes.addFlashAttribute("success", "Cliente eliminado exitosamente.");
        return "redirect:/lista";
    }
}
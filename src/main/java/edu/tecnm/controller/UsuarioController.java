package edu.tecnm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.tecnm.model.Cliente;
import edu.tecnm.model.Empleado;
import edu.tecnm.model.Perfil;
import edu.tecnm.model.Usuario;
import edu.tecnm.service.IClienteService;
import edu.tecnm.service.IEmpleadoService;
import edu.tecnm.service.IPerfilService;
import edu.tecnm.service.IUsuarioService;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private IUsuarioService usuarioService;
    
    @Autowired
    private IPerfilService perfilService;

    @Autowired
    private IClienteService clienteService;
    
    @Autowired
    private IEmpleadoService empleadoService;
    
    @GetMapping("/index")
    public String mostrarIndex(Model model) {
        List<Usuario> lista = usuarioService.buscarTodos();
        model.addAttribute("usuarios", lista);
        return "usuarios/listaUsuarios"; 
    }

    @GetMapping("/crear")
    public String mostrarFormularioCrear(Usuario usuario) {
        return "usuarios/formUsuario"; 
    }

    @PostMapping("/guardar")
    public String guardarUsuario(Usuario usuario, BindingResult result, RedirectAttributes attributes) {

        boolean esEdicion = (usuario.getId() != null);

        if (usuario.getPerfiles() == null || usuario.getPerfiles().isEmpty()) {
            Perfil perfilCliente = perfilService.buscarPorId(3); 
            usuario.agregarPerfil(perfilCliente);
        }

        usuarioService.guardar(usuario);
        usuarioService.sincronizarRolUsuario(usuario);

        if (esEdicion) {
            attributes.addFlashAttribute("msg", "Usuario actualizado correctamente.");
            return "redirect:/usuarios/index";
        }

        attributes.addFlashAttribute("msg", "Usuario registrado exitosamente.");
        return "redirect:/login";
    }
    
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable("id") int idUsuario, Model model) {
        Usuario usuario = usuarioService.buscarPorId(idUsuario);
        if (usuario != null) {
            model.addAttribute("usuario", usuario);
            return "usuarios/formUsuario";
        }
        return "redirect:/usuarios/index";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable("id") int idUsuario, RedirectAttributes attributes) {
        try {
            usuarioService.eliminar(idUsuario);
            attributes.addFlashAttribute("msg", "Usuario eliminado correctamente.");
        } catch (Exception e) {
            attributes.addFlashAttribute("msgError", "No se pudo eliminar el usuario.");
        }
        return "redirect:/usuarios/index";
    }

    @ModelAttribute("perfiles")
    public List<Perfil> getPerfiles() {
        return perfilService.buscarTodos();
    }
    
    /*
     * PASO 1: MUESTRA EL FORMULARIO PARA ASIGNAR UN LOGIN A UN CLIENTE
     */
    @GetMapping("/asignar/{clienteId}")
    public String mostrarFormAsignarLogin(@PathVariable("clienteId") int clienteId, Model model) {
        
        Cliente cliente = clienteService.buscarPorIdCliente(clienteId);
        model.addAttribute("cliente", cliente);
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("clienteId", clienteId);
        
        // ¡NUEVO! Buscamos y mandamos la lista de usuarios sin asignar
        List<Usuario> usuariosSinAsignar = usuarioService.findClientesSinAsignar();
        model.addAttribute("usuariosSinAsignar", usuariosSinAsignar);
        
        return "usuarios/asignarLogin";
    }

    /*
     * PASO 2: GUARDA EL NUEVO USUARIO Y LO ASOCIA AL CLIENTE
     */
    @PostMapping("/guardarLoginAsignado")
    public String guardarLoginAsignado(
            @ModelAttribute("usuario") Usuario usuario,
            @RequestParam("clienteId") int clienteId,
            @RequestParam(value = "usuarioIdExistente", required = false) Integer usuarioIdExistente,
            RedirectAttributes attributes) {
        
        try {
            Cliente cliente = clienteService.buscarPorIdCliente(clienteId);
            
            // LÓGICA 1: El usuario eligió VINCULAR UNO EXISTENTE
            if (usuarioIdExistente != null) {
                
                Usuario usuarioExistente = usuarioService.buscarPorId(usuarioIdExistente);
                cliente.setUsuario(usuarioExistente);
                clienteService.guardarCliente(cliente); // Actualiza el cliente
                attributes.addFlashAttribute("msg", "Usuario '" + usuarioExistente.getUsername() + "' vinculado exitosamente.");
            
            // LÓGICA 2: El usuario eligió CREAR UNO NUEVO
            } else {
                
                // Validación para campos vacíos (ya que no son 'required' en el HTML)
                if (usuario.getUsername() == null || usuario.getUsername().isEmpty() ||
                    usuario.getPassword() == null || usuario.getPassword().isEmpty()) {
                    
                    attributes.addFlashAttribute("msgError", "Para crear un nuevo usuario, el Username y la Contraseña son obligatorios.");
                    return "redirect:/usuarios/asignar/" + clienteId;
                }

                // Asignar el rol de CLIENTE por defecto (ID 3, según tu BD)
                Perfil perfilCliente = perfilService.buscarPorId(3); 
                usuario.agregarPerfil(perfilCliente);
                
                // Guardar el nuevo Usuario (el servicio encripta la contraseña)
                usuarioService.guardar(usuario); 
                
                // Vincular el nuevo usuario al cliente
                cliente.setUsuario(usuario);
                clienteService.guardarCliente(cliente);
                attributes.addFlashAttribute("msg", "Nuevo usuario '" + usuario.getUsername() + "' creado y vinculado exitosamente.");
            }

        } catch (DataIntegrityViolationException e) {
            // MANEJO DE ERROR
            attributes.addFlashAttribute("msgError", "Error: El username '" + usuario.getUsername() + "' ya existe. Elija otro.");
            return "redirect:/usuarios/asignar/" + clienteId;
        }

        return "redirect:/lista";
    }
    
    /*
     * PARA ASIGNAR LOGIN A EMPLEADOS
     */
    @GetMapping("/asignarEmpleado/{empleadoId}")
    public String mostrarFormAsignarLoginEmpleado(@PathVariable("empleadoId") String empleadoId, Model model) {
        
        // (Asumo que tu servicio tiene un 'buscarPorClave' o similar)
    	Empleado empleado = empleadoService.buscarPorClave(empleadoId);
        model.addAttribute("empleado", empleado);
        model.addAttribute("usuario", new Usuario());
        
        // El @ModelAttribute("perfiles") que ya tienes en el controlador
        // se encargará de pasar la lista de todos los perfiles a la vista.
        
        return "usuarios/asignarLoginEmpleado";
    }

    @PostMapping("/guardarLoginEmpleado")
    public String guardarLoginEmpleado(@ModelAttribute("usuario") Usuario usuario,
                                       @RequestParam("empleadoId") String empleadoId,
                                       RedirectAttributes attributes) {
        
        // ¡Validación Importante!
        // A diferencia de Clientes, aquí DEBEN seleccionar un rol.
        if (usuario.getPerfiles() == null || usuario.getPerfiles().isEmpty()) {
            attributes.addFlashAttribute("msgError", "Error: Debes seleccionar al menos un perfil (rol) para el empleado.");
            return "redirect:/usuarios/asignarEmpleado/" + empleadoId;
        }

        try {
            // 1. Guardar el nuevo Usuario (el servicio encripta la contraseña)
            usuarioService.guardar(usuario); 

            // 2. Encontrar al Empleado y asociarle el nuevo Usuario
            Empleado empleado = empleadoService.buscarPorClave(empleadoId);
            empleado.setUsuario(usuario);
            empleadoService.guardar(empleado); // Guardamos el empleado actualizado

        } catch (DataIntegrityViolationException e) {
            // Manejo de error para username duplicado
            attributes.addFlashAttribute("msgError", "Error: El username '" + usuario.getUsername() + "' ya existe. Elija otro.");
            return "redirect:/usuarios/asignarEmpleado/" + empleadoId;
        }

        attributes.addFlashAttribute("msg", "Login asignado exitosamente al empleado.");
        return "redirect:/empleados";
    }
}
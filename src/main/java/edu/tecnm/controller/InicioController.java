package edu.tecnm.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import edu.tecnm.model.Cliente;
import edu.tecnm.service.IClienteService;

import org.springframework.ui.Model;

@Controller
public class InicioController {
	
	private final IClienteService clienteService;
	
	@GetMapping("/home")
	public String mostrarInicio(Model model) {
		return "inicio";
	}
	
	
	public InicioController(IClienteService clienteService) {
        this.clienteService = clienteService;
    }
	
	 @GetMapping("/clientes")
	    public String listarClientes(Model model) {
	        List<Cliente> clientes = clienteService.buscarTodosClientes();
	        model.addAttribute("clientes", clientes);
	        return "listaClientes";
	    }

}

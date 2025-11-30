package edu.tecnm.service.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import edu.tecnm.model.Cliente;
import edu.tecnm.model.Usuario;
import edu.tecnm.service.IClienteService;

@Service
public class ClienteServiceImpl implements IClienteService {

	List<Cliente> listaCliente=null;
	
	public ClienteServiceImpl() {
		listaCliente = new LinkedList<Cliente>();
		
		Cliente cliente1=new Cliente();
		cliente1.setId(1);
		cliente1.setNombre("Juanito");
		cliente1.setApellidos("Cruz");
		cliente1.setEmail("correo@este.com");
		cliente1.setFotocliente("foto1.jpg");
		cliente1.setCredito(1600);
		cliente1.setTelefono("1234567892");
		cliente1.setDestacado(0);
		
		Cliente cliente2=new Cliente();
		cliente2.setId(2);
		cliente2.setNombre("Sol");
		cliente2.setApellidos("Garcia");
		cliente2.setEmail("correo2@este.com");
		cliente2.setFotocliente("foto2.jpg");
		cliente2.setCredito(1800);
		cliente2.setTelefono("12345000002");
		cliente2.setDestacado(1);
		
		Cliente cliente3=new Cliente();
		cliente3.setId(3);
		cliente3.setNombre("Maria");
		cliente3.setApellidos("Solache");
		cliente3.setEmail("correo3@este.com");
		cliente3.setFotocliente("foto32.jpg");
		cliente3.setCredito(1640);
		cliente3.setTelefono("1234567892");
		cliente3.setDestacado(0);
		
		listaCliente.add(cliente1);
		listaCliente.add(cliente2);
		listaCliente.add(cliente3);
	}
	
	
	
	@Override
	public List<Cliente> buscarTodosClientes() {
		
		return listaCliente;
	}
	
	public Cliente buscarPorIdCliente(Integer idCliente) {
		for(Cliente cli: listaCliente) 
			if(cli.getId() == idCliente)
				return cli;
			return null;
	}
	
	//Guardar un cliente
	@Override
	public void guardarCliente(Cliente cliente) {
		listaCliente.add(cliente);
	}



	@Override
	public void eliminarCliente(Integer idCliente) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public Optional<Cliente> buscarPorNombre(String nombre) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}



	@Override
	public List<Cliente> buscarPorNombreConteniendo(String cadena) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public Optional<Cliente> buscarPorEmail(String email) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}



	@Override
	public List<Cliente> buscarPorEmailTerminandoCon(String sufijo) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public List<Cliente> buscarPorCreditoEntre(double min, double max) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public List<Cliente> buscarPorCreditoMayorQue(double monto) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public List<Cliente> buscarPorDestacado(int estatus) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public List<Cliente> buscarPorNombreYCreditoMayorQue(String nombre, double valor) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public List<Cliente> buscarPorFotocliente(String nombreFoto) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public List<Cliente> buscarPorDestacadoYCreditoMayorQue(int estatus, double valor) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public List<Cliente> buscarTop5PorCreditoDesc() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public Cliente buscarPorUsuario(String username) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public Cliente buscarPorUsuario(Usuario usuario) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public Cliente buscarPorUsuario(Integer idUsuario) {
		// TODO Auto-generated method stub
		return null;
	}
	
}

package edu.tecnm.service.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import edu.tecnm.model.Cliente;
import edu.tecnm.model.Usuario;
import edu.tecnm.repository.ClienteRepository;
import edu.tecnm.service.IClienteService;

@Service
@Primary //dos clases de servicio:
//1. Clase ClienteServiceImp
//2. ClienteServiceJpa

public class ClienteServiceJpa implements IClienteService {
	
	@Autowired
	private ClienteRepository clienteRepo;

	
	@Override
	public List<Cliente> buscarTodosClientes(){
		return clienteRepo.findAll();
	}


	@Override
	public Cliente buscarPorIdCliente(Integer idCliente) {
		Optional<Cliente> optional=clienteRepo.findById(idCliente);
		if(optional.isPresent()) {
			return optional.get();
		}
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void guardarCliente(Cliente cliente) {
		// TODO Auto-generated method stub
		clienteRepo.save(cliente);
	}
	
	@Override
	public void eliminarCliente(Integer idCliente) {
		clienteRepo.deleteById(idCliente);
	}
	
	@Override
	public Optional<Cliente> buscarPorNombre(String nombre) {
		return clienteRepo.findByNombre(nombre);
	}

	@Override
	public List<Cliente> buscarPorNombreConteniendo(String cadena) {
		return clienteRepo.findByNombreContaining(cadena);
	}

	@Override
	public Optional<Cliente> buscarPorEmail(String email) {
		return clienteRepo.findByEmail(email);
	}

	@Override
	public List<Cliente> buscarPorEmailTerminandoCon(String sufijo) {
		return clienteRepo.findByEmailEndingWith(sufijo);
	}

	@Override
	public List<Cliente> buscarPorCreditoEntre(double min, double max) {
		return clienteRepo.findByCreditoBetween(min, max);
	}

	@Override
	public List<Cliente> buscarPorCreditoMayorQue(double monto) {
		return clienteRepo.findByCreditoGreaterThan(monto);
	}

	@Override
	public List<Cliente> buscarPorDestacado(int estatus) {
		return clienteRepo.findByDestacado(estatus);
	}

	@Override
	public List<Cliente> buscarPorNombreYCreditoMayorQue(String nombre, double valor) {
		return clienteRepo.findByNombreAndCreditoGreaterThan(nombre, valor);
	}

	@Override
	public List<Cliente> buscarPorFotocliente(String nombreFoto) {
		return clienteRepo.findByFotocliente(nombreFoto);
	}

	@Override
	public List<Cliente> buscarPorDestacadoYCreditoMayorQue(int estatus, double valor) {
		return clienteRepo.findByDestacadoAndCreditoGreaterThan(estatus, valor);
	}

	@Override
	public List<Cliente> buscarTop5PorCreditoDesc() {
		return clienteRepo.findTop5ByOrderByCreditoDesc();
	}
	
	@Override
    public Cliente buscarPorUsuario(String username) {

        return clienteRepo.findByUsuarioUsername(username);
    }
	
	@Override
	public Cliente buscarPorUsuario(Usuario usuario) {
	    return clienteRepo.findByUsuario(usuario);
	}
	
	@Override
    public Cliente buscarPorUsuario(Integer idUsuario) {
        return clienteRepo.findByUsuario_Id(idUsuario);
    }
}

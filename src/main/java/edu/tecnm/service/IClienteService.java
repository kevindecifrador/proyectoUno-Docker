package edu.tecnm.service;

import java.util.List;
import java.util.Optional;

import edu.tecnm.model.Cliente;
import edu.tecnm.model.Usuario;

public interface IClienteService {
	
	List<Cliente> buscarTodosClientes();
	
	//Metodo para buscar el cliente por id
	Cliente buscarPorIdCliente(Integer idCliente);
	
	//Guardara un cliente en la lista
	void guardarCliente(Cliente cliente);
	
	void eliminarCliente(Integer idCliente);
	
	// 1. Busca un cliente por su nombre exacto
    Optional<Cliente> buscarPorNombre(String nombre);

    // 2. Busca clientes cuyo nombre contenga una cadena
    List<Cliente> buscarPorNombreConteniendo(String cadena);

    // 3. Busca un cliente por su email exacto
    Optional<Cliente> buscarPorEmail(String email);

    // 4. Busca clientes cuyo email termine con "@gmail.com"
    List<Cliente> buscarPorEmailTerminandoCon(String sufijo);

    // 5. Busca clientes con crédito entre dos valores
    List<Cliente> buscarPorCreditoEntre(double min, double max);

    // 6. Busca clientes con crédito mayor a un monto
    List<Cliente> buscarPorCreditoMayorQue(double monto);

    // 7. Busca clientes destacados
    List<Cliente> buscarPorDestacado(int estatus);

    // 8. Busca clientes por nombre Y con crédito mayor a un valor
    List<Cliente> buscarPorNombreYCreditoMayorQue(String nombre, double valor);

    // 9. Busca clientes cuya foto sea un nombre específico
    List<Cliente> buscarPorFotocliente(String nombreFoto);

    // 10. Busca clientes destacados Y con crédito mayor a un valor
    List<Cliente> buscarPorDestacadoYCreditoMayorQue(int estatus, double valor);

    // 11. Busca los 5 clientes con el crédito más alto
    List<Cliente> buscarTop5PorCreditoDesc();
    
    Cliente buscarPorUsuario(String username);
    
    Cliente buscarPorUsuario(Usuario usuario);
    
    Cliente buscarPorUsuario(Integer idUsuario);
}
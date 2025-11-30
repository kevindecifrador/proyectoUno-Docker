package edu.tecnm.service;

import java.time.LocalDate;
import java.util.List;
import edu.tecnm.model.Pedido;

public interface IPedidoService {
    List<Pedido> buscarTodos();
    Pedido buscarPorId(Integer idPedido);
    void guardar(Pedido pedido);
    void eliminar(Integer idPedido);
    
    List<Pedido> buscarPorClaveDeEmpleado(String clave);
    List<Pedido> buscarPorNombreDeEmpleado(String nombre);
    
    List<Pedido> buscarPorFecha(LocalDate fecha);
    List<Pedido> buscarPorFechaEntre(LocalDate inicio, LocalDate fin);
    List<Pedido> buscarPorNombreDeCliente(String nombreCliente);
    
    List<Pedido> buscarPorClienteId(Integer idCliente);
    List<Pedido> buscarPorEmpleadoClave(String claveEmpleado);
}

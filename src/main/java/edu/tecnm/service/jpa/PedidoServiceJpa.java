package edu.tecnm.service.jpa;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import edu.tecnm.model.Pedido;
import edu.tecnm.repository.PedidoRepository;
import edu.tecnm.service.IPedidoService;

@Service
@Primary
public class PedidoServiceJpa implements IPedidoService {

    @Autowired
    private PedidoRepository pedidoRepo;

    @Override
    public List<Pedido> buscarTodos() {
        return pedidoRepo.findAll();
    }

    @Override
    public Pedido buscarPorId(Integer idPedido) {
        Optional<Pedido> optional = pedidoRepo.findById(idPedido);
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
    }

    @Override
    public void guardar(Pedido pedido) {
        pedidoRepo.save(pedido);
    }

    @Override
    public void eliminar(Integer idPedido) {
        pedidoRepo.deleteById(idPedido);
    }
    
    @Override
    public List<Pedido> buscarPorClaveDeEmpleado(String clave) {
        return pedidoRepo.findByEmpleadosQueAtiendenClave(clave);
    }
    
    @Override
    public List<Pedido> buscarPorNombreDeEmpleado(String nombre) {
        return pedidoRepo.findByEmpleadosQueAtiendenNombreCompletoContainingIgnoreCase(nombre);
    }
    
    @Override
    public List<Pedido> buscarPorFecha(LocalDate fecha) {
        return pedidoRepo.findByFecha(fecha);
    }
    
    @Override
    public List<Pedido> buscarPorFechaEntre(LocalDate inicio, LocalDate fin) {
        return pedidoRepo.findByFechaBetween(inicio, fin);
    }

    @Override
    public List<Pedido> buscarPorNombreDeCliente(String nombreCliente) {
        return pedidoRepo.findByClienteNombreContainingIgnoreCase(nombreCliente);
    }
    
    @Override
    public List<Pedido> buscarPorClienteId(Integer idCliente) {
        return pedidoRepo.findByCliente_Id(idCliente);
    }
    
    @Override
    public List<Pedido> buscarPorEmpleadoClave(String claveEmpleado) {
        return pedidoRepo.findByEmpleadoClave(claveEmpleado);
    }
}
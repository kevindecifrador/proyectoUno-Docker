package edu.tecnm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import edu.tecnm.model.DetallePedido;

public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Integer> {
    
}
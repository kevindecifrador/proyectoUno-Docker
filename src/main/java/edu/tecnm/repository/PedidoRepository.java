package edu.tecnm.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import edu.tecnm.model.Empleado;
import edu.tecnm.model.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Integer> {
    
    // Busca pedidos que contengan ESTE empleado específico en su colección
    List<Pedido> findByEmpleadosQueAtiendenContains(Empleado empleado); 
    // O puedes usar "IsContaining" o simplemente "Containing"
    
  // Busca pedidos que contengan un empleado con ESTA clave
    List<Pedido> findByEmpleadosQueAtiendenClave(String clave);

    // Busca pedidos que contengan un empleado con ESTE puesto
    List<Pedido> findByEmpleadosQueAtiendenPuesto(String puesto);
    
    // Busca pedidos que contengan un empleado cuyo nombre contenga el texto
    List<Pedido> findByEmpleadosQueAtiendenNombreCompletoContainingIgnoreCase(String nombre);
    
    // Busca pedidos por una fecha exacta
    List<Pedido> findByFecha(LocalDate fecha);
    
    // Busca pedidos por un rango de fechas
    List<Pedido> findByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin);
    
    // Busca pedidos por el nombre del cliente (navegando la relación)
    List<Pedido> findByClienteNombreContainingIgnoreCase(String nombreCliente);
    
    // Busca pedidos por el ID del cliente (navegando la relación @ManyToOne)
    List<Pedido> findByCliente_Id(Integer idCliente);
    
    // Busca pedidos que tengan un empleado con esta clave (navegando la relación @ManyToMany)
    List<Pedido> findByEmpleadosQueAtienden_Clave(String claveEmpleado);

    // Buscar pedidos donde uno de los empleados que atienden tenga esa clave
    // Usamos @Query porque es una relación de "Muchos a Muchos" (Lista de empleados)
    @Query("SELECT p FROM Pedido p JOIN p.empleadosQueAtienden e WHERE e.clave = :clave")
    List<Pedido> findByEmpleadoClave(@Param("clave") String clave);
}
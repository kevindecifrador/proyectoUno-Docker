package edu.tecnm.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import edu.tecnm.model.Mesa;
import edu.tecnm.model.Reservacion;

public interface ReservacionRepository extends JpaRepository<Reservacion, Integer> {

    // Metodos de busqueda para fechas
    List<Reservacion> findByFechaBetween(LocalDate inicio, LocalDate fin);
    List<Reservacion> findByFechaLessThanEqual(LocalDate fin);
    
    // Busca reservaciones en mesas por capacidad
    List<Reservacion> findByMesaCapacidadGreaterThanEqual(Integer capacidad);
    
 // Busca si existe alguna reservación para una mesa, fecha y hora específicas
    boolean existsByMesaAndFechaAndHora(Mesa mesa, LocalDate fecha, LocalTime hora);
    List<Reservacion> findByFecha(LocalDate fecha);
    
 // Busca reservaciones cuya fecha sea MAYOR O IGUAL (GreaterThanEqual) a la fecha de inicio
    List<Reservacion> findByFechaGreaterThanEqual(LocalDate fechaInicio);
    
 // Busca reservaciones donde el cliente tenga cierto ID
    List<Reservacion> findByCliente_Id(Integer idCliente);
}
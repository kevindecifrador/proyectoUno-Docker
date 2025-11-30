package edu.tecnm.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import edu.tecnm.model.Mesa;
import edu.tecnm.model.Reservacion;

public interface IReservacionService {
    List<Reservacion> buscarTodas();
    Reservacion buscarPorId(Integer id);
    void guardar(Reservacion reservacion);
    void eliminar(Integer id);
    void confirmar(Integer id);
    
    boolean existeConflicto(Mesa mesa, LocalDate fecha, LocalTime hora);
    
    // para busquedas
    List<Reservacion> buscarPorRangoDeFechas(LocalDate inicio, LocalDate fin);
    List<Reservacion> buscarPorFechaFin(LocalDate fin);
    List<Reservacion> buscarPorCapacidad(Integer capacidad);

	List<Reservacion> buscarPorFecha(LocalDate fecha);
	
	List<Reservacion> buscarPorFechaDesde(LocalDate fechaInicio);
	
	List<Reservacion> buscarPorClienteId(Integer idCliente);
}
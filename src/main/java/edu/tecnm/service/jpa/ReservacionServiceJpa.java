package edu.tecnm.service.jpa;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import edu.tecnm.model.EstatusReservacion;
import edu.tecnm.model.Mesa;
import edu.tecnm.model.Reservacion;
import edu.tecnm.repository.ReservacionRepository;
import edu.tecnm.service.IReservacionService;

@Service
@Primary
public class ReservacionServiceJpa implements IReservacionService {

    @Autowired
    private ReservacionRepository repoReservacion;

    @Override
    public List<Reservacion> buscarTodas() {
        return repoReservacion.findAll();
    }

    @Override
    public Reservacion buscarPorId(Integer id) {
        Optional<Reservacion> optional = repoReservacion.findById(id);
        return optional.orElse(null);
    }

    @Override
    public void guardar(Reservacion reservacion) {
        repoReservacion.save(reservacion);
    }

    @Override
    public void eliminar(Integer id) {
        repoReservacion.deleteById(id);
    }

    @Override
    public void confirmar(Integer id) {
        Reservacion r = buscarPorId(id);
        if (r != null) {
            r.setEstatus(EstatusReservacion.CONFIRMADA);
            repoReservacion.save(r);
        }
    }

    @Override
    public List<Reservacion> buscarPorRangoDeFechas(LocalDate inicio, LocalDate fin) {
        return repoReservacion.findByFechaBetween(inicio, fin);
    }
    
    @Override
    public List<Reservacion> buscarPorFechaFin(LocalDate fin) {
        return repoReservacion.findByFechaLessThanEqual(fin);
    }

    @Override
    public List<Reservacion> buscarPorCapacidad(Integer capacidad) {
        return repoReservacion.findByMesaCapacidadGreaterThanEqual(capacidad);
    }
    
    public boolean existeConflicto(Mesa mesa, LocalDate fecha, LocalTime hora) {
        return repoReservacion.existsByMesaAndFechaAndHora(mesa, fecha, hora);
    }

    @Override
    public List<Reservacion> buscarPorFecha(LocalDate fecha) {
        return repoReservacion.findByFecha(fecha);
    }
    
    @Override
    public List<Reservacion> buscarPorFechaDesde(LocalDate fechaInicio) {
        return repoReservacion.findByFechaGreaterThanEqual(fechaInicio);
    }
    
    @Override
    public List<Reservacion> buscarPorClienteId(Integer idCliente) {
        return repoReservacion.findByCliente_Id(idCliente);
    }
}
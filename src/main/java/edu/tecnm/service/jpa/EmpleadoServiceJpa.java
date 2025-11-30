package edu.tecnm.service.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import edu.tecnm.model.Empleado;
import edu.tecnm.model.Usuario;
import edu.tecnm.repository.EmpleadoRepository;
import edu.tecnm.service.IEmpleadoService;

@Service
@Primary
public class EmpleadoServiceJpa implements IEmpleadoService {

    @Autowired
    private EmpleadoRepository repoEmpleado;

    @Override
    public List<Empleado> buscarTodos() {
        return repoEmpleado.findAll();
    }

    @Override
    public Empleado buscarPorClave(String clave) {
        Optional<Empleado> optional = repoEmpleado.findById(clave);
        return optional.orElse(null);
    }

    @Override
    public void guardar(Empleado empleado) {
        repoEmpleado.save(empleado);
    }

    @Override
    public void eliminar(String clave) {
        repoEmpleado.deleteById(clave);
    }
    
    @Override
    public List<Empleado> buscarPorNombre(String nombre) {
        return repoEmpleado.findByNombreCompletoContainingIgnoreCase(nombre);
    }
    
    @Override
    public List<Empleado> buscarPorPuesto(String puesto) {
        return repoEmpleado.findByPuestoIgnoreCase(puesto);
    }
    
    @Override
    public Empleado buscarPorUsuario(String username) {
        return repoEmpleado.findByUsuarioUsername(username);
    }
    
    @Override
    public Empleado buscarPorUsuario(Usuario usuario) {
        return repoEmpleado.findByUsuario(usuario);
    }
    
    @Override
    public Empleado buscarPorUsuario(Integer idUsuario) {
        return repoEmpleado.findByUsuario_Id(idUsuario);
    }
}
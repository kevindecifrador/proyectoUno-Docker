package edu.tecnm.service;

import java.util.List;
import edu.tecnm.model.Empleado;
import edu.tecnm.model.Usuario;

public interface IEmpleadoService {
    List<Empleado> buscarTodos();
    Empleado buscarPorClave(String clave);
    void guardar(Empleado empleado);
    void eliminar(String clave);
    
    List<Empleado> buscarPorNombre(String nombre);
    List<Empleado> buscarPorPuesto(String puesto);
    
    Empleado buscarPorUsuario(String username);
    
    Empleado buscarPorUsuario(Usuario usuario);
    
    Empleado buscarPorUsuario(Integer idUsuario);
}
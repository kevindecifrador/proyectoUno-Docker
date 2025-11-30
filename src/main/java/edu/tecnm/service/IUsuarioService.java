package edu.tecnm.service;

import java.util.List;
import edu.tecnm.model.Usuario;

public interface IUsuarioService {

    /**
     * Guarda un usuario. Si ya tiene perfiles asignados,
     * la relación se guarda en automático.
     * @param usuario
     */
    void guardar(Usuario usuario);

    /**
     * Busca un usuario por su username
     * @param username
     * @return
     */
    Usuario buscarPorUsername(String username);

    /**
     * Devuelve todos los usuarios registrados
     * @return
     */
    List<Usuario> buscarTodos();
    
    /**
     * Busca un usuario por ID
     * @param idUsuario
     * @return
     */
    Usuario buscarPorId(Integer idUsuario);
    
    /**
     * Elimina un usuario por ID
     * @param idUsuario
     */
    void eliminar(Integer idUsuario);
   
    List<Usuario> findClientesSinAsignar();
    
    void sincronizarRolUsuario(Usuario usuario);
}
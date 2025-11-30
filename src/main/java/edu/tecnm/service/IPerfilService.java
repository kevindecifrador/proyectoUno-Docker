package edu.tecnm.service;

import java.util.List;
import edu.tecnm.model.Perfil;

public interface IPerfilService {

    /**
     * Devuelve todos los perfiles disponibles
     * @return Lista de Perfiles
     */
    List<Perfil> buscarTodos();

    /**
     * Busca un perfil por su ID
     * @param idPerfil
     * @return El Perfil si se encuentra, de lo contrario null
     */
    Perfil buscarPorId(Integer idPerfil);
    
    /**
     * Guarda un nuevo perfil
     * @param perfil
     */
    void guardar(Perfil perfil);
    
    // métodos después (eliminar, etc.)
}
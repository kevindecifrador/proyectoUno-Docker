package edu.tecnm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import edu.tecnm.model.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    
    // Método para buscar un usuario por su username
    // Esencial para el login
    Optional<Usuario> findByUsername(String username);
    
    // Método para buscar por email (útil para validar duplicados)
    Optional<Usuario> findByEmail(String email);
    

    /**
     * Busca usuarios con el perfil 'CLIENTE' que no estén
     * ya asignados a un registro en la tabla 'Cliente'.
     */
    @Query("SELECT u FROM Usuario u JOIN u.perfiles p " +
           "WHERE p.perfil = 'CLIENTE' AND u.id NOT IN " +
           "(SELECT c.usuario.id FROM Cliente c WHERE c.usuario IS NOT NULL)")
    List<Usuario> findClientesSinAsignar();
}
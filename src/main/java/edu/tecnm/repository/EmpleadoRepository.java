package edu.tecnm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import edu.tecnm.model.Empleado;
import edu.tecnm.model.Usuario;

public interface EmpleadoRepository extends JpaRepository<Empleado, String> {

	// Busca empleados cuyo nombre completo contenga la cadena proporcionada
    List<Empleado> findByNombreCompletoContainingIgnoreCase(String texto);
    
    List<Empleado> findByPuestoIgnoreCase(String puesto);
    
    @Query("SELECT e FROM Empleado e JOIN e.usuario u WHERE u.username = :username")
    Empleado findByUsuarioUsername(@Param("username") String username);
    
    Empleado findByUsuario(Usuario usuario);
    
    Empleado findByUsuario_Id(Integer idUsuario);
    
}
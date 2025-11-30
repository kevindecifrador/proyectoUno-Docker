package edu.tecnm.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import edu.tecnm.model.Cliente;
import edu.tecnm.model.Usuario;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

	// 1. Busca un cliente por su nombre exacto
    Optional<Cliente> findByNombre(String nombre);

    // 2. Busca clientes cuyo nombre contenga una cadena
    List<Cliente> findByNombreContaining(String cadena);

    // 3. Busca un cliente por su email exacto
    Optional<Cliente> findByEmail(String email);

    // 4. Busca clientes cuyo email termine con "@gmail.com"
    List<Cliente> findByEmailEndingWith(String sufijo);

    // 5. Busca clientes con crédito entre dos valores
    List<Cliente> findByCreditoBetween(double min, double max);

    // 6. Busca clientes con crédito mayor a un monto
    List<Cliente> findByCreditoGreaterThan(double monto);

    // 7. Busca clientes destacados (destacado = 1)
    List<Cliente> findByDestacado(int estatus);

    // 8. Busca clientes por nombre Y con crédito mayor a un valor
    List<Cliente> findByNombreAndCreditoGreaterThan(String nombre, double valor);

    // 9. Busca clientes cuya foto sea "no_imagen.jpg"
    List<Cliente> findByFotocliente(String nombreFoto);

    // 10. Busca clientes destacados Y con crédito mayor a un valor
    List<Cliente> findByDestacadoAndCreditoGreaterThan(int estatus, double valor);

    // 11. Busca los 5 clientes con el crédito más alto
    List<Cliente> findTop5ByOrderByCreditoDesc();
    
    @Query("SELECT c FROM Cliente c JOIN c.usuario u WHERE u.username = :username")
    Cliente findByUsuarioUsername(@Param("username") String username);
    
    Cliente findByUsuario(Usuario usuario);
    
    Cliente findByUsuario_Id(Integer idUsuario);
    
}

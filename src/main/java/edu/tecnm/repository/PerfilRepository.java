package edu.tecnm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import edu.tecnm.model.Perfil;
import java.util.Optional;

public interface PerfilRepository extends JpaRepository<Perfil, Integer> {
    
    Optional<Perfil> findByPerfil(String perfil);

}
package edu.tecnm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import edu.tecnm.model.Mesa;

public interface MesaRepository extends JpaRepository<Mesa, Integer> {
    
}
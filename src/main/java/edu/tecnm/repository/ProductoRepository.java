package edu.tecnm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import edu.tecnm.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    
	// Busca productos que coincidan exactamente con un tipo (ej: "bebida")
    List<Producto> findByTipoIgnoreCase(String tipo);

    // Busca productos con un precio menor o igual al valor dado
    List<Producto> findByPrecioLessThanEqual(double precioMax);
    
    // Busca productos de un tipo específico Y con un precio menor o igual
    List<Producto> findByTipoIgnoreCaseAndPrecioLessThanEqual(String tipo, double precioMax);
    
    // Busca productos cuyo nombre contenga una cadena de texto
    List<Producto> findByNombreContainingIgnoreCase(String nombre);
    
    // Busca productos por nombre Y tipo (ambos conteniendo)
    List<Producto> findByNombreContainingAndTipoContainingAllIgnoreCase(String nombre, String tipo);
    
    List<Producto> findByPrecioBetween(double precioMin, double precioMax);
    List<Producto> findByPrecioGreaterThanEqual(double precioMin);
    List<Producto> findByTipoIgnoreCaseAndPrecioBetween(String tipo, double precioMin, double precioMax);
    List<Producto> findByTipoIgnoreCaseAndPrecioGreaterThanEqual(String tipo, double precioMin);
}
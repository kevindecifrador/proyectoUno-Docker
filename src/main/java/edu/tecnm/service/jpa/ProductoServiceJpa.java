package edu.tecnm.service.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import edu.tecnm.model.Producto;
import edu.tecnm.repository.ProductoRepository;
import edu.tecnm.service.IProductoService;

@Service
@Primary // Marcamos esta como la implementación de servicio principal para Producto
public class ProductoServiceJpa implements IProductoService {

    @Autowired
    private ProductoRepository repoProducto;

    @Override
    public List<Producto> buscarTodosProductos() {
        return repoProducto.findAll();
    }

    @Override
    public Producto buscarPorIdProducto(Integer idProducto) {
        Optional<Producto> optional = repoProducto.findById(idProducto);
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
    }

    @Override
    public void guardarProducto(Producto producto) {
    	System.out.println("[SERVICE] Guardando: " + producto);
        repoProducto.save(producto);
    }

    @Override
    public void eliminarProducto(Integer idProducto) {
        repoProducto.deleteById(idProducto);
    }
    
    @Override
    public List<Producto> buscarPorTipo(String tipo) {
        return repoProducto.findByTipoIgnoreCase(tipo);
    }

    @Override
    public List<Producto> buscarPorPrecioMaximo(double precioMax) {
        return repoProducto.findByPrecioLessThanEqual(precioMax);
    }

    @Override
    public List<Producto> buscarPorTipoYPrecioMaximo(String tipo, double precioMax) {
        return repoProducto.findByTipoIgnoreCaseAndPrecioLessThanEqual(tipo, precioMax);
    }
    
    @Override
    public List<Producto> buscarPorPrecioEntre(double min, double max) {
        return repoProducto.findByPrecioBetween(min, max);
    }

    @Override
    public List<Producto> buscarPorPrecioMayorQue(double min) {
        return repoProducto.findByPrecioGreaterThanEqual(min);
    }

    @Override
    public List<Producto> buscarPorTipoYPrecioEntre(String tipo, double min, double max) {
        return repoProducto.findByTipoIgnoreCaseAndPrecioBetween(tipo, min, max);
    }

    @Override
    public List<Producto> buscarPorTipoYPrecioMayorQue(String tipo, double min) {
        return repoProducto.findByTipoIgnoreCaseAndPrecioGreaterThanEqual(tipo, min);
    }

	@Override
	public List<Producto> buscarPorNombreYTipo(String nombre, String tipo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Producto> buscarPorNombre(String nombre) {
		// TODO Auto-generated method stub
		return null;
	}

}
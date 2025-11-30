package edu.tecnm.service;

import java.util.List;
import edu.tecnm.model.Producto;

public interface IProductoService {
    List<Producto> buscarTodosProductos();
    Producto buscarPorIdProducto(Integer idProducto);
    
    void guardarProducto(Producto producto);
    
    void eliminarProducto(Integer idProducto);
    
    List<Producto> buscarPorTipo(String tipo);
    List<Producto> buscarPorPrecioMaximo(double precioMax);
    List<Producto> buscarPorTipoYPrecioMaximo(String tipo, double precioMax);
    
    List<Producto> buscarPorNombreYTipo(String nombre, String tipo);
    List<Producto> buscarPorNombre(String nombre);
    
    List<Producto> buscarPorPrecioEntre(double min, double max);
    List<Producto> buscarPorPrecioMayorQue(double min);
    List<Producto> buscarPorTipoYPrecioEntre(String tipo, double min, double max);
    List<Producto> buscarPorTipoYPrecioMayorQue(String tipo, double min);
	
}

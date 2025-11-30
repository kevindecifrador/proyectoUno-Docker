package edu.tecnm.service.impl;

import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Service;

import edu.tecnm.model.Producto;
import edu.tecnm.service.IProductoService;

@Service
public class ProductoServiceImpl implements IProductoService {

    private List<Producto> listaProductos;

    public ProductoServiceImpl() {
        listaProductos = new LinkedList<>();

        //PLATILLOS
        Producto p1 = new Producto();
        p1.setIdProducto(1);
        p1.setNombre("Tacos al pastor");
        p1.setDescripcion("Orden de 3 Tacos tradicionales con piña y cilantro");
        p1.setPrecio(65);
        p1.setTipo("platillo");
        p1.setFotoProducto("tacospastor.jpg");
        
        Producto p11 = new Producto();
        p11.setIdProducto(11);
        p11.setNombre("Tacos de suadero");
        p11.setDescripcion("Tacos de carne de suadero con cilantro");
        p11.setPrecio(55);
        p11.setTipo("platillo");
        p11.setFotoProducto("tacossuadero.jpg");
        
        Producto p12 = new Producto();
        p12.setIdProducto(12);
        p12.setNombre("Tacos de carnitas");
        p12.setDescripcion("Orden de 3 Tacos de carnistas con cilantro");
        p12.setPrecio(65);
        p12.setTipo("platillo");
        p12.setFotoProducto("tacoscarnitas.jpg");
        
        Producto p13 = new Producto();
        p13.setIdProducto(13);
        p13.setNombre("Pozole Blanco");
        p13.setDescripcion("Cazuela de Pozole blanco de tamaño Normal");
        p13.setPrecio(45);
        p13.setTipo("platillo");
        p13.setFotoProducto("pozoleblanco.jpg");
        
        Producto p14 = new Producto();
        p14.setIdProducto(14);
        p14.setNombre("Pozole Verde");
        p14.setDescripcion("Cazuela de Pozole Verde tradicional de Guerrero de tamaño Normal");
        p14.setPrecio(45);
        p14.setTipo("platillo");
        p14.setFotoProducto("pozoleverde.jpg");

        //BEBIDAS
        Producto p2 = new Producto();
        p2.setIdProducto(2);
        p2.setNombre("Coca-cola 600 ml");
        p2.setDescripcion("Refresco Coca-cola");
        p2.setPrecio(25);
        p2.setTipo("bebida");
        p2.setFotoProducto("cocaola.jpg");
        
        Producto p21 = new Producto();
        p21.setIdProducto(21);
        p21.setNombre("Yoli 600 ml");
        p21.setDescripcion("Refresco Yoli");
        p21.setPrecio(25);
        p21.setTipo("bebida");
        p21.setFotoProducto("yoli.jpg");
        
        Producto p22 = new Producto();
        p22.setIdProducto(22);
        p22.setNombre("Clericot");
        p22.setDescripcion("Fascinante bebida Clericot con manzana, nuez, fresa y vino al gusto");
        p22.setPrecio(55);
        p22.setTipo("bebida");
        p22.setFotoProducto("clericot.jpg");
        
        Producto p23 = new Producto();
        p23.setIdProducto(23);
        p23.setNombre("Margarita");
        p23.setDescripcion("Fascinante copa de Margarita con alcohol al gusto ");
        p23.setPrecio(65);
        p23.setTipo("bebida");
        p23.setFotoProducto("margarita.jpg");

        //POSTRES
        Producto p3 = new Producto();
        p3.setIdProducto(3);
        p3.setNombre("Flan");
        p3.setDescripcion("Postre casero napolitano");
        p3.setPrecio(35);
        p3.setTipo("postre");
        p3.setFotoProducto("flan.jpg");
        
        Producto p31 = new Producto();
        p31.setIdProducto(31);
        p31.setNombre("Mus de Mango");
        p31.setDescripcion("Postre casero de mango");
        p31.setPrecio(35);
        p31.setTipo("postre");
        p31.setFotoProducto("musmango.jpg");
        
        Producto p32 = new Producto();
        p32.setIdProducto(32);
        p32.setNombre("Mus de Durazno");
        p32.setDescripcion("Postre casero de durazno");
        p32.setPrecio(35);
        p32.setTipo("postre");
        p32.setFotoProducto("musdurazno.jpg");
        
        Producto p33 = new Producto();
        p33.setIdProducto(33);
        p33.setNombre("Mus de Fresa");
        p33.setDescripcion("Postre casero de fresa");
        p33.setPrecio(35);
        p33.setTipo("postre");
        p33.setFotoProducto("musfresa.jpg");
        
        Producto p34 = new Producto();
        p34.setIdProducto(34);
        p34.setNombre("Fresas con crema");
        p34.setDescripcion("Postre Fresas con crema");
        p34.setPrecio(35);
        p34.setTipo("postre");
        p34.setFotoProducto("fresasconcrema.jpg");

        listaProductos.add(p1);
        listaProductos.add(p11);
        listaProductos.add(p12);
        listaProductos.add(p13);
        listaProductos.add(p14);
        
        listaProductos.add(p2);
        listaProductos.add(p21);
        listaProductos.add(p22);
        listaProductos.add(p23);
        
        listaProductos.add(p3);
        listaProductos.add(p31);
        listaProductos.add(p32);
        listaProductos.add(p33);
        listaProductos.add(p34);
    }

    @Override
    public List<Producto> buscarTodosProductos() {
        return listaProductos;
    }

    @Override
    public Producto buscarPorIdProducto(Integer idProducto) {
        for (Producto p : listaProductos) {
            if (p.getIdProducto().equals(idProducto)) {
                return p;
            }
        }
        return null;
    }
    
    @Override
    public void guardarProducto(Producto producto) {
        // simulacion de un ID autoincremental
        if (producto.getIdProducto() == null) {
            int maxId = 0;
            // busqueda de el ID mas alto en la lista actual
            for (Producto p : listaProductos) {
                if (p.getIdProducto() > maxId) {
                    maxId = p.getIdProducto();
                }
            }
            // siguiente ID
            producto.setIdProducto(maxId + 1);
        }
        
        // Nuevo producto a la lista
        this.listaProductos.add(producto);
    }

	@Override
	public void eliminarProducto(Integer idProducto) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Producto> buscarPorTipo(String tipo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Producto> buscarPorPrecioMaximo(double precioMax) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Producto> buscarPorTipoYPrecioMaximo(String tipo, double precioMax) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Producto> buscarPorNombre(String nombre) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Producto> buscarPorNombreYTipo(String nombre, String tipo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Producto> buscarPorPrecioEntre(double min, double max) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Producto> buscarPorPrecioMayorQue(double min) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Producto> buscarPorTipoYPrecioEntre(String tipo, double min, double max) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Producto> buscarPorTipoYPrecioMayorQue(String tipo, double min) {
		// TODO Auto-generated method stub
		return null;
	}
}

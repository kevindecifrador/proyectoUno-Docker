package edu.tecnm.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tecnm.model.Cliente;
import edu.tecnm.model.DetallePedido;
import edu.tecnm.model.Pedido;
import edu.tecnm.model.Producto;
import edu.tecnm.service.CloudinaryService;
import edu.tecnm.service.IClienteService;
import edu.tecnm.service.IEmpleadoService;
import edu.tecnm.service.IPedidoService;
import edu.tecnm.service.IProductoService;
import edu.tecnm.service.IReservacionService;
import edu.tecnm.util.UploadFileHelper; 

@Controller
public class ProductoController {

    @Autowired
    private IProductoService productoService;
    
    @Autowired
    private IClienteService clienteService;
    
    @Autowired private IEmpleadoService serviceEmpleado;
    @Autowired private IReservacionService serviceReservacion;
    
    @Autowired
    private IPedidoService servicePedido;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private CloudinaryService cloudinaryService;
    
    
    // para listar productos
    @GetMapping("/productos")
    public String inicioProductos(Model model, 
            @RequestParam(value = "tipo", required = false) String tipo,
            @RequestParam(value = "precioMin", required = false) Double precioMin,
            @RequestParam(value = "precioMax", required = false) Double precioMax,
    		@RequestParam(value = "editarPedidoId", required = false) Integer editarPedidoId){

        List<Producto> productosFiltrados; // La causa del error
        boolean hasTipo = (tipo != null && !tipo.isEmpty());
        boolean hasMin = (precioMin != null);
        boolean hasMax = (precioMax != null);
        
        if (hasTipo) {
            if (hasMin && hasMax) {
                productosFiltrados = productoService.buscarPorTipoYPrecioEntre(tipo, precioMin, precioMax);
            } else if (hasMin) {
                productosFiltrados = productoService.buscarPorTipoYPrecioMayorQue(tipo, precioMin);
            } else if (hasMax) {
                productosFiltrados = productoService.buscarPorTipoYPrecioMaximo(tipo, precioMax);
            } else {
                productosFiltrados = productoService.buscarPorTipo(tipo);
            }
        } else {
            if (hasMin && hasMax) {
                productosFiltrados = productoService.buscarPorPrecioEntre(precioMin, precioMax);
            } else if (hasMin) {
                productosFiltrados = productoService.buscarPorPrecioMayorQue(precioMin);
            } else if (hasMax) {
                // CORREGIDO: Llamar al método que sí existe
                productosFiltrados = productoService.buscarPorPrecioMaximo(precioMax);
            } else {
                productosFiltrados = productoService.buscarTodosProductos();
            }
        }

        // El error ocurre en la siguiente línea si productosFiltrados es null
        List<Producto> platillos = productosFiltrados.stream()
            .filter(p -> "platillo".equalsIgnoreCase(p.getTipo()))
            .collect(Collectors.toList());

        List<Producto> bebidas = productosFiltrados.stream()
            .filter(p -> "bebida".equalsIgnoreCase(p.getTipo()))
            .collect(Collectors.toList());

        List<Producto> postres = productosFiltrados.stream()
            .filter(p -> "postre".equalsIgnoreCase(p.getTipo()))
            .collect(Collectors.toList());

        model.addAttribute("platillos", platillos);
        model.addAttribute("bebidas", bebidas);
        model.addAttribute("postres", postres);
        
     // LÓGICA DE EDICIÓN DE PEDIDO
        if (editarPedidoId != null) {
            Pedido pedidoParaEditar = servicePedido.buscarPorId(editarPedidoId);
            if (pedidoParaEditar != null) {
                // Pasamos el pedido completo para pre-seleccionar los dropdowns
                model.addAttribute("pedidoParaEditar", pedidoParaEditar); 
                
                // Creamos un JSON que tu JavaScript de "cart" pueda entender
                List<Map<String, Object>> cartItems = new ArrayList<>();
                for (DetallePedido detalle : pedidoParaEditar.getDetalles()) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", detalle.getProducto().getIdProducto());
                    item.put("name", detalle.getProducto().getNombre());
                    item.put("price", detalle.getPrecio());
                    item.put("quantity", detalle.getCantidad());
                    cartItems.add(item);
                }
                
                try {
                    model.addAttribute("carritoParaEditarJSON", objectMapper.writeValueAsString(cartItems));
                    model.addAttribute("pedidoIdParaEditar", editarPedidoId);
                } catch (Exception e) {
                    // Manejar error de JSON
                }
            }
        }
        
        model.addAttribute("clientes", clienteService.buscarTodosClientes());
        model.addAttribute("empleados", serviceEmpleado.buscarPorPuesto("Mesero"));

        LocalDate hoy = LocalDate.now();
        model.addAttribute("reservacionesActivas", serviceReservacion.buscarPorFecha(hoy));

        return "producto/InicioRestaurante";
    }

    @GetMapping("/productos/{id}")
    public String verDetalleProducto(@PathVariable Integer id, Model model) {
        model.addAttribute("producto", productoService.buscarPorIdProducto(id));
        return "producto/detallesproduc";
    }

    // FORMULARIO
    @GetMapping("/crearProducto")
    public String crearProducto(Model model) {
        model.addAttribute("producto", new Producto());
        return "producto/formProducto";
    }
    
    @PostMapping("/guardarProducto")
    public String guardarProducto(Producto producto, 
                                  @RequestParam("imagenFile") MultipartFile file, 
                                  RedirectAttributes attributes) {
        
        if (!file.isEmpty()) {
            // CASO 1: Hay imagen nueva -> Subir a Cloudinary
            String urlImagen = cloudinaryService.subirArchivo(file);
            
            if (urlImagen != null) {
                // Guardamos la URL de internet en la BD
                producto.setFotoProducto(urlImagen);
            }
        } else {
            // CASO 2: No hay imagen nueva -> Mantener la anterior
            if (producto.getIdProducto() != null) {
                Producto pExistente = productoService.buscarPorIdProducto(producto.getIdProducto());
                if (pExistente != null) {
                    producto.setFotoProducto(pExistente.getFotoProducto());
                }
            }
        }

        productoService.guardarProducto(producto);
        attributes.addFlashAttribute("success", "Producto guardado correctamente.");
        return "redirect:/productos/admin";
    }
    
    /**
     * Muestra la lista de productos para el administrador, con filtros.
     */
    @GetMapping("/productos/admin")
    public String listarProductosAdmin(Model model, 
                                       @RequestParam(value="nombre", required=false) String nombre,
                                       @RequestParam(value="tipo", required=false) String tipo) {
        List<Producto> productos;
        if (nombre != null && !nombre.isEmpty() && tipo != null && !tipo.isEmpty()) {
            productos = productoService.buscarPorNombreYTipo(nombre, tipo);
        } else if (nombre != null && !nombre.isEmpty()) {
            productos = productoService.buscarPorNombre(nombre);
        } else if (tipo != null && !tipo.isEmpty()) {
            productos = productoService.buscarPorTipo(tipo);
        } else {
            productos = productoService.buscarTodosProductos();
        }
        model.addAttribute("productos", productos);
        return "producto/listaProductosAdmin";
    }

    /**
     * Muestra el formulario para modificar un producto existente.
     * Reutiliza la vista formProducto.html
     */
    @GetMapping("/productos/modificar/{id}")
    public String modificarProducto(@PathVariable("id") Integer idProducto, Model model) {
        Producto producto = productoService.buscarPorIdProducto(idProducto);
        model.addAttribute("producto", producto);
        return "producto/formProducto";
    }

    /**
     * Elimina un producto de la base de datos.
     */
    @GetMapping("/productos/eliminar/{id}")
    public String eliminarProducto(@PathVariable("id") Integer idProducto, RedirectAttributes attributes) {
        productoService.eliminarProducto(idProducto);
        attributes.addFlashAttribute("success", "Producto eliminado con éxito.");
        return "redirect:/productos/admin";
    }
}

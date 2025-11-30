package edu.tecnm.controller;

import java.awt.Color;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

// Imports para PDF
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import edu.tecnm.dto.CartItemDto;
import edu.tecnm.dto.PedidoRequestDto;
import edu.tecnm.model.Cliente;
import edu.tecnm.model.DetallePedido;
import edu.tecnm.model.Empleado;
import edu.tecnm.model.EstatusReservacion;
import edu.tecnm.model.Pedido;
import edu.tecnm.model.Producto;
import edu.tecnm.model.Reservacion;
import edu.tecnm.service.IClienteService;
import edu.tecnm.service.IEmpleadoService;
import edu.tecnm.service.IPedidoService;
import edu.tecnm.service.IProductoService;
import edu.tecnm.service.IReservacionService;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    @Autowired private IPedidoService servicePedido;
    @Autowired private IProductoService serviceProducto;
    @Autowired private IClienteService serviceCliente;
    @Autowired private IEmpleadoService serviceEmpleado;
    @Autowired private IReservacionService serviceReservacion;

    @GetMapping
    public String listarPedidos(Model model,
            @RequestParam(value="fechaInicio", required=false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(value="fechaFin", required=false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(value="clienteId", required=false) Integer clienteId,
            @RequestParam(value="empleadoClave", required=false) String empleadoClave) {

        // 1. Obtener usuario y roles
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        boolean esMesero = auth.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("MESERO"));
        boolean esCliente = auth.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("CLIENTE"));
        boolean esAdmin = auth.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ADMIN") || r.getAuthority().equals("CAJERO"));

        List<Pedido> pedidos = new ArrayList<>();

        // 2. Lógica de Filtrado por Rol
        if (esMesero) {
            // --- MESERO: Solo sus mesas/pedidos ---
            Empleado mesero = serviceEmpleado.buscarPorUsuario(username);
            if (mesero != null) {
                // Usa tu método existente buscarPorEmpleadoClave
                pedidos = servicePedido.buscarPorEmpleadoClave(mesero.getClave());
            }
            
        } else if (esCliente) {
            // --- CLIENTE: Solo sus compras ---
            Cliente cliente = serviceCliente.buscarPorUsuario(username);
            if (cliente != null) {
                // Usa tu método existente buscarPorClienteId
                pedidos = servicePedido.buscarPorClienteId(cliente.getId());
            }
            
        } else {
            // --- ADMIN/CAJERO/COCINERO: Ven todo y usan filtros ---
            if (fechaInicio != null && fechaFin != null) {
                pedidos = servicePedido.buscarPorFechaEntre(fechaInicio, fechaFin);
            } else if (fechaInicio != null) {
                pedidos = servicePedido.buscarPorFecha(fechaInicio);
            } else if (clienteId != null) {
                pedidos = servicePedido.buscarPorClienteId(clienteId);
            } else if (empleadoClave != null && !empleadoClave.isEmpty()) {
                pedidos = servicePedido.buscarPorEmpleadoClave(empleadoClave);
            } else {
                pedidos = servicePedido.buscarTodos();
            }
        }

        model.addAttribute("pedidos", pedidos);
        
        // Cargamos listas para los filtros (solo si es admin vale la pena, pero no estorba dejarlas)
        model.addAttribute("clientes", serviceCliente.buscarTodosClientes());
        model.addAttribute("empleados", serviceEmpleado.buscarTodos());
        
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        model.addAttribute("objectMapper", objectMapper);
        
        return "pedido/listaPedidos";
    }
   
    /**
     * Recibe los datos del carrito (en formato JSON) desde el JavaScript,
     * crea el pedido y lo guarda en la base de datos.
     */
    @PostMapping("/crear")
    @ResponseBody
    public ResponseEntity<?> crearPedido(@RequestBody PedidoRequestDto pedidoRequest, RedirectAttributes attributes) {

        // 1. Buscar entidades relacionadas (Cliente y Empleado)
        Cliente cliente = serviceCliente.buscarPorIdCliente(pedidoRequest.getClienteId());
        Empleado empleado = serviceEmpleado.buscarPorClave(pedidoRequest.getEmpleadoClave());

        if (cliente == null || empleado == null) {
            return ResponseEntity.status(400).body("Cliente o empleado no encontrado.");
        }

        // 2. VALIDACIÓN DE RESERVACIÓN 
        // PASO A: FORZAMOS QUE EL ID EXISTA
        if (pedidoRequest.getReservacionId() == null) {
            return ResponseEntity.status(400).body("Error: Es obligatorio seleccionar una reservación confirmada para crear este pedido.");
        }
        
        // PASO B: Si el ID no es nulo, validamos la reservación
        Reservacion reservacion = serviceReservacion.buscarPorId(pedidoRequest.getReservacionId());
        LocalDate hoy = LocalDate.now();

        if (reservacion == null) {
            return ResponseEntity.status(400).body("La reservación indicada no existe.");
        }
        if (reservacion.getEstatus() != EstatusReservacion.CONFIRMADA) {
            return ResponseEntity.status(400).body("Error: El pedido solo puede asociarse a reservaciones CONFIRMADAS. Esta se encuentra: " + reservacion.getEstatus());
        }
        if (!reservacion.getFecha().isEqual(hoy)) {
            return ResponseEntity.status(400).body("Error: El pedido solo puede asociarse a reservaciones para el día de HOY (" + hoy + ").");
        }
        
        // Si llegamos aquí, la reservación es válida.
        // 3. Crear el objeto Pedido y asignar relaciones
        Pedido pedido = new Pedido();
        pedido.setFecha(LocalDate.now());
        pedido.setCliente(cliente);
        pedido.getEmpleadosQueAtienden().add(empleado);
        pedido.setReservacion(reservacion);

        // 4. Procesar los detalles del pedido
        List<DetallePedido> detalles = new ArrayList<>();
        double totalCalculado = 0.0;
        for (CartItemDto item : pedidoRequest.getItems()) {
            Producto producto = serviceProducto.buscarPorIdProducto(item.getId());

            if (producto != null) {
                DetallePedido detalle = new DetallePedido();
                detalle.setCantidad(item.getQuantity());
                detalle.setPrecio(producto.getPrecio());
                detalle.setProducto(producto);
                detalle.setPedido(pedido); 
                detalles.add(detalle);
                totalCalculado += detalle.getSubtotal();
            }
        }

        pedido.setTotal(totalCalculado);
        pedido.setDetalles(detalles); 
        
        // 5. Guardar el pedido
        servicePedido.guardar(pedido);

        return ResponseEntity.ok().build();
    }
    
    /**
     * Elimina un pedido por su ID y redirige a la lista.
     */
    @GetMapping("/eliminar/{id}")
    public String eliminarPedido(@PathVariable("id") Integer idPedido, RedirectAttributes attributes) {
        servicePedido.eliminar(idPedido);
        attributes.addFlashAttribute("success", "Pedido #" + idPedido + " eliminado con éxito.");
        return "redirect:/pedidos";
    }
    
    /**
     * Recibe los datos del carrito (en formato JSON) de un pedido existente
     * y actualiza sus detalles.
     */
    @PostMapping("/actualizar/{id}")
    @ResponseBody
    public ResponseEntity<?> actualizarPedido(@PathVariable("id") Integer idPedido,
                                              @RequestBody PedidoRequestDto pedidoRequest) {
        
        // 1. Cargar el pedido existente
        Pedido pedido = servicePedido.buscarPorId(idPedido);
        if (pedido == null) {
            return ResponseEntity.status(404).body("El pedido que intentas actualizar no existe.");
        }

        // 2. Cargar entidades relacionadas (Cliente y Empleado)
        Cliente cliente = serviceCliente.buscarPorIdCliente(pedidoRequest.getClienteId());
        Empleado empleado = serviceEmpleado.buscarPorClave(pedidoRequest.getEmpleadoClave());

        if (cliente == null || empleado == null) {
            return ResponseEntity.status(400).body("Cliente o empleado no encontrado.");
        }
        
     // 2. Validar la Reservación (SI SE PROPORCIONÓ UNA)
        Reservacion reservacion = null;
        if (pedidoRequest.getReservacionId() != null) {
            reservacion = serviceReservacion.buscarPorId(pedidoRequest.getReservacionId());
            LocalDate hoy = LocalDate.now();

            // VALIDACIONES DE LA RESERVACIÓN
            if (reservacion == null) {
                return ResponseEntity.status(400).body("La reservación indicada no existe.");
            }
            if (reservacion.getEstatus() != EstatusReservacion.CONFIRMADA) {
                return ResponseEntity.status(400).body("Error: El pedido solo puede asociarse a reservaciones CONFIRMADAS.");
            }
            if (!reservacion.getFecha().isEqual(hoy)) {
                return ResponseEntity.status(400).body("Error: El pedido solo puede asociarse a reservaciones para el día de HOY (" + hoy + ").");
            }
        }
        
        // 3. Actualizar los datos principales
        pedido.setCliente(cliente);
        pedido.getEmpleadosQueAtienden().clear();
        pedido.getEmpleadosQueAtienden().add(empleado);
        pedido.setReservacion(reservacion);
        
        
        pedido.getDetalles().clear(); // Borra los detalles viejos

        List<DetallePedido> nuevosDetalles = new ArrayList<>();
        double totalCalculado = 0.0;
        
        for (CartItemDto item : pedidoRequest.getItems()) {
            Producto producto = serviceProducto.buscarPorIdProducto(item.getId());
            if (producto != null) {
                DetallePedido detalle = new DetallePedido();
                detalle.setCantidad(item.getQuantity());
                detalle.setPrecio(producto.getPrecio());
                detalle.setProducto(producto);
                detalle.setPedido(pedido); // VINCULA AL PEDIDO PADRE
                nuevosDetalles.add(detalle);
                totalCalculado += detalle.getSubtotal();
            }
        }

        pedido.getDetalles().addAll(nuevosDetalles); // Agrega los nuevos
        pedido.setTotal(totalCalculado);
        
        // 5. Guardar los cambios
        servicePedido.guardar(pedido); 

        return ResponseEntity.ok().build();
    }
    
    /**
     * Genera un ticket en PDF para un pedido específico.
     */
    @GetMapping("/ticket/{id}")
    public void generarTicketPdf(@PathVariable("id") Integer idPedido, HttpServletResponse response)
            throws IOException, DocumentException {

        // --- PDF en la respuesta
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=ticket_pedido_" + idPedido + ".pdf");

        // --- Buscar pedido
        Pedido pedido = servicePedido.buscarPorId(idPedido);
        if (pedido == null) {
            response.getWriter().write("Error: Pedido no encontrado.");
            return;
        }

        // --- Configurar “ticket” 80mm (≈226 pt) con márgenes pequeños
        // alto generoso para evitar cortes en la mayoría de pedidos
        Rectangle ticketSize = new Rectangle(226f, 1000f);
        Document document = new Document(ticketSize, 10f, 10f, 12f, 12f);
        PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        // --- Fuentes y helpers
        Font fTitle   = FontFactory.getFont(FontFactory.COURIER_BOLD, 12, Color.BLACK);
        Font fSub     = FontFactory.getFont(FontFactory.COURIER_BOLD, 9, Color.BLACK);
        Font fBold    = FontFactory.getFont(FontFactory.COURIER_BOLD, 8, Color.BLACK);
        Font fNormal  = FontFactory.getFont(FontFactory.COURIER, 8, Color.BLACK);
        Font fTiny    = FontFactory.getFont(FontFactory.COURIER, 7, Color.DARK_GRAY);

        // Separador punteado
        com.lowagie.text.pdf.draw.DottedLineSeparator dots = new com.lowagie.text.pdf.draw.DottedLineSeparator();
        dots.setGap(2f); dots.setLineWidth(0.7f);

        // --- Encabezado (centrado, estilo ticket)
        Paragraph h = new Paragraph("RESTAURANTE PLUS\n", fTitle);
        h.setAlignment(Element.ALIGN_CENTER);
        h.setSpacingAfter(2f);
        document.add(h);

        Paragraph meta = new Paragraph(
                "Ticket de Compra\n" +
                ("Pedido #: " + pedido.getIdPedido()) + "\n" +
                ("Fecha: " + pedido.getFecha()), fSub);
        meta.setAlignment(Element.ALIGN_CENTER);
        document.add(meta);

        document.add(new Chunk(dots));
        document.add(Chunk.NEWLINE);

        // --- Cliente
        Paragraph cliente = new Paragraph(
                "Cliente: " + pedido.getCliente().getNombre() + " " + pedido.getCliente().getApellidos(),
                fNormal);
        cliente.setAlignment(Element.ALIGN_LEFT);
        document.add(cliente);

        // --- Atendido por (mismo dato, formato compacto)
        Paragraph atendidoPor = new Paragraph("Atendido por:", fNormal);
        atendidoPor.setSpacingBefore(2f);
        document.add(atendidoPor);
        for (Empleado emp : pedido.getEmpleadosQueAtienden()) {
            document.add(new Paragraph("  - " + emp.getNombreCompleto() + " (" + emp.getPuesto() + ")", fNormal));
        }

        // --- Reservación (si aplica)
        if (pedido.getReservacion() != null) {
            Paragraph res = new Paragraph(
                    "Reserva: #" + pedido.getReservacion().getIdServicio() +
                    "  (Mesa #" + pedido.getReservacion().getMesa().getIdMesa() + ")",
                    fNormal);
            res.setSpacingBefore(2f);
            document.add(res);
        }

        document.add(Chunk.NEWLINE);
        document.add(new Chunk(dots));
        document.add(Chunk.NEWLINE);

        // --- Detalles del Pedido (mismos campos, layout de ticket)
        Paragraph det = new Paragraph("Detalles del Pedido", fBold);
        det.setAlignment(Element.ALIGN_CENTER);
        det.setSpacingAfter(3f);
        document.add(det);

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{52f, 12f, 18f, 18f}); // Producto | Cant. | P.Unit | Subt.
        table.getDefaultCell().setBorder(Rectangle.NO_BORDER);

        // Encabezados
        String[] headers = {"Producto", "Cant.", "P. Unitario", "Subtotal"};
        for (int i = 0; i < headers.length; i++) {
            PdfPCell c = new PdfPCell(new Phrase(headers[i], fBold));
            c.setHorizontalAlignment(i == 0 ? Element.ALIGN_LEFT : Element.ALIGN_RIGHT);
            c.setBorder(Rectangle.NO_BORDER);
            c.setPaddingBottom(2f);
            table.addCell(c);
        }

        // Filas
        java.text.DecimalFormat df = new java.text.DecimalFormat("#,##0.00");
        for (DetallePedido d : pedido.getDetalles()) {
            PdfPCell c1 = new PdfPCell(new Phrase(d.getProducto().getNombre(), fNormal));
            c1.setBorder(Rectangle.NO_BORDER);
            table.addCell(c1);

            PdfPCell c2 = new PdfPCell(new Phrase(String.valueOf(d.getCantidad()), fNormal));
            c2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            c2.setBorder(Rectangle.NO_BORDER);
            table.addCell(c2);

            PdfPCell c3 = new PdfPCell(new Phrase("$" + df.format(d.getPrecio()), fNormal));
            c3.setHorizontalAlignment(Element.ALIGN_RIGHT);
            c3.setBorder(Rectangle.NO_BORDER);
            table.addCell(c3);

            PdfPCell c4 = new PdfPCell(new Phrase("$" + df.format(d.getSubtotal()), fNormal));
            c4.setHorizontalAlignment(Element.ALIGN_RIGHT);
            c4.setBorder(Rectangle.NO_BORDER);
            table.addCell(c4);
        }

        document.add(table);

        document.add(Chunk.NEWLINE);
        document.add(new Chunk(dots));

        // --- Total (mismo dato, estilo “ticket”)
        PdfPTable tot = new PdfPTable(2);
        tot.setWidthPercentage(100);
        tot.setWidths(new float[]{60f, 40f});
        tot.getDefaultCell().setBorder(Rectangle.NO_BORDER);

        PdfPCell lt = new PdfPCell(new Phrase("TOTAL", fSub));
        lt.setBorder(Rectangle.NO_BORDER);
        lt.setHorizontalAlignment(Element.ALIGN_LEFT);
        tot.addCell(lt);

        PdfPCell rt = new PdfPCell(new Phrase("$" + df.format(pedido.getTotal()), fTitle));
        rt.setBorder(Rectangle.NO_BORDER);
        rt.setHorizontalAlignment(Element.ALIGN_RIGHT);
        tot.addCell(rt);

        document.add(tot);

        document.add(Chunk.NEWLINE);
        document.add(new Chunk(dots));
        document.add(Chunk.NEWLINE);

        // --- Mensaje de cortesía (sin cambiar datos del ticket)
        Paragraph gracias = new Paragraph("¡Gracias por su visita!", fTiny);
        gracias.setAlignment(Element.ALIGN_CENTER);
        document.add(gracias);

        document.close();
        writer.close();
    }

}
package edu.tecnm.controller;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import edu.tecnm.model.Cliente;
import edu.tecnm.service.IClienteService;

@Controller
public class ClienteBusquedaController {

    @Autowired
    private IClienteService serviceCliente;

    @GetMapping("/busquedas")
    public String mostrarPaginaBusquedas(Model model) {
        // Pasamos un atributo para que la tabla no intente renderizarse la primera vez
        model.addAttribute("busquedaRealizada", false);
        return "cliente/BusquedasCliente";
    }

    @GetMapping("/busquedas/resultados")
    public String buscar(
            @RequestParam("tipo") String tipoBusqueda,
            @RequestParam(value="valor", required=false) String valor,
            @RequestParam(value="valor2", required=false) String valor2,
            Model model) {
        
        List<Cliente> resultados = new ArrayList<>();
        
        try {
            switch (tipoBusqueda) {
                // Requerimientos de la actividad:
                case "nombreExacto": // 1
                    serviceCliente.buscarPorNombre(valor).ifPresent(resultados::add);
                    break;
                case "nombreContiene": // 2
                    resultados = serviceCliente.buscarPorNombreConteniendo(valor);
                    break;
                case "emailExacto": // 3
                    serviceCliente.buscarPorEmail(valor).ifPresent(resultados::add);
                    break;
                case "emailGmail": // 4
                    resultados = serviceCliente.buscarPorEmailTerminandoCon("@gmail.com");
                    break;
                case "creditoEntre": // 5
                    resultados = serviceCliente.buscarPorCreditoEntre(Double.parseDouble(valor), Double.parseDouble(valor2));
                    break;
                case "creditoMayor": // 6
                    resultados = serviceCliente.buscarPorCreditoMayorQue(Double.parseDouble(valor));
                    break;
                case "destacados": // 7
                    resultados = serviceCliente.buscarPorDestacado(1);
                    break;
                case "nombreYCredito": // 8
                    resultados = serviceCliente.buscarPorNombreYCreditoMayorQue(valor, Double.parseDouble(valor2));
                    break;
                case "fotoNoImagen": // 9
                    resultados = serviceCliente.buscarPorFotocliente("no_imagen.jpg");
                    break;
                case "destacadoYCredito": // 10
                    resultados = serviceCliente.buscarPorDestacadoYCreditoMayorQue(1, Double.parseDouble(valor));
                    break;
                case "top5Credito": // 11
                    resultados = serviceCliente.buscarTop5PorCreditoDesc();
                    break;
            }
        } catch (NumberFormatException e) {
            //por si el usuario no introduce un numero valido
            System.err.println("Error de formato de número: " + e.getMessage());
        }

        model.addAttribute("clientesEncontrados", resultados);
        model.addAttribute("busquedaRealizada", true);
        return "cliente/BusquedasCliente";
    }
}
package edu.tecnm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import edu.tecnm.model.Mesa;
import edu.tecnm.service.IMesaService;

@Controller
@RequestMapping("/mesas")
public class MesaController {

    @Autowired
    private IMesaService serviceMesa;

    @GetMapping
    public String listarMesas(Model model) {
        model.addAttribute("mesas", serviceMesa.buscarTodas());
        return "mesa/listaMesas";
    }

    @GetMapping("/crear")
    public String crearMesa(Model model) {
        model.addAttribute("mesa", new Mesa());
        return "mesa/formMesa";
    }

    @PostMapping("/guardar")
    public String guardarMesa(Mesa mesa, RedirectAttributes attributes) {
        serviceMesa.guardar(mesa);
        attributes.addFlashAttribute("success", "Mesa guardada con éxito.");
        return "redirect:/mesas";
    }

    @GetMapping("/modificar/{id}")
    public String modificarMesa(@PathVariable("id") Integer idMesa, Model model) {
        Mesa mesa = serviceMesa.buscarPorId(idMesa);
        model.addAttribute("mesa", mesa);
        return "mesa/formMesa";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarMesa(@PathVariable("id") Integer idMesa, RedirectAttributes attributes) {
        serviceMesa.eliminar(idMesa);
        attributes.addFlashAttribute("success", "Mesa eliminada con éxito.");
        return "redirect:/mesas";
    }
}
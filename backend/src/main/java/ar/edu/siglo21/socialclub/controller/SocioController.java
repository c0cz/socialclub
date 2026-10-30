package ar.edu.siglo21.socialclub.controller;

import ar.edu.siglo21.socialclub.exception.RecursoNoEncontradoException;
import ar.edu.siglo21.socialclub.exception.ReglaNegocioException;
import ar.edu.siglo21.socialclub.model.EstadoCuota;
import ar.edu.siglo21.socialclub.model.Socio;
import ar.edu.siglo21.socialclub.service.SocioService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/socios")
public class SocioController {

    private static final int TAMANIO_PAGINA = 5;

    private final SocioService socioService;

    public SocioController(SocioService socioService) {
        this.socioService = socioService;
    }

    @GetMapping
    public String listar(@RequestParam(defaultValue = "") String apellido,
                         @RequestParam(defaultValue = "0") int pagina,
                         Model model) {
        Page<Socio> page = socioService.listar(apellido, pagina, TAMANIO_PAGINA);

        model.addAttribute("socios", page.getContent());
        model.addAttribute("apellido", apellido);
        model.addAttribute("pagina", page.getNumber());
        model.addAttribute("totalPaginas", page.getTotalPages());
        return "socios/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        cargarFormulario(model, new Socio(), "Nuevo socio");
        return "socios/form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        try {
            cargarFormulario(model, socioService.buscarPorId(id), "Editar socio");
            return "socios/form";
        } catch (RecursoNoEncontradoException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/socios";
        }
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Socio socio, Model model, RedirectAttributes redirect) {
        try {
            socioService.guardar(socio);
            redirect.addFlashAttribute("mensaje", "Socio guardado correctamente");
            return "redirect:/socios";
        } catch (ReglaNegocioException e) {
            model.addAttribute("error", e.getMessage());
            cargarFormulario(model, socio, socio.getId() == null ? "Nuevo socio" : "Editar socio");
            return "socios/form";
        }
    }

    private void cargarFormulario(Model model, Socio socio, String titulo) {
        model.addAttribute("socio", socio);
        model.addAttribute("titulo", titulo);
        model.addAttribute("estados", EstadoCuota.values());
    }
}
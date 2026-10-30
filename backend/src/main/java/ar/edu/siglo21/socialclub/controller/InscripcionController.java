package ar.edu.siglo21.socialclub.controller;

import ar.edu.siglo21.socialclub.exception.RecursoNoEncontradoException;
import ar.edu.siglo21.socialclub.exception.ReglaNegocioException;
import ar.edu.siglo21.socialclub.model.Inscripcion;
import ar.edu.siglo21.socialclub.service.ActividadService;
import ar.edu.siglo21.socialclub.service.InscripcionService;
import ar.edu.siglo21.socialclub.service.SocioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/inscripciones")
public class InscripcionController {

    private final InscripcionService inscripcionService;
    private final SocioService socioService;
    private final ActividadService actividadService;

    public InscripcionController(InscripcionService inscripcionService,
                                 SocioService socioService,
                                 ActividadService actividadService) {
        this.inscripcionService = inscripcionService;
        this.socioService = socioService;
        this.actividadService = actividadService;
    }

    @GetMapping("/nueva")
    public String nueva(Model model) {
        cargarListas(model);
        return "inscripciones/form";
    }

    @PostMapping("/guardar")
    public String guardar(@RequestParam Long socioId, @RequestParam Long actividadId,
                          Model model, RedirectAttributes redirect) {
        try {
            inscripcionService.inscribir(socioId, actividadId);
            redirect.addFlashAttribute("mensaje", "Inscripción realizada correctamente");
            return "redirect:/actividades/" + actividadId + "/inscriptos";
        } catch (ReglaNegocioException | RecursoNoEncontradoException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("socioId", socioId);
            model.addAttribute("actividadId", actividadId);
            cargarListas(model);
            return "inscripciones/form";
        }
    }

    @PostMapping("/{id}/baja")
    public String baja(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            Inscripcion inscripcion = inscripcionService.darDeBaja(id);
            redirect.addFlashAttribute("mensaje", "Inscripción dada de baja");
            return "redirect:/actividades/" + inscripcion.getActividad().getId() + "/inscriptos";
        } catch (RecursoNoEncontradoException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/actividades";
        }
    }

    private void cargarListas(Model model) {
        model.addAttribute("socios", socioService.listarTodos());
        model.addAttribute("actividades", actividadService.listar());
    }
}
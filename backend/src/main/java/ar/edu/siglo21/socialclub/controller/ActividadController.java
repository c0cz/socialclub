package ar.edu.siglo21.socialclub.controller;

import ar.edu.siglo21.socialclub.exception.RecursoNoEncontradoException;
import ar.edu.siglo21.socialclub.exception.ReglaNegocioException;
import ar.edu.siglo21.socialclub.model.Actividad;
import ar.edu.siglo21.socialclub.service.ActividadService;
import ar.edu.siglo21.socialclub.service.InscripcionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/actividades")
public class ActividadController {

    private final ActividadService actividadService;
    private final InscripcionService inscripcionService;

    public ActividadController(ActividadService actividadService,
                               InscripcionService inscripcionService) {
        this.actividadService = actividadService;
        this.inscripcionService = inscripcionService;
    }

    @GetMapping
    public String listar(Model model) {
        List<Actividad> actividades = actividadService.listar();

        Map<Long, Long> cupos = new HashMap<>();
        for (Actividad a : actividades) {
            cupos.put(a.getId(), inscripcionService.cupoDisponible(a));
        }

        model.addAttribute("actividades", actividades);
        model.addAttribute("cupos", cupos);
        return "actividades/lista";
    }

    @GetMapping("/nueva")
    public String nueva(Model model) {
        model.addAttribute("actividad", new Actividad());
        model.addAttribute("titulo", "Nueva actividad");
        return "actividades/form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        try {
            model.addAttribute("actividad", actividadService.buscarPorId(id));
            model.addAttribute("titulo", "Editar actividad");
            return "actividades/form";
        } catch (RecursoNoEncontradoException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/actividades";
        }
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Actividad actividad, Model model,
                          RedirectAttributes redirect) {
        try {
            actividadService.guardar(actividad);
            redirect.addFlashAttribute("mensaje", "Actividad guardada correctamente");
            return "redirect:/actividades";
        } catch (ReglaNegocioException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("titulo", actividad.getId() == null ? "Nueva actividad" : "Editar actividad");
            return "actividades/form";
        }
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            actividadService.eliminar(id);
            redirect.addFlashAttribute("mensaje", "Actividad eliminada");
        } catch (ReglaNegocioException | RecursoNoEncontradoException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/actividades";
    }
    @GetMapping("/{id}/inscriptos")
    public String inscriptos(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        try {
            Actividad actividad = actividadService.buscarPorId(id);
            model.addAttribute("actividad", actividad);
            model.addAttribute("inscripciones", inscripcionService.activasDeActividad(id));
            model.addAttribute("cupoDisponible", inscripcionService.cupoDisponible(actividad));
            return "actividades/inscriptos";
        } catch (RecursoNoEncontradoException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/actividades";
        }
    }
}
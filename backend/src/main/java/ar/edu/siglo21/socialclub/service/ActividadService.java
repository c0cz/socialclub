package ar.edu.siglo21.socialclub.service;

import ar.edu.siglo21.socialclub.exception.RecursoNoEncontradoException;
import ar.edu.siglo21.socialclub.exception.ReglaNegocioException;
import ar.edu.siglo21.socialclub.model.Actividad;
import ar.edu.siglo21.socialclub.model.EstadoInscripcion;
import ar.edu.siglo21.socialclub.model.Inscripcion;
import ar.edu.siglo21.socialclub.repository.ActividadRepository;
import ar.edu.siglo21.socialclub.repository.InscripcionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ActividadService {

    private final ActividadRepository actividadRepository;
    private final InscripcionRepository inscripcionRepository;

    public ActividadService(ActividadRepository actividadRepository,
                            InscripcionRepository inscripcionRepository) {
        this.actividadRepository = actividadRepository;
        this.inscripcionRepository = inscripcionRepository;
    }

    public List<Actividad> listar() {
        return actividadRepository.findAll();
    }

    public List<Actividad> listarPorDisciplina(String disciplina) {
        if (disciplina == null || disciplina.isBlank()) {
            return actividadRepository.findAll();
        }
        return actividadRepository.findByDisciplina(disciplina);
    }

    public Actividad buscarPorId(Long id) {
        return actividadRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Actividad no encontrada"));
    }

    public Actividad guardar(Actividad actividad) {
        if (actividad.getEdadMinima() > actividad.getEdadMaxima()) {
            throw new ReglaNegocioException("VALIDACION",
                    "La edad mínima no puede ser mayor que la máxima");
        }
        if (actividad.getCupoMaximo() <= 0) {
            throw new ReglaNegocioException("VALIDACION", "El cupo debe ser mayor a 0");
        }
        return actividadRepository.save(actividad);
    }

    @Transactional
    public void eliminar(Long id) {
        buscarPorId(id);

        long activas = inscripcionRepository.contarActivasPorActividad(id);
        if (activas > 0) {
            throw new ReglaNegocioException("BAJA_ACTIVIDAD",
                    "No se puede eliminar: la actividad tiene inscriptos activos");
        }

        // Borramos el historial de bajas para no romper la clave foránea
        List<Inscripcion> bajas = inscripcionRepository
                .findByActividadIdAndEstado(id, EstadoInscripcion.BAJA);
        inscripcionRepository.deleteAll(bajas);

        actividadRepository.deleteById(id);
    }
}

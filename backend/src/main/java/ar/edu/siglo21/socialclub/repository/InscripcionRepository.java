package ar.edu.siglo21.socialclub.repository;

import ar.edu.siglo21.socialclub.model.EstadoInscripcion;
import ar.edu.siglo21.socialclub.model.Inscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {

    // Cuenta las inscripciones ACTIVAS de una actividad (para RN1)
    @Query("SELECT COUNT(i) FROM Inscripcion i " +
           "WHERE i.actividad.id = :actividadId " +
           "AND i.estado = ar.edu.siglo21.socialclub.model.EstadoInscripcion.ACTIVA")
    long contarActivasPorActividad(@Param("actividadId") Long actividadId);

    // ¿Ya está inscripto el socio en esa actividad? (para RN2)
    boolean existsBySocioIdAndActividadIdAndEstado(Long socioId, Long actividadId, EstadoInscripcion estado);

    // Cuántas actividades activas tiene el socio (para RN5)
    long countBySocioIdAndEstado(Long socioId, EstadoInscripcion estado);

    // Inscripciones de un socio según estado (app del socio)
    List<Inscripcion> findBySocioIdAndEstado(Long socioId, EstadoInscripcion estado);

    // Inscriptos de una actividad según estado (listado de secretaría)
    List<Inscripcion> findByActividadIdAndEstado(Long actividadId, EstadoInscripcion estado);
}
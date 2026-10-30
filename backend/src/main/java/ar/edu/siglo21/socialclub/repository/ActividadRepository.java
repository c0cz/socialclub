package ar.edu.siglo21.socialclub.repository;

import ar.edu.siglo21.socialclub.model.Actividad;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ActividadRepository extends JpaRepository<Actividad, Long> {

    List<Actividad> findByDisciplina(String disciplina);
}
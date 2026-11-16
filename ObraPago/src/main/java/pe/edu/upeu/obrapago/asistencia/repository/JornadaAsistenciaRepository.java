package pe.edu.upeu.obrapago.asistencia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.obrapago.asistencia.entity.EstadoJornada;
import pe.edu.upeu.obrapago.asistencia.entity.JornadaAsistencia;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface JornadaAsistenciaRepository
        extends JpaRepository<JornadaAsistencia, Long> {

    boolean existsByFecha(LocalDate fecha);

    Optional<JornadaAsistencia> findByFecha(LocalDate fecha);

    List<JornadaAsistencia>
    findByFechaBetweenOrderByFechaDesc(
            LocalDate desde,
            LocalDate hasta
    );

    List<JornadaAsistencia>
    findByEstadoAndFechaBetweenOrderByFechaDesc(
            EstadoJornada estado,
            LocalDate desde,
            LocalDate hasta
    );
}
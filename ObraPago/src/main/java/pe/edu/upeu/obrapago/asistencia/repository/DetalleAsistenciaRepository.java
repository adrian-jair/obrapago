package pe.edu.upeu.obrapago.asistencia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.obrapago.asistencia.entity.DetalleAsistencia;
import pe.edu.upeu.obrapago.asistencia.entity.EstadoAsistencia;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DetalleAsistenciaRepository extends JpaRepository<DetalleAsistencia, Long> {
    List<DetalleAsistencia> findByJornadaId(Long jornadaId);
    List<DetalleAsistencia> findByObreroIdOrderByJornadaFechaDesc(Long obreroId);
    List<DetalleAsistencia> findByObreroIdAndJornadaFechaBetweenOrderByJornadaFechaDesc(Long obreroId, LocalDate desde, LocalDate hasta);
    List<DetalleAsistencia> findByEstadoAsistencia(EstadoAsistencia estado);
}

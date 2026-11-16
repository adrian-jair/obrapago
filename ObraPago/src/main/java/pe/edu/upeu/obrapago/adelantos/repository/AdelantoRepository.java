package pe.edu.upeu.obrapago.adelantos.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.obrapago.adelantos.entity.Adelanto;
import pe.edu.upeu.obrapago.adelantos.entity.EstadoAdelanto;

import java.time.LocalDate;
import java.util.List;

public interface AdelantoRepository extends JpaRepository<Adelanto, Long> {
    @EntityGraph(attributePaths = "obrero")
    List<Adelanto> findByFechaBetweenOrderByFechaDesc(LocalDate desde, LocalDate hasta);

    @EntityGraph(attributePaths = "obrero")
    List<Adelanto> findByObreroIdAndFechaBetweenOrderByFechaDesc(Long obreroId, LocalDate desde, LocalDate hasta);

    List<Adelanto> findByObreroIdAndEstadoAndFechaBetween(Long obreroId, EstadoAdelanto estado, LocalDate desde, LocalDate hasta);
    List<Adelanto> findByObreroIdOrderByFechaDesc(Long obreroId);

    @EntityGraph(attributePaths = "obrero")
    List<Adelanto> findAllByOrderByFechaAscIdAsc();

    long countByObreroId(Long obreroId);
}

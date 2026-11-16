package pe.edu.upeu.obrapago.respaldo.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.obrapago.respaldo.entity.Respaldo;

import java.util.List;
import java.util.Optional;

public interface RespaldoRepository extends JpaRepository<Respaldo, Long> {
    @EntityGraph(attributePaths = "planilla")
    List<Respaldo> findAllByOrderByGeneradoEnDesc();

    @EntityGraph(attributePaths = "planilla")
    Optional<Respaldo> findOneById(Long id);
}

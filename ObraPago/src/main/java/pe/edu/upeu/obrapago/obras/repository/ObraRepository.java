package pe.edu.upeu.obrapago.obras.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.obrapago.obras.entity.EstadoObra;
import pe.edu.upeu.obrapago.obras.entity.Obra;

import java.util.List;
import java.util.Optional;

@Repository
public interface ObraRepository extends JpaRepository<Obra, Long> {

    boolean existsByCodigo(String codigo);

    boolean existsByCodigoAndIdNot(String codigo, Long id);

    @Query("SELECT o FROM Obra o LEFT JOIN FETCH o.obreros WHERE o.id = :id")
    Optional<Obra> findByIdConObreros(@Param("id") Long id);

    @Query("SELECT o FROM Obra o LEFT JOIN FETCH o.obreros")
    List<Obra> findAllConObreros();

    @Query("SELECT o FROM Obra o LEFT JOIN FETCH o.obreros WHERE o.estado = :estado")
    List<Obra> findByEstadoConObreros(@Param("estado") EstadoObra estado);

    @Query("SELECT o FROM Obra o JOIN o.obreros obr WHERE obr.id = :obreroId")
    List<Obra> findByObreroId(@Param("obreroId") Long obreroId);
}

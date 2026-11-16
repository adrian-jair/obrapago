package pe.edu.upeu.obrapago.obras.service;

import pe.edu.upeu.obrapago.obras.dto.ObraRequest;
import pe.edu.upeu.obrapago.obras.dto.ObraResponse;
import pe.edu.upeu.obrapago.obras.entity.EstadoObra;

import java.util.List;

public interface ObraService {

    List<ObraResponse> listar(EstadoObra estado, String buscar);

    ObraResponse obtenerPorId(Long id);

    ObraResponse crear(ObraRequest request);

    ObraResponse actualizar(Long id, ObraRequest request);

    void eliminar(Long id);

    ObraResponse asignarObrerosMasivo(Long obraId, List<Long> obreroIds);

    List<ObraResponse> listarPorObrero(Long obreroId);
}

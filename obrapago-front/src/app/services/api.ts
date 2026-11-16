import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private baseUrl = 'http://localhost:8080/api/v1';

  constructor(private http: HttpClient) {}

  // ===============================
  // 1. OBREROS Y RENIEC
  // ===============================
  getObreros(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/obreros`);
  }

  getObrero(id: number): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/obreros/${id}`);
  }

  getObreroPorDni(dni: string): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/obreros/dni/${dni}`);
  }

  createObrero(obrero: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/obreros`, obrero);
  }

  updateObrero(id: number, obrero: any): Observable<any> {
    return this.http.put(`${this.baseUrl}/obreros/${id}`, obrero);
  }

  deleteObrero(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/obreros/${id}`);
  }

  consultarDni(dni: string): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/reniec/${dni}`);
  }

  // ===============================
  // 2. USUARIOS
  // ===============================
  getUsuarios(soloActivos?: boolean): Observable<any[]> {
    let params = new HttpParams();
    if (soloActivos !== undefined) {
      params = params.set('soloActivos', soloActivos);
    }
    return this.http.get<any[]>(`${this.baseUrl}/usuarios`, { params });
  }

  getUsuario(id: number): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/usuarios/${id}`);
  }

  createUsuario(usuario: any): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/usuarios`, usuario);
  }

  updateUsuario(id: number, usuario: any): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/usuarios/${id}`, usuario);
  }

  cambiarEstadoUsuario(id: number, activo: boolean): Observable<any> {
    return this.http.patch<any>(`${this.baseUrl}/usuarios/${id}/estado?activo=${activo}`, {});
  }

  // ===============================
  // 3. CONTRATOS
  // ===============================
  getContratos(obreroId?: number, estado?: string): Observable<any[]> {
    let params = new HttpParams();
    if (obreroId) params = params.set('obreroId', obreroId);
    if (estado) params = params.set('estado', estado);
    return this.http.get<any[]>(`${this.baseUrl}/contratos`, { params });
  }

  getContrato(id: number): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/contratos/${id}`);
  }

  getContratoVigente(obreroId: number): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/contratos/obrero/${obreroId}/vigente`);
  }

  createContrato(contrato: any): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/contratos`, contrato);
  }

  updateContrato(id: number, contrato: any): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/contratos/${id}`, contrato);
  }

  cambiarEstadoContrato(id: number, estado: string): Observable<any> {
    return this.http.patch<any>(`${this.baseUrl}/contratos/${id}/estado?estado=${estado}`, {});
  }

  // ===============================
  // 4. JORNADAS_ASISTENCIA
  // ===============================
  getJornadas(desde?: string, hasta?: string, estado?: string): Observable<any[]> {
    let params = new HttpParams();
    if (desde) params = params.set('desde', desde);
    if (hasta) params = params.set('hasta', hasta);
    if (estado) params = params.set('estado', estado);
    return this.http.get<any[]>(`${this.baseUrl}/asistencias`, { params });
  }

  getJornada(id: number): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/asistencias/${id}`);
  }

  getJornadaPorFecha(fecha: string): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/asistencias/fecha/${fecha}`);
  }

  registrarAsistencia(asistencia: any): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/asistencias`, asistencia);
  }

  cerrarJornada(id: number): Observable<any> {
    return this.http.patch<any>(`${this.baseUrl}/asistencias/${id}/cerrar`, {});
  }

  getResumenAsistencias(desde?: string, hasta?: string): Observable<any> {
    let params = new HttpParams();
    if (desde) params = params.set('desde', desde);
    if (hasta) params = params.set('hasta', hasta);
    return this.http.get<any>(`${this.baseUrl}/asistencias/resumen`, { params });
  }

  // ===============================
  // 5. PLANILLA_MENSUAL
  // ===============================
  getPlanillas(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/pagos`);
  }

  getPlanilla(id: number): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/pagos/${id}`);
  }

  procesarPlanilla(fechas: { fechaInicio: string; fechaFin: string; descuentoPorTardanza?: number }): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/pagos/procesar`, fechas);
  }

  pagarPlanilla(id: number): Observable<any> {
    return this.http.patch<any>(`${this.baseUrl}/pagos/${id}/pagar`, {});
  }

  anularPlanilla(id: number): Observable<any> {
    return this.http.patch<any>(`${this.baseUrl}/pagos/${id}/anular`, {});
  }

  deletePlanilla(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/pagos/${id}`);
  }

  // ===============================
  // 6. DETALLES_ASISTENCIA
  // ===============================
  getDetallesAsistencia(filtros: {
    jornadaId?: number;
    obreroId?: number;
    desde?: string;
    hasta?: string;
    estado?: string;
  }): Observable<any[]> {
    let params = new HttpParams();
    if (filtros.jornadaId) params = params.set('jornadaId', filtros.jornadaId);
    if (filtros.obreroId) params = params.set('obreroId', filtros.obreroId);
    if (filtros.desde) params = params.set('desde', filtros.desde);
    if (filtros.hasta) params = params.set('hasta', filtros.hasta);
    if (filtros.estado) params = params.set('estado', filtros.estado);
    return this.http.get<any[]>(`${this.baseUrl}/asistencias/detalles`, { params });
  }

  actualizarDetalleAsistencia(
    id: number,
    estado?: string,
    horasExtra?: number,
    observacion?: string
  ): Observable<any> {
    let params = new HttpParams();
    if (estado) params = params.set('estado', estado);
    if (horasExtra !== undefined) params = params.set('horasExtra', horasExtra);
    if (observacion !== undefined) params = params.set('observacion', observacion);
    return this.http.put<any>(`${this.baseUrl}/asistencias/detalles/${id}`, {}, { params });
  }

  // ===============================
  // 7. ADELANTOS
  // ===============================
  getAdelantos(obreroId?: number, desde?: string, hasta?: string): Observable<any[]> {
    let params = new HttpParams();
    if (obreroId) params = params.set('obreroId', obreroId);
    if (desde) params = params.set('desde', desde);
    if (hasta) params = params.set('hasta', hasta);
    return this.http.get<any[]>(`${this.baseUrl}/adelantos`, { params });
  }

  getAdelantosPorObrero(obreroId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/adelantos?obreroId=${obreroId}`);
  }

  registrarAdelanto(adelanto: any): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/adelantos`, adelanto);
  }

  anularAdelanto(id: number): Observable<any> {
    return this.http.patch<any>(`${this.baseUrl}/adelantos/${id}/anular`, {});
  }

  getResumenAdelantos(obreroId?: number, desde?: string, hasta?: string): Observable<any> {
    let params = new HttpParams();
    if (obreroId) params = params.set('obreroId', obreroId);
    if (desde) params = params.set('desde', desde);
    if (hasta) params = params.set('hasta', hasta);
    return this.http.get<any>(`${this.baseUrl}/adelantos/resumen`, { params });
  }

  // ===============================
  // 8. DETALLES_PAGO
  // ===============================
  getDetallesPagoPorPlanilla(planillaId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/pagos/${planillaId}/detalles`);
  }

  getDetallePago(detalleId: number): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/pagos/detalles/${detalleId}`);
  }

  getDetallesPagoPorObrero(obreroId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/pagos/obrero/${obreroId}`);
  }

  // ===============================
  // 9. ACCESOS
  // ===============================
  getAccesos(username?: string): Observable<any[]> {
    let params = new HttpParams();
    if (username) params = params.set('username', username);
    return this.http.get<any[]>(`${this.baseUrl}/accesos`, { params });
  }

  registrarAcceso(acceso: any): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/accesos`, acceso);
  }

  deleteAcceso(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/accesos/${id}`);
  }

  // ===============================
  // 10. CONSULTA_OBRERO_PAGO
  // ===============================
  consultarObrero(criterio: string): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/consultas/obrero/${encodeURIComponent(criterio)}`);
  }

  // ===============================
  // 11. OBRAS Y ASIGNACIÓN MASIVA (N:M)
  // ===============================
  getObras(estado?: string, buscar?: string): Observable<any[]> {
    let params = new HttpParams();
    if (estado) params = params.set('estado', estado);
    if (buscar) params = params.set('buscar', buscar);
    return this.http.get<any[]>(`${this.baseUrl}/obras`, { params });
  }

  getObra(id: number): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/obras/${id}`);
  }

  createObra(obra: any): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/obras`, obra);
  }

  updateObra(id: number, obra: any): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/obras/${id}`, obra);
  }

  deleteObra(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/obras/${id}`);
  }

  asignarObrerosAObra(obraId: number, obreroIds: number[]): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/obras/${obraId}/obreros`, { obreroIds });
  }

  getObrasPorObrero(obreroId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/obras/obrero/${obreroId}`);
  }
}
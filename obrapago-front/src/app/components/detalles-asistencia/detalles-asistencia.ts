import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api';
import { KeycloakService } from '../../services/keycloak.service';

@Component({
  selector: 'app-detalles-asistencia',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './detalles-asistencia.html',
  styleUrls: ['./detalles-asistencia.css']
})
export class DetallesAsistenciaComponent implements OnInit {
  detalles: any[] = [];
  obreros: any[] = [];
  cargando: boolean = false;

  filtroObreroId: number | null = null;
  filtroEstado: string = '';
  filtroDesde: string = '';
  filtroHasta: string = '';

  detalleEdicion: any = null;

  estadosAsistencia = [
    'PRESENTE',
    'TARDANZA',
    'FALTA_JUSTIFICADA',
    'FALTA_INJUSTIFICADA'
  ];

  constructor(
    private api: ApiService,
    private cd: ChangeDetectorRef,
    public keycloak: KeycloakService
  ) {}

  ngOnInit(): void {
    this.cargarObreros();
    this.cargarDetalles();
  }

  cargarObreros(): void {
    this.api.getObreros().subscribe({
      next: (data) => {
        this.obreros = (data || []).sort((a: any, b: any) =>
          (a.apellidos || '').localeCompare(b.apellidos || '', 'es', { sensitivity: 'base' })
        );
        this.cd.detectChanges();
      },
      error: (err) => console.error('Error al cargar obreros', err)
    });
  }

  cargarDetalles(): void {
    this.cargando = true;
    const obreroNum = this.filtroObreroId ? Number(this.filtroObreroId) : undefined;
    this.api.getDetallesAsistencia({
      obreroId: obreroNum,
      estado: this.filtroEstado || undefined,
      desde: this.filtroDesde || undefined,
      hasta: this.filtroHasta || undefined
    }).subscribe({
      next: (data) => {
        let items = data || [];
        // Filtro defensivo del cliente para respuesta inmediata
        if (this.filtroEstado) {
          items = items.filter((d: any) => d.estadoAsistencia === this.filtroEstado);
        }
        if (obreroNum) {
          items = items.filter((d: any) => Number(d.obreroId) === obreroNum);
        }
        this.detalles = items.sort((a: any, b: any) => {
          const compFecha = (b.fecha || '').localeCompare(a.fecha || '');
          if (compFecha !== 0) return compFecha;
          return (a.obreroNombreCompleto || '').localeCompare(b.obreroNombreCompleto || '', 'es', { sensitivity: 'base' });
        });
        this.cargando = false;
        this.cd.detectChanges();
      },
      error: (err) => {
        console.error('Error al cargar detalles de asistencia', err);
        this.cargando = false;
        this.cd.detectChanges();
      }
    });
  }

  get totalPresentes(): number {
    return this.detalles.filter(d => d.estadoAsistencia === 'PRESENTE').length;
  }

  get totalTardanzas(): number {
    return this.detalles.filter(d => d.estadoAsistencia === 'TARDANZA').length;
  }

  get totalFaltasJustificadas(): number {
    return this.detalles.filter(d => d.estadoAsistencia === 'FALTA_JUSTIFICADA').length;
  }

  get totalFaltasInjustificadas(): number {
    return this.detalles.filter(d => d.estadoAsistencia === 'FALTA_INJUSTIFICADA').length;
  }

  get totalHorasExtra(): number {
    return this.detalles.reduce((sum, d) => sum + (Number(d.horasExtra) || 0), 0);
  }

  setFiltroEstado(estado: string): void {
    this.filtroEstado = (this.filtroEstado === estado) ? '' : estado;
    this.cargarDetalles();
  }

  abrirEdicion(item: any): void {
    this.detalleEdicion = {
      id: item.id,
      obreroNombre: item.obreroNombreCompleto,
      fecha: item.fecha,
      estadoAsistencia: item.estadoAsistencia,
      horasExtra: item.horasExtra,
      observacion: item.observacion || ''
    };
  }

  cerrarEdicion(): void {
    this.detalleEdicion = null;
  }

  guardarEdicion(): void {
    if (!this.detalleEdicion) return;

    this.api.actualizarDetalleAsistencia(
      this.detalleEdicion.id,
      this.detalleEdicion.estadoAsistencia,
      Number(this.detalleEdicion.horasExtra) || 0,
      this.detalleEdicion.observacion
    ).subscribe({
      next: (actualizado) => {
        alert('¡Detalle de asistencia actualizado exitosamente!');
        this.cerrarEdicion();
        this.cargarDetalles();
      },
      error: (err) => {
        console.error(err);
        alert('Error al actualizar detalle: ' + (err.error?.message || err.message));
      }
    });
  }

  limpiarFiltros(): void {
    this.filtroObreroId = null;
    this.filtroEstado = '';
    this.filtroDesde = '';
    this.filtroHasta = '';
    this.cargarDetalles();
  }
}

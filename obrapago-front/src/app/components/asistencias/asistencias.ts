import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api';

import { KeycloakService } from '../../services/keycloak.service';

interface FilaObrero {
  id: number;
  dni: string;
  nombres: string;
  apellidos: string;
  oficio: string;
  estado: string; // 'PRESENTE' | 'TARDANZA' | 'FALTA_JUSTIFICADA' | 'FALTA_INJUSTIFICADA'
  horasExtra: number;
  observacion: string;
  guardado: boolean;
}

@Component({
  selector: 'app-asistencias',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './asistencias.html',
  styleUrls: ['./asistencias.css']
})
export class AsistenciasComponent implements OnInit {
  fechaSeleccionada: string = new Date().toISOString().split('T')[0];
  obrerosLista: FilaObrero[] = [];
  cargando: boolean = false;
  jornadaActual: any = null;
  filtroTexto: string = '';

  constructor(
    private api: ApiService,
    private cd: ChangeDetectorRef,
    public keycloak: KeycloakService
  ) {}

  ngOnInit(): void {
    this.cargarDatosDelDia();
  }

  cambiarFecha(): void {
    this.cargarDatosDelDia();
  }

  cargarDatosDelDia(): void {
    this.cargando = true;
    this.jornadaActual = null;
    this.cd.detectChanges();

    // 1. Obtenemos todos los obreros registrados activos
    this.api.getObreros().subscribe({
      next: (obreros: any[]) => {
        // 2. Traemos jornada y asistencias registradas para esta fecha
        this.api.getJornadaPorFecha(this.fechaSeleccionada).subscribe({
          next: (jornada: any) => {
            this.jornadaActual = jornada;
            const detalles = jornada?.detalles || [];
            this.armarLista(obreros, detalles);
            this.cargando = false;
            this.cd.detectChanges();
          },
          error: () => {
            this.armarLista(obreros, []);
            this.cargando = false;
            this.cd.detectChanges();
          }
        });
      },
      error: (err) => {
        console.error('Error al cargar obreros:', err);
        this.cargando = false;
        this.cd.detectChanges();
      }
    });
  }

  armarLista(obreros: any[], detalles: any[]): void {
    this.obrerosLista = obreros.map(obr => {
      const reg = detalles.find((d: any) =>
        (d.obreroId === obr.id) || (d.dni === obr.dni)
      );

      return {
        id: obr.id,
        dni: obr.dni,
        nombres: obr.nombres,
        apellidos: obr.apellidos,
        oficio: obr.oficio,
        estado: reg ? reg.estado : 'PRESENTE',
        horasExtra: reg ? (reg.horasExtra || 0) : 0,
        observacion: reg ? (reg.observacion || '') : '',
        guardado: !!reg
      };
    }).sort((a, b) => (a.apellidos || '').localeCompare(b.apellidos || '', 'es', { sensitivity: 'base' }));
  }

  cambiarEstado(obrero: FilaObrero, nuevoEstado: string): void {
    if (this.isJornadaCerrada) {
      alert('La jornada está cerrada. No se pueden modificar asistencias.');
      return;
    }
    obrero.estado = nuevoEstado;
    obrero.guardado = false;
  }

  marcarTodos(estado: string): void {
    if (this.isJornadaCerrada) return;
    this.obrerosLista.forEach(o => {
      o.estado = estado;
      o.guardado = false;
    });
  }

  guardarTodos(): void {
    if (this.obrerosLista.length === 0) {
      alert('No hay obreros en la lista.');
      return;
    }

    if (this.isJornadaCerrada) {
      alert('No se puede modificar una jornada CERRADA.');
      return;
    }

    const payload = {
      fecha: this.fechaSeleccionada,
      detalles: this.obrerosLista.map(o => ({
        obreroId: o.id,
        estado: o.estado,
        horasExtra: Number(o.horasExtra) || 0,
        observacion: o.observacion || ''
      }))
    };

    this.api.registrarAsistencia(payload).subscribe({
      next: (jornadaGuardada) => {
        this.jornadaActual = jornadaGuardada;
        this.obrerosLista.forEach(o => o.guardado = true);
        alert(`¡Asistencia del ${this.fechaSeleccionada} guardada con éxito para ${this.obrerosLista.length} obreros!`);
        this.cd.detectChanges();
      },
      error: (err) => {
        console.error('Error al guardar jornada de asistencia:', err);
        alert('Error al registrar jornada: ' + (err.error?.message || err.message));
      }
    });
  }

  cerrarJornada(): void {
    if (!this.jornadaActual?.id) {
      alert('Debes guardar la jornada antes de poder cerrarla.');
      return;
    }

    if (confirm(`¿Estás seguro de CERRAR definitivamente la jornada del ${this.fechaSeleccionada}? Ya no se podrán modificar asistencias.`)) {
      this.api.cerrarJornada(this.jornadaActual.id).subscribe({
        next: (jornadaCerrada) => {
          this.jornadaActual = jornadaCerrada;
          alert('¡Jornada de asistencia CERRADA exitosamente!');
          this.cd.detectChanges();
        },
        error: (err) => alert('Error al cerrar jornada: ' + err.message)
      });
    }
  }

  get isJornadaCerrada(): boolean {
    return this.jornadaActual?.estado === 'CERRADA';
  }

  get totalPresentes(): number {
    return this.obrerosLista.filter(o => o.estado === 'PRESENTE').length;
  }

  get totalTardanzas(): number {
    return this.obrerosLista.filter(o => o.estado === 'TARDANZA').length;
  }

  get totalFaltas(): number {
    return this.obrerosLista.filter(o => o.estado === 'FALTA_JUSTIFICADA' || o.estado === 'FALTA_INJUSTIFICADA').length;
  }

  get totalHorasExtra(): number {
    return this.obrerosLista.reduce((acc, cur) => acc + (Number(cur.horasExtra) || 0), 0);
  }

  get obrerosFiltrados(): FilaObrero[] {
    if (!this.filtroTexto.trim()) return this.obrerosLista;
    const t = this.filtroTexto.toLowerCase();
    return this.obrerosLista.filter(o =>
      o.dni.includes(t) ||
      o.nombres.toLowerCase().includes(t) ||
      o.apellidos.toLowerCase().includes(t) ||
      o.oficio.toLowerCase().includes(t)
    );
  }
}
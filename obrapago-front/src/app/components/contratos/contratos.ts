import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api';
import { KeycloakService } from '../../services/keycloak.service';

@Component({
  selector: 'app-contratos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './contratos.html',
  styleUrls: ['./contratos.css']
})
export class ContratosComponent implements OnInit {
  contratos: any[] = [];
  obreros: any[] = [];
  cargando: boolean = false;
  filtroObreroId: number | null = null;
  filtroEstado: string = '';
  idEdicion: number | null = null;

  nuevoContrato: any = {
    obreroId: null,
    codigoContrato: '',
    tipoContrato: 'POR_OBRA',
    fechaInicio: new Date().toISOString().substring(0, 10),
    fechaFin: '',
    tarifaPactada: 90,
    puesto: 'Operario de Albañilería',
    estado: 'ACTIVO',
    observaciones: ''
  };

  tiposContrato = ['POR_OBRA', 'PLAZO_FIJO', 'POR_JORNAL', 'INDEFINIDO'];
  estadosContrato = ['ACTIVO', 'VENCIDO', 'FINALIZADO'];

  constructor(
    private api: ApiService,
    private cd: ChangeDetectorRef,
    public keycloak: KeycloakService
  ) {}

  ngOnInit(): void {
    this.cargarDatos();
  }

  cargarDatos(): void {
    this.cargando = true;
    this.api.getObreros().subscribe({
      next: (obrerosData) => {
        const listaObreros = obrerosData || [];

        this.api.getContratos(
          this.filtroObreroId || undefined,
          this.filtroEstado || undefined
        ).subscribe({
          next: (contratosData) => {
            this.contratos = (contratosData || []).sort((a: any, b: any) =>
              (a.obreroNombreCompleto || '').localeCompare(b.obreroNombreCompleto || '', 'es', { sensitivity: 'base' })
            );

            const contratadosIds = new Set(
              this.contratos
                .filter(c => c.estado === 'ACTIVO')
                .map(c => Number(c.obreroId))
            );

            // Marcamos cada obrero y ordenamos: primero los que FALTAN contratar
            this.obreros = listaObreros.map((o: any) => ({
              ...o,
              tieneContrato: contratadosIds.has(Number(o.id))
            })).sort((a: any, b: any) => {
              if (a.tieneContrato !== b.tieneContrato) {
                return a.tieneContrato ? 1 : -1; // primero sin contrato
              }
              return (a.apellidos || '').localeCompare(b.apellidos || '', 'es', { sensitivity: 'base' });
            });

            this.cargando = false;
            this.cd.detectChanges();
          },
          error: (err) => {
            console.error('Error al cargar contratos', err);
            this.cargando = false;
            this.cd.detectChanges();
          }
        });
      },
      error: (err) => {
        console.error('Error al cargar obreros', err);
        this.cargando = false;
        this.cd.detectChanges();
      }
    });
  }

  cargarContratos(): void {
    this.cargarDatos();
  }

  get obrerosSinContrato(): any[] {
    return this.obreros.filter(o => !o.tieneContrato);
  }

  get obrerosConContrato(): any[] {
    return this.obreros.filter(o => o.tieneContrato);
  }

  onObreroSeleccionado(): void {
    const ob = this.obreros.find(o => o.id === Number(this.nuevoContrato.obreroId));
    if (ob) {
      this.nuevoContrato.tarifaPactada = ob.tarifaJornal || 90;
      this.nuevoContrato.puesto = `Trabajador - ${ob.oficio || 'OPERARIO'}`;
    }
  }

  seleccionarParaContratar(obreroId: number): void {
    this.nuevoContrato.obreroId = obreroId;
    this.onObreroSeleccionado();
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  guardarContrato(): void {
    if (!this.nuevoContrato.obreroId) {
      alert('Debes seleccionar un obrero para asociar el contrato');
      return;
    }

    const payload = {
      obreroId: Number(this.nuevoContrato.obreroId),
      codigoContrato: this.nuevoContrato.codigoContrato || undefined,
      tipoContrato: this.nuevoContrato.tipoContrato,
      fechaInicio: this.nuevoContrato.fechaInicio,
      fechaFin: this.nuevoContrato.fechaFin || null,
      tarifaPactada: Number(this.nuevoContrato.tarifaPactada),
      puesto: this.nuevoContrato.puesto,
      estado: this.nuevoContrato.estado,
      observaciones: this.nuevoContrato.observaciones
    };

    if (this.idEdicion) {
      this.api.updateContrato(this.idEdicion, payload).subscribe({
        next: () => {
          alert('¡Contrato laboral actualizado con éxito!');
          this.limpiarFormulario();
          this.cargarDatos();
        },
        error: (err) => alert('Error al actualizar contrato: ' + (err.error?.message || err.message))
      });
    } else {
      this.api.createContrato(payload).subscribe({
        next: () => {
          alert('¡Contrato laboral registrado con éxito!');
          this.limpiarFormulario();
          this.cargarDatos();
        },
        error: (err) => alert('Error al registrar contrato: ' + (err.error?.message || err.message))
      });
    }
  }

  editar(c: any): void {
    this.idEdicion = c.id;
    this.nuevoContrato = {
      obreroId: c.obreroId,
      codigoContrato: c.codigoContrato,
      tipoContrato: c.tipoContrato,
      fechaInicio: c.fechaInicio,
      fechaFin: c.fechaFin || '',
      tarifaPactada: c.tarifaPactada,
      puesto: c.puesto,
      estado: c.estado,
      observaciones: c.observaciones || ''
    };
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  cambiarEstado(c: any, nuevoEstado: string): void {
    if (confirm(`¿Deseas cambiar el estado del contrato ${c.codigoContrato} a ${nuevoEstado}?`)) {
      this.api.cambiarEstadoContrato(c.id, nuevoEstado).subscribe({
        next: () => {
          c.estado = nuevoEstado;
          this.cargarDatos();
        },
        error: (err) => alert('Error al actualizar estado: ' + err.message)
      });
    }
  }

  limpiarFormulario(): void {
    this.idEdicion = null;
    this.nuevoContrato = {
      obreroId: null,
      codigoContrato: '',
      tipoContrato: 'POR_OBRA',
      fechaInicio: new Date().toISOString().substring(0, 10),
      fechaFin: '',
      tarifaPactada: 90,
      puesto: 'Operario de Albañilería',
      estado: 'ACTIVO',
      observaciones: ''
    };
  }
}

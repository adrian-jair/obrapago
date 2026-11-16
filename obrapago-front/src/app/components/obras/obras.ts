import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api';

import { KeycloakService } from '../../services/keycloak.service';

export interface FilaAsignacionObrero {
  id: number;
  dni: string;
  nombres: string;
  apellidos: string;
  oficio: string;
  tarifaJornal: number;
  celular: string;
  fotoUrl?: string;
  asignado: boolean;
  guardadoEnBd: boolean;
}

@Component({
  selector: 'app-obras',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './obras.html',
  styleUrls: ['./obras.css']
})
export class ObrasComponent implements OnInit {
  obras: any[] = [];
  obraSeleccionadaId: number | null = null;
  obraActual: any = null;

  obrerosTodos: any[] = [];
  cuadrillaLista: FilaAsignacionObrero[] = [];

  cargando: boolean = false;
  guardando: boolean = false;
  filtroTexto: string = '';
  filtroOficio: string = '';
  mensajeExito: string = '';
  mensajeError: string = '';

  // Modal / Formulario de Creación / Edición de Obra
  mostrarFormularioObra: boolean = false;
  modoEdicion: boolean = false;
  formularioObra: any = {
    codigo: '',
    nombre: '',
    ubicacion: '',
    cliente: '',
    presupuesto: 250000,
    estado: 'EN_EJECUCION',
    fechaInicio: new Date().toISOString().substring(0, 10),
    fechaFin: '',
    descripcion: ''
  };

  estadosObra = ['PLANIFICADA', 'EN_EJECUCION', 'PARALIZADA', 'FINALIZADA'];

  constructor(
    private api: ApiService,
    private cd: ChangeDetectorRef,
    public keycloak: KeycloakService
  ) {}

  ngOnInit(): void {
    this.cargarDatosIniciales();
  }

  cargarDatosIniciales(): void {
    this.cargando = true;
    this.cd.detectChanges();

    // 1. Cargar todas las obras
    this.api.getObras().subscribe({
      next: (obras) => {
        this.obras = obras;

        // 2. Cargar todos los obreros
        this.api.getObreros().subscribe({
          next: (obreros) => {
            this.obrerosTodos = obreros;

            if (this.obras.length > 0) {
              if (!this.obraSeleccionadaId) {
                this.obraSeleccionadaId = this.obras[0].id;
              }
              this.seleccionarObra(this.obraSeleccionadaId!);
            } else {
              this.cargando = false;
              this.cd.detectChanges();
            }
          },
          error: (err) => {
            console.error('Error al cargar obreros', err);
            this.cargando = false;
            this.cd.detectChanges();
          }
        });
      },
      error: (err) => {
        console.error('Error al cargar obras', err);
        this.cargando = false;
        this.cd.detectChanges();
      }
    });
  }

  seleccionarObra(id: number): void {
    this.obraSeleccionadaId = Number(id);
    this.cargando = true;
    this.mensajeExito = '';
    this.mensajeError = '';
    this.cd.detectChanges();

    this.api.getObra(this.obraSeleccionadaId).subscribe({
      next: (obra) => {
        this.obraActual = obra;
        this.armarCuadrilla(this.obrerosTodos, obra.obreros || []);
        this.cargando = false;
        this.cd.detectChanges();
      },
      error: (err) => {
        console.error('Error al obtener obra', err);
        this.cargando = false;
        this.cd.detectChanges();
      }
    });
  }

  armarCuadrilla(todos: any[], asignadosEnObra: any[]): void {
    const idsAsignados = new Set(asignadosEnObra.map((o: any) => o.id));

    this.cuadrillaLista = todos.map((obr) => {
      const estaAsignado = idsAsignados.has(obr.id);
      return {
        id: obr.id,
        dni: obr.dni,
        nombres: obr.nombres,
        apellidos: obr.apellidos,
        oficio: obr.oficio,
        tarifaJornal: obr.tarifaJornal,
        celular: obr.celular,
        fotoUrl: obr.fotoUrl,
        asignado: estaAsignado,
        guardadoEnBd: estaAsignado
      };
    }).sort((a, b) => (a.apellidos || '').localeCompare(b.apellidos || '', 'es', { sensitivity: 'base' }));
  }

  get obrerosFiltrados(): FilaAsignacionObrero[] {
    return this.cuadrillaLista.filter((item) => {
      const coincideTexto =
        !this.filtroTexto ||
        (item.nombres + ' ' + item.apellidos).toLowerCase().includes(this.filtroTexto.toLowerCase()) ||
        item.dni.includes(this.filtroTexto);

      const coincideOficio = !this.filtroOficio || item.oficio === this.filtroOficio;

      return coincideTexto && coincideOficio;
    });
  }

  get totalAsignados(): number {
    return this.cuadrillaLista.filter((o) => o.asignado).length;
  }

  get totalDisponibles(): number {
    return this.cuadrillaLista.filter((o) => !o.asignado).length;
  }

  // ===============================
  // ACCIONES MASIVAS (Estilo Asistencia)
  // ===============================
  marcarTodos(asignar: boolean): void {
    this.obrerosFiltrados.forEach((o) => (o.asignado = asignar));
    this.cd.detectChanges();
  }

  marcarPorOficio(oficio: string): void {
    this.cuadrillaLista.forEach((o) => {
      if (o.oficio === oficio) {
        o.asignado = true;
      }
    });
    this.cd.detectChanges();
  }

  toggleAsignacion(obrero: FilaAsignacionObrero): void {
    obrero.asignado = !obrero.asignado;
  }

  guardarAsignacionesMasivas(): void {
    if (!this.obraSeleccionadaId) {
      alert('Selecciona una obra primero.');
      return;
    }

    const idsParaAsignar = this.cuadrillaLista
      .filter((o) => o.asignado)
      .map((o) => o.id);

    this.guardando = true;
    this.mensajeExito = '';
    this.mensajeError = '';
    this.cd.detectChanges();

    this.api.asignarObrerosAObra(this.obraSeleccionadaId, idsParaAsignar).subscribe({
      next: (obraActualizada) => {
        this.obraActual = obraActualizada;
        // Sincronizar estado guardado
        this.cuadrillaLista.forEach((o) => {
          o.guardadoEnBd = o.asignado;
        });

        // Actualizar en el selector de obras el conteo
        const itemEnLista = this.obras.find((ob) => ob.id === this.obraSeleccionadaId);
        if (itemEnLista) {
          itemEnLista.totalObreros = idsParaAsignar.length;
        }

        this.guardando = false;
        this.mensajeExito = `¡Cuadrilla actualizada exitosamente! ${idsParaAsignar.length} obreros asignados a la obra.`;
        this.cd.detectChanges();

        setTimeout(() => {
          this.mensajeExito = '';
          this.cd.detectChanges();
        }, 5000);
      },
      error: (err) => {
        console.error('Error al guardar asignaciones', err);
        this.guardando = false;
        this.mensajeError = err.error?.message || 'Error al actualizar las asignaciones.';
        this.cd.detectChanges();
      }
    });
  }

  // ===============================
  // GESTIÓN DE OBRAS (CRUD)
  // ===============================
  abrirFormularioNuevaObra(): void {
    this.modoEdicion = false;
    this.formularioObra = {
      codigo: 'OBR-' + (this.obras.length + 1).toString().padStart(3, '0'),
      nombre: '',
      ubicacion: 'Lima, Perú',
      cliente: 'Consorcio Constructor',
      presupuesto: 350000,
      estado: 'EN_EJECUCION',
      fechaInicio: new Date().toISOString().substring(0, 10),
      fechaFin: '',
      descripcion: 'Proyecto de edificación / infraestructura'
    };
    this.mostrarFormularioObra = true;
  }

  abrirFormularioEditarObra(): void {
    if (!this.obraActual) return;
    this.modoEdicion = true;
    this.formularioObra = {
      codigo: this.obraActual.codigo,
      nombre: this.obraActual.nombre,
      ubicacion: this.obraActual.ubicacion,
      cliente: this.obraActual.cliente,
      presupuesto: this.obraActual.presupuesto,
      estado: this.obraActual.estado,
      fechaInicio: this.obraActual.fechaInicio,
      fechaFin: this.obraActual.fechaFin,
      descripcion: this.obraActual.descripcion
    };
    this.mostrarFormularioObra = true;
  }

  cerrarFormulario(): void {
    this.mostrarFormularioObra = false;
  }

  guardarFormularioObra(): void {
    if (!this.formularioObra.codigo || !this.formularioObra.nombre) {
      alert('Código y Nombre de Obra son obligatorios.');
      return;
    }

    if (this.modoEdicion && this.obraSeleccionadaId) {
      this.api.updateObra(this.obraSeleccionadaId, this.formularioObra).subscribe({
        next: (act) => {
          alert('¡Obra actualizada correctamente!');
          this.mostrarFormularioObra = false;
          this.cargarDatosIniciales();
        },
        error: (err) => alert('Error: ' + (err.error?.message || err.message))
      });
    } else {
      this.api.createObra(this.formularioObra).subscribe({
        next: (creada) => {
          alert('¡Obra creada correctamente!');
          this.mostrarFormularioObra = false;
          this.obraSeleccionadaId = creada.id;
          this.cargarDatosIniciales();
        },
        error: (err) => alert('Error: ' + (err.error?.message || err.message))
      });
    }
  }

  eliminarObraActual(): void {
    if (!this.obraSeleccionadaId || !this.obraActual) return;
    if (!confirm(`¿Estás seguro de eliminar la obra "${this.obraActual.nombre}"?`)) return;

    this.api.deleteObra(this.obraSeleccionadaId).subscribe({
      next: () => {
        alert('Obra eliminada.');
        this.obraSeleccionadaId = null;
        this.cargarDatosIniciales();
      },
      error: (err) => alert('Error: ' + (err.error?.message || err.message))
    });
  }
}

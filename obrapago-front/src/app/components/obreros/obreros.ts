import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { ApiService } from '../../services/api';
import { KeycloakService } from '../../services/keycloak.service';

@Component({
  selector: 'app-obreros',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './obreros.html'
})
export class ObrerosComponent implements OnInit {
  obreros: any[] = [];
  cargandoDni: boolean = false;
  idEdicion: number | null = null;
  filtroContrato: string = 'TODOS'; // 'TODOS' | 'CONTRATADOS' | 'PENDIENTES'
  filtroTexto: string = '';

  nuevoObrero: any = {
    dni: '',
    nombres: '',
    apellidos: '',
    oficio: 'OPERARIO',
    tarifaJornal: 90,
    celular: '',
    fotoUrl: ''
  };

  constructor(
    private api: ApiService,
    private cd: ChangeDetectorRef,
    public keycloak: KeycloakService
  ) { }

  ngOnInit(): void {
    this.cargarObreros();
  }

  cargarObreros(): void {
    this.api.getObreros().subscribe({
      next: (data: any) => {
        const listaObreros = data || [];
        this.api.getContratos().subscribe({
          next: (contratos: any[]) => {
            const contratosActivosMap = new Map<number, any>();
            (contratos || []).forEach(c => {
              if (c.estado === 'ACTIVO') {
                contratosActivosMap.set(Number(c.obreroId), c);
              }
            });

            this.obreros = listaObreros.map((o: any) => {
              const contrato = contratosActivosMap.get(Number(o.id));
              return {
                ...o,
                tieneContrato: !!contrato,
                codigoContrato: contrato ? contrato.codigoContrato : null
              };
            }).sort((a: any, b: any) =>
              (a.apellidos || '').localeCompare(b.apellidos || '', 'es', { sensitivity: 'base' })
            );
            this.cd.detectChanges();
          },
          error: () => {
            this.obreros = listaObreros.sort((a: any, b: any) =>
              (a.apellidos || '').localeCompare(b.apellidos || '', 'es', { sensitivity: 'base' })
            );
            this.cd.detectChanges();
          }
        });
      },
      error: (err: any) => console.error('Error al cargar obreros', err)
    });
  }

  get totalContratados(): number {
    return this.obreros.filter(o => o.tieneContrato).length;
  }

  get totalFaltanContratar(): number {
    return this.obreros.filter(o => !o.tieneContrato).length;
  }

  get obrerosFiltrados(): any[] {
    let lista = this.obreros;

    if (this.filtroContrato === 'CONTRATADOS') {
      lista = lista.filter(o => o.tieneContrato);
    } else if (this.filtroContrato === 'PENDIENTES') {
      lista = lista.filter(o => !o.tieneContrato);
    }

    if (this.filtroTexto.trim()) {
      const q = this.filtroTexto.toLowerCase();
      lista = lista.filter(o =>
        (o.dni && o.dni.includes(q)) ||
        (o.nombres && o.nombres.toLowerCase().includes(q)) ||
        (o.apellidos && o.apellidos.toLowerCase().includes(q)) ||
        (o.oficio && o.oficio.toLowerCase().includes(q))
      );
    }

    return lista;
  }

  onFileSelected(event: any): void {
    const file: File = event.target.files[0];
    if (file) {
      if (file.size > 2 * 1024 * 1024) {
        alert('La imagen es muy grande. Por favor selecciona una foto menor a 2MB.');
        return;
      }
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.nuevoObrero.fotoUrl = e.target.result;
        this.cd.detectChanges();
      };
      reader.readAsDataURL(file);
    }
  }

  buscarDni(): void {
    const dni = (this.nuevoObrero.dni || '').trim();
    if (dni.length !== 8) {
      alert('Ingresa un DNI válido de 8 dígitos');
      return;
    }

    this.cargandoDni = true;
    this.cd.detectChanges();

    this.api.consultarDni(dni).subscribe({
      next: (data: any) => {
        this.cargandoDni = false;
        if (data && (data.nombres || data.nombre)) {
          this.nuevoObrero.nombres = data.nombres || data.nombre || '';
          const apPat = data.apellidoPaterno || data.apellido_paterno || '';
          const apMat = data.apellidoMaterno || data.apellido_materno || '';
          this.nuevoObrero.apellidos = `${apPat} ${apMat}`.trim();
        } else {
          alert('No se encontraron datos oficiales para este DNI en RENIEC.');
        }
        this.cd.detectChanges();
      },
      error: (err: any) => {
        this.cargandoDni = false;
        console.error('Error al consultar DNI:', err);
        alert('El servicio de RENIEC no respondió o no encontró este DNI.');
        this.cd.detectChanges();
      }
    });
  }

  guardarObrero(): void {
    if (this.idEdicion) {
      this.api.updateObrero(this.idEdicion, this.nuevoObrero).subscribe({
        next: () => {
          alert('¡Datos del obrero actualizados con éxito!');
          this.limpiarFormulario();
          this.cargarObreros();
        },
        error: (err: any) => {
          console.error(err);
          alert('Error al actualizar datos del obrero');
        }
      });
    } else {
      this.api.createObrero(this.nuevoObrero).subscribe({
        next: () => {
          alert('¡Obrero registrado con éxito!');
          this.limpiarFormulario();
          this.cargarObreros();
        },
        error: (err: any) => {
          console.error(err);
          alert('Error al registrar obrero');
        }
      });
    }
  }

  cargarParaEditar(o: any): void {
    this.idEdicion = o.id;
    this.nuevoObrero = {
      dni: o.dni,
      nombres: o.nombres,
      apellidos: o.apellidos,
      oficio: o.oficio,
      tarifaJornal: o.tarifaJornal,
      celular: o.celular,
      fotoUrl: o.fotoUrl || ''
    };
    this.cd.detectChanges();
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  cancelarEdicion(): void {
    this.limpiarFormulario();
  }

  eliminarObrero(id: number): void {
    if (confirm('¿Estás seguro de dar de baja / retirar a este obrero del proyecto?')) {
      this.api.deleteObrero(id).subscribe({
        next: () => {
          alert('¡Obrero retirado con éxito!');
          this.cargarObreros();
        },
        error: (err: any) => {
          console.error('Error al retirar obrero:', err);
          alert('No se pudo dar de baja al obrero: es posible que tenga asistencias, contratos o pagos asociados.');
        }
      });
    }
  }

  limpiarFormulario(): void {
    this.idEdicion = null;
    this.nuevoObrero = {
      dni: '',
      nombres: '',
      apellidos: '',
      oficio: 'OPERARIO',
      tarifaJornal: 90,
      celular: '',
      fotoUrl: ''
    };
    this.cd.detectChanges();
  }
}

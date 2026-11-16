import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api';

@Component({
  selector: 'app-consulta-obrero',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './consulta-obrero.html',
  styleUrls: ['./consulta-obrero.css']
})
export class ConsultaObreroComponent implements OnInit {
  obreros: any[] = [];
  criterioBusqueda: string = '74859632'; // Default sample DNI
  cargando: boolean = false;
  resultado: any = null;
  errorMensaje: string = '';

  pestanaActiva: string = 'pagos'; // 'pagos' | 'adelantos' | 'asistencias' | 'obras'
  obrasAsignadas: any[] = [];

  constructor(
    private api: ApiService,
    private cd: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.cargarObreros();
    this.buscar();
  }

  cargarObreros(): void {
    this.api.getObreros().subscribe({
      next: (data) => {
        this.obreros = (data || []).sort((a: any, b: any) =>
          (a.apellidos || '').localeCompare(b.apellidos || '', 'es', { sensitivity: 'base' })
        );
        this.cd.detectChanges();
      },
      error: (err) => console.error(err)
    });
  }

  seleccionarObrero(dni: string): void {
    this.criterioBusqueda = dni;
    this.buscar();
  }

  buscar(): void {
    if (!this.criterioBusqueda.trim()) {
      alert('Ingresa un DNI o ID de obrero para consultar');
      return;
    }

    this.cargando = true;
    this.errorMensaje = '';
    this.resultado = null;
    this.obrasAsignadas = [];
    this.cd.detectChanges();

    this.api.consultarObrero(this.criterioBusqueda.trim()).subscribe({
      next: (data) => {
        this.resultado = data;
        // Cargar también las obras asignadas a este obrero (Relación Muchos a Muchos)
        if (data && data.obrero && data.obrero.id) {
          this.api.getObrasPorObrero(data.obrero.id).subscribe({
            next: (obras) => {
              this.obrasAsignadas = obras || [];
              this.cd.detectChanges();
            },
            error: (err) => console.error('Error al cargar obras del obrero:', err)
          });
        }
        this.cargando = false;
        this.cd.detectChanges();
      },
      error: (err) => {
        this.cargando = false;
        this.errorMensaje = err.error?.message || 'No se encontraron datos para el criterio ingresado.';
        this.cd.detectChanges();
      }
    });
  }

  imprimir(): void {
    window.print();
  }
}

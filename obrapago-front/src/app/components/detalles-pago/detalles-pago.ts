import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { ApiService } from '../../services/api';

@Component({
  selector: 'app-detalles-pago',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './detalles-pago.html',
  styleUrls: ['./detalles-pago.css']
})
export class DetallesPagoComponent implements OnInit {
  planillas: any[] = [];
  planillaSeleccionadaId: number | null = null;
  detalles: any[] = [];
  cargando: boolean = false;
  planillaActual: any = null;

  boletaSeleccionada: any = null;

  constructor(
    private api: ApiService,
    private route: ActivatedRoute,
    private cd: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.cargarPlanillas();
  }

  cargarPlanillas(): void {
    this.api.getPlanillas().subscribe({
      next: (data) => {
        const raw = data || [];
        // Orden cronológico para numeración correlativa (Planilla N° 1, N° 2, N° 3...)
        const ordenadas = [...raw].sort((a, b) => {
          const compFecha = (a.fechaInicio || '').localeCompare(b.fechaInicio || '');
          if (compFecha !== 0) return compFecha;
          return (Number(a.id) || 0) - (Number(b.id) || 0);
        });

        const mapa = new Map<number, number>();
        ordenadas.forEach((p, idx) => {
          mapa.set(p.id, idx + 1);
        });

        this.planillas = raw.map((p: any) => ({
          ...p,
          numeroSecuencial: p.numeroSecuencial || mapa.get(p.id) || 1
        }));

        this.route.queryParams.subscribe((params) => {
          if (params['planillaId']) {
            this.planillaSeleccionadaId = Number(params['planillaId']);
          } else if (this.planillas.length > 0) {
            this.planillaSeleccionadaId = this.planillas[0].id;
          }
          if (this.planillaSeleccionadaId) {
            this.cargarDetallesDePlanilla();
          }
        });
        this.cd.detectChanges();
      },
      error: (err) => console.error('Error al cargar planillas:', err)
    });
  }

  cargarDetallesDePlanilla(): void {
    if (!this.planillaSeleccionadaId) return;

    this.cargando = true;
    this.planillaActual = this.planillas.find(p => Number(p.id) === Number(this.planillaSeleccionadaId));

    this.api.getDetallesPagoPorPlanilla(this.planillaSeleccionadaId).subscribe({
      next: (data) => {
        this.detalles = (data || []).sort((a: any, b: any) =>
          (a.nombreCompleto || '').localeCompare(b.nombreCompleto || '', 'es', { sensitivity: 'base' })
        );
        this.cargando = false;
        this.cd.detectChanges();
      },
      error: (err) => {
        console.error('Error al cargar detalles de pago:', err);
        this.cargando = false;
        this.cd.detectChanges();
      }
    });
  }

  verBoleta(d: any): void {
    this.boletaSeleccionada = {
      ...d,
      numeroPlanilla: this.planillaActual?.numeroSecuencial || 1,
      periodoInicio: this.planillaActual?.fechaInicio,
      periodoFin: this.planillaActual?.fechaFin,
      estadoPlanilla: this.planillaActual?.estado
    };
  }

  cerrarBoleta(): void {
    this.boletaSeleccionada = null;
  }

  imprimirBoleta(): void {
    window.print();
  }

  get totalBrutoGeneral(): number {
    return this.detalles.reduce((acc, d) => acc + (Number(d.bruto) || 0), 0);
  }

  get totalAdelantosGeneral(): number {
    return this.detalles.reduce((acc, d) => acc + (Number(d.adelantos) || 0), 0);
  }

  get totalNetoGeneral(): number {
    return this.detalles.reduce((acc, d) => acc + (Number(d.neto) || 0), 0);
  }
}

import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ApiService } from '../../services/api';
import { KeycloakService } from '../../services/keycloak.service';

@Component({
  selector: 'app-pagos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './pagos.html'
})
export class PagosComponent implements OnInit {
  planillas: any[] = [];
  cargando: boolean = false;

  fechas = {
    fechaInicio: new Date(new Date().getFullYear(), new Date().getMonth(), 1).toISOString().substring(0, 10),
    fechaFin: new Date().toISOString().substring(0, 10),
    descuentoPorTardanza: 5
  };

  planillaSeleccionada: any = null;

  constructor(
    private api: ApiService,
    private router: Router,
    private cd: ChangeDetectorRef,
    public keycloak: KeycloakService
  ) { }

  ngOnInit(): void {
    this.cargarPlanillas();
  }

  cargarPlanillas(): void {
    this.cargando = true;
    this.api.getPlanillas().subscribe({
      next: (data: any) => {
        const raw = data || [];
        // Orden cronológico para garantizar secuencia consecutiva 1, 2, 3...
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

        this.cargando = false;
        this.cd.detectChanges();
      },
      error: (err: any) => {
        console.error('Error al cargar planillas:', err);
        this.cargando = false;
        this.cd.detectChanges();
      }
    });
  }

  obtenerTextoPlanilla(id: number): string {
    const p = this.planillas.find(item => item.id === id);
    return p ? `Planilla N° ${p.numeroSecuencial}` : `Planilla #${id}`;
  }

  establecerMesActual(): void {
    const ahora = new Date();
    const y = ahora.getFullYear();
    const m = ahora.getMonth();
    const primerDia = new Date(y, m, 1).toISOString().substring(0, 10);
    const ultimoDia = new Date(y, m + 1, 0).toISOString().substring(0, 10);
    this.fechas.fechaInicio = primerDia;
    this.fechas.fechaFin = ultimoDia;
  }

  establecerMesAnterior(): void {
    const ahora = new Date();
    const y = ahora.getFullYear();
    const m = ahora.getMonth() - 1;
    const primerDia = new Date(y, m, 1).toISOString().substring(0, 10);
    const ultimoDia = new Date(y, m + 1, 0).toISOString().substring(0, 10);
    this.fechas.fechaInicio = primerDia;
    this.fechas.fechaFin = ultimoDia;
  }

  procesar(): void {
    if (!this.fechas.fechaInicio || !this.fechas.fechaFin) {
      alert('Debes seleccionar las fechas de inicio y fin para calcular la planilla');
      return;
    }

    this.api.procesarPlanilla(this.fechas).subscribe({
      next: (res) => {
        const num = res.numeroSecuencial || (this.planillas.length + 1);
        alert(`¡Planilla N° ${num} procesada con éxito! Se liquidaron jornales, horas extra y adelantos.`);
        this.cargarPlanillas();
      },
      error: (err: any) => {
        console.error(err);
        alert('Error al procesar planilla x : ' + (err.error?.message || err.message));
      }
    });
  }

  pagar(id: number): void {
    const texto = this.obtenerTextoPlanilla(id);
    if (confirm(`¿Confirmas el desembolso y pago de la ${texto}?`)) {
      this.api.pagarPlanilla(id).subscribe({
        next: () => {
          alert(`¡${texto} marcada como PAGADA exitosamente!`);
          this.cargarPlanillas();
        },
        error: (err: any) => alert('Error al registrar pago: ' + (err.error?.message || err.message))
      });
    }
  }

  anular(id: number): void {
    const texto = this.obtenerTextoPlanilla(id);
    if (confirm(`¿Estás seguro de ANULAR la ${texto}?`)) {
      this.api.anularPlanilla(id).subscribe({
        next: () => {
          alert(`${texto} anulada.`);
          this.cargarPlanillas();
        },
        error: (err: any) => alert('Error al anular: ' + (err.error?.message || err.message))
      });
    }
  }

  eliminar(id: number): void {
    const texto = this.obtenerTextoPlanilla(id);
    if (confirm(`¿Estás seguro de ELIMINAR permanentemente la ${texto}? Podrás recalcular este periodo nuevamente.`)) {
      this.api.deletePlanilla(id).subscribe({
        next: () => {
          alert(`¡${texto} eliminada permanentemente!`);
          this.cargarPlanillas();
        },
        error: (err: any) => alert('Error al eliminar: ' + (err.error?.message || err.message))
      });
    }
  }

  verDetalles(planilla: any): void {
    this.router.navigate(['/detalles-pago'], { queryParams: { planillaId: planilla.id } });
  }
}

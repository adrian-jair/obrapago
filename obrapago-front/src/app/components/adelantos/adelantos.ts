import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api';
import { KeycloakService } from '../../services/keycloak.service';

@Component({
  selector: 'app-adelantos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './adelantos.html'
})
export class AdelantosComponent implements OnInit {
  obreros: any[] = [];
  todosAdelantos: any[] = [];
  adelantos: any[] = [];
  cargando: boolean = false;
  filtroObreroId: number | null = null;

  nuevoAdelanto: {
    obreroId: number | null;
    fecha: string;
    importe: number;
    concepto: string;
  } = {
      obreroId: null,
      fecha: new Date().toISOString().substring(0, 10),
      importe: 20,
      concepto: 'Vale de caja chica'
    };

  constructor(
    private api: ApiService,
    private cd: ChangeDetectorRef,
    public keycloak: KeycloakService
  ) { }

  ngOnInit(): void {
    this.cargarObreros();
    this.cargarAdelantos();
  }

  cargarObreros(): void {
    this.api.getObreros().subscribe({
      next: (data: any) => {
        this.obreros = (data || []).sort((a: any, b: any) =>
          (a.apellidos || '').localeCompare(b.apellidos || '', 'es', { sensitivity: 'base' })
        );
        this.cd.detectChanges();
      },
      error: (err: any) => console.error(err)
    });
  }

  cargarAdelantos(): void {
    this.cargando = true;
    const obreroNum = this.filtroObreroId ? Number(this.filtroObreroId) : undefined;

    this.api.getAdelantos().subscribe({
      next: (data: any) => {
        const rawItems = data || [];
        this.todosAdelantos = this.calcularSecuenciaPorObrero(rawItems);

        let filtrados = this.todosAdelantos;
        if (obreroNum) {
          filtrados = filtrados.filter((a: any) => Number(a.obreroId) === obreroNum);
        }

        // Ordenar agrupando por obrero y ordenando secuencialmente (1, 2, 3...)
        this.adelantos = filtrados.sort((a: any, b: any) => {
          const nombreA = a.nombreCompleto || a.obreroNombreCompleto || '';
          const nombreB = b.nombreCompleto || b.obreroNombreCompleto || '';
          const compObrero = nombreA.localeCompare(nombreB, 'es', { sensitivity: 'base' });
          if (compObrero !== 0) return compObrero;
          return (a.numeroSecuencial || 0) - (b.numeroSecuencial || 0);
        });

        this.cargando = false;
        this.cd.detectChanges();
      },
      error: (err: any) => {
        console.error(err);
        this.cargando = false;
        this.cd.detectChanges();
      }
    });
  }

  private calcularSecuenciaPorObrero(items: any[]): any[] {
    // Orden cronológico para reconstruir la secuencia histórica exacta
    const ordenados = [...items].sort((a, b) => {
      const fechaComp = (a.fecha || '').localeCompare(b.fecha || '');
      if (fechaComp !== 0) return fechaComp;
      return (Number(a.id) || 0) - (Number(b.id) || 0);
    });

    const contadorPorObrero = new Map<number, number>();
    const mapaSecuencia = new Map<number, number>();

    for (const ad of ordenados) {
      const oid = Number(ad.obreroId);
      const seqActual = (contadorPorObrero.get(oid) || 0) + 1;
      contadorPorObrero.set(oid, seqActual);
      mapaSecuencia.set(ad.id, seqActual);
    }

    return items.map((ad: any) => {
      const oid = Number(ad.obreroId);
      const seq = ad.numeroSecuencial || mapaSecuencia.get(ad.id) || 1;
      return {
        ...ad,
        numeroSecuencial: seq,
        totalAdelantosObrero: contadorPorObrero.get(oid) || 1
      };
    });
  }

  obtenerTotalAdelantosObrero(obreroId: number | null): number {
    if (!obreroId) return 0;
    return this.todosAdelantos.filter(a => Number(a.obreroId) === Number(obreroId)).length;
  }

  obtenerSiguienteSecuencia(obreroId: number | null): number {
    return this.obtenerTotalAdelantosObrero(obreroId) + 1;
  }

  obtenerNombreObrero(obreroId: number | null): string {
    if (!obreroId) return '';
    const ob = this.obreros.find(o => Number(o.id) === Number(obreroId));
    return ob ? `${ob.apellidos}, ${ob.nombres}` : `Obrero #${obreroId}`;
  }

  limpiarFiltroObrero(): void {
    this.filtroObreroId = null;
    this.cargarAdelantos();
  }

  guardarAdelanto(): void {
    if (!this.nuevoAdelanto.obreroId) {
      alert('Seleccione un obrero');
      return;
    }

    const siguienteNum = this.obtenerSiguienteSecuencia(this.nuevoAdelanto.obreroId);

    const payload = {
      obreroId: Number(this.nuevoAdelanto.obreroId),
      fecha: this.nuevoAdelanto.fecha,
      importe: Number(this.nuevoAdelanto.importe),
      concepto: this.nuevoAdelanto.concepto
    };

    this.api.registrarAdelanto(payload).subscribe({
      next: () => {
        alert(`¡Adelanto N° ${siguienteNum} registrado correctamente!`);
        this.nuevoAdelanto.importe = 20;
        this.cargarAdelantos();
      },
      error: (err: any) => {
        console.error(err);
        alert('Error al registrar el adelanto: ' + (err.error?.message || err.message));
      }
    });
  }

  anular(id: number): void {
    const item = this.todosAdelantos.find(a => a.id === id);
    const numTexto = item ? `Adelanto N° ${item.numeroSecuencial}` : `Vale #${id}`;
    if (confirm(`¿Estás seguro de anular el ${numTexto}?`)) {
      this.api.anularAdelanto(id).subscribe({
        next: () => {
          alert(`${numTexto} anulado correctamente.`);
          this.cargarAdelantos();
        },
        error: (err) => alert('Error al anular: ' + (err.error?.message || err.message))
      });
    }
  }

  get totalImporteActivo(): number {
    return this.adelantos
      .filter(a => a.estado === 'REGISTRADO')
      .reduce((acc, a) => acc + (Number(a.importe) || 0), 0);
  }
}

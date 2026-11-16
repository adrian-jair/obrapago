import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api';

@Component({
  selector: 'app-accesos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './accesos.html',
  styleUrls: ['./accesos.css']
})
export class AccesosComponent implements OnInit {
  accesos: any[] = [];
  cargando: boolean = false;
  filtroUsuario: string = '';

  nuevoLog = {
    username: 'admin',
    rol: 'ADMIN',
    accion: 'LOGIN_EXITOSO',
    ipRemota: '127.0.0.1',
    navegador: navigator.userAgent.substring(0, 80),
    exito: true,
    mensaje: 'Acceso registrado desde frontend Angular'
  };

  constructor(
    private api: ApiService,
    private cd: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.cargarAccesos();
  }

  cargarAccesos(): void {
    this.cargando = true;
    this.api.getAccesos(this.filtroUsuario || undefined).subscribe({
      next: (data) => {
        this.accesos = data;
        this.cargando = false;
        this.cd.detectChanges();
      },
      error: (err) => {
        console.error('Error al cargar accesos:', err);
        this.cargando = false;
        this.cd.detectChanges();
      }
    });
  }

  simularEvento(): void {
    this.api.registrarAcceso(this.nuevoLog).subscribe({
      next: () => {
        alert('¡Evento de auditoría registrado correctamente!');
        this.cargarAccesos();
      },
      error: (err) => alert('Error al registrar evento: ' + err.message)
    });
  }

  eliminar(id: number): void {
    if (confirm(`¿Estás seguro de eliminar este registro de auditoría?`)) {
      this.api.deleteAcceso(id).subscribe({
        next: () => {
          this.cargarAccesos();
        },
        error: (err) => alert('Error al eliminar registro: ' + err.message)
      });
    }
  }
}

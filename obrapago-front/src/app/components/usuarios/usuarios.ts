import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api';
import { KeycloakService } from '../../services/keycloak.service';

@Component({
  selector: 'app-usuarios',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './usuarios.html',
  styleUrls: ['./usuarios.css']
})
export class UsuariosComponent implements OnInit {
  usuarios: any[] = [];
  cargando: boolean = false;
  filtro: string = '';
  idEdicion: number | null = null;

  nuevoUsuario: any = {
    username: '',
    nombreCompleto: '',
    email: '',
    rol: 'SUPERVISOR',
    activo: true
  };

  rolesDisponibles = ['ADMIN', 'SUPERVISOR', 'PLANILLERO', 'CONSULTA'];

  constructor(
    private api: ApiService,
    public keycloak: KeycloakService,
    private cd: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.cargarUsuarios();
  }

  cargarUsuarios(): void {
    this.cargando = true;
    this.api.getUsuarios().subscribe({
      next: (data) => {
        this.usuarios = data;
        this.cargando = false;
        this.cd.detectChanges();
      },
      error: (err) => {
        console.error('Error al cargar usuarios:', err);
        this.cargando = false;
        this.cd.detectChanges();
      }
    });
  }

  guardarUsuario(): void {
    if (!this.nuevoUsuario.username || !this.nuevoUsuario.nombreCompleto || !this.nuevoUsuario.email) {
      alert('Por favor completa todos los campos requeridos');
      return;
    }

    if (this.idEdicion) {
      this.api.updateUsuario(this.idEdicion, this.nuevoUsuario).subscribe({
        next: () => {
          alert('¡Usuario actualizado exitosamente!');
          this.limpiarFormulario();
          this.cargarUsuarios();
        },
        error: (err) => {
          console.error(err);
          alert('Error al actualizar el usuario: ' + (err.error?.message || err.message));
        }
      });
    } else {
      this.api.createUsuario(this.nuevoUsuario).subscribe({
        next: () => {
          alert('¡Usuario creado exitosamente!');
          this.limpiarFormulario();
          this.cargarUsuarios();
        },
        error: (err) => {
          console.error(err);
          alert('Error al crear usuario: ' + (err.error?.message || err.message));
        }
      });
    }
  }

  editar(u: any): void {
    this.idEdicion = u.id;
    this.nuevoUsuario = {
      username: u.username,
      nombreCompleto: u.nombreCompleto,
      email: u.email,
      rol: u.rol,
      activo: u.activo
    };
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  cambiarEstado(u: any): void {
    const nuevoEstado = !u.activo;
    const confirmMsg = nuevoEstado
      ? `¿Deseas activar al usuario ${u.username}?`
      : `¿Deseas suspender/desactivar al usuario ${u.username}?`;

    if (confirm(confirmMsg)) {
      this.api.cambiarEstadoUsuario(u.id, nuevoEstado).subscribe({
        next: () => {
          u.activo = nuevoEstado;
          this.cd.detectChanges();
        },
        error: (err) => alert('Error al cambiar estado: ' + err.message)
      });
    }
  }

  limpiarFormulario(): void {
    this.idEdicion = null;
    this.nuevoUsuario = {
      username: '',
      nombreCompleto: '',
      email: '',
      rol: 'SUPERVISOR',
      activo: true
    };
  }

  get usuariosFiltrados(): any[] {
    if (!this.filtro.trim()) return this.usuarios;
    const term = this.filtro.toLowerCase();
    return this.usuarios.filter(u =>
      (u.username && u.username.toLowerCase().includes(term)) ||
      (u.nombreCompleto && u.nombreCompleto.toLowerCase().includes(term)) ||
      (u.email && u.email.toLowerCase().includes(term)) ||
      (u.rol && u.rol.toLowerCase().includes(term))
    );
  }
}

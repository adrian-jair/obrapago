import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { KeycloakService } from '../../services/keycloak.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class LoginComponent implements OnInit {
  username: string = 'admin';
  password: string = 'admin';
  mensajeError: string = '';
  cargando: boolean = false;
  keycloakOnline: boolean = false;

  cuentasDemo = [
    { username: 'admin', rol: 'ADMIN', label: 'Admin (Control Total)' },
    { username: 'supervisor', rol: 'SUPERVISOR', label: 'Supervisor (Obras & Asistencia)' },
    { username: 'planillero', rol: 'PLANILLERO', label: 'Planillero (Finanzas & Pagos)' },
    { username: 'obrero', rol: 'CONSULTA', label: 'Obrero (Solo Consulta)' }
  ];

  constructor(
    public keycloak: KeycloakService,
    private router: Router
  ) {}

  async ngOnInit(): Promise<void> {
    // Si ya está autenticado, redirigir al panel principal
    if (this.keycloak.isLoggedIn()) {
      this.router.navigate(['/obreros']);
      return;
    }
    this.keycloakOnline = await this.keycloak.checkKeycloakAvailability();
  }

  seleccionarCuenta(c: any): void {
    this.username = c.username;
    this.password = c.username;
    this.iniciarSesionLocal();
  }

  iniciarSesionLocal(): void {
    this.mensajeError = '';
    this.cargando = true;

    const ok = this.keycloak.loginLocal(this.username.trim(), this.password);
    this.cargando = false;

    if (ok) {
      this.router.navigate(['/obreros']);
    } else {
      this.mensajeError = 'Credenciales inválidas. Usa una de las cuentas demo indicadas abajo.';
    }
  }

  iniciarConKeycloak(): void {
    this.keycloak.login();
  }
}

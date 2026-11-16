import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterOutlet, RouterLink, RouterLinkActive, Router } from '@angular/router';
import { KeycloakService } from './services/keycloak.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './app.html',
  styleUrls: ['./app.css']
})
export class AppComponent {
  title = 'ObraPago';

  constructor(
    public keycloak: KeycloakService,
    private router: Router
  ) {}

  login(): void {
    this.keycloak.login();
  }

  logout(): void {
    this.keycloak.logout();
  }

  cambiarUsuario(username: string): void {
    this.keycloak.switchUser(username);
    // Redirigir a obreros o refrescar la vista con los nuevos permisos
    this.router.navigate(['/obreros']).then(() => {
      window.location.reload();
    });
  }
}
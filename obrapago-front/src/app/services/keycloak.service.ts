import { Injectable, inject } from '@angular/core';
import { Router } from '@angular/router';
import Keycloak from 'keycloak-js';

export interface UserProfile {
  id?: string;
  username: string;
  email?: string;
  firstName?: string;
  lastName?: string;
  roles: string[];
}

@Injectable({
  providedIn: 'root'
})
export class KeycloakService {
  private keycloakInstance: Keycloak | null = null;
  private authenticated: boolean = false;
  private isKeycloakSso: boolean = false;
  private currentUser: UserProfile | null = null;

  private router = inject(Router);

  constructor() {}

  async init(): Promise<boolean> {
    // 1. Verificar si hay sesión local previamente guardada
    const savedSession = localStorage.getItem('OBRAPAGO_USER_SESSION');
    if (savedSession) {
      try {
        this.currentUser = JSON.parse(savedSession);
        this.authenticated = true;
        this.isKeycloakSso = false;
        console.log('[Auth] Sesión local activa:', this.currentUser?.username);
        return true;
      } catch (e) {
        localStorage.removeItem('OBRAPAGO_USER_SESSION');
      }
    }

    // 2. Intentar autenticación con Keycloak si el servidor responde
    try {
      this.keycloakInstance = new Keycloak({
        url: 'http://localhost:8081',
        realm: 'obrapago-realm',
        clientId: 'obrapago-front'
      });

      const authPromise = this.keycloakInstance.init({
        onLoad: 'check-sso',
        checkLoginIframe: false,
        silentCheckSsoRedirectUri: window.location.origin + '/silent-check-sso.html'
      });

      const timeoutPromise = new Promise<boolean>((resolve) => {
        setTimeout(() => resolve(false), 2000);
      });

      const res = await Promise.race([authPromise, timeoutPromise]);
      if (res && this.keycloakInstance.authenticated) {
        this.authenticated = true;
        this.isKeycloakSso = true;
        console.log('[Auth] Keycloak SSO autenticado:', this.getUsername());
        return true;
      }
    } catch (err) {
      console.warn('[Auth] Keycloak server no disponible o no autenticado.');
    }

    // Si no hay sesión, permanecer sin autenticar (irá a /login)
    this.authenticated = false;
    return false;
  }

  async checkKeycloakAvailability(): Promise<boolean> {
    try {
      const res = await fetch('http://localhost:8081/realms/obrapago-realm', { method: 'GET', mode: 'no-cors' });
      return true;
    } catch (e) {
      return false;
    }
  }

  isLoggedIn(): boolean {
    return this.authenticated;
  }

  isDevMode(): boolean {
    return !this.isKeycloakSso;
  }

  loginLocal(username: string, password: string): boolean {
    const rolesMap: Record<string, string[]> = {
      admin: ['ADMIN'],
      supervisor: ['SUPERVISOR'],
      planillero: ['PLANILLERO'],
      obrero: ['CONSULTA']
    };

    const userLower = username.toLowerCase();
    const roles = rolesMap[userLower] || ['CONSULTA'];

    const userNamesMap: Record<string, { first: string; last: string }> = {
      admin: { first: 'Admin', last: 'Principal (ADMIN)' },
      supervisor: { first: 'Carlos', last: 'Supervisor de Obra' },
      planillero: { first: 'Ana', last: 'Planillas y Finanzas' },
      obrero: { first: 'Juan', last: 'Estrada (Obrero)' }
    };

    const names = userNamesMap[userLower] || {
      first: username.charAt(0).toUpperCase() + username.slice(1),
      last: 'Usuario'
    };

    this.currentUser = {
      username: username,
      email: `${userLower}@obrapago.pe`,
      firstName: names.first,
      lastName: names.last,
      roles: roles
    };

    this.authenticated = true;
    this.isKeycloakSso = false;
    localStorage.setItem('OBRAPAGO_USER_SESSION', JSON.stringify(this.currentUser));
    return true;
  }

  switchUser(username: string): void {
    this.loginLocal(username, username);
  }

  async login(): Promise<void> {
    if (this.keycloakInstance) {
      try {
        await this.keycloakInstance.login({ redirectUri: window.location.origin + '/obreros' });
        return;
      } catch (e) {
        console.error('Error al redirigir a Keycloak:', e);
      }
    }
    this.router.navigate(['/login']);
  }

  async logout(): Promise<void> {
    localStorage.removeItem('OBRAPAGO_USER_SESSION');
    this.currentUser = null;
    this.authenticated = false;

    if (this.keycloakInstance && this.isKeycloakSso) {
      try {
        await this.keycloakInstance.logout({ redirectUri: window.location.origin + '/login' });
        return;
      } catch (e) {
        console.error('Error logout Keycloak:', e);
      }
    }

    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    if (this.isKeycloakSso && this.keycloakInstance?.token) {
      return this.keycloakInstance.token;
    }
    return this.authenticated ? 'LOCAL_DEMO_BEARER_TOKEN' : null;
  }

  getUsername(): string {
    if (this.isKeycloakSso) {
      return (
        this.keycloakInstance?.tokenParsed?.['preferred_username'] ||
        this.keycloakInstance?.tokenParsed?.['name'] ||
        'Usuario'
      );
    }
    return this.currentUser?.username || 'Usuario';
  }

  getUserFullName(): string {
    if (this.isKeycloakSso) {
      const token = this.keycloakInstance?.tokenParsed;
      if (token) {
        const given = token['given_name'] || '';
        const family = token['family_name'] || '';
        const full = `${given} ${family}`.trim();
        return full || token['name'] || this.getUsername();
      }
    }
    if (this.currentUser) {
      return `${this.currentUser.firstName} ${this.currentUser.lastName}`;
    }
    return this.getUsername();
  }

  getRoles(): string[] {
    if (this.isKeycloakSso) {
      const realmAccess = this.keycloakInstance?.tokenParsed?.['realm_access'];
      if (realmAccess && realmAccess.roles) {
        return realmAccess.roles;
      }
    }
    return this.currentUser?.roles || ['CONSULTA'];
  }

  hasRole(role: string): boolean {
    return this.getRoles().includes(role);
  }

  // Permisos granulares de seguridad
  isAdmin(): boolean {
    return this.hasRole('ADMIN');
  }

  isSupervisor(): boolean {
    return this.hasRole('SUPERVISOR');
  }

  isPlanillero(): boolean {
    return this.hasRole('PLANILLERO');
  }

  isObrero(): boolean {
    return this.hasRole('CONSULTA') || (!this.isAdmin() && !this.isSupervisor() && !this.isPlanillero());
  }

  canEdit(): boolean {
    return this.isAdmin();
  }

  canDelete(): boolean {
    return this.isAdmin();
  }

  canManageObreros(): boolean {
    return this.isAdmin();
  }

  canManageObras(): boolean {
    return this.isAdmin();
  }

  canAssignCuadrilla(): boolean {
    return this.isAdmin() || this.isSupervisor();
  }

  canManageAsistencia(): boolean {
    return this.isAdmin() || this.isSupervisor();
  }

  canManageAdelantos(): boolean {
    return this.isAdmin() || this.isPlanillero();
  }

  canManagePagos(): boolean {
    return this.isAdmin() || this.isPlanillero();
  }

  canManageContratos(): boolean {
    return this.isAdmin();
  }

  canManageUsuarios(): boolean {
    return this.isAdmin();
  }

  getRoleBadgeInfo(): { label: string; color: string; desc: string } {
    if (this.isAdmin()) {
      return { label: 'ADMINISTRADOR', color: 'bg-danger', desc: 'Control Total' };
    }
    if (this.isSupervisor()) {
      return { label: 'SUPERVISOR', color: 'bg-info text-dark', desc: 'Obras & Asistencia' };
    }
    if (this.isPlanillero()) {
      return { label: 'PLANILLERO', color: 'bg-warning text-dark', desc: 'Finanzas & Pagos' };
    }
    return { label: 'OBRERO', color: 'bg-secondary', desc: 'Solo Consulta' };
  }

  setMockRole(role: string): void {
    if (this.currentUser) {
      this.currentUser.roles = [role];
      localStorage.setItem('OBRAPAGO_USER_SESSION', JSON.stringify(this.currentUser));
    }
  }
}

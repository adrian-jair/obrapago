import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { KeycloakService } from '../services/keycloak.service';

export const authGuard: CanActivateFn = (route, state) => {
  const keycloak = inject(KeycloakService);
  const router = inject(Router);

  if (keycloak.isLoggedIn()) {
    const requiredRoles = route.data?.['roles'] as Array<string>;
    if (requiredRoles && requiredRoles.length > 0) {
      const hasPermission = requiredRoles.some(r => keycloak.hasRole(r));
      if (!hasPermission) {
        alert('Acceso restringido: tu rol no tiene permisos para acceder a esta sección.');
        return false;
      }
    }
    return true;
  }

  router.navigate(['/login']);
  return false;
};

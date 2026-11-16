import { Routes } from '@angular/router';
import { authGuard } from './guards/auth.guard';

import { LoginComponent } from './components/login/login';
import { ObrerosComponent } from './components/obreros/obreros';
import { UsuariosComponent } from './components/usuarios/usuarios';
import { ContratosComponent } from './components/contratos/contratos';
import { AsistenciasComponent } from './components/asistencias/asistencias';
import { PagosComponent } from './components/pagos/pagos';
import { DetallesAsistenciaComponent } from './components/detalles-asistencia/detalles-asistencia';
import { AdelantosComponent } from './components/adelantos/adelantos';
import { DetallesPagoComponent } from './components/detalles-pago/detalles-pago';
import { AccesosComponent } from './components/accesos/accesos';
import { ConsultaObreroComponent } from './components/consulta-obrero/consulta-obrero';
import { ObrasComponent } from './components/obras/obras';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: '', redirectTo: 'obreros', pathMatch: 'full' },

  // Módulo 1: OBREROS
  { path: 'obreros', component: ObrerosComponent, canActivate: [authGuard] },

  // Módulo 2: USUARIOS
  { path: 'usuarios', component: UsuariosComponent, canActivate: [authGuard], data: { roles: ['ADMIN'] } },

  // Módulo 3: CONTRATOS
  { path: 'contratos', component: ContratosComponent, canActivate: [authGuard] },

  // Módulo 4: JORNADAS_ASISTENCIA
  { path: 'asistencias', component: AsistenciasComponent, canActivate: [authGuard] },
  { path: 'jornadas', redirectTo: 'asistencias', pathMatch: 'full' },

  // Módulo 5: PLANILLA_MENSUAL
  { path: 'pagos', component: PagosComponent, canActivate: [authGuard] },
  { path: 'planilla-mensual', redirectTo: 'pagos', pathMatch: 'full' },

  // Módulo 6: DETALLES_ASISTENCIA
  { path: 'detalles-asistencia', component: DetallesAsistenciaComponent, canActivate: [authGuard] },

  // Módulo 7: ADELANTOS
  { path: 'adelantos', component: AdelantosComponent, canActivate: [authGuard] },

  // Módulo 8: DETALLES_PAGO
  { path: 'detalles-pago', component: DetallesPagoComponent, canActivate: [authGuard] },

  // Módulo 9: ACCESOS (Auditoría)
  { path: 'accesos', component: AccesosComponent, canActivate: [authGuard], data: { roles: ['ADMIN'] } },

  // Módulo 10: CONSULTA_OBRERO_PAGO
  { path: 'consulta-obrero', component: ConsultaObreroComponent, canActivate: [authGuard] },

  // Módulo N:M — OBRAS Y ASIGNACIÓN MASIVA
  { path: 'obras', component: ObrasComponent, canActivate: [authGuard] },

  { path: '**', redirectTo: 'obreros' }
];
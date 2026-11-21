import { inject } from '@angular/core';
import { Routes } from '@angular/router';
import { authGuard, roleGuard } from './core/auth.guard';
import { AuthService } from './core/auth.service';
import { ShellComponent } from './layout/shell.component';
import { LoginComponent } from './pages/login/login.component';
import { ProfessoresComponent } from './pages/admin/professores/professores.component';
import { RequisicoesComponent } from './pages/admin/requisicoes/requisicoes.component';
import { SalasAdminComponent } from './pages/admin/salas/salas-admin.component';
import { MinhasReservasComponent } from './pages/professor/reservas/minhas-reservas.component';
import { RecomendacaoComponent } from './pages/professor/recomendacao/recomendacao.component';
import { SalasComponent } from './pages/professor/salas/salas.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent, title: 'Entrar' },
  {
    path: '',
    component: ShellComponent,
    canActivate: [authGuard],
    children: [
      { path: '', pathMatch: 'full', redirectTo: () => inject(AuthService).paginaInicial() },

      // Professor
      { path: 'salas', component: SalasComponent, canActivate: [roleGuard('PROFESSOR')], title: 'Salas' },
      { path: 'reservas', component: MinhasReservasComponent, canActivate: [roleGuard('PROFESSOR')], title: 'Minhas reservas' },
      { path: 'recomendacao', component: RecomendacaoComponent, canActivate: [roleGuard('PROFESSOR')], title: 'Sugestão de sala' },

      // Administrador
      { path: 'admin/requisicoes', component: RequisicoesComponent, canActivate: [roleGuard('ADMIN')], title: 'Pedidos' },
      { path: 'admin/salas', component: SalasAdminComponent, canActivate: [roleGuard('ADMIN')], title: 'Salas' },
      { path: 'admin/professores', component: ProfessoresComponent, canActivate: [roleGuard('ADMIN')], title: 'Professores' },
    ],
  },
  { path: '**', redirectTo: 'login' },
];

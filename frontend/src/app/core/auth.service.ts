import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';
import { LoginResponse, Role } from './models';

interface Sessao {
  token: string;
  role: Role;
  login: string;
  expiraEm: number;
}

const CHAVE = 'reserva-salas.sessao';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private sessao: Sessao | null = this.carregar();

  entrar(login: string, senha: string): Observable<void> {
    return this.http.post<LoginResponse>('/api/auth/login', { login, senha }).pipe(
      map((resp) => {
        this.sessao = {
          token: resp.token,
          role: resp.role,
          login,
          expiraEm: Date.now() + resp.expiraEmSegundos * 1000,
        };
        localStorage.setItem(CHAVE, JSON.stringify(this.sessao));
      })
    );
  }

  sair(): void {
    this.sessao = null;
    localStorage.removeItem(CHAVE);
  }

  logado(): boolean {
    return this.sessao !== null && this.sessao.expiraEm > Date.now();
  }

  token(): string | null {
    return this.logado() ? this.sessao!.token : null;
  }

  role(): Role | null {
    return this.sessao?.role ?? null;
  }

  login(): string {
    return this.sessao?.login ?? '';
  }

  paginaInicial(): string {
    return this.role() === 'ADMIN' ? '/admin/requisicoes' : '/salas';
  }

  private carregar(): Sessao | null {
    try {
      const salvo = localStorage.getItem(CHAVE);
      return salvo ? (JSON.parse(salvo) as Sessao) : null;
    } catch {
      return null;
    }
  }
}

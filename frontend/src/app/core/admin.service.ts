import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Requisicao, Sala, StatusRequisicao, TipoAula, TipoSala } from './models';

export interface NovaSala {
  tamanho: number;
  tipoSala: TipoSala;
}

export interface NovoProfessor {
  nome: string;
  cpf: string;
  disciplina: string;
  tipoAula: TipoAula;
  login: string;
  senha: string;
}

export interface Professor {
  id: number;
  nome: string;
  cpf: string;
  disciplina: string;
  tipoAula: TipoAula;
  login: string;
}

@Injectable({ providedIn: 'root' })
export class AdminService {
  private http = inject(HttpClient);

  // Requisições
  listarRequisicoes(status?: StatusRequisicao) {
    const params: Record<string, string> = status ? { status } : {};
    return this.http.get<Requisicao[]>('/api/requisicoes', { params });
  }

  aprovar(id: number) {
    return this.http.patch<Requisicao>(`/api/requisicoes/${id}/aprovar`, {});
  }

  rejeitar(id: number) {
    return this.http.patch<Requisicao>(`/api/requisicoes/${id}/rejeitar`, {});
  }

  // Salas
  criarSala(sala: NovaSala) {
    return this.http.post<Sala>('/api/admin/salas', sala);
  }

  atualizarSala(id: number, sala: NovaSala) {
    return this.http.put<Sala>(`/api/admin/salas/${id}`, sala);
  }

  alterarDisponibilidade(id: number, disponivel: boolean) {
    return this.http.patch<Sala>(`/api/admin/salas/${id}/disponibilidade`, {}, {
      params: { disponivel },
    });
  }

  excluirSala(id: number) {
    return this.http.delete<void>(`/api/admin/salas/${id}`);
  }

  // Professores
  cadastrarProfessor(dados: NovoProfessor) {
    return this.http.post<Professor>('/api/admin/professores', dados);
  }
}

import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { NovaRequisicao, Requisicao } from './models';

@Injectable({ providedIn: 'root' })
export class RequisicoesService {
  private http = inject(HttpClient);

  solicitar(dados: NovaRequisicao) {
    return this.http.post<Requisicao>('/api/requisicoes', dados);
  }

  minhas() {
    return this.http.get<Requisicao[]>('/api/requisicoes/minhas');
  }

  cancelar(id: number) {
    return this.http.patch<Requisicao>(`/api/requisicoes/${id}/cancelar`, {});
  }
}

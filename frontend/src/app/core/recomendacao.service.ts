import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { TipoSala } from './models';

export interface PedidoRecomendacao {
  data: string;
  horaInicio: string;
  horaFim: string;
  quantidadeAlunos: number | null;
}

export interface SalaRecomendada {
  salaId: number;
  tipoSala: TipoSala;
  capacidade: number;
  posicao: number;
  justificativa: string;
}

export interface ResultadoRecomendacao {
  disciplina: string;
  tipoSolicitado: TipoSala;
  totalSalasElegiveis: number;
  recomendacoes: SalaRecomendada[];
}

@Injectable({ providedIn: 'root' })
export class RecomendacaoService {
  private http = inject(HttpClient);

  recomendar(pedido: PedidoRecomendacao) {
    return this.http.post<ResultadoRecomendacao>('/api/recomendacoes/ia', pedido);
  }
}

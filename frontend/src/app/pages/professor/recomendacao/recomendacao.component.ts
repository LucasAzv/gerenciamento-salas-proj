import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { mensagemErro } from '../../../core/erros';
import { RecomendacaoService, ResultadoRecomendacao, SalaRecomendada } from '../../../core/recomendacao.service';
import { RequisicoesService } from '../../../core/requisicoes.service';
import { DataBrPipe, TipoSalaPipe, hojeIso } from '../../../shared/formatos';

interface Consulta {
  data: string;
  inicio: string;
  fim: string;
  resultado: ResultadoRecomendacao;
}

@Component({
  selector: 'app-recomendacao',
  imports: [FormsModule, RouterLink, DataBrPipe, TipoSalaPipe],
  templateUrl: './recomendacao.component.html',
})
export class RecomendacaoComponent {
  private recomendacoes = inject(RecomendacaoService);
  private requisicoes = inject(RequisicoesService);

  hoje = hojeIso();
  data = this.hoje;
  inicio = '08:00';
  fim = '10:00';
  alunos: number | null = null;

  consulta: Consulta | null = null;
  solicitadas = new Set<number>();
  carregando = false;
  enviandoId: number | null = null;
  erro = '';
  aviso = '';

  buscar() {
    this.erro = '';
    this.aviso = '';

    if (!this.data || !this.inicio || !this.fim) {
      this.erro = 'Preencha data, início e fim.';
      return;
    }
    if (this.inicio >= this.fim) {
      this.erro = 'O horário de início precisa ser antes do fim.';
      return;
    }

    const { data, inicio, fim } = this;
    this.carregando = true;
    this.consulta = null;

    this.recomendacoes
      .recomendar({ data, horaInicio: inicio, horaFim: fim, quantidadeAlunos: this.alunos || null })
      .subscribe({
        next: (resultado) => {
          this.consulta = { data, inicio, fim, resultado };
          this.solicitadas.clear();
          this.carregando = false;
        },
        error: (err) => {
          this.erro = mensagemErro(err, 'Não foi possível obter as sugestões agora. Tente de novo em instantes.');
          this.carregando = false;
        },
      });
  }

  solicitar(item: SalaRecomendada, consulta: Consulta) {
    this.erro = '';
    this.aviso = '';
    this.enviandoId = item.salaId;

    this.requisicoes
      .solicitar({ salaId: item.salaId, data: consulta.data, horaInicio: consulta.inicio, horaFim: consulta.fim })
      .subscribe({
        next: () => {
          this.solicitadas.add(item.salaId);
          this.aviso = `Pedido da sala ${item.salaId} enviado. Ele fica pendente até o administrador aprovar.`;
          this.enviandoId = null;
        },
        error: (err) => {
          this.erro = mensagemErro(err);
          this.enviandoId = null;
        },
      });
  }
}

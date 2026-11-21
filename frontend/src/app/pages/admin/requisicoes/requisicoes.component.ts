import { LowerCasePipe } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AdminService } from '../../../core/admin.service';
import { mensagemErro } from '../../../core/erros';
import { Requisicao, StatusRequisicao } from '../../../core/models';
import { DataBrPipe, HoraPipe, StatusPipe } from '../../../shared/formatos';

@Component({
  selector: 'app-requisicoes',
  imports: [FormsModule, LowerCasePipe, DataBrPipe, HoraPipe, StatusPipe],
  templateUrl: './requisicoes.component.html',
})
export class RequisicoesComponent implements OnInit {
  private admin = inject(AdminService);

  filtro: StatusRequisicao | '' = 'PENDENTE';
  requisicoes: Requisicao[] = [];
  carregando = true;
  processandoId: number | null = null;
  erro = '';

  ngOnInit() {
    this.carregar();
  }

  carregar() {
    this.carregando = true;
    this.erro = '';

    this.admin.listarRequisicoes(this.filtro || undefined).subscribe({
      next: (lista) => {
        this.requisicoes = [...lista].sort((a, b) =>
          (a.data + a.horaInicio).localeCompare(b.data + b.horaInicio)
        );
        this.carregando = false;
      },
      error: (err) => {
        this.erro = mensagemErro(err);
        this.carregando = false;
      },
    });
  }

  aprovar(r: Requisicao) {
    this.decidir(r, 'aprovar');
  }

  rejeitar(r: Requisicao) {
    if (!confirm(`Rejeitar o pedido #${r.id}?`)) return;
    this.decidir(r, 'rejeitar');
  }

  private decidir(r: Requisicao, acao: 'aprovar' | 'rejeitar') {
    this.erro = '';
    this.processandoId = r.id;

    const chamada = acao === 'aprovar' ? this.admin.aprovar(r.id) : this.admin.rejeitar(r.id);

    chamada.subscribe({
      next: (atualizada) => {
        this.processandoId = null;
        // Com o filtro "Pendentes", o pedido decidido sai da lista.
        if (this.filtro && atualizada.status !== this.filtro) {
          this.requisicoes = this.requisicoes.filter((x) => x.id !== atualizada.id);
        } else {
          this.requisicoes = this.requisicoes.map((x) => (x.id === atualizada.id ? atualizada : x));
        }
      },
      error: (err) => {
        this.erro = mensagemErro(err);
        this.processandoId = null;
      },
    });
  }
}

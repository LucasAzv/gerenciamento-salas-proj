import { LowerCasePipe } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { mensagemErro } from '../../../core/erros';
import { Requisicao } from '../../../core/models';
import { RequisicoesService } from '../../../core/requisicoes.service';
import { DataBrPipe, HoraPipe, StatusPipe } from '../../../shared/formatos';

@Component({
  selector: 'app-minhas-reservas',
  imports: [LowerCasePipe, DataBrPipe, HoraPipe, StatusPipe],
  templateUrl: './minhas-reservas.component.html',
})
export class MinhasReservasComponent implements OnInit {
  private requisicoes = inject(RequisicoesService);

  reservas: Requisicao[] = [];
  carregando = true;
  erro = '';

  ngOnInit() {
    this.carregar();
  }

  carregar() {
    this.carregando = true;
    this.requisicoes.minhas().subscribe({
      next: (lista) => {
        this.reservas = lista;
        this.carregando = false;
      },
      error: (err) => {
        this.erro = mensagemErro(err);
        this.carregando = false;
      },
    });
  }

  podeCancelar(r: Requisicao) {
    return r.status === 'PENDENTE' || r.status === 'APROVADA';
  }

  cancelar(r: Requisicao) {
    if (!confirm(`Cancelar a reserva da sala ${r.salaId}?`)) return;

    this.erro = '';
    this.requisicoes.cancelar(r.id).subscribe({
      next: (atualizada) => {
        this.reservas = this.reservas.map((x) => (x.id === atualizada.id ? atualizada : x));
      },
      error: (err) => (this.erro = mensagemErro(err)),
    });
  }
}

import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { mensagemErro } from '../../../core/erros';
import { Disponibilidade, Sala } from '../../../core/models';
import { RequisicoesService } from '../../../core/requisicoes.service';
import { SalasService } from '../../../core/salas.service';
import { DataBrPipe, TipoSalaPipe, hojeIso } from '../../../shared/formatos';

interface Busca {
  data: string;
  inicio: string;
  fim: string;
  salas: Disponibilidade[];
}

@Component({
  selector: 'app-salas',
  imports: [FormsModule, RouterLink, DataBrPipe, TipoSalaPipe],
  templateUrl: './salas.component.html',
})
export class SalasComponent {
  private salasService = inject(SalasService);
  private requisicoes = inject(RequisicoesService);

  hoje = hojeIso();
  data = this.hoje;
  inicio = '08:00';
  fim = '10:00';

  busca: Busca | null = null;
  solicitadas = new Set<number>();
  carregando = false;
  enviandoId: number | null = null;
  erro = '';
  aviso = '';

  consultar() {
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

    this.salasService.disponibilidade(data, inicio, fim).subscribe({
      next: (salas) => {
        this.busca = { data, inicio, fim, salas: [...salas].sort((a, b) => a.sala.id - b.sala.id) };
        this.solicitadas.clear();
        this.carregando = false;
      },
      error: (err) => {
        this.erro = mensagemErro(err);
        this.busca = null;
        this.carregando = false;
      },
    });
  }

  solicitar(sala: Sala, busca: Busca) {
    this.erro = '';
    this.aviso = '';
    this.enviandoId = sala.id;

    this.requisicoes
      .solicitar({ salaId: sala.id, data: busca.data, horaInicio: busca.inicio, horaFim: busca.fim })
      .subscribe({
        next: () => {
          this.solicitadas.add(sala.id);
          this.aviso = `Pedido da sala ${sala.id} enviado. Ele fica pendente até o administrador aprovar.`;
          this.enviandoId = null;
        },
        error: (err) => {
          this.erro = mensagemErro(err);
          this.enviandoId = null;
        },
      });
  }
}

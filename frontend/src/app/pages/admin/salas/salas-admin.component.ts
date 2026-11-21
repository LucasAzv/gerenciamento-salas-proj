import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AdminService } from '../../../core/admin.service';
import { mensagemErro } from '../../../core/erros';
import { Sala, TipoSala } from '../../../core/models';
import { SalasService } from '../../../core/salas.service';
import { TipoSalaPipe } from '../../../shared/formatos';

@Component({
  selector: 'app-salas-admin',
  imports: [FormsModule, TipoSalaPipe],
  templateUrl: './salas-admin.component.html',
})
export class SalasAdminComponent implements OnInit {
  private salasService = inject(SalasService);
  private admin = inject(AdminService);

  salas: Sala[] = [];
  carregando = true;
  salvando = false;
  erro = '';
  aviso = '';

  editandoId: number | null = null;
  tamanho: number | null = null;
  tipoSala: TipoSala = 'TEORICA';

  ngOnInit() {
    this.carregar();
  }

  carregar() {
    this.carregando = true;
    this.salasService.listar().subscribe({
      next: (lista) => {
        this.salas = [...lista].sort((a, b) => a.id - b.id);
        this.carregando = false;
      },
      error: (err) => {
        this.erro = mensagemErro(err);
        this.carregando = false;
      },
    });
  }

  editar(sala: Sala) {
    this.editandoId = sala.id;
    this.tamanho = sala.tamanho;
    this.tipoSala = sala.tipoSala;
    this.erro = '';
    this.aviso = '';
  }

  limparFormulario() {
    this.editandoId = null;
    this.tamanho = null;
    this.tipoSala = 'TEORICA';
  }

  salvar() {
    this.erro = '';
    this.aviso = '';

    if (!this.tamanho || this.tamanho < 1) {
      this.erro = 'Informe a capacidade da sala.';
      return;
    }

    const dados = { tamanho: this.tamanho, tipoSala: this.tipoSala };
    const editando = this.editandoId;
    const chamada = editando
      ? this.admin.atualizarSala(editando, dados)
      : this.admin.criarSala(dados);

    this.salvando = true;
    chamada.subscribe({
      next: (sala) => {
        this.aviso = editando ? `Sala ${sala.id} atualizada.` : `Sala ${sala.id} criada.`;
        this.salvando = false;
        this.limparFormulario();
        this.carregar();
      },
      error: (err) => {
        this.erro = mensagemErro(err);
        this.salvando = false;
      },
    });
  }

  alternarDisponibilidade(sala: Sala) {
    this.erro = '';
    this.aviso = '';
    this.admin.alterarDisponibilidade(sala.id, !sala.disponivel).subscribe({
      next: (atualizada) => {
        this.salas = this.salas.map((s) => (s.id === atualizada.id ? atualizada : s));
      },
      error: (err) => (this.erro = mensagemErro(err)),
    });
  }

  excluir(sala: Sala) {
    if (!confirm(`Excluir a sala ${sala.id}?`)) return;

    this.erro = '';
    this.aviso = '';
    this.admin.excluirSala(sala.id).subscribe({
      next: () => {
        this.salas = this.salas.filter((s) => s.id !== sala.id);
        this.aviso = `Sala ${sala.id} excluída.`;
        if (this.editandoId === sala.id) this.limparFormulario();
      },
      error: (err) => (this.erro = mensagemErro(err)),
    });
  }
}

import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AdminService, NovoProfessor } from '../../../core/admin.service';
import { mensagemErro } from '../../../core/erros';

function formularioVazio(): NovoProfessor {
  return { nome: '', cpf: '', disciplina: '', tipoAula: 'TEORICA', login: '', senha: '' };
}

@Component({
  selector: 'app-professores',
  imports: [FormsModule],
  templateUrl: './professores.component.html',
})
export class ProfessoresComponent {
  private admin = inject(AdminService);

  form = formularioVazio();
  salvando = false;
  erro = '';
  aviso = '';

  cadastrar() {
    this.erro = '';
    this.aviso = '';

    const f = this.form;
    if (!f.nome.trim() || !f.cpf.trim() || !f.disciplina.trim() || !f.login.trim() || !f.senha) {
      this.erro = 'Preencha todos os campos.';
      return;
    }
    if (f.cpf.replace(/\D/g, '').length !== 11) {
      this.erro = 'O CPF precisa ter 11 dígitos.';
      return;
    }
    if (f.senha.length < 8) {
      this.erro = 'A senha precisa ter pelo menos 8 caracteres.';
      return;
    }

    this.salvando = true;
    this.admin.cadastrarProfessor({ ...f, nome: f.nome.trim(), login: f.login.trim() }).subscribe({
      next: (prof) => {
        this.aviso = `${prof.nome} cadastrado(a). Login: ${prof.login}`;
        this.form = formularioVazio();
        this.salvando = false;
      },
      error: (err) => {
        this.erro = mensagemErro(err);
        this.salvando = false;
      },
    });
  }
}

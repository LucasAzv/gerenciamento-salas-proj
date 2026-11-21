import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/auth.service';
import { mensagemErro } from '../../core/erros';

@Component({
  selector: 'app-login',
  imports: [FormsModule],
  templateUrl: './login.component.html',
})
export class LoginComponent {
  private auth = inject(AuthService);
  private router = inject(Router);

  login = '';
  senha = '';
  erro = '';
  enviando = false;

  entrar() {
    if (!this.login.trim() || !this.senha) {
      this.erro = 'Informe login e senha.';
      return;
    }

    this.enviando = true;
    this.erro = '';

    this.auth.entrar(this.login.trim(), this.senha).subscribe({
      next: () => this.router.navigateByUrl(this.auth.paginaInicial()),
      error: (err: HttpErrorResponse) => {
        this.erro = err.status === 401 ? 'Login ou senha incorretos.' : mensagemErro(err);
        this.enviando = false;
      },
    });
  }
}

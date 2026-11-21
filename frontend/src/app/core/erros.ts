import { HttpErrorResponse } from '@angular/common/http';

// O back-end devolve { erro: "..." } ou { erro: "...", campos: ["..."] }.
export function mensagemErro(err: unknown, padrao = 'Não foi possível concluir a operação.'): string {
  if (!(err instanceof HttpErrorResponse)) {
    return padrao;
  }
  if (err.status === 0) {
    return 'Não foi possível falar com o servidor. Verifique se o back-end está rodando.';
  }
  const corpo = err.error;
  if (corpo && Array.isArray(corpo.campos) && corpo.campos.length) {
    return corpo.campos.join('; ');
  }
  if (corpo && typeof corpo.erro === 'string') {
    return corpo.erro;
  }
  if (err.status === 403) {
    return 'Você não tem permissão para fazer isso.';
  }
  // Com o back-end desligado, o proxy do Angular responde 500/504 sem corpo do Spring.
  if (err.status >= 500) {
    return 'O servidor não respondeu. Verifique se o back-end está rodando.';
  }
  return padrao;
}

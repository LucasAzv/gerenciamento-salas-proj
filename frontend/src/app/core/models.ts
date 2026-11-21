export type Role = 'ADMIN' | 'PROFESSOR';
export type TipoSala = 'PRATICA' | 'TEORICA';
export type TipoAula = TipoSala;
export type StatusRequisicao = 'PENDENTE' | 'APROVADA' | 'REJEITADA' | 'CANCELADA';

export interface LoginResponse {
  token: string;
  tipo: string;
  role: Role;
  expiraEmSegundos: number;
}

export interface Sala {
  id: number;
  tamanho: number;
  tipoSala: TipoSala;
  disponivel: boolean;
}

export interface Disponibilidade {
  sala: Sala;
  livreNoHorario: boolean;
}

export interface Requisicao {
  id: number;
  professorId: number;
  salaId: number;
  data: string;
  horaInicio: string;
  horaFim: string;
  status: StatusRequisicao;
}

export interface NovaRequisicao {
  salaId: number;
  data: string;
  horaInicio: string;
  horaFim: string;
}

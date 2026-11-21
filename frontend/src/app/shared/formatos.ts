import { Pipe, PipeTransform } from '@angular/core';
import { StatusRequisicao, TipoSala } from '../core/models';

const NOMES_STATUS: Record<StatusRequisicao, string> = {
  PENDENTE: 'Pendente',
  APROVADA: 'Aprovada',
  REJEITADA: 'Rejeitada',
  CANCELADA: 'Cancelada',
};

const NOMES_TIPO: Record<TipoSala, string> = {
  PRATICA: 'Prática',
  TEORICA: 'Teórica',
};

/** Data de hoje no formato aaaa-mm-dd (usado nos inputs de data). */
export function hojeIso(): string {
  const d = new Date();
  d.setMinutes(d.getMinutes() - d.getTimezoneOffset());
  return d.toISOString().slice(0, 10);
}

/** 2026-11-20 -> 20/11/2026 */
@Pipe({ name: 'dataBr' })
export class DataBrPipe implements PipeTransform {
  transform(valor: string | null | undefined): string {
    if (!valor) return '';
    const [ano, mes, dia] = valor.split('-');
    return `${dia}/${mes}/${ano}`;
  }
}

/** 08:00:00 -> 08:00 */
@Pipe({ name: 'hora' })
export class HoraPipe implements PipeTransform {
  transform(valor: string | null | undefined): string {
    return valor ? valor.slice(0, 5) : '';
  }
}

@Pipe({ name: 'status' })
export class StatusPipe implements PipeTransform {
  transform(valor: StatusRequisicao): string {
    return NOMES_STATUS[valor] ?? valor;
  }
}

@Pipe({ name: 'tipoSala' })
export class TipoSalaPipe implements PipeTransform {
  transform(valor: TipoSala): string {
    return NOMES_TIPO[valor] ?? valor;
  }
}

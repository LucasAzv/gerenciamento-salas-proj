import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Disponibilidade, Sala } from './models';

@Injectable({ providedIn: 'root' })
export class SalasService {
  private http = inject(HttpClient);

  listar() {
    return this.http.get<Sala[]>('/api/salas');
  }

  disponibilidade(data: string, inicio: string, fim: string) {
    return this.http.get<Disponibilidade[]>('/api/salas/disponibilidade', {
      params: { data, inicio, fim },
    });
  }
}

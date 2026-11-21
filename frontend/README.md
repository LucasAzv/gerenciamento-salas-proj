# Front-end (Angular)

Interface web do sistema de reserva de salas.

## Como rodar

Pré-requisitos: Node.js 20 ou 22 e o back-end rodando em `http://localhost:8080`.

```bash
cd frontend
npm install
npm start
```

Abra `http://localhost:4200`.

As chamadas para `/api` são repassadas para o back-end pelo `proxy.conf.json`, então não é preciso configurar CORS no Spring.

## Telas

- **Login** – professor ou administrador.
- **Professor**: consultar salas livres e solicitar, pedir sugestão de sala (IA), acompanhar/cancelar as próprias reservas.
- **Administrador**: aprovar ou rejeitar pedidos, gerenciar salas e cadastrar professores.

# Plano de testes

| Caso | Procedimento | Evidência esperada |
|---|---|---|
| T01 MQTT | Iniciar simulador e observar logs da ingestão | Mensagens recebidas no tópico `agro/+/readings` |
| T02 Persistência | Consultar `/api/readings` autenticado | Leituras armazenadas no MongoDB |
| T03 Edge | Aguardar umidade simulada abaixo de 25% | Log `[EDGE ALERT]` antes da publicação e status CRITICAL persistido |
| T04 JWT | Consultar endpoint sem token | HTTP 401/403 |
| T05 JWT válido | Obter token e consultar `/api/dashboard` | HTTP 200 e resumo |
| T06 Paralelismo | Chamar `/internal/benchmark?items=100000&threads=4` | Tempos sequencial/paralelo, speedup e checksumEqual=true |
| T07 Falha | Parar MongoDB e observar comportamento | Erro identificável; registrar limitação e recuperação |

## Métricas a coletar

- Latência: tempo entre publicação MQTT e persistência/consulta. Registrar amostras e calcular média, mediana e p95.
- Throughput: mensagens processadas com sucesso por segundo durante janela definida.
- Speedup: tempo sequencial dividido pelo tempo paralelo no benchmark.
- Repetir testes pelo menos 5 vezes, informar hardware, versões, quantidade de mensagens e configuração de threads.

Não preencher resultados hipotéticos como se fossem medições reais.

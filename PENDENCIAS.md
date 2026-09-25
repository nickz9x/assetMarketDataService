# Pendências — assetMarketDataService

Atualizado: 2026-09-25

---

## Contexto rápido

Serviço de dados de mercado do **ChainTracker**. Busca cotações na API do
CoinGecko (e saldo de wallet na Alchemy), cacheia no Redis e publica as mudanças
no Kafka para os outros serviços.

**Stack:** Spring Boot 4.1.1, Java 21, PostgreSQL, Redis, Kafka, Keycloak (JWT).

**Porta:** 8082.

**O que ele expõe:**

| Método | Rota | O que faz |
|---|---|---|
| GET | `/api/currency` | Top 100 moedas por market cap |
| GET | `/api/currency/{id}` | Uma moeda (cache-first) |
| GET | `/api/currency/prices?ids=` | Preços simples em lote |
| POST | `/api/wallet/balance/{address}` | Saldo ETH de uma wallet (Alchemy) |

**Chaves no Redis (é o contrato com o resto do sistema):**

| Chave | TTL | Quem escreve |
|---|---|---|
| `coin:{id}` | 2 min | `MarketDataService.updateTop100Data` (a cada 30s) **e** `CoingeckoService.getCoin` |
| `coin:simple:{id}` | 1 min | `MarketDataService.updateSimplePrice` |
| `coins:top100` | 2 min | `MarketDataService.updateTop100Data` |

O `portfolioService` **lê `coin:{id}`** — não consome o HTTP. As duas pontas
precisam concordar no formato do JSON, e nada no compilador garante isso.
Mexer no `CoinMarketResponse` quebra o outro serviço em silêncio.

**Kafka:** publica no tópico `market.updated`, chave = id da moeda, valor = o
`CoinMarketResponse` inteiro. Um evento por moeda, 100 a cada 30 segundos.

**Como o dado entra:** `CoingeckoScheduler` chama `updateTop100Data` a cada 30s
(`fixedRate = 30000`). Não há endpoint que force a atualização.

---

## 🔴 Bloqueadores

### 1. API keys hardcoded e versionadas

**Onde:** `src/main/resources/application.yaml`, chaves `alchemy.api-key` e
`coingecko.api-key` (por volta das linhas 33 e 36).

O arquivo é rastreado pelo git, então as duas chaves **já estão no histórico**.
Diferente de uma senha de banco local, essas são credenciais de serviços externos
com cota e possível cobrança.

**Fix:**

1. Trocar por `${ALCHEMY_API_KEY}` e `${COINGECKO_API_KEY}`.
2. Rotacionar as duas chaves nos painéis da Alchemy e do CoinGecko.
3. Cobrir `.env` no `.gitignore` e registrar as variáveis na config da IDE.

---

## 🟡 Importante

### 2. Id de moeda inexistente devolve 200 com corpo vazio

**Verificado em 2026-09-25** chamando `GET /api/currency/nao-existe-xyz`:

```
chamada 1: HTTP 200 | corpo=0b
chamada 2: HTTP 200 | corpo=0b
chamada 3: HTTP 200 | corpo=0b
```

**Causa:** `CoingeckoService.getCoin:36-38`. Quando o CoinGecko não conhece o id,
`CoingeckoClient.getCoin:52` devolve `null`. O service então chama
`redisTemplate.opsForValue().set("coin:"+id, null, Duration.ofMinutes(2))` — o
serializador grava uma string **vazia** (confirmei: `STRLEN` = 0, `TTL` = 120).
Na leitura seguinte, uma string vazia desserializa como `null`, então o código
trata como cache miss, busca de novo, recebe `null` outra vez e devolve 200 vazio.

**Duas consequências:**

1. **Contrato errado.** O cliente pediu uma moeda que não existe e recebeu 200
   com corpo vazio — não dá para distinguir de uma resposta legítima. O certo é
   404 (`ResponseStatusException(HttpStatus.NOT_FOUND)`), que é o padrão já usado
   no `portfolioService`.
2. **Sujeira no Redis compartilhado.** Cada id desconhecido cria uma chave vazia
   por 2 minutos. Um cliente (ou bug de digitação no front) varrendo ids inventados
   enche o Redis de lixo — e como o Redis é o mesmo do `portfolioService`, o
   impacto não fica só aqui.

**Fix:** se `coin == null`, lançar 404 **antes** de escrever no cache. Vale o
mesmo para `MarketDataService.updateSimplePrice:41`, onde `map.get(id)` é `null`
quando o CoinGecko não devolve aquele id.

### 3. Dois escritores na mesma chave `coin:{id}`

`MarketDataService.updateTop100Data` grava `coin:{id}` a cada 30 segundos para
as 100 moedas do topo, e `CoingeckoService.getCoin` grava a mesma chave no cache
miss. São dois donos da mesma chave, com TTLs iguais mas origens diferentes.

Hoje não quebra porque o dado é o mesmo (ambos vêm do CoinGecko), mas não existe
regra dizendo quem manda. Se um dia o TTL divergir, o comportamento passa a
depender de quem escreveu por último — e é difícil de reproduzir.

**Fix:** escolher um dono. O natural é o scheduler ser o único escritor, e o
`getCoin` só ler (o cache miss cai no HTTP e não escreve).

### 4. `GET /api/currency` e `/prices` não usam o cache

O `getTop100` (`CoingeckoService:27`) vai direto no `client`, ignorando a chave
`coins:top100` que o scheduler mantém quentinha a cada 30s. É uma chamada HTTP
externa por request, com cota do CoinGecko, para um dado que já está no Redis.

O `getSimpleCoin` acerta nesse ponto — lê `coin:simple:{id}` e só busca o que
falta.

### 5. Saldo de wallet pode estourar NPE

`AssetMarketDataService.getBalance:17` faz `client.getBalance(address).result()`.
Se a Alchemy responder com erro (endereço malformado, cota estourada), o corpo
vem sem `result` e isso vira `NullPointerException` — 500 sem explicação.

Além disso `balance.substring(2)` assume o prefixo `0x`; e a rota é `POST` para
uma operação que só lê.

**Não verificado:** não testei com endereço inválido.

---

## 🔵 Limpeza

### 6. Imports órfãos

- `service/CoingeckoService.java:7` — `Cacheable`
- `service/MarketDataService.java:6` — `AllArgsConstructor`; `:9` — `CachePut`
- `scheduler/CoingeckoScheduler.java:7` — `CachePut`
- `scheduler/CoingeckoScheduler.java:3` — importa `CoingeckoClient`, que não usa

### 7. `@Scheduled` com retorno

`CoingeckoScheduler.updateTop100Data:21` está declarado devolvendo
`List<CoinMarketResponse>`. Método agendado deve ser `void` — o valor de retorno
é descartado pelo Spring, o que dá a impressão de que alguém consome esse
resultado. Não consome.

### 8. Datasource configurado e não usado

O `application.yaml` aponta para um Postgres (`localhost:5433/assetDataService`)
e existe um container para ele no `docker-compose` do ChainTracker, mas o serviço
não tem entidade, repository nem dependência de JPA. É configuração morta.

Decidir: ou o serviço vai persistir algo (histórico de preços, por exemplo), ou a
configuração e o container saem.

### 9. Encapsulamento

`WalletController.java:17` — `public AssetMarketDataService service;`. Os outros
controllers usam `private`. O campo fica exposto e mutável.

### 10. Inconsistência de `final` nos campos injetados

`CoingeckoService.java:22` — `private CoingeckoClient client;` sem `final`,
enquanto os três campos seguintes são `final`. Mesma coisa em
`MarketDataService` e `CoingeckoScheduler`. Marcar como `final` deixa o Lombok
incluir no construtor e impede reatribuição.

---

## 🧪 DX e testes

### 11. Carga do scheduler

A cada 30 segundos: 1 chamada ao CoinGecko, 101 escritas no Redis e **100
mensagens no Kafka**. São 288 mil mensagens por dia publicadas no tópico
`market.updated`. Nenhum consumidor lê hoje (o `portfolioService` não tem
`@KafkaListener` para esse tópico).

Vale decidir: o scheduler deveria publicar só o que **mudou**, não o top 100
inteiro a cada 30s. Isso derruba o volume e é o comportamento esperado de um
evento de "mercado atualizado".

### 12. Não existe teste de verdade

Só o `AssetMarketDataServiceApplicationTests` gerado pelo Initializr. Nada cobre
o `CoingeckoClient` (parsing das respostas), nem o cálculo de saldo da Alchemy
(conversão de wei para ETH), que são lógica testável sem rede.

---

## ✅ Já resolvido (não refazer)

- **Preço vinha `null`.** O `getCoin` usava `coins/{id}`, e nesse endpoint o
  CoinGecko aninha os preços em `market_data.current_price.usd` — o
  `CoinMarketResponse` é plano e recebia tudo nulo. Trocado por `coins/markets?ids=`,
  que devolve o formato plano e é o mesmo endpoint do top 100.
- **`getCoin` não populava o cache.** Lia `coin:{id}` mas não escrevia no miss,
  então só o scheduler alimentava a chave. Agora escreve com TTL de 2 min.
- `RedisConfig` com `GenericJacksonJsonRedisSerializer` + `StringRedisSerializer`
  nas chaves, alinhado com o `portfolioService`.
- `RestClient` com `baseUrl` e header `x-cg-demo-api-key` no construtor do
  `CoingeckoClient`.

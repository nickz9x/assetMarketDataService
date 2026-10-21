# Pendências — assetMarketDataService

Atualizado: 2026-10-15

---

## Contexto rápido

Serviço de dados de mercado do **ChainTracker**. Busca cotações de cripto no
CoinGecko e de ações no Alpaca (e saldo de wallet na Alchemy), cacheia no Redis e
publica as mudanças no Kafka para os outros serviços.

**Stack:** Spring Boot 4.1.1, Java 25, PostgreSQL, Redis, Kafka, Keycloak (JWT).

**Porta:** 8082.

**O que ele expõe:**

| Método | Rota | O que faz |
|---|---|---|
| GET | `/api/currency` | Top 100 moedas por market cap |
| GET | `/api/currency/{id}` | Uma moeda (cache-first) |
| GET | `/api/currency/prices?ids=` | Preços simples em lote |
| GET | `/api/currency/data/{assetId}` | Catálogo da moeda (nome e símbolo) |
| GET | `/api/stock` | Top 100 ações (cache-first) |
| GET | `/api/stock/{symbol}` | Uma ação |
| POST | `/api/wallet/balance/{address}` | Saldo ETH de uma wallet (Alchemy) |

Só `/api/currency/**` e `/error` são públicos (`SecurityConfiguration`); o resto
exige Bearer token do Keycloak.

**Chaves no Redis (é o contrato com o resto do sistema):**

| Chave | TTL | Quem escreve |
|---|---|---|
| `coin:{id}` | 2 min no `refreshTop100`, 1 min no `getMarketData` | `CryptoMarketService.refreshTop100` (a cada 30s) **e** `CryptoMarketService.getMarketData` |
| `coin:simple:{id}` | 1 min | `CryptoMarketService.refreshSimplePrices` |
| `coins:top100` | 1 min | `CryptoMarketService.refreshTop100` |
| `coin.data:{id}` | 10 min | `CryptoAssetService.getAsset` |
| `stock:{symbol}` | 3 min | `StockMarketService.refreshTop100` **e** `StockMarketService.getMarketData` |
| `stocks:top100` | 3 min | `StockMarketService.refreshTop100` |
| `stock.data:{symbol}` | 4 h | `StockAssetService.getAssetBySymbol` |

O `portfolioService` **lê `coin:{id}`** — não consome o HTTP. As duas pontas
precisam concordar no formato do JSON, e nada no compilador garante isso.
Mexer no `CryptoMarketResponse` quebra o outro serviço em silêncio.

**Kafka:** publica em `market.cripto.updated` (chave = id da moeda) e
`market.tradfi.updated` (chave = símbolo da ação), valor = o DTO de mercado
inteiro. Um evento por ativo.

**Como o dado entra:** `CryptoScheduler` chama `CryptoMarketService.refreshTop100`
a cada 30s (`fixedRate = 30000`); `StockScheduler.refreshTop100` roda a cada 15s e
`StockScheduler.refreshCatalog` sincroniza o catálogo da Alpaca 1x por dia. Não há
endpoint que force a atualização.

---

## 🔴 Bloqueadores

### 1. API keys hardcoded e versionadas

**Onde:** `src/main/resources/application.yaml`, chaves `alchemy.api-key`,
`coingecko.api-key` e `alpaca.api-key`/`alpaca.api-secret`.

O arquivo é rastreado pelo git, então as chaves **já estão no histórico**.
Diferente de uma senha de banco local, essas são credenciais de serviços externos
com cota e possível cobrança.

**Fix:**

1. Trocar por `${ALCHEMY_API_KEY}`, `${COINGECKO_API_KEY}` e `${ALPACA_API_KEY}`.
2. Rotacionar as chaves nos painéis da Alchemy, do CoinGecko e do Alpaca.
3. Cobrir `.env` no `.gitignore` e registrar as variáveis na config da IDE.

---

## 🟡 Importante

### ✅ 2. Id de moeda inexistente devolve 200 com corpo vazio

**Resolvido em 2026-10-15.** As duas pontas do fix estão feitas:

- `CryptoMarketService.getMarketData:47` lança `ResponseStatusException(HttpStatus.NOT_FOUND)`
  quando o `getCoin` devolve `null`, antes de escrever no cache.
- `CryptoMarketService.refreshSimplePrices:86-97` só grava em `coin:simple:{id}` quando o
  `map.get(id)` existe — e devolve `Map.of()` se o CoinGecko vier com corpo vazio.

As duas consequências abaixo sumiram (não há mais chave vazia no Redis).

**Verificado em 2026-09-25** chamando `GET /api/currency/nao-existe-xyz`:

```
chamada 1: HTTP 200 | corpo=0b
chamada 2: HTTP 200 | corpo=0b
chamada 3: HTTP 200 | corpo=0b
```

**Causa:** `CryptoMarketService.getMarketData:32`. Quando o CoinGecko não conhece
o id, `CoingeckoClient.getCoin` devolve `null`. O service então chama
`redisTemplate.opsForValue().set("coin:"+id, null, Duration.ofMinutes(2))` (linha
38) — o serializador grava uma string **vazia** (confirmei: `STRLEN` = 0,
`TTL` = 120). Na leitura seguinte, uma string vazia desserializa como `null`, então
o código trata como cache miss, busca de novo, recebe `null` outra vez e devolve
200 vazio.

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
mesmo para `CryptoMarketService.refreshSimplePrices:76`, onde `map.get(id)` é
`null` quando o CoinGecko não devolve aquele id.

### 3. Dois escritores na mesma chave `coin:{id}`

`CryptoMarketService.refreshTop100` grava `coin:{id}` a cada 30 segundos para
as 100 moedas do topo, e `CryptoMarketService.getMarketData` grava a mesma chave
no cache miss. São dois donos da mesma chave, com TTLs iguais mas origens
diferentes.

Hoje não quebra porque o dado é o mesmo (ambos vêm do CoinGecko), mas não existe
regra dizendo quem manda. Se um dia o TTL divergir, o comportamento passa a
depender de quem escreveu por último — e é difícil de reproduzir.

**Atualização 2026-10-15:** o `getTop100` chegou a virar um terceiro escritor de
`coin:{id}` (e sem TTL); corrigido — o cache miss agora cai em `refreshTop100()`.
Seguem os dois donos originais.

**E os TTLs já divergiram:** o commit `07c7f30` baixou o `getMarketData` para 1 min,
enquanto o `refreshTop100` segue gravando 2 min na mesma chave. É exatamente o cenário
descrito acima — hoje quem manda é quem escreveu por último.

O mesmo padrão existe no lado stock: `StockScheduler.refreshTop100` e
`StockMarketService.getMarketData` escrevem `stock:{symbol}`.

**Fix:** escolher um dono. O natural é o scheduler ser o único escritor, e o
`getMarketData` só ler (o cache miss cai no HTTP e não escreve).

### ✅ 4. `GET /api/currency` e `/prices` não usam o cache

**Resolvido em 2026-10-15 (working tree, não commitado).** O `getTop100`
(`CryptoMarketService:31-38`) lê `coins:top100` primeiro, com o null check e o
`convertValue` que faltavam.

**Fallback corrigido em 2026-10-15.** No cache vazio o `getTop100` chama `refreshTop100()`
(`CryptoMarketService:37`) — grava as 101 chaves com TTL e alimenta as duas chaves.

No stock o bloco que chamava `refreshTop100()` duas vezes saiu: o cache miss agora é
`return refreshTop100();` (`StockMarketService:32`).

O `getTop100` (`CryptoMarketService:28`) vai direto no `client`, ignorando a chave
`coins:top100` que o scheduler mantém quentinha a cada 30s. É uma chamada HTTP
externa por request, com cota do CoinGecko, para um dado que já está no Redis.

O `getSimplePrices` acerta nesse ponto — lê `coin:simple:{id}` e só busca o que
falta. No lado stock o `getTop100` também lê `stocks:top100` primeiro.

### 5. Saldo de wallet pode estourar NPE

`WalletService.getBalance:17` faz `client.getBalance(address).result()`.
Se a Alchemy responder com erro (endereço malformado, cota estourada), o corpo
vem sem `result` e isso vira `NullPointerException` — 500 sem explicação.

Além disso `balance.substring(2)` assume o prefixo `0x`; e a rota é `POST` para
uma operação que só lê.

**Não verificado:** não testei com endereço inválido.

---

## 🔵 Limpeza

### ✅ 6. Inconsistência de `final` nos campos injetados

**Resolvido em 2026-10-15.** Todos os campos injetados viraram `final`: os dois
services de cotação, os dois schedulers, `CryptoAssetService`, `WalletService` e os
três controllers (`StockAssetService` já estava certo).

`CryptoMarketService.java:22` — `private CoingeckoClient client;` sem `final`,
enquanto os quatro campos seguintes são `final`. Mesma coisa em
`StockMarketService` (todos os campos sem `final`) e nos dois schedulers. Marcar
como `final` deixa o Lombok incluir no construtor e impede reatribuição.

### ✅ 7. `CryptoController.getAsset` duplica rota

**Resolvido em 2026-10-15.** O `InternalController` saiu inteiro — sobraram
`/api/currency/data/{assetId}` (público) para o catálogo da moeda e
`/api/stock/{symbol}` para a ação. Efeito colateral: `StockAssetService.getAssetBySymbol`
(que servia `/api/internal/data/stock/{symbol}`) ficou sem chamador.

`/api/currency/data/{assetId}` devolve exatamente o que
`/api/internal/data/currency/{assetId}` devolve — só que sem autenticação. Se o
front não usa, uma das duas sai.

**Revertido em 2026-10-18:** o `InternalController` voltou. O `portfolioService`
continua consumindo `/api/internal/data/currency/{assetId}` e
`/api/internal/data/stock/{symbol}` para validar o ativo e pegar o id canônico
no create de posição e no alerta; sem as rotas, as chamadas viram 401/404 e
estouram 500 lá. O `getAssetBySymbol` voltou a ter chamador.

---

## 🧪 DX e testes

### 8. Carga do scheduler

A cada 30 segundos: 1 chamada ao CoinGecko, 101 escritas no Redis e **100
mensagens no Kafka** (`market.cripto.updated`) — 288 mil por dia. O lado stock
publica mais: `StockScheduler.refreshTop100` roda a cada 15s e manda ~100
mensagens em `market.tradfi.updated`, outras 576 mil por dia. São ~864 mil
mensagens diárias. Nenhum consumidor lê hoje (o `portfolioService` não tem
`@KafkaListener` para esses tópicos).

Vale decidir: o scheduler deveria publicar só o que **mudou**, não o top 100
inteiro a cada ciclo. Isso derruba o volume e é o comportamento esperado de um
evento de "mercado atualizado".

### 9. Não existe teste de verdade

O `AssetMarketDataServiceApplicationTests` gerado pelo Initializr foi removido na
branch de migração, então hoje não há nenhum teste. Nada cobre o
`CoingeckoClient` (parsing das respostas), nem o cálculo de saldo da Alchemy
(conversão de wei para ETH), que são lógica testável sem rede.

---

## ✅ Já resolvido (não refazer)

- **Entidades e repository.** O serviço passou a persistir o catálogo de ativos
  (`Crypto` e `Stock`) com JPA, ligado ao Postgres que já estava configurado no
  `application.yaml`. A tabela antiga `coin` fica órfã depois do rename para
  `crypto` — pode dar `DROP TABLE coin;` quando quiser.
- **Imports órfãos.** Removidos de `CoingeckoService`, `MarketDataService`,
  `CoingeckoScheduler`, `StockRepository` e `StockService`.
- **`@Scheduled` com retorno.** Os métodos agendados viraram `void` — o valor de
  retorno era descartado pelo Spring e dava a impressão de que alguém consumia.
- **Encapsulamento do `WalletController`.** O campo `public AssetMarketDataService
  service` virou `private WalletService walletService`.
- **Preço vinha `null`.** O `getCoin` usava `coins/{id}`, e nesse endpoint o
  CoinGecko aninha os preços em `market_data.current_price.usd` — o
  `CryptoMarketResponse` é plano e recebia tudo nulo. Trocado por `coins/markets?ids=`,
  que devolve o formato plano e é o mesmo endpoint do top 100.
- **`getCoin` não populava o cache.** Lia `coin:{id}` mas não escrevia no miss,
  então só o scheduler alimentava a chave. Agora escreve com TTL de 2 min.
- `RedisConfig` com `GenericJacksonJsonRedisSerializer` + `StringRedisSerializer`
  nas chaves, alinhado com o `portfolioService`.
- `RestClient` com `baseUrl` e header `x-cg-demo-api-key` no construtor do
  `CoingeckoClient`.

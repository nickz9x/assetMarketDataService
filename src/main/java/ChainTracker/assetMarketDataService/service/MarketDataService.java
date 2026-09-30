package ChainTracker.assetMarketDataService.service;

import ChainTracker.assetMarketDataService.client.AlpacaDataClient;
import ChainTracker.assetMarketDataService.client.CoingeckoClient;
import ChainTracker.assetMarketDataService.dto.*;
import ChainTracker.assetMarketDataService.mapper.StockMarketMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class MarketDataService {

    private final CoingeckoClient client;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final RedisTemplate<String,Object> redisTemplate;
    private final AlpacaDataClient alpacaDataClient;
    private final CoinService coinService;

    public List<CoinMarketResponse> updateTop100Data(){
        List<CoinMarketResponse> coins = client.getTop100Coins();
        for (CoinMarketResponse coin : coins) {
            coinService.save(new CoinDataSimpleRequest(coin.id(),coin.name(),coin.symbol()));
            redisTemplate.opsForValue().set("coin:"+ coin.id(),coin, Duration.ofMinutes(2));
            kafkaTemplate.send("market.cripto.updated",coin.id(),coin);
        }
        redisTemplate.opsForValue().set("coins:top100",coins ,Duration.ofMinutes(2));
        return coins;
    }

    public Map<String, CoinMarketSimpleResponse> updateSimplePrice(List<String> ids){
        Map<String, CoinMarketSimpleResponse> map = client.getSimple(ids);
        for (String id: ids){
            redisTemplate.opsForValue().set("coin:simple:"+ id, map.get(id),Duration.ofMinutes(1));
        }
        return map;
    }

    public Map<String, StockMarketResponse> updateTop100Stocks(){
        Map<String,StockMarketResponse> stocksMap = new HashMap<>();
        Map<String, StockSnapshotResponse> quotes = alpacaDataClient.getSnapshots();
        for (Map.Entry<String, StockSnapshotResponse> stock : quotes.entrySet()) {
            StockMarketResponse response = StockMarketMapper.toResponse(stock.getKey(), stock.getValue());
            stocksMap.put(stock.getKey(),response);
            redisTemplate.opsForValue().set("stock:"+stock.getKey(), response ,Duration.ofMinutes(3));
            kafkaTemplate.send("market.tradfi.updated",stock.getKey(),response);
        }
        redisTemplate.opsForValue().set("stocks:top100",stocksMap,Duration.ofMinutes(3));
        return stocksMap;
    }

    public StockMarketResponse getStock(String symbol){
        StockSnapshotResponse snapshot = alpacaDataClient.getSnapshot(symbol);
        StockMarketResponse response = StockMarketMapper.toResponse(symbol, snapshot);
        redisTemplate.opsForValue().set("stock:"+response.symbol(),response,Duration.ofMinutes(3));
        kafkaTemplate.send("market.tradfi.updated",symbol,response);
        return response;
    }
}

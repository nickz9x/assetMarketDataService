package ChainTracker.assetMarketDataService.service;

import ChainTracker.assetMarketDataService.client.AlpacaDataClient;
import ChainTracker.assetMarketDataService.dto.StockMarketResponse;
import ChainTracker.assetMarketDataService.dto.StockSnapshotResponse;
import ChainTracker.assetMarketDataService.mapper.StockMarketMapper;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class StockMarketService {
    private final RedisTemplate<String,Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final AlpacaDataClient alpacaDataClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public Map<String, StockMarketResponse> getTop100(){
        Object cached = redisTemplate.opsForValue().get("stocks:top100");
        if (cached != null){
            return objectMapper.convertValue(cached, new TypeReference<Map<String, StockMarketResponse>>() {
            });
        }
        return refreshTop100();
    }

    public StockMarketResponse getMarketData(String symbol){
        symbol = symbol.toUpperCase();
        Object cached = redisTemplate.opsForValue().get("stock:"+symbol);
        if (cached != null){
            return objectMapper.convertValue(cached, StockMarketResponse.class);
        }
        return refreshMarketData(symbol);
    }

    public Map<String, StockMarketResponse> refreshTop100(){
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

    private StockMarketResponse refreshMarketData(String symbol){
        StockSnapshotResponse snapshot = alpacaDataClient.getSnapshot(symbol);
        StockMarketResponse response = StockMarketMapper.toResponse(symbol, snapshot);
        redisTemplate.opsForValue().set("stock:"+response.symbol(),response,Duration.ofMinutes(3));
        kafkaTemplate.send("market.tradfi.updated",symbol,response);
        return response;
    }
}

package ChainTracker.assetMarketDataService.service;

import ChainTracker.assetMarketDataService.client.AlpacaAssetClient;
import ChainTracker.assetMarketDataService.dto.StockAssetResponse;
import ChainTracker.assetMarketDataService.dto.StockMarketResponse;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class AlpacaService {
    private RedisTemplate<String,Object> redisTemplate;
    private MarketDataService service;
    private ObjectMapper objectMapper;
    private AlpacaAssetClient assetClient;
    private StockService stockService;

    public Map<String, StockMarketResponse> getTop100Stock(){
        Object cached = redisTemplate.opsForValue().get("stocks:top100");
        if (cached != null){
            return objectMapper.convertValue(cached, new TypeReference<Map<String, StockMarketResponse>>() {
            });
        }
        return service.updateTop100Stocks();
    }

    public StockMarketResponse getStock(String symbol){
        symbol = symbol.toUpperCase();
        Object cached = redisTemplate.opsForValue().get("stock:"+symbol);
        if (cached != null){
            return objectMapper.convertValue(cached, StockMarketResponse.class);
        }
        return service.getStock(symbol);
    }

    public List<StockAssetResponse> getUsEquity(){
        List<StockAssetResponse> list = assetClient.updateUsEquity();
        stockService.saveAll(list);
        return list;
    }
}

package ChainTracker.assetMarketDataService.service;

import ChainTracker.assetMarketDataService.client.CoingeckoClient;
import ChainTracker.assetMarketDataService.dto.CoinDataSimpleResponse;
import ChainTracker.assetMarketDataService.dto.CoinMarketResponse;
import ChainTracker.assetMarketDataService.dto.CoinMarketSimpleResponse;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class CoingeckoService {
    private CoingeckoClient client;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final MarketDataService marketDataService;

    public List<CoinMarketResponse> getTop100(){
        return client.getTop100Coins();
    }

    public CoinMarketResponse getCoin(String id){
        Object cached = redisTemplate.opsForValue().get("coin:" + id);
        if(cached != null){
            return objectMapper.convertValue(cached, CoinMarketResponse.class);
        }
        CoinMarketResponse coin = client.getCoin(id);
        redisTemplate.opsForValue().set("coin:"+id,coin, Duration.ofMinutes(2));
        return coin;
    }

    public Map<String, CoinMarketSimpleResponse> getSimpleCoin(List<String> ids){
        List<String> missIds = new ArrayList<>();
        Map<String,CoinMarketSimpleResponse> result = new HashMap<>();

        for (String id : ids) {
            Object cached = redisTemplate.opsForValue().get("coin:simple:"+id);
            if(cached != null){
                result.put(id,objectMapper.convertValue(cached, CoinMarketSimpleResponse.class));
            }
            else {
                missIds.add(id);
            }
        }
        if (!missIds.isEmpty()){
            Map<String, CoinMarketSimpleResponse> updated = marketDataService.updateSimplePrice(missIds);
            result.putAll(updated);
        }
        return result;
    }


}

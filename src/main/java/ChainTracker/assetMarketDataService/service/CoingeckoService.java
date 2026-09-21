package ChainTracker.assetMarketDataService.service;

import ChainTracker.assetMarketDataService.client.CoingeckoClient;
import ChainTracker.assetMarketDataService.dto.CoinMarketResponse;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Service
@AllArgsConstructor
public class CoingeckoService {
    private CoingeckoClient client;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public List<CoinMarketResponse> getTop100(){
        Object cached = redisTemplate.opsForValue().get("coins:top100");
        if(cached != null){
            return objectMapper.convertValue(cached, new TypeReference<List<CoinMarketResponse>>() {} );
        }
        return client.getTop100Coins();
    }

    public CoinMarketResponse getCoin(String id){
        Object cached = redisTemplate.opsForValue().get("coin:" + id);
        if(cached != null){
            return objectMapper.convertValue(cached, CoinMarketResponse.class);
        }
        return client.getCoin(id);
    }
}

package ChainTracker.assetMarketDataService.service;

import ChainTracker.assetMarketDataService.client.CoingeckoClient;
import ChainTracker.assetMarketDataService.dto.CryptoDataRequest;
import ChainTracker.assetMarketDataService.dto.CryptoMarketResponse;
import ChainTracker.assetMarketDataService.dto.CryptoMarketSimpleResponse;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class CryptoMarketService {
    private CoingeckoClient client;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final CryptoAssetService cryptoAssetService;

    public List<CryptoMarketResponse> getTop100(){
        return client.getTop100Coins();
    }

    public CryptoMarketResponse getMarketData(String id){
        Object cached = redisTemplate.opsForValue().get("coin:" + id);
        if(cached != null){
            return objectMapper.convertValue(cached, CryptoMarketResponse.class);
        }
        CryptoMarketResponse coin = client.getCoin(id);
        redisTemplate.opsForValue().set("coin:"+id,coin, Duration.ofMinutes(2));
        return coin;
    }

    public Map<String, CryptoMarketSimpleResponse> getSimplePrices(List<String> ids){
        List<String> missIds = new ArrayList<>();
        Map<String,CryptoMarketSimpleResponse> result = new HashMap<>();

        for (String id : ids) {
            Object cached = redisTemplate.opsForValue().get("coin:simple:"+id);
            if(cached != null){
                result.put(id,objectMapper.convertValue(cached, CryptoMarketSimpleResponse.class));
            }
            else {
                missIds.add(id);
            }
        }
        if (!missIds.isEmpty()){
            Map<String, CryptoMarketSimpleResponse> updated = refreshSimplePrices(missIds);
            result.putAll(updated);
        }
        return result;
    }

    public List<CryptoMarketResponse> refreshTop100(){
        List<CryptoMarketResponse> coins = client.getTop100Coins();
        for (CryptoMarketResponse coin : coins) {
            cryptoAssetService.saveAsset(new CryptoDataRequest(coin.id(),coin.name(),coin.symbol()));
            redisTemplate.opsForValue().set("coin:"+ coin.id(),coin, Duration.ofMinutes(2));
            kafkaTemplate.send("market.cripto.updated",coin.id(),coin);
        }
        redisTemplate.opsForValue().set("coins:top100",coins ,Duration.ofMinutes(2));
        return coins;
    }

    private Map<String, CryptoMarketSimpleResponse> refreshSimplePrices(List<String> ids){
        Map<String, CryptoMarketSimpleResponse> map = client.getSimple(ids);
        for (String id: ids){
            redisTemplate.opsForValue().set("coin:simple:"+ id, map.get(id),Duration.ofMinutes(1));
        }
        return map;
    }
}

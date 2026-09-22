package ChainTracker.assetMarketDataService.service;

import ChainTracker.assetMarketDataService.client.CoingeckoClient;
import ChainTracker.assetMarketDataService.dto.CoinMarketResponse;
import ChainTracker.assetMarketDataService.dto.CoinMarketSimpleResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class MarketDataService {

    private final CoingeckoClient client;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final RedisTemplate<String,Object> redisTemplate;


    public List<CoinMarketResponse> updateTop100Data(){
        List<CoinMarketResponse> coins = client.getTop100Coins();
        for (CoinMarketResponse coin : coins) {
            redisTemplate.opsForValue().set("coin:"+ coin.id(),coin, Duration.ofMinutes(2));

            kafkaTemplate.send("market.updated",coin.id(),coin);
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

}

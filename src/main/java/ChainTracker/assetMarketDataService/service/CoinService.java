package ChainTracker.assetMarketDataService.service;

import ChainTracker.assetMarketDataService.dto.CoinDataSimpleRequest;
import ChainTracker.assetMarketDataService.dto.CoinDataSimpleResponse;
import ChainTracker.assetMarketDataService.mapper.CoinMapper;
import ChainTracker.assetMarketDataService.model.Coin;
import ChainTracker.assetMarketDataService.repository.CoinRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CoinService {
    private CoinRepository coinRepository;
    private RedisTemplate<String,Object> redisTemplate;
    private ObjectMapper objectMapper;


    public CoinDataSimpleResponse save(CoinDataSimpleRequest dto) {
        if (!coinRepository.existsByAssetId(dto.assetId())) {
            Coin coin = new Coin();
            coin.setAssetId(dto.assetId());
            coin.setName(dto.name());
            coin.setSymbol(dto.symbol());
            coinRepository.save(coin);
            return new CoinDataSimpleResponse(coin.getAssetId(), coin.getName(), coin.getSymbol());
        }
        else {
            return null;
        }
    }


    public CoinDataSimpleResponse getCoinData(String assetId) {
        assetId = assetId.toLowerCase();
        Object cached = redisTemplate.opsForValue().get("coin.data:"+assetId);

        if (cached != null) {
            return objectMapper.convertValue(cached, CoinDataSimpleResponse.class);
        }
        Coin coin = coinRepository.findByAssetId(assetId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Coin not found"));

        CoinDataSimpleResponse response = CoinMapper.toResponse(coin);

        redisTemplate.opsForValue().set("coin.data:"+assetId, response, Duration.ofMinutes(10));
        return response;
    }
}

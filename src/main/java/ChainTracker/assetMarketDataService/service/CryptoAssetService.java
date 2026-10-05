package ChainTracker.assetMarketDataService.service;

import ChainTracker.assetMarketDataService.dto.CryptoDataRequest;
import ChainTracker.assetMarketDataService.dto.CryptoDataResponse;
import ChainTracker.assetMarketDataService.mapper.CryptoMapper;
import ChainTracker.assetMarketDataService.model.Crypto;
import ChainTracker.assetMarketDataService.repository.CryptoRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;

@Service
@AllArgsConstructor
public class CryptoAssetService {
    private CryptoRepository cryptoRepository;
    private RedisTemplate<String,Object> redisTemplate;
    private ObjectMapper objectMapper;


    public CryptoDataResponse saveAsset(CryptoDataRequest dto) {
        if (!cryptoRepository.existsByAssetId(dto.assetId())) {
            Crypto crypto = new Crypto();
            crypto.setAssetId(dto.assetId());
            crypto.setName(dto.name());
            crypto.setSymbol(dto.symbol());
            cryptoRepository.save(crypto);
            return new CryptoDataResponse(crypto.getAssetId(), crypto.getName(), crypto.getSymbol());
        }
        else {
            return null;
        }
    }


    public CryptoDataResponse getAsset(String assetId) {
        assetId = assetId.toLowerCase();
        Object cached = redisTemplate.opsForValue().get("coin.data:"+assetId);

        if (cached != null) {
            return objectMapper.convertValue(cached, CryptoDataResponse.class);
        }
        Crypto crypto = cryptoRepository.findByAssetId(assetId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Crypto not found"));

        CryptoDataResponse response = CryptoMapper.toResponse(crypto);

        redisTemplate.opsForValue().set("coin.data:"+assetId, response, Duration.ofMinutes(10));
        return response;
    }
}

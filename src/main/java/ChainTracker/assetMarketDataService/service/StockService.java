package ChainTracker.assetMarketDataService.service;

import ChainTracker.assetMarketDataService.client.AlpacaAssetClient;
import ChainTracker.assetMarketDataService.dto.StockAssetResponse;
import ChainTracker.assetMarketDataService.mapper.StockAssetMapper;
import ChainTracker.assetMarketDataService.model.Stock;
import ChainTracker.assetMarketDataService.repository.StockRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StockService {
    private final JdbcTemplate jdbcTemplate;
    private final StockRepository stockRepository;
    private final RedisTemplate<String,Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final AlpacaAssetClient alpacaAssetClient;

    @Transactional
    public void saveAll(List<StockAssetResponse> responses) {


        if (responses == null || responses.isEmpty()) {
            return;
        }

        String sql = """
            INSERT INTO stock (id,symbol, name, exchange, active)
            VALUES (?, ?, ?, ?, ?)
            ON CONFLICT (symbol) DO UPDATE SET
                name = EXCLUDED.name,
                exchange = EXCLUDED.exchange,
                active = EXCLUDED.active
            """; // 

        jdbcTemplate.batchUpdate(sql, responses, 500, (ps, dto) -> {
            ps.setObject(1, UUID.randomUUID());
            ps.setString(2, dto.symbol());
            ps.setString(3, dto.name());
            ps.setString(4, dto.exchange());
            ps.setString(5, dto.status());
        });
    }

    public StockAssetResponse findBySymbol(String symbol){
        symbol = symbol.toUpperCase();
        Object cached = redisTemplate.opsForValue().get("stock.data:" + symbol);
        if(cached == null){
            Stock stock = stockRepository.findBySymbol(symbol).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            StockAssetResponse dto = StockAssetMapper.toResponse(stock);
            redisTemplate.opsForValue().set("stock.data:"+stock.getSymbol(),dto, Duration.ofHours(4));
            return dto;
        }
        return objectMapper.convertValue(cached,StockAssetResponse.class);
    }

    public List<StockAssetResponse> syncUsEquity(){
        List<StockAssetResponse> list = alpacaAssetClient.updateUsEquity();
        saveAll(list);
        return list;
    }
}

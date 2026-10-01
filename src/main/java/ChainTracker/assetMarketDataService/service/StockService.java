package ChainTracker.assetMarketDataService.service;

import ChainTracker.assetMarketDataService.dto.StockAssetResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StockService {
    private final JdbcTemplate jdbcTemplate;

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
}

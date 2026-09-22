package ChainTracker.assetMarketDataService.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.Instant;

public record CoinMarketSimpleResponse(BigDecimal usd,
                                       @JsonProperty("usd_market_cap")
                                       BigDecimal usdMarketCap,
                                       @JsonProperty("usd_24h_vol")
                                       BigDecimal usd24HrVol,
                                       @JsonProperty("usd_24h_change")
                                       BigDecimal usd24HrChange,
                                       @JsonProperty("last_updated_at")
                                       Long lastUpdated) {
}

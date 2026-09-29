package ChainTracker.assetMarketDataService.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record StockSnapshotResponse(

        @JsonProperty("latestTrade")
        LatestTrade latestTrade,

        @JsonProperty("dailyBar")
        DailyBar dailyBar

) {

    public record LatestTrade(

            @JsonProperty("p")
            BigDecimal price

    ) {
    }

    public record DailyBar(

            @JsonProperty("o")
            BigDecimal open,

            @JsonProperty("h")
            BigDecimal high,

            @JsonProperty("l")
            BigDecimal low

    ) {
    }
}
package ChainTracker.assetMarketDataService.dto;

import java.math.BigDecimal;

public record StockMarketResponse(
        String symbol,
        BigDecimal currentPrice,
        BigDecimal openingPrice,
        BigDecimal highestPrice,
        BigDecimal lowestPrice
) {
}
package ChainTracker.assetMarketDataService.dto;

public record StockAssetResponse(String symbol,
                                 String name,
                                 String exchange,
                                 String status) {
}

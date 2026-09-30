package ChainTracker.assetMarketDataService.dto;

public record StockAssetRequest(String symbol,
                                String name,
                                String exchange,
                                String status) {
}

package ChainTracker.assetMarketDataService.dto;

import ChainTracker.assetMarketDataService.model.Crypto;
import ChainTracker.assetMarketDataService.model.Stock;

public record AssetSearchResponse(
        String type,
        String assetId,
        String symbol,
        String name,
        String exchange,
        String status
) {

    public static AssetSearchResponse crypto(Crypto crypto) {
        return new AssetSearchResponse(
                "CRIPTO",
                crypto.getAssetId(),
                crypto.getSymbol() == null ? null : crypto.getSymbol().toUpperCase(),
                crypto.getName(),
                null,
                Boolean.TRUE.equals(crypto.getActive()) ? "active" : "inactive"
        );
    }

    public static AssetSearchResponse stock(Stock stock) {
        String symbol = stock.getSymbol() == null ? null : stock.getSymbol().toUpperCase();
        return new AssetSearchResponse(
                "TRADFI",
                symbol,
                symbol,
                stock.getName(),
                stock.getExchange(),
                stock.getActive()
        );
    }
}

package ChainTracker.assetMarketDataService.mapper;

import ChainTracker.assetMarketDataService.dto.StockAssetResponse;
import ChainTracker.assetMarketDataService.model.Stock;

public class StockAssetMapper {

    public static StockAssetResponse toResponse(Stock stock){
        return new StockAssetResponse(stock.getSymbol(), stock.getName(), stock.getExchange(), stock.getActive());
    }
}

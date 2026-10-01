package ChainTracker.assetMarketDataService.mapper;

import ChainTracker.assetMarketDataService.dto.StockAssetResponse;
import ChainTracker.assetMarketDataService.model.Stock;

public class StockAssetMapper {

    public static Stock toStock(StockAssetResponse dto){
        Stock stock = new Stock();
        stock.setActive(dto.status());
        stock.setSymbol(dto.symbol());
        stock.setName(dto.name());
        stock.setExchange(dto.exchange());
        return stock;
    }

}

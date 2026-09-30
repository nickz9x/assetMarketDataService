package ChainTracker.assetMarketDataService.mapper;

import ChainTracker.assetMarketDataService.dto.CoinDataSimpleResponse;
import ChainTracker.assetMarketDataService.model.Coin;

public class CoinMapper {

    public static CoinDataSimpleResponse toResponse(Coin coin){
        return new CoinDataSimpleResponse(coin.getAssetId(), coin.getName(), coin.getSymbol());
    }
}

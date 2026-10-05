package ChainTracker.assetMarketDataService.mapper;

import ChainTracker.assetMarketDataService.dto.CryptoDataResponse;
import ChainTracker.assetMarketDataService.model.Crypto;

public class CryptoMapper {

    public static CryptoDataResponse toResponse(Crypto crypto){
        return new CryptoDataResponse(crypto.getAssetId(), crypto.getName(), crypto.getSymbol());
    }
}

package ChainTracker.assetMarketDataService.service;

import ChainTracker.assetMarketDataService.dto.CoinDataSimpleRequest;
import ChainTracker.assetMarketDataService.dto.CoinDataSimpleResponse;
import ChainTracker.assetMarketDataService.model.Coin;
import ChainTracker.assetMarketDataService.repository.CoinRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class CoinService {
    private CoinRepository coinRepository;


    public CoinDataSimpleResponse save(CoinDataSimpleRequest dto) {
        if (!coinRepository.existsByAssetId(dto.assetId())) {
            Coin coin = new Coin();
            coin.setAssetId(dto.assetId());
            coin.setName(dto.name());
            coin.setSymbol(dto.symbol());
            coinRepository.save(coin);

            return new CoinDataSimpleResponse(coin.getAssetId(), coin.getName(), coin.getSymbol());
        }
        else {
            return null;
        }
    }
}

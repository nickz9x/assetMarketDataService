package ChainTracker.assetMarketDataService.service;

import ChainTracker.assetMarketDataService.dto.AssetSearchResponse;
import ChainTracker.assetMarketDataService.model.Crypto;
import ChainTracker.assetMarketDataService.model.Stock;
import ChainTracker.assetMarketDataService.repository.CryptoRepository;
import ChainTracker.assetMarketDataService.repository.StockRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class AssetSearchService {
    private final CryptoRepository cryptoRepository;
    private final StockRepository stockRepository;

    public List<AssetSearchResponse> search(String term) {
        String query = term == null ? "" : term.trim();
        if (query.length() < 2) {
            return List.of();
        }

        List<AssetSearchResponse> results = new ArrayList<>();
        for (Crypto crypto : cryptoRepository
                .findTop10ByAssetIdContainingIgnoreCaseOrNameContainingIgnoreCaseOrSymbolContainingIgnoreCaseOrderBySymbolAsc(
                        query, query, query)) {
            results.add(AssetSearchResponse.crypto(crypto));
        }
        for (Stock stock : stockRepository
                .findTop10BySymbolContainingIgnoreCaseOrNameContainingIgnoreCaseOrderBySymbolAsc(query, query)) {
            results.add(AssetSearchResponse.stock(stock));
        }
        return results;
    }
}

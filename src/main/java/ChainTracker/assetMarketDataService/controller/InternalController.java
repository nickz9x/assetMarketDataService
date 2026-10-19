package ChainTracker.assetMarketDataService.controller;

import ChainTracker.assetMarketDataService.dto.CryptoDataResponse;
import ChainTracker.assetMarketDataService.dto.StockAssetResponse;
import ChainTracker.assetMarketDataService.service.CryptoAssetService;
import ChainTracker.assetMarketDataService.service.StockAssetService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/internal")
@AllArgsConstructor
public class InternalController {
    private final StockAssetService stockAssetService;
    private final CryptoAssetService cryptoAssetService;

    @GetMapping("/data/stock/{symbol}")
    public ResponseEntity<StockAssetResponse> getStockAsset(@PathVariable String symbol){
        return ResponseEntity.ok(stockAssetService.getAssetBySymbol(symbol));
    }

    @GetMapping("/data/currency/{assetId}")
    public ResponseEntity<CryptoDataResponse> getCryptoAsset(@PathVariable String assetId){
        return ResponseEntity.ok(cryptoAssetService.getAsset(assetId));
    }
}

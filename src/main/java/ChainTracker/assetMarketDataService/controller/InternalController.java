package ChainTracker.assetMarketDataService.controller;

import ChainTracker.assetMarketDataService.dto.CryptoDataResponse;
import ChainTracker.assetMarketDataService.dto.StockAssetResponse;
import ChainTracker.assetMarketDataService.service.CryptoAssetService;
import ChainTracker.assetMarketDataService.service.StockService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/internal")
@AllArgsConstructor
public class InternalController {
    private StockService stockService;
    private CryptoAssetService cryptoAssetService;

    @GetMapping("/data/stock/{symbol}")
    public ResponseEntity<StockAssetResponse> getDataBySymbol(@PathVariable String symbol){
        return ResponseEntity.ok(stockService.findBySymbol(symbol));
    }

    @GetMapping("/data/currency/{assetId}")
    public ResponseEntity<CryptoDataResponse> getCryptoAsset(@PathVariable String assetId){
        return ResponseEntity.ok(cryptoAssetService.getAsset(assetId));
    }
}

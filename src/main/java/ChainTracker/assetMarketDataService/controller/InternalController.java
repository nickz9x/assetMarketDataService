package ChainTracker.assetMarketDataService.controller;

import ChainTracker.assetMarketDataService.dto.CoinDataSimpleResponse;
import ChainTracker.assetMarketDataService.dto.StockAssetResponse;
import ChainTracker.assetMarketDataService.service.CoinService;
import ChainTracker.assetMarketDataService.service.StockService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/internal")
@AllArgsConstructor
public class InternalController {
    private StockService stockService;
    private CoinService coinService;

    @GetMapping("/data/stock/{symbol}")
    public ResponseEntity<StockAssetResponse> getDataBySymbol(@PathVariable String symbol){
        return ResponseEntity.ok(stockService.findBySymbol(symbol));
    }

    @GetMapping("/data/currency/{assetId}")
    public ResponseEntity<CoinDataSimpleResponse> getDataByAssetId(@PathVariable String assetId){
        return ResponseEntity.ok(coinService.getCoinData(assetId));
    }
}

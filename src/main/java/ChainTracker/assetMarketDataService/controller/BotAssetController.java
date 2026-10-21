package ChainTracker.assetMarketDataService.controller;

import ChainTracker.assetMarketDataService.dto.CryptoDataResponse;
import ChainTracker.assetMarketDataService.dto.CryptoMarketResponse;
import ChainTracker.assetMarketDataService.dto.StockAssetResponse;
import ChainTracker.assetMarketDataService.dto.StockMarketResponse;
import ChainTracker.assetMarketDataService.service.CryptoAssetService;
import ChainTracker.assetMarketDataService.service.CryptoMarketService;
import ChainTracker.assetMarketDataService.service.StockAssetService;
import ChainTracker.assetMarketDataService.service.StockMarketService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Copias das rotas de mercado usadas pelo fluxo do bot. O portfolioService
 * chama estas rotas anonimamente quando atende uma requisicao /api/bot/**;
 * as rotas originais (/api/currency, /api/stock, /api/internal) continuam
 * exigindo JWT.
 */
@RestController
@RequestMapping("/api/bot")
@AllArgsConstructor
public class BotAssetController {
    private final CryptoMarketService cryptoMarketService;
    private final CryptoAssetService cryptoAssetService;
    private final StockMarketService stockMarketService;
    private final StockAssetService stockAssetService;

    @GetMapping("/currency")
    public ResponseEntity<List<CryptoMarketResponse>> getTop100(){
        return ResponseEntity.ok(cryptoMarketService.getTop100());
    }

    @GetMapping("/currency/{id}")
    public ResponseEntity<CryptoMarketResponse> getCryptoMarketData(@PathVariable String id){
        return ResponseEntity.ok(cryptoMarketService.getMarketData(id));
    }

    @GetMapping("/currency/{id}/data")
    public ResponseEntity<CryptoDataResponse> getCryptoAsset(@PathVariable String id){
        return ResponseEntity.ok(cryptoAssetService.getAsset(id));
    }

    @GetMapping("/stock/{symbol}")
    public ResponseEntity<StockMarketResponse> getStockMarketData(@PathVariable String symbol){
        return ResponseEntity.ok(stockMarketService.getMarketData(symbol));
    }

    @GetMapping("/stock/{symbol}/data")
    public ResponseEntity<StockAssetResponse> getStockAsset(@PathVariable String symbol){
        return ResponseEntity.ok(stockAssetService.getAssetBySymbol(symbol));
    }
}

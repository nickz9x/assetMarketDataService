package ChainTracker.assetMarketDataService.controller;

import ChainTracker.assetMarketDataService.dto.CryptoDataResponse;
import ChainTracker.assetMarketDataService.dto.CryptoMarketResponse;
import ChainTracker.assetMarketDataService.dto.CryptoMarketSimpleResponse;
import ChainTracker.assetMarketDataService.service.CryptoAssetService;
import ChainTracker.assetMarketDataService.service.CryptoMarketService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/currency")
@AllArgsConstructor
public class CryptoController {
    private CryptoMarketService cryptoMarketService;
    private CryptoAssetService cryptoAssetService;

    @GetMapping
    public ResponseEntity<List<CryptoMarketResponse>> getTop100(){
        return ResponseEntity.ok(cryptoMarketService.getTop100());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CryptoMarketResponse> getMarketData(@PathVariable String id){
        return ResponseEntity.ok(cryptoMarketService.getMarketData(id));
    }

    @GetMapping("/prices")
    public ResponseEntity<Map<String, CryptoMarketSimpleResponse>> getSimplePrices(@RequestParam List<String> ids){
        return ResponseEntity.ok(cryptoMarketService.getSimplePrices(ids));
    }

    @GetMapping("/data/{assetId}")
    public ResponseEntity<CryptoDataResponse> getAsset(@PathVariable String assetId){
        return ResponseEntity.ok(cryptoAssetService.getAsset(assetId));
    }

}

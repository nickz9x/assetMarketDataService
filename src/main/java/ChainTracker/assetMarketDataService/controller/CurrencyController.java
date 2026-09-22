package ChainTracker.assetMarketDataService.controller;

import ChainTracker.assetMarketDataService.dto.CoinMarketResponse;
import ChainTracker.assetMarketDataService.dto.CoinMarketSimpleResponse;
import ChainTracker.assetMarketDataService.service.CoingeckoService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/currency")
@AllArgsConstructor
public class CurrencyController {
    private CoingeckoService service;


    @GetMapping
    public ResponseEntity<List<CoinMarketResponse>> getTop100(){
        return ResponseEntity.ok(service.getTop100());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CoinMarketResponse> getCoin(@PathVariable String id){
        return ResponseEntity.ok(service.getCoin(id));
    }

    @GetMapping("/prices")
    public ResponseEntity<Map<String, CoinMarketSimpleResponse>> getSimplePrices(@RequestParam List<String> ids){
        return ResponseEntity.ok(service.getSimpleCoin(ids));
    }

}

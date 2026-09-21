package ChainTracker.assetMarketDataService.controller;

import ChainTracker.assetMarketDataService.dto.CoinMarketResponse;
import ChainTracker.assetMarketDataService.service.CoingeckoService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

}

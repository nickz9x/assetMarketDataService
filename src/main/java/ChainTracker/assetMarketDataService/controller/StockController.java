package ChainTracker.assetMarketDataService.controller;


import ChainTracker.assetMarketDataService.dto.StockMarketResponse;
import ChainTracker.assetMarketDataService.service.StockMarketService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/stock")
@AllArgsConstructor
public class StockController {
    private final StockMarketService stockMarketService;



    @GetMapping
    public ResponseEntity<Map<String, StockMarketResponse>> getTop100(){
        return ResponseEntity.ok(stockMarketService.getTop100());
    }

    @GetMapping("/{symbol}")
    public ResponseEntity<StockMarketResponse> getMarketData(@PathVariable String symbol){
        return ResponseEntity.ok(stockMarketService.getMarketData(symbol));
    }

}

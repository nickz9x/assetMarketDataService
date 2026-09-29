package ChainTracker.assetMarketDataService.controller;


import ChainTracker.assetMarketDataService.dto.StockMarketResponse;
import ChainTracker.assetMarketDataService.service.AlpacaService;
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
    private AlpacaService service;



    @GetMapping
    public ResponseEntity<Map<String, StockMarketResponse>> getTop50Stocks(){
        return ResponseEntity.ok(service.getTop50Stock());
    }

    @GetMapping("/{symbol}")
    public ResponseEntity<StockMarketResponse> getStock(@PathVariable String symbol){
        return ResponseEntity.ok(service.getStock(symbol));
    }

}

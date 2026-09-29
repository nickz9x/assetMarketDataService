package ChainTracker.assetMarketDataService.controller;

import ChainTracker.assetMarketDataService.dto.StockAssetResponse;
import ChainTracker.assetMarketDataService.service.AlpacaService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/internal")
@AllArgsConstructor
public class InternalController {
    private AlpacaService alpacaService;

    @GetMapping("/us_equity")
    public ResponseEntity<List<StockAssetResponse>> updateUsEquity(){
        return ResponseEntity.ok(alpacaService.getUsEquity());
    }
}

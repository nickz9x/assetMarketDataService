package ChainTracker.assetMarketDataService.controller;

import ChainTracker.assetMarketDataService.service.AssetMarketDataService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/wallet")
@AllArgsConstructor
public class WalletController {
    public AssetMarketDataService service;


    @PostMapping("/balance/{address}")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable String address){
        return ResponseEntity.ok(service.getBalance(address));
    }
}

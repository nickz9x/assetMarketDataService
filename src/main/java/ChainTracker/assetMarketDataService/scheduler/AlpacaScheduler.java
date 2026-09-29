package ChainTracker.assetMarketDataService.scheduler;

import ChainTracker.assetMarketDataService.dto.StockMarketResponse;
import ChainTracker.assetMarketDataService.service.MarketDataService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@AllArgsConstructor
public class AlpacaScheduler {
    private MarketDataService service;


    @Scheduled(fixedRate = 15000)
    public Map<String, StockMarketResponse> getTop100Stocks(){
        return service.updateTop100Stocks();
    }
}

package ChainTracker.assetMarketDataService.scheduler;

import ChainTracker.assetMarketDataService.service.AlpacaService;
import ChainTracker.assetMarketDataService.service.MarketDataService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AlpacaScheduler {
    private MarketDataService service;
    private AlpacaService alpacaService;

    @Scheduled(fixedRate = 15000)
    public void getTop100Stocks(){
        service.updateTop100Stocks();
    }

    @Scheduled(fixedRate = 86400000)
    public void updateUsEquityData(){
        alpacaService.getUsEquity();
    }
}

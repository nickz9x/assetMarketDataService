package ChainTracker.assetMarketDataService.scheduler;

import ChainTracker.assetMarketDataService.service.MarketDataService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CoingeckoScheduler {
    private MarketDataService service;

    @Scheduled(fixedRate = 30000)
    public void updateTop100Data(){
        service.updateTop100Data();
    }
}

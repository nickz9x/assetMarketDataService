package ChainTracker.assetMarketDataService.scheduler;

import ChainTracker.assetMarketDataService.service.CoingeckoService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CoingeckoScheduler {
    private CoingeckoService coingeckoService;

    @Scheduled(fixedRate = 30000)
    public void updateTop100Data(){
        coingeckoService.refreshTop100();
    }
}

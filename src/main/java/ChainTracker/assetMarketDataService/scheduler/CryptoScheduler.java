package ChainTracker.assetMarketDataService.scheduler;

import ChainTracker.assetMarketDataService.service.CryptoMarketService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CryptoScheduler {
    private CryptoMarketService cryptoMarketService;

    @Scheduled(fixedRate = 30000)
    public void refreshTop100(){
        cryptoMarketService.refreshTop100();
    }
}

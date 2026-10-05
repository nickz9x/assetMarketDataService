package ChainTracker.assetMarketDataService.scheduler;

import ChainTracker.assetMarketDataService.service.StockAssetService;
import ChainTracker.assetMarketDataService.service.StockMarketService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class StockScheduler {
    private StockMarketService stockMarketService;
    private StockAssetService stockAssetService;

    @Scheduled(fixedRate = 15000)
    public void refreshTop100(){
        stockMarketService.refreshTop100();
    }

    @Scheduled(fixedRate = 86400000)
    public void refreshCatalog(){
        stockAssetService.syncUsEquity();
    }
}

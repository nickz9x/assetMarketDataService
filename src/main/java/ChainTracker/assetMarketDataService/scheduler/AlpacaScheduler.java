package ChainTracker.assetMarketDataService.scheduler;

import ChainTracker.assetMarketDataService.dto.StockAssetResponse;
import ChainTracker.assetMarketDataService.dto.StockMarketResponse;
import ChainTracker.assetMarketDataService.service.AlpacaService;
import ChainTracker.assetMarketDataService.service.MarketDataService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
public class AlpacaScheduler {
    private MarketDataService service;
    private AlpacaService alpacaService;

    @Scheduled(fixedRate = 15000)
    public Map<String, StockMarketResponse> getTop100Stocks(){
        return service.updateTop100Stocks();
    }

    @Scheduled(fixedRate = 86400000)
    public List<StockAssetResponse> updateUsEquityData(){
        return alpacaService.getUsEquity();
    }
}

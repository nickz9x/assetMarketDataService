package ChainTracker.assetMarketDataService.scheduler;

import ChainTracker.assetMarketDataService.client.CoingeckoClient;
import ChainTracker.assetMarketDataService.dto.CoinMarketResponse;
import ChainTracker.assetMarketDataService.service.MarketDataService;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class CoingeckoScheduler {
    private MarketDataService service;



    @Scheduled(fixedRate = 300000)
    public List<CoinMarketResponse> updateTop100Data(){
        return service.updateTop100Data();
    }
}

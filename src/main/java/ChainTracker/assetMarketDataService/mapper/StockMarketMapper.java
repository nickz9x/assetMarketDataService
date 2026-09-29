package ChainTracker.assetMarketDataService.mapper;

import ChainTracker.assetMarketDataService.dto.StockMarketResponse;
import ChainTracker.assetMarketDataService.dto.StockSnapshotResponse;
import org.springframework.stereotype.Component;

@Component
public class StockMarketMapper {

    public static StockMarketResponse toResponse(
            String symbol,
            StockSnapshotResponse snapshot
    ) {
        return new StockMarketResponse(
                symbol,
                snapshot.latestTrade().price(),
                snapshot.dailyBar().open(),
                snapshot.dailyBar().high(),
                snapshot.dailyBar().low()
        );
    }
}
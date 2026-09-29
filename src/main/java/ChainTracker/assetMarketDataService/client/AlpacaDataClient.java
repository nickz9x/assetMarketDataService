package ChainTracker.assetMarketDataService.client;

import ChainTracker.assetMarketDataService.dto.StockSnapshotResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
public class AlpacaDataClient {
    private final RestClient restClient;

    public AlpacaDataClient(RestClient.Builder builder, @Value("${alpaca.api-key}") String apiKey,
                            @Value("${alpaca.api-secret}") String apiSecretKey, @Value("${alpaca.url}") String url) {
        this.restClient = builder.baseUrl(url).defaultHeader("APCA-API-KEY-ID", apiKey).defaultHeader("APCA-API-SECRET-KEY", apiSecretKey).build();
    }


    public Map<String, StockSnapshotResponse> getSnapshots() {
        List<String> symbols = List.of("NVDA", "AAPL", "MSFT", "AMZN", "GOOGL", "GOOG", "META", "AVGO", "TSLA", "MU",
                "BRK.B", "LLY", "AMD", "JPM", "WMT", "V", "XOM", "JNJ", "INTC", "MA",
                "ABBV", "PLTR", "CSCO", "COST", "CVX", "ORCL", "LRCX", "BAC", "AMAT", "CAT",
                "KO", "MRK", "PG", "DELL", "UNH", "GE", "PANW", "MS", "PM", "HD",
                "NFLX", "GS", "CRWD", "ANET", "TXN", "GEV", "RTX", "TMO", "KLAC", "CRM",
                "IBM", "NOW", "MCD", "PEP", "DIS", "AXP", "UBER", "ABT", "ISRG", "QCOM",
                "BKNG", "INTU", "AMGN", "NEE", "HON", "LOW", "PFE", "T", "SPGI", "SYK",
                "BLK", "C", "SCHW", "AMT", "BA", "ADP", "MDT", "VRTX", "ADI", "GILD",
                "SOFI", "COIN", "HOOD", "MSTR", "MARA", "RIVN", "LCID", "SNOW", "SHOP", "SQ",
                "PYPL", "NKE", "SBUX", "CMCSA", "UPS", "FDX", "GM", "F", "AAL", "WBD");

        return restClient.get().uri(uriBuilder -> uriBuilder.path("/stocks/snapshots").queryParam("symbols", String.join(",", symbols)).queryParam("feed", "iex").build()).retrieve().body(new ParameterizedTypeReference<Map<String, StockSnapshotResponse>>() {
        });
    }

    public StockSnapshotResponse getSnapshot(String symbol){

        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/stocks/{symbol}/snapshot")
                        .queryParam("feed","iex")
                        .build(symbol)
                ).retrieve()
                .body(StockSnapshotResponse.class);
    }
}

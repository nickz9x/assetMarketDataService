package ChainTracker.assetMarketDataService.client;

import ChainTracker.assetMarketDataService.dto.StockAssetResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class AlpacaAssetClient {
    private final RestClient restClient;

    public AlpacaAssetClient(RestClient.Builder builder, @Value("${alpaca.api-key}") String apiKey,
                            @Value("${alpaca.api-secret}") String apiSecretKey, @Value("${alpaca.trading-url}") String url) {
        this.restClient = builder.baseUrl(url).defaultHeader("APCA-API-KEY-ID", apiKey).defaultHeader("APCA-API-SECRET-KEY", apiSecretKey).build();
    }


    public List<StockAssetResponse> updateUsEquity(){
        return restClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("assets")
                        .queryParam("status","active")
                        .queryParam("asset_class","us_equity")
                        .build()
                ).retrieve()
                .body(new ParameterizedTypeReference<List<StockAssetResponse>>() {
                });
    }

}

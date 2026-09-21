package ChainTracker.assetMarketDataService.client;

import ChainTracker.assetMarketDataService.dto.CoinMarketResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
@Component
public class CoingeckoClient {

    private final RestClient restClient;

    public CoingeckoClient(
            RestClient.Builder builder,
            @Value("${coingecko.api-key}") String apiKey,
            @Value("${coingecko.url}") String url
    ) {
        this.restClient = builder
                .baseUrl(url)
                .defaultHeader("x-cg-pro-api-key",apiKey)
                .build();
    }

    public List<CoinMarketResponse> getTop100Coins(){
       return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("coins/markets")
                        .queryParam("vs_currency","usd")
                        .queryParam("order","market_cap_desc")
                        .queryParam("per_page",100)
                        .queryParam("page",1)
                        .build()
                ).retrieve()
                .body(new ParameterizedTypeReference<List<CoinMarketResponse>>() {});
    }

    public CoinMarketResponse getCoin(String id){
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("coins/{id}")
                        .build(id)
                ).retrieve().body(CoinMarketResponse.class);
    }
}

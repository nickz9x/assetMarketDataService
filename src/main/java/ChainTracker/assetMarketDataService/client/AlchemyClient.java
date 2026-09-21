package ChainTracker.assetMarketDataService.client;

import ChainTracker.assetMarketDataService.dto.AlchemyResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
public class AlchemyClient {

    private final RestClient restClient;

    public AlchemyClient(
            RestClient.Builder builder,
            @Value("${alchemy.api-key}") String apiKey,
            @Value("${alchemy.url}") String url
    ) {
        this.restClient = builder
                .baseUrl(url + apiKey)
                .build();
    }

    public AlchemyResponse getBalance(String address) {

        Map<String, Object> request = Map.of(
                "jsonrpc", "2.0",
                "method", "eth_getBalance",
                "params", List.of(address, "latest"),
                "id", 1
        );

        return restClient.post()
                .body(request)
                .retrieve()
                .body(AlchemyResponse.class);
    }


}

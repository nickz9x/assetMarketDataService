package ChainTracker.assetMarketDataService.service;

import ChainTracker.assetMarketDataService.client.AlchemyClient;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;

@Service
@AllArgsConstructor
public class WalletService {
    private final AlchemyClient client;


    public BigDecimal getBalance(String address) {
        String balance = client.getBalance(address).result();
        BigInteger wei = new BigInteger(
                balance.substring(2),
                16
        );

        return new BigDecimal(wei)
                .divide(BigDecimal.TEN.pow(18));
    }


}

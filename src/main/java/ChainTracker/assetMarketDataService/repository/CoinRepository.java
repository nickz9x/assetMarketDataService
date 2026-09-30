package ChainTracker.assetMarketDataService.repository;

import ChainTracker.assetMarketDataService.model.Coin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CoinRepository extends JpaRepository<Coin, UUID> {
    Optional<Coin> findByAssetId(String assetId);
    boolean existsByAssetId(String assetId);
}

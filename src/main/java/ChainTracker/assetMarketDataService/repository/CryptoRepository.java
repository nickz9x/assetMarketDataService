package ChainTracker.assetMarketDataService.repository;

import ChainTracker.assetMarketDataService.model.Crypto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CryptoRepository extends JpaRepository<Crypto, UUID> {
    Optional<Crypto> findByAssetId(String assetId);
    boolean existsByAssetId(String assetId);
}

package ChainTracker.assetMarketDataService.repository;

import ChainTracker.assetMarketDataService.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StockRepository extends JpaRepository<Stock, UUID> {
     Optional<Stock> findBySymbol(String symbol);

}

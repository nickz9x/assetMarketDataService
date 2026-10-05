package ChainTracker.assetMarketDataService.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
public class Crypto {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String assetId;
    private String name;
    private String symbol;
    private Boolean active = true;
}

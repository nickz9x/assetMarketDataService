package ChainTracker.assetMarketDataService.controller;

import ChainTracker.assetMarketDataService.dto.CryptoDataResponse;
import ChainTracker.assetMarketDataService.dto.StockAssetResponse;
import ChainTracker.assetMarketDataService.service.CryptoAssetService;
import ChainTracker.assetMarketDataService.service.StockAssetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Dados internos", description = "Metadados de ativos consumidos por outros serviços do ChainTracker.")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/internal")
@AllArgsConstructor
public class InternalController {
    private final StockAssetService stockAssetService;
    private final CryptoAssetService cryptoAssetService;

    @Operation(
            summary = "Consulta os metadados de uma ação",
            description = "Retorna símbolo, nome, bolsa e status do catálogo local."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Metadados retornados"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "404", description = "Ação não encontrada no catálogo")
    })
    @GetMapping("/data/stock/{symbol}")
    public ResponseEntity<StockAssetResponse> getStockAsset(
            @Parameter(description = "Símbolo da ação", example = "AAPL")
            @PathVariable String symbol){
        return ResponseEntity.ok(stockAssetService.getAssetBySymbol(symbol));
    }

    @Operation(
            summary = "Consulta os metadados de uma criptomoeda",
            description = "Retorna assetId, nome e símbolo do catálogo local."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Metadados retornados"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "404", description = "Criptomoeda não encontrada no catálogo")
    })
    @GetMapping("/data/currency/{assetId}")
    public ResponseEntity<CryptoDataResponse> getCryptoAsset(
            @Parameter(description = "ID da moeda na CoinGecko", example = "bitcoin")
            @PathVariable String assetId){
        return ResponseEntity.ok(cryptoAssetService.getAsset(assetId));
    }
}

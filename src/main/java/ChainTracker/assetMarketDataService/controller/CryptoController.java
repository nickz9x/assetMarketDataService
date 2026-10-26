package ChainTracker.assetMarketDataService.controller;

import ChainTracker.assetMarketDataService.dto.CryptoDataResponse;
import ChainTracker.assetMarketDataService.dto.CryptoMarketResponse;
import ChainTracker.assetMarketDataService.dto.CryptoMarketSimpleResponse;
import ChainTracker.assetMarketDataService.service.CryptoAssetService;
import ChainTracker.assetMarketDataService.service.CryptoMarketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Criptomoedas", description = "Catálogo, cotações e preços simplificados de criptomoedas.")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/currency")
@AllArgsConstructor
public class CryptoController {
    private final CryptoMarketService cryptoMarketService;
    private final CryptoAssetService cryptoAssetService;

    @Operation(
            summary = "Lista as 100 maiores criptomoedas",
            description = "Retorna as 100 moedas de maior valor de mercado, com cotação e dados de capitalização."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    })
    @GetMapping
    public ResponseEntity<List<CryptoMarketResponse>> getTop100(){
        return ResponseEntity.ok(cryptoMarketService.getTop100());
    }

    @Operation(
            summary = "Consulta a cotação de uma criptomoeda",
            description = "Busca a cotação pelo id da CoinGecko. Usa o cache Redis e consulta o provedor em caso de cache miss."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cotação retornada"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "404", description = "Criptomoeda não encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CryptoMarketResponse> getMarketData(
            @Parameter(description = "ID da moeda na CoinGecko", example = "bitcoin")
            @PathVariable String id){
        return ResponseEntity.ok(cryptoMarketService.getMarketData(id));
    }

    @Operation(
            summary = "Consulta preços simplificados em lote",
            description = "Retorna preço em USD, market cap, volume e variação de 24h para os ids informados."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Preços retornados"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    })
    @GetMapping("/prices")
    public ResponseEntity<Map<String, CryptoMarketSimpleResponse>> getSimplePrices(
            @Parameter(description = "IDs da CoinGecko separados por vírgula", example = "bitcoin,ethereum")
            @RequestParam List<String> ids){
        return ResponseEntity.ok(cryptoMarketService.getSimplePrices(ids));
    }

    @Operation(
            summary = "Consulta os metadados de uma criptomoeda",
            description = "Retorna o registro do catálogo local com assetId, nome e símbolo."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Metadados retornados"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "404", description = "Criptomoeda não encontrada no catálogo")
    })
    @GetMapping("/data/{assetId}")
    public ResponseEntity<CryptoDataResponse> getAsset(
            @Parameter(description = "ID da moeda na CoinGecko", example = "bitcoin")
            @PathVariable String assetId){
        return ResponseEntity.ok(cryptoAssetService.getAsset(assetId));
    }

}

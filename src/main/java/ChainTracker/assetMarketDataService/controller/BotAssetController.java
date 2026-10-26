package ChainTracker.assetMarketDataService.controller;

import ChainTracker.assetMarketDataService.dto.CryptoDataResponse;
import ChainTracker.assetMarketDataService.dto.CryptoMarketResponse;
import ChainTracker.assetMarketDataService.dto.StockAssetResponse;
import ChainTracker.assetMarketDataService.dto.StockMarketResponse;
import ChainTracker.assetMarketDataService.service.CryptoAssetService;
import ChainTracker.assetMarketDataService.service.CryptoMarketService;
import ChainTracker.assetMarketDataService.service.StockAssetService;
import ChainTracker.assetMarketDataService.service.StockMarketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Bot - mercado", description = "Consultas de mercado usadas pelo fluxo do Telegram. Exigem token com escopo bot.")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/bot")
@AllArgsConstructor
public class BotAssetController {
    private final CryptoMarketService cryptoMarketService;
    private final CryptoAssetService cryptoAssetService;
    private final StockMarketService stockMarketService;
    private final StockAssetService stockAssetService;

    @Operation(summary = "Lista as 100 maiores criptomoedas para o bot")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Token sem o escopo bot")
    })
    @GetMapping("/currency")
    public ResponseEntity<List<CryptoMarketResponse>> getTop100(){
        return ResponseEntity.ok(cryptoMarketService.getTop100());
    }

    @Operation(summary = "Consulta a cotação de uma criptomoeda para o bot")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cotação retornada"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Token sem o escopo bot"),
            @ApiResponse(responseCode = "404", description = "Criptomoeda não encontrada")
    })
    @GetMapping("/currency/{id}")
    public ResponseEntity<CryptoMarketResponse> getCryptoMarketData(
            @Parameter(description = "ID da moeda na CoinGecko", example = "bitcoin")
            @PathVariable String id){
        return ResponseEntity.ok(cryptoMarketService.getMarketData(id));
    }

    @Operation(summary = "Consulta os metadados de uma criptomoeda para o bot")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Metadados retornados"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Token sem o escopo bot"),
            @ApiResponse(responseCode = "404", description = "Criptomoeda não encontrada")
    })
    @GetMapping("/currency/{id}/data")
    public ResponseEntity<CryptoDataResponse> getCryptoAsset(
            @Parameter(description = "ID da moeda na CoinGecko", example = "bitcoin")
            @PathVariable String id){
        return ResponseEntity.ok(cryptoAssetService.getAsset(id));
    }

    @Operation(summary = "Consulta a cotação de uma ação para o bot")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cotação retornada"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Token sem o escopo bot"),
            @ApiResponse(responseCode = "404", description = "Ação não encontrada")
    })
    @GetMapping("/stock/{symbol}")
    public ResponseEntity<StockMarketResponse> getStockMarketData(
            @Parameter(description = "Símbolo da ação", example = "AAPL")
            @PathVariable String symbol){
        return ResponseEntity.ok(stockMarketService.getMarketData(symbol));
    }

    @Operation(summary = "Consulta os metadados de uma ação para o bot")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Metadados retornados"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Token sem o escopo bot"),
            @ApiResponse(responseCode = "404", description = "Ação não encontrada")
    })
    @GetMapping("/stock/{symbol}/data")
    public ResponseEntity<StockAssetResponse> getStockAsset(
            @Parameter(description = "Símbolo da ação", example = "AAPL")
            @PathVariable String symbol){
        return ResponseEntity.ok(stockAssetService.getAssetBySymbol(symbol));
    }
}

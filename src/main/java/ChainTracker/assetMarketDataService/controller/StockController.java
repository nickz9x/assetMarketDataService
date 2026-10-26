package ChainTracker.assetMarketDataService.controller;


import ChainTracker.assetMarketDataService.dto.StockMarketResponse;
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

import java.util.Map;

@Tag(name = "Ações", description = "Cotações das ações acompanhadas pelo ChainTracker.")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/stock")
@AllArgsConstructor
public class StockController {
    private final StockMarketService stockMarketService;


    @Operation(
            summary = "Lista as ações acompanhadas",
            description = "Retorna o mapa das 100 ações mantidas pelo serviço, com a cotação mais recente."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Mapa de ações retornado"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    })
    @GetMapping
    public ResponseEntity<Map<String, StockMarketResponse>> getTop100(){
        return ResponseEntity.ok(stockMarketService.getTop100());
    }

    @Operation(
            summary = "Consulta a cotação de uma ação",
            description = "Retorna preço atual, abertura, máxima e mínima do snapshot mais recente da Alpaca."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cotação retornada"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "404", description = "Ação não encontrada")
    })
    @GetMapping("/{symbol}")
    public ResponseEntity<StockMarketResponse> getMarketData(
            @Parameter(description = "Símbolo da ação", example = "AAPL")
            @PathVariable String symbol){
        return ResponseEntity.ok(stockMarketService.getMarketData(symbol));
    }

}

package io.github.ktrzaskoma.controller;

import io.github.ktrzaskoma.dto.TransactionReadModel;
import io.github.ktrzaskoma.dto.TransactionWriteModel;
import io.github.ktrzaskoma.model.Portfolio;
import io.github.ktrzaskoma.service.PortfolioService;
import io.github.ktrzaskoma.service.StockTransactionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/portfolio")
public class PortfolioController {

    private final PortfolioService portfolioService;
    private final StockTransactionService transactionService;

    public PortfolioController(PortfolioService portfolioService, StockTransactionService transactionService) {
        this.portfolioService = portfolioService;
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<Portfolio> createPortfolio(@RequestParam String name) {
        return ResponseEntity.ok(portfolioService.createPortfolio(name));
    }

    @GetMapping
    public List<Portfolio> getAll() {
        return portfolioService.getAllPortfolios().stream().peek(p -> {
            if (p.getTransactions() != null) {
                p.getTransactions().forEach(t -> t.setPortfolio(null));
            }
        }).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Portfolio> getOne(@PathVariable Long id) {
        Portfolio p = portfolioService.getPortfolioById(id);
        if (p.getTransactions() != null) {
            p.getTransactions().forEach(t -> t.setPortfolio(null));
        }
        return ResponseEntity.ok(p);
    }

    @PostMapping("/{id}/buy")
    public ResponseEntity<TransactionReadModel> buyStock(@PathVariable Long id,
                                                         @Valid @RequestBody TransactionWriteModel request) {
        return ResponseEntity.ok(transactionService.handleBuy(id, request));
    }

    @PostMapping("/{id}/sell")
    public ResponseEntity<?> sellStock(@PathVariable Long id,
                                       @Valid @RequestBody TransactionWriteModel request) {
        try {
            return ResponseEntity.ok(transactionService.handleSell(id, request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}/summary")
    public ResponseEntity<byte[]> summary(@PathVariable Long id) {
        return transactionService.generateSummaryCsv(id);
    }

}

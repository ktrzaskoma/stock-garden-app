package io.github.ktrzaskoma.controller;

import io.github.ktrzaskoma.model.Portfolio;
import io.github.ktrzaskoma.model.StockTransaction;
import io.github.ktrzaskoma.service.PortfolioService;
import io.github.ktrzaskoma.service.StockTransactionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
        }).collect(Collectors.toList());
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
    public ResponseEntity<StockTransaction> buyStock(@PathVariable Long id,
                                                     @RequestParam String symbol,
                                                     @RequestParam int quantity,
                                                     @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime time) {
        Portfolio portfolio = portfolioService.getPortfolioById(id);
        StockTransaction tx = transactionService.buy(portfolio, symbol, quantity, time);
        tx.setPortfolio(null);
        return ResponseEntity.ok(tx);
    }

    @PostMapping("/{id}/sell")
    public ResponseEntity<?> sellStock(@PathVariable Long id,
                                       @RequestParam String symbol,
                                       @RequestParam int quantity,
                                       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime time) {
        try {
            Portfolio portfolio = portfolioService.getPortfolioById(id);
            StockTransaction tx = transactionService.sell(portfolio, symbol, quantity, time);
            tx.setPortfolio(null);
            return ResponseEntity.ok(tx);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}/summary")
    public ResponseEntity<String> summary(@PathVariable Long id) {
        Portfolio portfolio = portfolioService.getPortfolioById(id);
        double profit = transactionService.calculateProfit(id);

        double roundProfit = Math.round(profit * 100.0) / 100.0;

        if (profit >= 0) {
          return ResponseEntity.ok("Profit: " + roundProfit);
        }
        return ResponseEntity.ok("Loss: " + roundProfit);
    }

}

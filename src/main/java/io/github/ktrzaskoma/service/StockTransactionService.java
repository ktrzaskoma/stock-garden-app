package io.github.ktrzaskoma.service;

import io.github.ktrzaskoma.model.Portfolio;
import io.github.ktrzaskoma.model.StockTransaction;
import io.github.ktrzaskoma.model.TransactionType;
import io.github.ktrzaskoma.repository.StockTransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StockTransactionService {

    private final StockTransactionRepository stockTransactionRepository;
    private final AlphaVantageService alphaVantageService;


    public StockTransactionService(StockTransactionRepository stockTransactionRepository, AlphaVantageService alphaVantageService) {
        this.stockTransactionRepository = stockTransactionRepository;
        this.alphaVantageService = alphaVantageService;
    }

    public StockTransaction buy(Portfolio portfolio, String symbol, int quantity, LocalDateTime timestamp) {
        double price = alphaVantageService.getTransactionPrice(symbol, timestamp);
        StockTransaction tx = new StockTransaction(symbol, quantity, TransactionType.BUY, price, timestamp, portfolio);
        return stockTransactionRepository.save(tx);
    }


    public StockTransaction sell(Portfolio portfolio, String symbol, int quantity, LocalDateTime time) {
        List<StockTransaction> all = stockTransactionRepository.findByPortfolioId(portfolio.getId());

        int owned = all.stream()
                .filter(tx -> tx.getSymbol().equalsIgnoreCase(symbol))
                .mapToInt(tx -> tx.getTransactionType() == TransactionType.BUY ? tx.getQuantity() : -tx.getQuantity())
                .sum();

        if (quantity > owned) {
            throw new IllegalArgumentException("Nie możesz sprzedać więcej akcji niż posiadasz. Masz: " + owned);
        }

        double price = alphaVantageService.getTransactionPrice(symbol, time);
        StockTransaction tx = new StockTransaction(symbol, quantity, TransactionType.SELL, price, time, portfolio);
        return stockTransactionRepository.save(tx);
    }


    public List<StockTransaction> findByPortfolio(Long portfolioId) {
        return stockTransactionRepository.findByPortfolioId(portfolioId);
    }


    public double calculateProfit(Long portfolioId) {
        List<StockTransaction> all = stockTransactionRepository.findByPortfolioId(portfolioId);

        double totalBuy = all.stream()
                .filter(tx -> tx.getTransactionType() == TransactionType.BUY)
                .mapToDouble(tx -> tx.getQuantity() * tx.getPrice())
                .sum();

        double totalSell = all.stream()
                .filter(tx -> tx.getTransactionType() == TransactionType.SELL)
                .mapToDouble(tx -> tx.getQuantity() * tx.getPrice())
                .sum();

        return totalSell - totalBuy;
    }


}

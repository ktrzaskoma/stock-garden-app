package io.github.ktrzaskoma.service;

import io.github.ktrzaskoma.dto.TransactionReadModel;
import io.github.ktrzaskoma.dto.TransactionWriteModel;
import io.github.ktrzaskoma.model.Portfolio;
import io.github.ktrzaskoma.model.StockTransaction;
import io.github.ktrzaskoma.model.TransactionType;
import io.github.ktrzaskoma.repository.StockTransactionRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class StockTransactionService {

    private final StockTransactionRepository transactionRepo;
    private final PortfolioService portfolioService;
    private final AlphaVantageService alphaService;

    public StockTransactionService(StockTransactionRepository transactionRepo, PortfolioService portfolioService, AlphaVantageService alphaService) {
        this.transactionRepo = transactionRepo;
        this.portfolioService = portfolioService;
        this.alphaService = alphaService;
    }

    public TransactionReadModel handleBuy(Long portfolioId, TransactionWriteModel request) {
        Portfolio portfolio = portfolioService.getPortfolioById(portfolioId);
        double price = alphaService.getTransactionPrice(request.getSymbol(), request.getTime());
        StockTransaction tx = new StockTransaction(request.getSymbol(), request.getQuantity(), TransactionType.BUY, price, request.getTime(), portfolio);
        transactionRepo.save(tx);
        return mapToDto(tx);
    }

    public TransactionReadModel handleSell(Long portfolioId, TransactionWriteModel request) {
        Portfolio portfolio = portfolioService.getPortfolioById(portfolioId);
        int owned = transactionRepo.findByPortfolioId(portfolioId).stream()
                .filter(t -> t.getSymbol().equalsIgnoreCase(request.getSymbol()))
                .mapToInt(t -> t.getTransactionType() == TransactionType.BUY ? t.getQuantity() : -t.getQuantity())
                .sum();

        if (request.getQuantity() > owned) {
            throw new IllegalArgumentException("Portfolio status: " + owned);
        }

        double price = alphaService.getTransactionPrice(request.getSymbol(), request.getTime());
        StockTransaction tx = new StockTransaction(request.getSymbol(), request.getQuantity(), TransactionType.SELL, price, request.getTime(), portfolio);
        transactionRepo.save(tx);
        return mapToDto(tx);
    }

    public ResponseEntity<byte[]> generateSummaryCsv(Long portfolioId) {
        Portfolio portfolio = portfolioService.getPortfolioById(portfolioId);
        List<StockTransaction> txs = transactionRepo.findByPortfolioId(portfolioId);

        double profit = txs.stream()
                .mapToDouble(t -> t.getTransactionType() == TransactionType.BUY ? -t.getQuantity() * t.getPrice() : t.getQuantity() * t.getPrice())
                .sum();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(out);
        writer.println("ID,Symbol,Type,Quantity,Price,Timestamp");
        txs.forEach(t -> writer.printf("%d,%s,%s,%d,%.2f,%s\n",
                t.getId(), t.getSymbol(), t.getTransactionType(), t.getQuantity(), t.getPrice(), t.getTimestamp()));
        writer.printf("\nTotal Profit/Loss:,%.2f", profit);
        writer.flush();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + portfolio.getName().replaceAll("[^a-zA-Z0-9_-]", "_") + "_summary.csv");

        return ResponseEntity.ok().headers(headers).body(out.toByteArray());
    }

    private TransactionReadModel mapToDto(StockTransaction tx) {
        return new TransactionReadModel(
                tx.getId(),
                tx.getSymbol(),
                tx.getTransactionType().name(),
                tx.getQuantity(),
                tx.getPrice(),
                tx.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        );
    }

}

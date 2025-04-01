package io.github.ktrzaskoma.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class StockTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String symbol;

    private int quantity;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    private double price;

    private LocalDateTime timeStamp;

    @ManyToOne
    @JoinColumn(name = "portfolio_id")
    private Portfolio portfolio;


    public StockTransaction() {
    }

    public StockTransaction(String symbol, int quantity, TransactionType transactionType, double price, LocalDateTime timeStamp, Portfolio portfolio) {
        this.symbol = symbol;
        this.quantity = quantity;
        this.transactionType = transactionType;
        this.price = price;
        this.timeStamp = timeStamp;
        this.portfolio = portfolio;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(LocalDateTime dateTime) {
        this.timeStamp = dateTime;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }
}

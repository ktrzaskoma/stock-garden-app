package io.github.ktrzaskoma.dto;

public class TransactionReadModel {
    private Long id;
    private String symbol;
    private String type;
    private int quantity;
    private double price;
    private String timestamp;

    public TransactionReadModel(Long id, String symbol, String type, int quantity, double price, String timestamp) {
        this.id = id;
        this.symbol = symbol;
        this.type = type;
        this.quantity = quantity;
        this.price = price;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getType() {
        return type;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public String getTimestamp() {
        return timestamp;
    }
}

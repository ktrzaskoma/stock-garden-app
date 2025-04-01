package io.github.ktrzaskoma.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Portfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL)
    private List<StockTransaction> transactions;

    public Portfolio() {
    }

    public Portfolio(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<StockTransaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<StockTransaction> transactions) {
        this.transactions = transactions;
    }
}

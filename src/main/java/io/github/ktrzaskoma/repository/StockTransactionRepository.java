package io.github.ktrzaskoma.repository;

import io.github.ktrzaskoma.model.StockTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockTransactionRepository extends JpaRepository<StockTransaction, Long> {

    List<StockTransaction> findBySymbol(String symbol);
    List<StockTransaction> findByPortfolioId(Long portfolioId);

}

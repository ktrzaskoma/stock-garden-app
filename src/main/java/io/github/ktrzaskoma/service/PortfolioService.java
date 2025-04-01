package io.github.ktrzaskoma.service;

import io.github.ktrzaskoma.model.Portfolio;
import io.github.ktrzaskoma.repository.PortfolioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;

    public PortfolioService(PortfolioRepository portfolioRepository) {
        this.portfolioRepository = portfolioRepository;
    }

    public Portfolio createPortfolio(String name) {
        return portfolioRepository.save(new Portfolio(name));
    }

    public List<Portfolio> getAllPortfolios() {
        return portfolioRepository.findAll();
    }

    public Portfolio getPortfolioById(Long id) {
        return portfolioRepository.findById(id).orElseThrow();
    }
}

package io.github.ktrzaskoma.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Map;

@Service
public class AlphaVantageService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public AlphaVantageService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Value("${alphavantage.api.key}")
    private String apiKey;


    public double getTransactionPrice(String symbol, LocalDateTime dateTime) {
        String url = String.format(
                "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol=%s&interval=5min&apikey=%s",
                symbol, apiKey
        );

        String timpeStampKey = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:00"));


        try {
            JsonNode root = objectMapper.readTree(restTemplate.getForObject(url, String.class));
            JsonNode timeSeries = root.get("Time Series (5min)");

            if (timeSeries.has(timpeStampKey)) {
                return Double.parseDouble(timeSeries.get(timpeStampKey).get("1. open").asText());
            } else {
                Iterator<Map.Entry<String, JsonNode>> it = timeSeries.fields();
                while (it.hasNext()) {
                    Map.Entry<String, JsonNode> entry = it.next();
                    if (entry.getKey().compareTo(timpeStampKey) < 0) {
                        return Double.parseDouble(entry.getValue().get("1. open").asText());
                    }
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Alpha Vantage error!", e);
        }
        throw new RuntimeException("There is no price for chosen date!");
    }


}

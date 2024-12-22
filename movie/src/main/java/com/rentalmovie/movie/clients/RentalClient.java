package com.rentalmovie.movie.clients;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Log4j2
@Component
public class RentalClient {

    private final RestTemplate restTemplate;

    public RentalClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @CircuitBreaker(name = "circuitbreakerInstance")
    public List<UUID> getAllActiveMoviesToUser(String token) {
        log.debug(token);

        String url = "http://rentalmovie-rental-service:8085/rentalmovie-rental/rental";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<String> requestEntity = new HttpEntity<>("parameters", headers);
        log.debug("Request URL: " + url);
        log.info("Request URL: " + url);

        ParameterizedTypeReference<List<UUID>> responseType = new ParameterizedTypeReference<>() {};
        ResponseEntity<List<UUID>> result = restTemplate.exchange(url, HttpMethod.GET, requestEntity, responseType);
        var searchResult = result.getBody();

        log.debug("Response number of elements: {}", searchResult.size());
        log.info("Ending request /rental");
        return searchResult;
    }
}

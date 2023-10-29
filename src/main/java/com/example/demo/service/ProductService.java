package com.example.demo.service;


import com.example.demo.dto.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class ProductService {
    private final RethinkDbService rethinkDbService;
    private final String databaseName;
    private final String tableName;

    public ProductService(RethinkDbService rethinkDbService, @Value("${rethinkdb.database}") String databaseName, @Value("${rethinkdb.table}") String tableName) {
        this.rethinkDbService = rethinkDbService;
        this.databaseName = databaseName;
        this.tableName = tableName;
    }

    public Flux<Product> getAllProducts() {
        return Flux.fromStream(rethinkDbService.getAll(databaseName, tableName).stream());
    }

    public Mono<Product> getProductById(Long id) {
        return Mono.just(rethinkDbService.getById(databaseName, tableName, id));
    }

    public void createProduct(Product product) {
        rethinkDbService.insert(databaseName, tableName, product);
    }
}

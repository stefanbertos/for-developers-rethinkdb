package com.example.demo.service;


import com.example.demo.dto.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private final RethinkDbService rethinkDbService;
    private final String databaseName;
    private final String tableName;

    public ProductService(RethinkDbService rethinkDbService, @Value("${rethinkdb.database}") String databaseName, @Value("${rethinkdb.table}") String tableName) {
        this.rethinkDbService = rethinkDbService;
        this.databaseName = databaseName;
        this.tableName = tableName;
    }

    public List<Product> getAllProducts() {
        return rethinkDbService.getAll(databaseName, tableName);
    }

    public Product getProductById(Long id) {
        return rethinkDbService.getById(databaseName, tableName, id);
    }

    public Product createProduct(Product product) {
        return rethinkDbService.insert(databaseName, tableName, product);
    }
}

package com.example.demo.service;

import com.example.demo.dto.Product;
import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
public class RethinkDbService {
    public static final RethinkDB r = RethinkDB.r;
    private final String host;
    private final Integer port;
    private final String username;
    private final String password;

    public RethinkDbService(@Value("${rethinkdb.host}") String host, @Value("${rethinkdb.port}") Integer port, @Value("${rethinkdb.username}") String username, @Value("${rethinkdb.password}") String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public void createDatabase(String databaseName) {
        try (Connection connection = r.connection().hostname(host).port(port).user(username, password).connect()) {
            try {
                r.dbCreate(databaseName).run(connection).forEach(item -> log.info("Database created {}", item));
            } catch (Exception e) {
                //not a nice way but the checks wouldn't work as well see https://github.com/rethinkdb/rethinkdb/issues/5900
                log.warn("Problem creating database {}",e);
            }
        }
    }

    public void createTable(String databaseName, String tableName) {
        try (Connection connection = r.connection().hostname(host).port(port).user(username, password).connect()) {
            try {
                // you can specify pk, shards, replicas default is just 1 etc see https://rethinkdb.com/api/javascript/table_create
                r.db(databaseName).tableCreate(tableName).optArg("shards", 2).optArg("replicas", 3).run(connection).forEach(item -> log.info("Table created {}", item));
            }catch (Exception e) {
                //not a nice way but the checks wouldn't work as well see https://github.com/rethinkdb/rethinkdb/issues/5900
                log.warn("Problem creating table {}",e);
            }
        }
    }

    public void insert(String databaseName, String tableName, Product data) {
        try (Connection connection = r.connection().hostname(host).port(port).user(username, password).connect()) {
            r.db(databaseName).table(tableName).insert(data).run(connection).forEach(item -> log.info("Data inserted {}", item));
        }
    }

    public void update(String databaseName, String tableName, String id) {
        try (Connection connection = r.connection().hostname(host).port(port).user(username, password).connect()) {
            r.db(databaseName).table(tableName).get(id).run(connection).forEach(item -> log.info("Data updated {}", item));
        }
    }

    public List<Product> getAll(String databaseName, String tableName) {
        try (Connection connection = r.connection().hostname(host).port(port).user(username, password).connect()) {
            return r.db(databaseName).table(tableName).run(connection, Product.class).toList();
        }
    }

    public Product getById(String databaseName, String tableName, Long id) {
        try (Connection connection = r.connection().hostname(host).port(port).user(username, password).connect()) {
            return r.db(databaseName).table(tableName).get(id).run(connection, Product.class).single();
        }
    }

    public void listenToChanges(String databaseName, String tableName) {
        try (Connection connection = r.connection().hostname(host).port(port).user(username, password).connect()) {
            r.db(databaseName).table(tableName).changes().run(connection).forEach(item -> log.info("Change {}", item));
        }
    }
}

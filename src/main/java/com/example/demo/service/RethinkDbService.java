package com.example.demo.service;

import com.example.demo.dto.Product;
import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;


@Service
@Slf4j
public class RethinkDbService {
    public static final RethinkDB r = RethinkDB.r;
    private final String host;
    private final Integer port;
    private final String username;
    private final String password;

    public RethinkDbService(
            @Value("${rethinkdb.host}") String host,
            @Value("${rethinkdb.port}") Integer port,
            @Value("${rethinkdb.username}") String username,
            @Value("${rethinkdb.password}") String password
    ) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    // TODO connection pooling
    private Connection createDbConnection() {
        return r.connection().hostname(host).port(port).user(username, password).connect();
    }

    // TODO transactions
    private <T> T performOperationAndLog(Function<Connection, T> action, String actionDetails, String resourceDetails) {
        return performQueryTask(connection -> {
            T result = action.apply(connection);
            logOperationResult(result, actionDetails, resourceDetails);
            return result;
        });
    }

    private <T> void logOperationResult(T result, String actionDetails, String resourceDetails) {
        Iterable<?> iterableResult = result instanceof Iterable ? (Iterable<?>) result : List.of(result);
        if (iterableResult.iterator().hasNext()) {
            log.info("{}, {}", actionDetails, iterableResult);
        } else {
            log.info("{}, {}", actionDetails, resourceDetails);
        }
    }

    private <T> T performQueryTask(Function<Connection, T> queryTask) {
        try (Connection connection = createDbConnection()) {
            return queryTask.apply(connection);
        } catch (Exception e) {
            logException(e);
            throw new RuntimeException("An error occurred while performing the database operation", e);
        }
    }

    private void logException(Exception e) {
        log.warn("Problem with database operation", e);
    }

    public void createDatabase(String databaseName) {
        //see https://github.com/rethinkdb/rethinkdb/issues/5900
        performOperationAndLog(connection -> r.dbCreate(databaseName).run(connection), "Database created", databaseName);
    }

    public void createTable(String databaseName, String tableName) {
        // you can specify pk, shards, replicas default is just 1 see https://rethinkdb.com/api/javascript/table_create
        // see https://github.com/rethinkdb/rethinkdb/issues/5900
        performOperationAndLog(connection -> r.db(databaseName).tableCreate(tableName).optArg("shards", 2).optArg("replicas", 2).run(connection), "Table created", tableName);
    }

    public Product insert(String databaseName, String tableName, Product data) {
        performOperationAndLog(connection -> r.db(databaseName).table(tableName).insert(data).run(connection), "Data inserted", data.toString());
        return data;
    }

    public List<Product> getAll(String databaseName, String tableName) {
        return performOperationAndLog(connection -> r.db(databaseName).table(tableName).run(connection, Product.class).toList(), "All data fetched", tableName);
    }

    public Product getById(String databaseName, String tableName, Long id) {
        return performOperationAndLog(connection -> r.db(databaseName).table(tableName).get(id).run(connection, Product.class).single(), "Data fetched", Long.toString(id));
    }

    public void listenToChanges(String databaseName, String tableName) {
        performOperationAndLog(connection -> r.db(databaseName).table(tableName).changes().run(connection), "Change detected", tableName);
    }
}

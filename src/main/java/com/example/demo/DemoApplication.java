package com.example.demo;

import com.example.demo.service.RethinkDbService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class DemoApplication {
    private final RethinkDbService rethinkDbService;
    private final String databaseName;
    private final String tableName;

    public DemoApplication(RethinkDbService rethinkDbService, @Value("${rethinkdb.database}") String databaseName, @Value("${rethinkdb.table}") String tableName) {
        this.rethinkDbService = rethinkDbService;
        this.databaseName = databaseName;
        this.tableName = tableName;
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Async
    @EventListener
    void onApplicationEvent(ApplicationStartedEvent event) {
        rethinkDbService.createDatabase(databaseName);
        rethinkDbService.createTable(databaseName, tableName);
        rethinkDbService.listenToChanges(databaseName, tableName);
    }
}

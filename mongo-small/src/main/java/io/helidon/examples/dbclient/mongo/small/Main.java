/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
 */
package io.helidon.examples.dbclient.mongo.small;

import java.util.concurrent.CountDownLatch;

import io.helidon.config.Config;
import io.helidon.dbclient.DbClient;
import io.helidon.dbclient.DbRows;

/**
 * Smallest dependency DB Client.
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {
        DbClient dbClient = DbClient.create(Config.create().get("db"));

        DbClient.builder()
                .config(Config.create().get("db"))
                //TODO where is source cofniguration?
                .build();

        CountDownLatch cdl = new CountDownLatch(1);

        dbClient.execute(exec -> exec.namedQuery("select-all"))
                .thenCompose(DbRows::collect)
                .thenAccept(rows -> rows.forEach(System.out::println))
                .exceptionally(throwable -> {
                    throwable.printStackTrace();
                    return null;
                })
                .handle((nothing, throwable) -> {
                    cdl.countDown();
                    return null;
                });

        // all threads are daemon, need to wait for our request to finish
        cdl.await();
    }
}

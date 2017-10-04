package com.redhat.coolstore.catalog.verticle.service;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.serviceproxy.ProxyHelper;

import java.util.Optional;

public class CatalogVerticle extends AbstractVerticle {

    private CatalogService catalogService;

    private MongoClient mongoClient;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        
        mongoClient = MongoClient.createShared(vertx, config());
        
        //----
        // * Create an instance of `CatalogService`.
        // * Register the catalogService on the event bus
        // * Complete the future
        //----
        mongoClient = MongoClient.createShared(vertx, config());
        catalogService = CatalogService.create(vertx, config(), mongoClient);
        ProxyHelper.registerService(CatalogService.class, vertx, catalogService, CatalogService.ADDRESS);

        startFuture.complete();

    }

    @Override
    public void stop() throws Exception {
        Optional.ofNullable(mongoClient).ifPresent(c -> c.close());
    }

}

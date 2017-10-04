package com.redhat.coolstore.catalog.verticle.service;

import com.redhat.coolstore.catalog.model.Product;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class CatalogServiceImpl implements CatalogService {

    private MongoClient mongoClient;

    public CatalogServiceImpl(Vertx vertx, JsonObject config, MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    @Override
    public void getProducts(Handler<AsyncResult<List<Product>>> resulthandler) {
      System.out.println("#+#+#+# #+#+#+# #+#+#+# getProducts #+#+#+# #+#+#+# #+#+#+#");
      // ----
      // To be implemented
      //
      // Use the `MongoClient.find()` method.
      // Use an empty JSONObject for the query
      // The collection to search is "products"
      // In the handler implementation, transform the `List<JSONObject>` to `List<Person>` - use Java8 Streams!
      // Use a Future to set the result on the handle() method of the result handler
      // Don't forget to handle failures!
        JsonObject query = new JsonObject();

        mongoClient.find("products", query, asyncResult -> {

            if (asyncResult.succeeded()) {
              List<JsonObject> resultList = asyncResult.result();
              // TODO Test, wonder if the cast could work or if forEach is needed
              List<Product> products = resultList.stream().filter(Product.class::isInstance)
                      .map(Product.class::cast)
                      .collect(toList());
              resulthandler.handle(Future.succeededFuture(products));

            } else {
                // TBD
                asyncResult.cause().printStackTrace();
            }

        });
    }

    @Override
    public void getProduct(String itemId, Handler<AsyncResult<Product>> resulthandler) {
      System.out.println("#+#+#+# #+#+#+# #+#+#+# getProduct #+#+#+# #+#+#+# #+#+#+#");
        // ----
        // To be implemented
        // 
        // Use the `MongoClient.find()` method. 
        // Use a JSONObject for the query with the field 'itemId' set to the product itemId
        // The collection to search is "products"
        // In the handler implementation, transform the `List<JSONObject>` to `Person` - use Java8 Streams!
        // If the product is not found, the result should be set to null
        // Use a Future to set the result on the handle() method of the result handler
        // Don't forget to handle failures!
        // ----
       JsonObject query = new JsonObject().put(Product.ITEM_ID_KEY, itemId);

        mongoClient.find("products", query, asyncResult -> {
        // TODO Java 8 streams
        if (asyncResult.succeeded()) {
          List<JsonObject> resultList = asyncResult.result();
          JsonObject resultJsonObject;
          Product product;

          Integer numberOfProducts = resultList.size();
          System.out.println("#+#+#+# #+#+#+# #+#+#+# getProduct - numberOfProducts: " + String.valueOf(numberOfProducts) + " #+#+#+# #+#+#+# #+#+#+#");
          System.out.println("#+#+#+# #+#+#+# #+#+#+# getProduct (numberOfProducts > 1)?:" + (numberOfProducts > 1) + " #+#+#+# #+#+#+# #+#+#+#");
          if (! (numberOfProducts == 1)) {
            throw new AssertionError("Found " + String.valueOf(numberOfProducts) + ", but there should exactly be one. Check your data.");
          } else {
            System.out.println("#+#+#+# #+#+#+# #+#+#+# getProduct else inner #+#+#+# #+#+#+# #+#+#+#");
            resultJsonObject = resultList.get(0);
            product = new Product(resultJsonObject);
            resulthandler.handle(Future.succeededFuture(product));
          }
        } else {
          System.out.println("#+#+#+# #+#+#+# #+#+#+# getProduct else outer #+#+#+# #+#+#+# #+#+#+#");
          asyncResult.cause().printStackTrace();
        }
      });
    }

    @Override
    public void addProduct(Product product, Handler<AsyncResult<String>> resulthandler) {
      System.out.println("#+#+#+# #+#+#+# #+#+#+# addProducts #+#+#+# #+#+#+# #+#+#+#");
        mongoClient.save("products", toDocument(product), resulthandler);
    }

    @Override
    public void ping(Handler<AsyncResult<String>> resultHandler) {
        resultHandler.handle(Future.succeededFuture("OK"));
    }

    private JsonObject toDocument(Product product) {
        JsonObject document = product.toJson();
        document.put("_id", product.getItemId());
        return document;
    }
}

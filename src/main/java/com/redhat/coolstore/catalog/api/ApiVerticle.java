package com.redhat.coolstore.catalog.api;

import com.redhat.coolstore.catalog.model.Product;
import com.redhat.coolstore.catalog.verticle.service.CatalogService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.List;

public class ApiVerticle extends AbstractVerticle {

    private CatalogService catalogService;

    public ApiVerticle(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {

        Router router = Router.router(vertx);
        //----
        // Add routes to the Router
        // * A route for HTTP GET requests that matches the "/products" path.
        //   The handler for this route is implemented by the `getProducts()` method.
        // * A route for HTTP GET requests that matches the /product/:itemId path.
        //   The handler for this route is implemented by the `getProduct()` method.
        // * A route for the path "/product" to which a `BodyHandler` is attached.
        // * A route for HTTP POST requests that matches the "/product" path.
        //   The handler for this route is implemented by the `addProduct()` method.
        //----
        router.get("/products").handler(this::getProducts);
        router.get("/product/:itemId").handler(this::getProduct);
        router.route("/product").handler(BodyHandler.create());
        router.post("/product").handler(this::addProduct);

        //----
        // Create a HTTP server.
        // * Use the `Router` as request handler
        // * Use the verticle configuration to obtain the port to listen to.
        //   Get the configuration from the `config()` method of AbstractVerticle.
        //   Look for the key "catalog.http.host", which returns an Integer.
        //   The default value (if the key is not set in the configuration) is 8080.
        //  * If the HTTP server is correctly instantiated, complete the `Future`. If there is a failure, fail the `Future`.
        //----

        vertx.createHttpServer()
             .requestHandler(router::accept)
             .listen(config().getInteger("catalog.http.port", 8080), result -> {
                 if (result.succeeded()) {
                     startFuture.complete();
                 } else {
                     startFuture.fail(result.cause());
                 }
             });
    }

    private void getProducts(RoutingContext routingContext) {
        //----
        // Needs to be implemented
        // In the implementation:
        // * Call the `getProducts()` method of the CatalogService.
        // * In the handler, transform the `List<Product>` response to a `JsonArray` object.
        // * Put a "Content-type: application/json" header on the `HttpServerResponse` object. 
        // * Write the `JsonArray` to the `HttpServerResponse`, and end the response.
        // * If the `getProducts()` method returns a failure, fail the `RoutingContext`.
        //----
        catalogService.getProducts(asyncResult -> {
            if (asyncResult.succeeded()) {
                List<Product> products = asyncResult.result();
                JsonArray jsonArray = new JsonArray();
                products.stream()
                        .map(Product::toJson)
                        .forEach(jsonArray::add);
                routingContext.response()
                  .putHeader("Content-type", "application/json")
                  .end(jsonArray.encodePrettily());
            } else {
                routingContext.response().setStatusCode(404);
                routingContext.fail(asyncResult.cause());
            }
        });


    }

    private void getProduct(RoutingContext routingContext) {
        //----
        // Needs to be implemented
        // In the implementation:
        // * Call the `getProduct()` method of the CatalogService.
        // * In the handler, transform the `Product response to a `JsonObject` object.
        // * Put a "Content-type: application/json" header on the `HttpServerResponse` object. 
        // * Write the `JsonObject` to the `HttpServerResponse`, and end the response.
        // * If the `getProduct()` method of the CatalogService returns null,  fail the RoutingContext with a 404 HTTP status code 
        // * If the `getProduct()` method returns a failure, fail the `RoutingContext`.
        //----
        String itemId = routingContext.request().getParam("itemid");
        catalogService.getProduct(itemId, ar -> {
            if (ar.succeeded()) {
                Product product = ar.result();
                if (product != null) {
                    routingContext.response()
                      .putHeader("Content-type", "application/json")
                      .end(product.toJson().encodePrettily());
                }
            } else {
                routingContext.response().setStatusCode(404);
                routingContext.fail(ar.cause());
            }
        });

    }

    private void addProduct(RoutingContext routingContext) {
        //----
        // Needs to be implemented
        // In the implementation:
        // * Obtain the body contents from the `RoutingContext`. Expect the body to be JSON.
        // * Transform the JSON payload to a `Product` object.
        // * Call the `addProduct()` method of the CatalogService. 
        // * If the call succeeds, set a HTTP status code 201 on the `HttpServerResponse`, and end the response. 
        // * If the call fails, fail the `RoutingContext`.
        //----
        JsonObject jsonObject = routingContext.getBodyAsJson();
        catalogService.addProduct(new Product(jsonObject), ar -> {
            if (ar.succeeded()) {
                routingContext.response().setStatusCode(201).end();
            } else {
                routingContext.response().setStatusCode(404);
                routingContext.fail(ar.cause());
            }
        });

    }
}

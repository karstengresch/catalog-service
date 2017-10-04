package com.redhat.coolstore.catalog.verticle.service;

import com.redhat.coolstore.catalog.model.Product;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(VertxUnitRunner.class)
public class CatalogServiceTest extends MongoTestBase {

    private Vertx vertx;

    private List<Product> getProductList()
    {
        Product product1 = new Product("product1Id", "product1", "product1 description", 12.55);
        Product product2 = new Product("product2Id", "product2", "product2 description", 700.12);
        Product product3 = new Product("product3Id", "product3", "product3 description", 20.01);
        List<Product> products = new CopyOnWriteArrayList<>(Arrays.asList(product1, product2, product3));
        return products;
    }

    @Before
    public void setup(TestContext context) throws Exception {
        vertx = Vertx.vertx();
        vertx.exceptionHandler(context.exceptionHandler());
        JsonObject config = getConfig();
        mongoClient = MongoClient.createNonShared(vertx, config);
        Async async = context.async();
        dropCollection(mongoClient, "products", async, context);
        async.await(10000);
    }

    @After
    public void tearDown() throws Exception {
        mongoClient.close();
        vertx.close();
    }

    @Test
    public void testAddProduct(TestContext context) throws Exception {
        String itemId = "999999";
        String name = "productName";
        Product product = new Product();
        product.setItemId(itemId);
        product.setName(name);
        product.setDesc("productDescription");
        product.setPrice(100.0);

        CatalogService service = new CatalogServiceImpl(vertx, getConfig(), mongoClient);

        Async async = context.async();

        service.addProduct(product, asyncResult -> {
            if (asyncResult.failed()) {
                context.fail(asyncResult.cause().getMessage());
            } else {
                JsonObject query = new JsonObject().put("_id", itemId);
                mongoClient.findOne("products", query, null, ar1 -> {
                    if (ar1.failed()) {
                        context.fail(ar1.cause().getMessage());
                    } else {
                        assertThat(ar1.result().getString("name"), equalTo(name));
                        async.complete();
                    }
                });
            }
        });
    }

    @Test
    public void testGetProducts(TestContext context) throws Exception {
        // ----
        // To be implemented
        // 
        // In your test:
        // -Insert two or more products in MongoDB. Use the `MongoClient.save` method to do so.
        // - Retrieve the products from Mongo using the `testGetProducts` method.
        // - Verify that no failures happened, 
        //   that the number of products retrieved corresponds to the number inserted, 
        //   and that the product values match what was inserted.
        // 
        // ----
        List<Product> products = getProductList();

        CatalogService catalogService = new CatalogServiceImpl(vertx, getConfig(), mongoClient);

        Async asyncContext = context.async();

        // TODO streams ;)
        for (Product product : products) {
            catalogService.addProduct(product, asyncResult -> {
                if (asyncResult.failed()) {
                    context.fail(asyncResult.cause().getMessage());
                } else {
                    JsonObject query = new JsonObject().put("_id", product.getItemId());
                    mongoClient.findOne("products", query, null, ar1 -> {
                        if (ar1.failed()) {
                            context.fail(ar1.cause().getMessage());
                        } else {
                            assertThat(ar1.result().getString("name"), equalTo(product.getName()));

                        }
                    });
                }
            });
        }

        JsonObject query = new JsonObject();

        catalogService.getProducts(asyncResult1 -> {
            if (asyncResult1.failed()) {
                context.fail(asyncResult1.cause().getMessage());
            } else {

                mongoClient.find("products", query, asyncResult2 -> {

                    if (asyncResult2.succeeded()) {
                        List<JsonObject> resultList = asyncResult2.result();
                        // TODO Test, wonder if the cast could work or if forEach is needed
                        List<Product> productsFound = resultList.stream().filter(Product.class::isInstance)
                                                           .map(Product.class::cast)
                                                           .collect(toList());
                        Assert.assertEquals(productsFound.size(), products.size());

                    } else {
                        // TBD
                        asyncResult2.cause().printStackTrace();
                    }

                });
            }
        });

        asyncContext.complete();
    }

    @Test
    public void testGetProduct(TestContext context) throws Exception {
        // ----
        // To be implemented
        // 
        // ----
        List<Product> products = getProductList();
        CatalogService catalogService = new CatalogServiceImpl(vertx, getConfig(), mongoClient);

        Async asyncContext = context.async();

        String itemId = products.get(1).getItemId();
        String name = products.get(1).getName();

        catalogService.getProduct(itemId, ar -> {
            if (ar.failed()) {
                context.fail(ar.cause().getMessage());
            } else {
                JsonObject query = new JsonObject().put("_id", itemId);
                mongoClient.findOne("products", query, null, ar1 -> {
                    if (ar1.failed()) {
                        context.fail(ar1.cause().getMessage());
                    } else {
                        assertThat(ar1.result().getString("name"), equalTo(name));
                    }
                });
            }
        });

        asyncContext.complete();
    }

//    @Test
    public void testGetNonExistingProduct(TestContext context) throws Exception {
        // ----
        // To be implemented
        // 
        // ----
    }

//    @Test
    public void testPing(TestContext context) throws Exception {
        CatalogService service = new CatalogServiceImpl(vertx, getConfig(), mongoClient);
        
        Async async = context.async();
        service.ping(ar -> {
            assertThat(ar.succeeded(), equalTo(true));
            async.complete();
        });
    }

}

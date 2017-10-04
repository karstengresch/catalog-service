package com.redhat.coolstore.catalog.api;

import com.redhat.coolstore.catalog.model.Product;
import com.redhat.coolstore.catalog.verticle.service.CatalogService;
import io.vertx.core.*;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(VertxUnitRunner.class)
public class ApiVerticleTest {

    private Vertx vertx;
    private Integer port;
    private CatalogService catalogService;

    /**
     * Before executing our test, let's deploy our verticle.
     * <p/>
     * This method instantiates a new Vertx and deploy the verticle. Then, it waits in the verticle has successfully
     * completed its start sequence (thanks to `context.asyncAssertSuccess`).
     *
     * @param context the test context.
     */
    @Before
    public void setUp(TestContext context) throws IOException {
      vertx = Vertx.vertx();

      // Register the context exception handler
      vertx.exceptionHandler(context.exceptionHandler());

      // Let's configure the verticle to listen on the 'test' port (randomly picked).
      // We create deployment options and set the _configuration_ json object:
      ServerSocket serverSocket = new ServerSocket(0);
      port = serverSocket.getLocalPort();
      serverSocket.close();

      DeploymentOptions deploymentOptions = new DeploymentOptions().setConfig(new JsonObject().put("catalog.http.port", port));

      //Mock the catalog Service
      catalogService = mock(CatalogService.class);

      // We pass the options as the second parameter of the deployVerticle method.
      vertx.deployVerticle(new ApiVerticle(catalogService), deploymentOptions, context.asyncAssertSuccess());
    }

    /**
     * This method, called after our test, just cleanup everything by closing
     * the vert.x instance
     *
     * @param context
     *            the test context
     */
    @After
    public void tearDown(TestContext context) {
      vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void testGetProducts(TestContext context) throws Exception {
        //----
        // To be implemented
        //
        // * Stub the `getProducts()` method of `CatalogService` mock to return a `List<Product>`
        // * Use the Vert.x Web client to execute a GET request to the "/products" endpoint.
        //   Use the `getNow()` method of the HTTP client.
        // * Verify that the return code of the request equal to 200,
        //   and that the response has a header "Content-type: application/json".
        // * Use the `BodyHandler` method of the `HttpClientResponse` object to obtain and verify the response body.
        //
        //----
      String itemId1 = "product1Id";
      JsonObject json1 = new JsonObject()
          .put("itemId", itemId1)
          .put("name", "productName1")
          .put("desc", "productDescription1")
          .put("price", new Double(100.0));
      String itemId2 = "product2Id";
      JsonObject json2 = new JsonObject()
          .put("itemId", itemId2)
          .put("name", "productName2")
          .put("desc", "productDescription2")
          .put("price", new Double(100.0));
      List<Product> products = new ArrayList<>();
      products.add(new Product(json1));
      products.add(new Product(json2));
      doAnswer(new Answer<Void>() {
        public Void answer(InvocationOnMock invocation){
          Handler<AsyncResult<List<Product>>> handler = invocation.getArgument(0);
          handler.handle(Future.succeededFuture(products));
          return null;
        }
      }).when(catalogService).getProducts(any());

      Async async = context.async();
      vertx.createHttpClient().get(port, "localhost", "/products", response -> {
        assertThat(response.statusCode(), equalTo(200));
        assertThat(response.headers().get("Content-type"), equalTo("application/json"));
        response.bodyHandler(body -> {
          JsonArray json = body.toJsonArray();
          Set<String> itemIds =  json.stream()
                                     .map(j -> new Product((JsonObject)j))
                                     .map(p -> p.getItemId())
                                     .collect(Collectors.toSet());
          assertThat(itemIds.size(), equalTo(2));
          assertThat(itemIds, allOf(hasItem(itemId1),hasItem(itemId2)));
          verify(catalogService).getProducts(any());
          async.complete();
        })
                .exceptionHandler(context.exceptionHandler());
      })
           .exceptionHandler(context.exceptionHandler())
           .end();
    }


    //@Test
    public void testGetProduct(TestContext context) throws Exception {
        //----
        // To be implemented
        //
        //----
      String itemId = "product1Id";
      JsonObject json = new JsonObject()
          .put("itemId", itemId)
          .put("name", "productName1")
          .put("desc", "productDescription1")
          .put("price", new Double(100.0));
      Product product = new Product(json);
      doAnswer(new Answer<Void>() {
        public Void answer(InvocationOnMock invocation){
          Handler<AsyncResult<Product>> handler = invocation.getArgument(1);
          handler.handle(Future.succeededFuture(product));
          return null;
        }
      }).when(catalogService).getProduct(eq("product1Id"),any());

      Async async = context.async();
      vertx.createHttpClient().get(port, "localhost", "/product/product1Id", response -> {
        assertThat(response.statusCode(), equalTo(200));
        assertThat(response.headers().get("Content-type"), equalTo("application/json"));
        response.bodyHandler(body -> {
          JsonObject result = body.toJsonObject();
          assertThat(result, notNullValue());
          // TODO is (Hamcrest) not resolved
          // assertThat(result.containsKey("itemId"), is(true));
          assertThat(result.getString("itemId"), equalTo("product1Id"));
          verify(catalogService).getProduct(eq("product1Id"),any());
          async.complete();
        })
                .exceptionHandler(context.exceptionHandler());
      })
           .exceptionHandler(context.exceptionHandler())
           .end();


    }

    //@Test
    public void testGetNonExistingProduct(TestContext context) throws Exception {
        //----
        // To be implemented
        //
        //----
      doAnswer(new Answer<Void>() {
        public Void answer(InvocationOnMock invocation){
          Handler<AsyncResult<Product>> handler = invocation.getArgument(1);
          handler.handle(Future.succeededFuture(null));
          return null;
        }
      }).when(catalogService).getProduct(eq("111111"),any());

      Async async = context.async();
      vertx.createHttpClient().get(port, "localhost", "/product/111111", response -> {
        assertThat(response.statusCode(), equalTo(404));
        async.complete();
      })
           .exceptionHandler(context.exceptionHandler())
           .end();
    }

    //@Test
    public void testAddProduct(TestContext context) throws Exception {
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation){
                Handler<AsyncResult<String>> handler = invocation.getArgument(1);
                handler.handle(Future.succeededFuture(null));
                return null;
             }
         }).when(catalogService).addProduct(any(),any());

        Async async = context.async();
        String itemId = "111111";
        JsonObject json = new JsonObject()
                .put("itemId", itemId)
                .put("name", "productName")
                .put("desc", "productDescription")
                .put("price", new Double(100.0));
        String body = json.encodePrettily();
        String length = Integer.toString(body.length());
        vertx.createHttpClient().post(port, "localhost", "/product")
            .exceptionHandler(context.exceptionHandler())
            .putHeader("Content-type", "application/json")
            .putHeader("Content-length", length)
            .handler(response -> {
                assertThat(response.statusCode(), equalTo(201));
                ArgumentCaptor<Product> argument = ArgumentCaptor.forClass(Product.class);
                verify(catalogService).addProduct(argument.capture(), any());
                assertThat(argument.getValue().getItemId(), equalTo(itemId));
                async.complete();
            })
            .write(body)
            .end();
    }

}

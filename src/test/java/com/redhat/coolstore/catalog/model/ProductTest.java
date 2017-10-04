package com.redhat.coolstore.catalog.model;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Karsten Gresch on 04.10.17.
 */
public class ProductTest
{
  private Vertx vertx;

  // @Before
  public void setup(TestContext context) throws Exception {
    vertx = Vertx.vertx();
    vertx.exceptionHandler(context.exceptionHandler());
  }

  @Test
  public void testCreateProductFromJson() throws Exception {

    String jsonString = "{ \"itemId\" : \"329199\", \"name\" : \"Forge Laptop Sticker\", \"desc\" : \"JBoss Community Forge Project Sticker\", \"price\" : 8.50 }";
    JsonObject jsonObject = new JsonObject(jsonString);
    Product product = new Product(jsonObject);
    Assert.assertNotNull(product);


  }

  @Test
  public void testGetJsonFromProduct() throws Exception {
    // TBD trailing zero problem w/ double val
    String jsonString = "{ \"itemId\" : \"329199\", \"name\" : \"Forge Laptop Sticker\", \"desc\" : \"JBoss Community Forge Project Sticker\", \"price\" : 8.51 }".replace(" ", "");
    JsonObject jsonObject = new JsonObject(jsonString);
    Product product = new Product(jsonObject);
    String fromProductJsonString = product.toJson().toString();
    Assert.assertTrue(jsonString.equals(fromProductJsonString));



  }



}

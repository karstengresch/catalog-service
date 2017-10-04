package com.redhat.coolstore.catalog.model;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.io.Serializable;

@DataObject
public class Product implements Serializable {

    private static final long serialVersionUID = -6994655395272795259L;
    
    private String itemId;
    private String name;
    private String desc;
    private double price;

    public static final String ITEM_ID_KEY = "itemId";
    public static final String NAME_KEY = "name";
    public static final String DESC_KEY = "desc";
    public static final String PRICE_KEY = "price";
    
    public Product() {
        
    }
    
    //-----
    // Add a constructor which takes a JSON object as parameter. 
    // The JSON representation of the Product class is:
    // 
    //  {
    //    "itemId" : "329199",
    //    "name" : "Forge Laptop Sticker",
    //    "desc" : "JBoss Community Forge Project Sticker",
    //    "price" : 8.50
    //  }
    //
    //-----

    public Product(String itemId, String name, String desc, Double price) {
        assert (!itemId.isEmpty());

        this.itemId = itemId;
        this.name = name;
        this.desc = desc;
        this.price = price;
    }



    public Product(JsonObject jsonObject) {
        this.itemId = jsonObject.getString(ITEM_ID_KEY);
        this.name = jsonObject.getString(NAME_KEY);
        this.desc= jsonObject.getString(DESC_KEY);
        this.price= jsonObject.getDouble(PRICE_KEY);

        // TBD mapTo approach?

    }
    
    public String getItemId() {
        return itemId;
    }
    
    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDesc() {
        return desc;
    }
    
    public void setDesc(String desc) {
        this.desc = desc;
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    //-----
    // Implement the toJson method which returns a JsonObject representing this instance. 
    // The JSON representation of the Product class is:
    // 
    //  {
    //    "itemId" : "329199",
    //    "name" : "Forge Laptop Sticker",
    //    "desc" : "JBoss Community Forge Project Sticker",
    //    "price" : 8.50
    //  }
    //
    //-----
    public JsonObject toJson() {

        JsonObject jsonObject = new JsonObject();

        jsonObject.put(ITEM_ID_KEY, itemId)
                  .put(NAME_KEY, name)
                  .put(DESC_KEY, desc)
                  .put(PRICE_KEY, price);

        return jsonObject;
    }
}

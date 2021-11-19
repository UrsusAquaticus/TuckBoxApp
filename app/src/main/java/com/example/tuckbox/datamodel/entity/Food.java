package com.example.tuckbox.datamodel.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Entity(
        tableName = "FoodItems"
)
public class Food extends BaseEntity {

    //todo: load images from url

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "food_id")
    Long id;
    String name;
    String description;
    String category;
    @ColumnInfo(name = "image_id")
    int imgId;
    double price;

    @Exclude
    final public static String COLLECTION = "FOOD_ITEMS";
//    final public static String NAME = "NAME";
//    final public static String DESCRIPTION = "DESCRIPTION";
//    final public static String CATEGORY = "CATEGORY";
//    final public static String IMAGE_ID = "IMAGE_ID";
//    final public static String PRICE = "PRICE";

    @Ignore
    public Food() {
    }

    public Food(Long id, String name, String description, String category, int imgId, double price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.imgId = imgId;
        this.price = price;
    }

    @Exclude
    @Override
    public String getDataCollection() {
        return COLLECTION;
    }
//
//    @Override
//    public Map<String, Object> getMap() {
//        Map<String, Object> map = new HashMap<>();
//        map.put(ENTITY_ID, this.id);
//        map.put(NAME, this.name);
//        map.put(DESCRIPTION, this.description);
//        map.put(CATEGORY, this.category);
//        map.put(IMAGE_ID, this.imgId);
//        map.put(PRICE, this.price);
//        return map;
//    }
//
//    @Override
//    public Food setEntityFromDoc(QueryDocumentSnapshot doc) {
//        //Create from firebase doc
//        Map<String, Object> map = doc.getData();
//        this.id = (Long) map.get(ENTITY_ID);
//        this.name = (String) map.get(NAME);
//        this.description = (String) map.get(DESCRIPTION);
//        this.category = (String) map.get(CATEGORY);
//        this.imgId = (int) map.get(IMAGE_ID);
//        this.price = (double) map.get(PRICE);
//        return this;
//    }

    @Exclude
    public String getPriceString() {
        NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
        return nf.format(price);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NonNull
    @Override
    public String toString() {
        return this.name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}

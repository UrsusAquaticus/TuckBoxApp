package com.example.tuckbox.datamodel.entity;

import static androidx.room.ForeignKey.CASCADE;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Entity(
        tableName = "FoodOptions",
        foreignKeys = {
                @ForeignKey(
                        entity = Food.class,
                        parentColumns = "food_id",
                        childColumns = "food_id",
                        onDelete = CASCADE
                ),
        }
)
public class FoodOption extends BaseEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "food_option_id")
    Long id;
    @ColumnInfo(name = "food_id", index = true)
    Long foodId;
    String name;
    String category;
    double price;

    @Exclude
    final public static String COLLECTION = "FOOD_OPTIONS";
//    final public static String FOOD_ID = "FOOD_ID";
//    final public static String NAME = "NAME";
//    final public static String CATEGORY = "CATEGORY";
//    final public static String PRICE = "PRICE";

    @Ignore
    public FoodOption() {
    }

    public FoodOption(Long id, Long foodId, String name, String category, double price) {
        this.id = id;
        this.foodId = foodId;
        this.name = name;
        this.category = category;
        this.price = price;
    }

    @Exclude
    @Override
    public String getDataCollection() {
        return COLLECTION;
    }

//    @Override
//    public Map<String, Object> getMap() {
//        Map<String, Object> map = new HashMap<>();
//        map.put(ENTITY_ID, this.id);
//        map.put(FOOD_ID, this.foodId);
//        map.put(NAME, this.name);
//        map.put(CATEGORY, this.category);
//        map.put(PRICE, this.price);
//        return map;
//    }
//
//    @Override
//    public FoodOption setEntityFromDoc(QueryDocumentSnapshot doc) {
//        //Create from firebase doc
//        Map<String, Object> map = doc.getData();
//        this.id = (Long) map.get(ENTITY_ID);
//        this.foodId = (Long) map.get(FOOD_ID);
//        this.name = (String) map.get(NAME);
//        this.category = (String) map.get(CATEGORY);
//        this.price = (double) map.get(PRICE);
//        return this;
//    }

    @NonNull
    @Override
    public String toString() {
        return category + ": " + name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFoodId() {
        return foodId;
    }

    public void setFoodId(Long foodId) {
        this.foodId = foodId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}

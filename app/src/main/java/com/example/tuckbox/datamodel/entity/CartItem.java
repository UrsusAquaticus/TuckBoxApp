package com.example.tuckbox.datamodel.entity;

import static androidx.room.ForeignKey.CASCADE;
import static androidx.room.ForeignKey.RESTRICT;
import static androidx.room.ForeignKey.SET_NULL;

import android.util.Log;

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
        tableName = "CartItems",
        foreignKeys = {
                @ForeignKey(
                        entity = Order.class,
                        parentColumns = "order_id",
                        childColumns = "order_id",
                        onDelete = CASCADE
                ),
                @ForeignKey(
                        entity = FoodOption.class,
                        parentColumns = "food_option_id",
                        childColumns = "food_option_id",
                        onDelete = SET_NULL
                ),
        }
)
public class CartItem extends BaseEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "cart_item_id")
    Long id;
    @ColumnInfo(name = "order_id", index = true)
    Long orderId;
    @ColumnInfo(name = "food_option_id", index = true)
    Long foodOptionId;
    @ColumnInfo(name = "is_complete", index = true)
    boolean isComplete;
    String notes;
    int amount;

    @Exclude
    final public static String COLLECTION = "CART_ITEMS";
//    final static String ORDER_ID = "ORDER_ID";
//    final static String FOOD_OPTION_ID = "FOOD_OPTION_ID";
//    final static String IS_COMPLETE = "IS_COMPLETE";
//    final static String NOTES = "NOTES";
//    final static String AMOUNT = "AMOUNT";

    @Ignore
    public CartItem() {
    }

    public CartItem(Long id, Long orderId, Long foodOptionId, boolean isComplete, String notes, int amount) {
        this.id = id;
        this.orderId = orderId;
        this.foodOptionId = foodOptionId;
        this.isComplete = isComplete;
        this.notes = notes;
        this.amount = amount;
        Log.d("CART_ITEM", toString());
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
//        map.put(ORDER_ID, this.orderId);
//        map.put(FOOD_OPTION_ID, this.foodOptionId);
//        map.put(IS_COMPLETE, this.isComplete);
//        map.put(NOTES, this.notes);
//        map.put(AMOUNT, this.amount);
//        return map;
//    }

//    @Override
//    public CartItem setEntityFromDoc(QueryDocumentSnapshot doc) {
//        //Create from firebase doc
//        Map<String, Object> map = doc.getData();
//        this.id = (Long) map.get(ENTITY_ID);
//        this.orderId = (Long) map.get(ORDER_ID);
//        this.foodOptionId = (Long) map.get(FOOD_OPTION_ID);
//        this.isComplete = (boolean) map.get(IS_COMPLETE);
//        this.notes = (String) map.get(NOTES);
//        this.amount = (int) map.get(AMOUNT);
//        return this;
//    }

    @Exclude
    public void addAmount() {
        this.amount++;
    }

    @Exclude
    public void reduceAmount() {
        this.amount--;
    }

    @Exclude
    public String getAmountString() {
        return String.valueOf(amount);
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
        return this.id + "/" + this.orderId + "/" + this.foodOptionId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getFoodOptionId() {
        return foodOptionId;
    }

    public void setFoodOptionId(Long foodOptionId) {
        this.foodOptionId = foodOptionId;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}

package com.example.tuckbox.datamodel.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.tuckbox.datamodel.entity.Address;
import com.example.tuckbox.datamodel.entity.CartItem;
import com.example.tuckbox.datamodel.entity.Food;
import com.example.tuckbox.datamodel.relations.CartItemWithFoodOption;

import java.util.ArrayList;
import java.util.List;

@Dao
public abstract class CartItemDao extends BaseDao<CartItem> {
    @Transaction
    @Query("Select * From CartItems Where CartItems.is_complete = 0")
    abstract public LiveData<List<CartItemWithFoodOption>> getLiveAllCartItems();

    @Query("Select * From CartItems " +
            "Where CartItems.food_option_id = :optionId And " +
            "CartItems.is_complete = 0")
    abstract public CartItem getCartItemByFoodOptionId(long optionId);

    @Query("Delete From CartItems")
    abstract public void nukeTable();

    @Query("Select * from CartItems")
    abstract public List<CartItem> getAll();

    @Query("DELETE FROM cartItems WHERE cart_item_id " +
            "IN (SELECT cart_item_id FROM cartItems " +
            "WHERE cart_item_id NOT IN (:ids))")
    abstract public int deleteUnused(List<Long> ids);

    @Query("UPDATE CartItems " +
            "SET cart_item_id = :newId " +
            "WHERE cart_item_id = :oldId")
    abstract public int updateId(long oldId, long newId);

    //Combine upsert and delsert
    //https://www.py4u.net/discuss/668276
    @Transaction
    public void delupsert(List<CartItem> cartItems) {
        List<Long> ids = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            ids.add(cartItem.getId());
        }
        deleteUnused(ids);
        upsert(cartItems);
    }
}

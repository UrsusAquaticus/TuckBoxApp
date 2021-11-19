package com.example.tuckbox.datamodel.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.tuckbox.datamodel.entity.Address;
import com.example.tuckbox.datamodel.entity.Food;
import com.example.tuckbox.datamodel.relations.FoodWithOptions;

import java.util.ArrayList;
import java.util.List;

@Dao
public abstract class FoodDao extends BaseDao<Food> {

    @Query("Select * from FoodItems")
    abstract public LiveData<List<Food>> getLiveAll();

    @Query("Select * from FoodItems")
    abstract public List<Food> getAll();

    @Transaction
    @Query("Select * from FoodItems")
    abstract public LiveData<List<FoodWithOptions>> getFoodWithOptions();

    @Query("Delete From FoodItems")
    abstract public void nukeTable();

    @Query("DELETE FROM foodItems WHERE food_id " +
            "IN (SELECT food_id FROM foodItems " +
            "WHERE food_id NOT IN (:ids))")
    abstract public int deleteUnused(List<Long> ids);

    //Combine upsert and delsert
    //https://www.py4u.net/discuss/668276
    @Transaction
    public void delupsert(List<Food> foodItems) {
        List<Long> ids = new ArrayList<>();
        for (Food food : foodItems) {
            ids.add(food.getId());
        }
        deleteUnused(ids);
        upsert(foodItems);
    }

}

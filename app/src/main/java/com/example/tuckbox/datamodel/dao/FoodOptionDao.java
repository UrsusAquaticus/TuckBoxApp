package com.example.tuckbox.datamodel.dao;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.tuckbox.datamodel.entity.Address;
import com.example.tuckbox.datamodel.entity.FoodOption;

import java.util.ArrayList;
import java.util.List;

@Dao
public abstract class FoodOptionDao extends BaseDao<FoodOption> {
    @Query("Select * from FoodOptions")
    abstract public List<FoodOption> getAll();

    @Query("Delete From FoodOptions")
    abstract public void nukeTable();

    @Query("DELETE FROM foodOptions WHERE food_option_id " +
            "IN (SELECT food_option_id FROM foodOptions " +
            "WHERE food_option_id NOT IN (:ids))")
    abstract public int deleteUnused(List<Long> ids);

    //Combine upsert and delsert
    //https://www.py4u.net/discuss/668276
    @Transaction
    public void delupsert(List<FoodOption> foodOptions) {
        List<Long> ids = new ArrayList<>();
        for (FoodOption foodOption : foodOptions) {
            ids.add(foodOption.getId());
        }
        deleteUnused(ids);
        upsert(foodOptions);
    }

}

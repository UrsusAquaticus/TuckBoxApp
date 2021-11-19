package com.example.tuckbox.datamodel.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.tuckbox.datamodel.entity.Order;
import com.example.tuckbox.datamodel.entity.Store;

import java.util.ArrayList;
import java.util.List;

@Dao
public abstract class StoreDao extends BaseDao<Store> {
    @Query("Select * from Stores")
    abstract public LiveData<List<Store>> getLiveAll();

    @Query("Delete From Stores")
    abstract public void nukeTable();

    @Query("DELETE FROM stores WHERE store_id " +
            "IN (SELECT store_id FROM stores " +
            "WHERE store_id NOT IN (:ids))")
    abstract public int deleteUnused(List<Long> ids);

    //Combine upsert and delsert
    //https://www.py4u.net/discuss/668276
    @Transaction
    public void delupsert(List<Store> stores) {
        List<Long> ids = new ArrayList<>();
        for (Store store : stores) {
            ids.add(store.getId());
        }
        deleteUnused(ids);
        upsert(stores);
    }
}

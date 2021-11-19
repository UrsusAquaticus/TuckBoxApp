package com.example.tuckbox.datamodel.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.tuckbox.datamodel.entity.Address;
import com.example.tuckbox.datamodel.entity.Food;
import com.example.tuckbox.datamodel.entity.Timeslot;

import java.util.ArrayList;
import java.util.List;

@Dao
public abstract class AddressDao extends BaseDao<Address> {
    @Query("Select * from Addresses " +
            "Where user_id = :userId")
    abstract public LiveData<List<Address>> getLiveAddressesByUserId(long userId);

    @Query("Delete From Addresses")
    abstract public void nukeTable();

    @Query("DELETE FROM addresses WHERE address_id " +
            "IN (SELECT address_id FROM addresses " +
            "WHERE address_id NOT IN (:ids))")
    abstract public int deleteUnused(List<Long> ids);

    //Combine upsert and delsert
    //https://www.py4u.net/discuss/668276
    @Transaction
    public void delupsert(List<Address> addresses) {
        List<Long> ids = new ArrayList<>();
        for (Address address : addresses) {
            ids.add(address.getId());
        }
        deleteUnused(ids);
        upsert(addresses);
    }
}

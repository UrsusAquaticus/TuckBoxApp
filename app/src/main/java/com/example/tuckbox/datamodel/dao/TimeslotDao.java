package com.example.tuckbox.datamodel.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.tuckbox.datamodel.entity.Order;
import com.example.tuckbox.datamodel.entity.Timeslot;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

@Dao
public abstract class TimeslotDao extends BaseDao<Timeslot> {
    @Query("Select * from Timeslots")
    abstract public LiveData<List<Timeslot>> getLiveAll();

    @Query("Delete From Timeslots")
    abstract public void nukeTable();

    @Query("DELETE FROM timeslots WHERE timeslot_id " +
            "IN (SELECT timeslot_id FROM timeslots " +
            "WHERE timeslot_id NOT IN (:ids))")
    abstract public int deleteUnused(List<Long> ids);

    //Combine upsert and delsert
    //https://www.py4u.net/discuss/668276
    @Transaction
    public void delupsert(List<Timeslot> timeslots) {
        List<Long> ids = new ArrayList<>();
        for (Timeslot timeslot : timeslots) {
            ids.add(timeslot.getId());
        }
        deleteUnused(ids);
        upsert(timeslots);
    }
}

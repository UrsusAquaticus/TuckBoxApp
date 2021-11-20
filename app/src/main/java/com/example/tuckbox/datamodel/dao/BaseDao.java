package com.example.tuckbox.datamodel.dao;

import android.util.Log;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.tuckbox.datamodel.entity.BaseEntity;

import java.util.ArrayList;
import java.util.List;

@Dao
public abstract class BaseDao<T> {
    //Upsert reference
    //https://www.py4u.net/discuss/668276

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long insert(T obj);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long[] insert(List<T> objs);

    @Update
    public abstract int update(T obj);

    @Update
    public abstract int update(List<T> objs);

    @Delete
    public abstract int delete(T obj);

    @Delete
    public abstract int delete(List<T> objs);

    @Transaction
    public long upsert(T obj) {
        //attempt to insert
        long id = insert(obj);
        if (id == -1) {
            //try update if insert failed
            try {
                update(obj);
            } catch (Exception e) {
                Log.e("BASEDAO", "It Broke", e);
            }
        }
        return id;
    }

    @Transaction
    public long[] upsert(List<T> objs) {
        //attempt to insert
        long[] insertResults = insert(objs);
        List<T> updateList = new ArrayList<>();
        //Add failed inserts to list
        for (int i = 0; i < insertResults.length; i++) {
            if (insertResults[i] == -1) {
                updateList.add(objs.get(i));
            }
        }
        //Try update failed inserts
        if (!updateList.isEmpty()) {
            update(updateList);
        }
        return insertResults;
    }
}

package com.example.tuckbox.datamodel.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.tuckbox.datamodel.entity.Order;
import com.example.tuckbox.datamodel.entity.User;

import java.util.ArrayList;
import java.util.List;

@Dao
public abstract class UserDao extends BaseDao<User> {

    //Should only contain logged in userdata
    @Query("Select * from Users")
    abstract public List<User> getAll();

    @Query("Select * from Users where email like :email")
    abstract public List<User> getUsersByEmail(String email);

    @Query("Select * from Users where email like :email and password like :password ")
    abstract public List<User> getUsersByEmailAndPassword(String email, String password);

    @Query("Select * from Users where Users.user_id = :userId")
    abstract public LiveData<User> getUserById(long userId);

    @Query("Delete From Users")
    abstract public void nukeTable();

    @Query("DELETE FROM users WHERE user_id " +
            "IN (SELECT user_id FROM users " +
            "WHERE user_id NOT IN (:ids))")
    abstract public int deleteUnused(List<Long> ids);

    //Combine upsert and delsert
    //https://www.py4u.net/discuss/668276
    @Transaction
    public void delupsert(List<User> users) {
        List<Long> ids = new ArrayList<>();
        for (User user : users) {
            ids.add(user.getId());
        }
        deleteUnused(ids);
        upsert(users);
    }
}

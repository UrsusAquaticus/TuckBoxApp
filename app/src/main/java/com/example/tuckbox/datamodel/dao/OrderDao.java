package com.example.tuckbox.datamodel.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.tuckbox.datamodel.entity.Address;
import com.example.tuckbox.datamodel.entity.Food;
import com.example.tuckbox.datamodel.entity.Order;
import com.example.tuckbox.datamodel.relations.OrderWithHistory;

import java.util.ArrayList;
import java.util.List;

@Dao
public abstract class OrderDao extends BaseDao<Order> {
    @Transaction
    @Query("Select * From Orders " +
            "Where Orders.user_id = :userId " +
            "Order By Orders.timestamp Desc")
    abstract public LiveData<List<OrderWithHistory>> getOrderHistory(long userId);

    @Query("Delete From Orders")
    abstract public void nukeTable();

    @Query("Select * from Orders")
    abstract public List<Order> getAll();

    @Query("DELETE FROM orders WHERE order_id " +
            "IN (SELECT order_id FROM orders " +
            "WHERE order_id NOT IN (:ids))")
    abstract public int deleteUnused(List<Long> ids);

    //Combine upsert and delsert
    //https://www.py4u.net/discuss/668276
    @Transaction
    public void delupsert(List<Order> orders) {
        List<Long> ids = new ArrayList<>();
        for (Order order : orders) {
            ids.add(order.getId());
        }
        deleteUnused(ids);
        upsert(orders);
    }
}

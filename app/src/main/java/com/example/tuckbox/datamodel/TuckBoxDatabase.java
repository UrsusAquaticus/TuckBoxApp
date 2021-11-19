package com.example.tuckbox.datamodel;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.tuckbox.datamodel.dao.AddressDao;
import com.example.tuckbox.datamodel.dao.CartItemDao;
import com.example.tuckbox.datamodel.dao.FoodDao;
import com.example.tuckbox.datamodel.dao.FoodOptionDao;
import com.example.tuckbox.datamodel.dao.OrderDao;
import com.example.tuckbox.datamodel.dao.StoreDao;
import com.example.tuckbox.datamodel.dao.TimeslotDao;
import com.example.tuckbox.datamodel.dao.UserDao;
import com.example.tuckbox.datamodel.entity.Address;
import com.example.tuckbox.datamodel.entity.CartItem;
import com.example.tuckbox.datamodel.entity.Food;
import com.example.tuckbox.datamodel.entity.FoodOption;
import com.example.tuckbox.datamodel.entity.Order;
import com.example.tuckbox.datamodel.entity.Store;
import com.example.tuckbox.datamodel.entity.Timeslot;
import com.example.tuckbox.datamodel.entity.User;

@Database(
        entities = {
                Address.class,
                CartItem.class,
                Food.class,
                FoodOption.class,
                Order.class,
                Store.class,
                Timeslot.class,
                User.class
        },
        version = 1,
        exportSchema = false
)
@TypeConverters(DataTypeConverter.class)
public abstract class TuckBoxDatabase extends RoomDatabase {


    private static TuckBoxDatabase Instance = null;

    public abstract AddressDao getAddressDao();

    public abstract CartItemDao getCartItemDao();

    public abstract FoodDao getFoodDao();

    public abstract FoodOptionDao getFoodOptionDao();

    public abstract OrderDao getOrderDao();

    public abstract StoreDao getStoreDao();

    public abstract TimeslotDao getTimeslotDao();

    public abstract UserDao getUserDao();

    public static TuckBoxDatabase createInstance(Context context) {
        if (Instance == null) {
            Instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    TuckBoxDatabase.class,
                    "Tuckshop_Database"
            )
                    .allowMainThreadQueries()
                    .build();
        }
        return Instance;
    }
}

package com.example.tuckbox.datamodel.relations;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.tuckbox.datamodel.entity.Address;
import com.example.tuckbox.datamodel.entity.CartItem;
import com.example.tuckbox.datamodel.entity.Order;
import com.example.tuckbox.datamodel.entity.Store;
import com.example.tuckbox.datamodel.entity.Timeslot;
import com.example.tuckbox.datamodel.entity.User;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class OrderWithHistory {
    @Embedded
    public Order order;
    @Relation(
            parentColumn = "user_id",
            entityColumn = "user_id"
    )
    public User user;
    @Relation(
            parentColumn = "address_id",
            entityColumn = "address_id"
    )
    public Address address;
    @Relation(
            parentColumn = "store_id",
            entityColumn = "store_id"
    )
    public Store store;
    @Relation(
            parentColumn = "timeslot_id",
            entityColumn = "timeslot_id"
    )
    public Timeslot timeslot;
    @Relation(
            parentColumn = "order_id",
            entityColumn = "order_id",
            entity = CartItem.class
    )
    public List<CartItemWithFoodOption> cartItemWithFoodOptionList;

    public OrderWithHistory(Order order) {
        this.order = order;
    }

    public String getTimestamp() {
        return new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.getDefault()).format(order.getTimestamp());
    }

    public String getFoodNames() {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (CartItemWithFoodOption cartItemWithFoodOption :
                cartItemWithFoodOptionList) {
            sb.append(
                    cartItemWithFoodOption.optionWithFood.food.getName()
            );
            if (i++ != cartItemWithFoodOptionList.size() - 1) {
                sb.append("\n"); //Add new line if not last item
            }
        }
        return sb.toString();
    }

    public String getFoodOptions() {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (CartItemWithFoodOption cartItemWithFoodOption :
                cartItemWithFoodOptionList) {
            sb.append(
                    cartItemWithFoodOption.optionWithFood.foodOption.getName()
            );
            if (i++ != cartItemWithFoodOptionList.size() - 1) {
                sb.append("\n"); //Add new line if not last item
            }
        }
        return sb.toString();
    }

    public String getFoodAmounts() {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (CartItemWithFoodOption cartItemWithFoodOption :
                cartItemWithFoodOptionList) {
            sb.append(
                    cartItemWithFoodOption.cartItem.getAmountString()
            );
            if (i++ != cartItemWithFoodOptionList.size() - 1) {
                sb.append("\n"); //Add new line if not last item
            }
        }
        return sb.toString();
    }


    public String getFoodPrices() {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (CartItemWithFoodOption cartItemWithFoodOption :
                cartItemWithFoodOptionList) {
            sb.append(
                    cartItemWithFoodOption.getSubTotalString()
            );
            if (i++ != cartItemWithFoodOptionList.size() - 1) {
                sb.append("\n"); //Add new line if not last item
            }
        }
        return sb.toString();
    }
}

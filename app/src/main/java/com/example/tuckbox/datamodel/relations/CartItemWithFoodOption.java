package com.example.tuckbox.datamodel.relations;

import android.util.Log;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.tuckbox.datamodel.entity.CartItem;
import com.example.tuckbox.datamodel.entity.Food;
import com.example.tuckbox.datamodel.entity.FoodOption;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class CartItemWithFoodOption {
    @Embedded
    public CartItem cartItem;
    @Relation(
            parentColumn = "food_option_id",
            entityColumn = "food_option_id",
            entity = FoodOption.class
    )
    public OptionWithFood optionWithFood;

    public CartItemWithFoodOption(CartItem cartItem) {
        this.cartItem = cartItem;
    }

    public double getSubTotal() {
        return optionWithFood.food.getPrice() * cartItem.getAmount();
    }

    public String getSubTotalString() {
        NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
        return nf.format(getSubTotal());
    }

    public static List<CartItem> getCartItems(List<CartItemWithFoodOption> cartItemWithFoodOptionList) {
        List<CartItem> cartItems = new ArrayList<>();
        for (CartItemWithFoodOption cartItemWithFoodOption :
                cartItemWithFoodOptionList) {
            cartItems.add(cartItemWithFoodOption.cartItem);
        }
        return cartItems;
    }
}

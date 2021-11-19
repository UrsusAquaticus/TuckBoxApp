package com.example.tuckbox.datamodel.recycler;

import androidx.annotation.NonNull;

import com.example.tuckbox.datamodel.relations.CartItemWithFoodOption;
import com.example.tuckbox.datamodel.relations.FoodWithOptions;

import java.util.List;

public class CartViewAdapter extends SingleLayoutAdapter {
    //https://medium.com/androiddevelopers/android-data-binding-recyclerview-db7c40d9f0e4

    List<CartItemWithFoodOption> cartList = null;

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        Object obj = getObjForPosition(position);
        holder.bind(obj);
    }

    public CartViewAdapter(int layoutId, List<CartItemWithFoodOption> cartList) {
        super(layoutId);
        this.cartList = cartList;
    }

    @Override
    protected Object getObjForPosition(int position) {
        return cartList.get(position);
    }

    @Override
    public int getItemCount() {
        if (cartList == null) {
            return 0;
        } else {
            return cartList.size();
        }
    }
}

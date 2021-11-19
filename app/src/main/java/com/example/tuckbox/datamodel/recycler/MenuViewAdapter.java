package com.example.tuckbox.datamodel.recycler;

import androidx.annotation.NonNull;

import com.example.tuckbox.datamodel.relations.FoodWithOptions;

import java.util.List;

public class MenuViewAdapter extends SingleLayoutAdapter {
    //https://medium.com/androiddevelopers/android-data-binding-recyclerview-db7c40d9f0e4

    List<FoodWithOptions> foodList = null;

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        Object obj = getObjForPosition(position);
        holder.bind(obj);
    }

    public MenuViewAdapter(int layoutId, List<FoodWithOptions> foodList) {
        super(layoutId);
        this.foodList = foodList;
    }

    @Override
    protected Object getObjForPosition(int position) {
        return foodList.get(position);
    }

    @Override
    public int getItemCount() {
        if (foodList == null) {
            return 0;
        } else {
            return foodList.size();
        }
    }
}

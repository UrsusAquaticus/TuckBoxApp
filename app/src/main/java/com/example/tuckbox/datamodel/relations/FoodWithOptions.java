package com.example.tuckbox.datamodel.relations;

import android.util.Log;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.tuckbox.datamodel.entity.Food;
import com.example.tuckbox.datamodel.entity.FoodOption;

import java.util.ArrayList;
import java.util.List;

public class FoodWithOptions {
    @Embedded
    public Food food;
    @Relation(
            parentColumn = "food_id",
            entityColumn = "food_id"
    )
    public List<FoodOption> foodOptions;

    public FoodWithOptions(Food food) {
        this.food = food;
    }

    public String[] getOptionStrings() {
        List<String> strings = new ArrayList<>();
        for (FoodOption option :
                foodOptions) {
            strings.add(option.getName());
        }
        return strings.toArray(new String[0]);
    }
}

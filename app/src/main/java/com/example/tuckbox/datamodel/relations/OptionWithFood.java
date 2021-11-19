package com.example.tuckbox.datamodel.relations;

import android.util.Log;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.tuckbox.datamodel.entity.Food;
import com.example.tuckbox.datamodel.entity.FoodOption;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class OptionWithFood {
    @Embedded
    public FoodOption foodOption;
    @Relation(
            parentColumn = "food_id",
            entityColumn = "food_id"
    )
    public Food food;

    public OptionWithFood(FoodOption foodOption) {
        this.foodOption = foodOption;
    }
}

package com.example.tuckbox.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tuckbox.MainActivity;
import com.example.tuckbox.R;
import com.example.tuckbox.datamodel.TuckBoxViewModel;
import com.example.tuckbox.databinding.FragmentMenuBinding;
import com.example.tuckbox.datamodel.entity.CartItem;
import com.example.tuckbox.datamodel.entity.Food;
import com.example.tuckbox.datamodel.entity.FoodOption;
import com.example.tuckbox.datamodel.relations.FoodWithOptions;
import com.example.tuckbox.datamodel.recycler.MenuViewAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class MenuFragment extends Fragment {

    FragmentMenuBinding binding;

    static Food selectedFood = null;
    static FoodOption selectedOption = null;
    TuckBoxViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMenuBinding.inflate(inflater, container, false);
        viewModel = MainActivity.getViewModel();

        RecyclerView recyclerView = binding.menuRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        viewModel.getFoodWithOptions().observe(this.getViewLifecycleOwner(), foodWithOptions -> {
            recyclerView.setAdapter(new MenuViewAdapter(R.layout.menu_list_item_layout, foodWithOptions));
        });
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @BindingAdapter("android:onClick")
    public static void setOnClick(View view, FoodWithOptions foodWithOptions) {
        view.setOnClickListener(
                v -> new MaterialAlertDialogBuilder(v.getContext(), R.style.Picker)
                        .setTitle(foodWithOptions.food.getName())
                        .setPositiveButton("Add",
                                (dialog, which) -> {
                                    onSubmit(v.getContext(), foodWithOptions);
                                })
                        .setSingleChoiceItems(
                                foodWithOptions.getOptionStrings(),
                                0,
                                (dialog, which) -> {
                                    onSelect(foodWithOptions, which);
                                }
                        )
                        .show()
        );
    }

    //https://stackoverflow.com/questions/35809290/set-drawable-resource-id-in-androidsrc-for-imageview-using-data-binding-in-andr
    @BindingAdapter("android:src")
    public static void setImageResource(ImageView imageView, int resource) {
        imageView.setImageResource(resource);
    }

    private static void onSubmit(Context context, FoodWithOptions foodWithOptions) {
        selectedFood = foodWithOptions.food;
        selectedOption = selectedOption == null ? foodWithOptions.foodOptions.get(0) : selectedOption;

        CartItem cartItem = new CartItem(
                null,
                null,
                selectedOption.getId(),
                false,
                "Has been added",
                1
        );

        MainActivity.getViewModel().insertCartItem(cartItem);
        selectedFood = null;
        selectedOption = null;
    }

    private static void onSelect(FoodWithOptions foodWithOptions, int selectIndex) {
        selectedOption = foodWithOptions.foodOptions.get(selectIndex);
        Log.d("MENU", "OPTION: " + selectedOption.getName());
    }
}

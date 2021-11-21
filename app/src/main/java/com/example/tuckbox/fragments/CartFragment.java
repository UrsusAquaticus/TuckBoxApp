package com.example.tuckbox.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
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
import com.example.tuckbox.OrderActivity;
import com.example.tuckbox.R;
import com.example.tuckbox.databinding.FragmentCartBinding;
import com.example.tuckbox.datamodel.TuckBoxViewModel;
import com.example.tuckbox.datamodel.entity.CartItem;
import com.example.tuckbox.datamodel.recycler.CartViewAdapter;
import com.example.tuckbox.datamodel.relations.CartItemWithFoodOption;
import com.example.tuckbox.datamodel.relations.FoodWithOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class CartFragment extends Fragment {

    //todo: Add animation to when cart item is removed

    FragmentCartBinding binding;
    TuckBoxViewModel viewModel;

    private final CartBadgeSetter cartBadgeSetter;
    static int itemCount;

    public interface CartBadgeSetter {
        void set(int count);
    }

    public CartFragment(CartBadgeSetter cartBadgeSetter) {
        this.cartBadgeSetter = cartBadgeSetter;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCartBinding.inflate(inflater, container, false);
        viewModel = MainActivity.getViewModel();

        RecyclerView recyclerView = binding.cartRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView.setAdapter(
                new CartViewAdapter(
                        R.layout.cart_list_item_layout
                )
        );
        viewModel.getLiveCartItemsWithFoodOption().observe(getViewLifecycleOwner(),
                cartItemWithFoodOptions -> {
                    //update the recycler
                    CartViewAdapter adapter = (CartViewAdapter) recyclerView.getAdapter();
                    if (adapter != null) adapter.updateData(cartItemWithFoodOptions);

                    //Add up all the cart items
                    itemCount = 0;
                    double totalPrice = 0;
                    for (CartItemWithFoodOption item : cartItemWithFoodOptions) {
                        itemCount += item.cartItem.getAmount();
                        totalPrice += item.getSubTotal();
                    }
                    //For badge
                    cartBadgeSetter.set(itemCount);
                    //For cart total
                    NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
                    binding.setTotalString(nf.format(totalPrice));
                    //For displaying empty cart message
                    binding.setIsEmpty(itemCount == 0);

                });

        return binding.getRoot();
    }

    @BindingAdapter("android:onClickAddItem")
    public static void setOnClickAddItem(View view, CartItem cartItem) {
        view.setOnClickListener(
                //Probably fine to be null
                v -> MainActivity.getViewModel()
                        .insertCartItem(cartItem)
        );
    }

    @BindingAdapter("android:onClickReduceItem")
    public static void setOnClickReduceItem(View view, CartItem cartItem) {
        view.setOnClickListener(
                //Probably fine to be null
                v -> MainActivity.getViewModel()
                        .reduceCartItem(cartItem)
        );
    }

    @BindingAdapter("android:onClickCheckout")
    public static void setOnClickCheckout(View view, int i) {
        view.setOnClickListener(
                v -> goToCheckout(v.getContext())
        );
    }

    private static void goToCheckout(Context context) {
        if (itemCount > 0) {
            Intent intent = new Intent(context, OrderActivity.class);
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "Cart is empty", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

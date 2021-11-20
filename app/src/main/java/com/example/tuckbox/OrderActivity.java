package com.example.tuckbox;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tuckbox.databinding.ActivityOrderBinding;
import com.example.tuckbox.datamodel.TuckBoxViewModel;
import com.example.tuckbox.datamodel.entity.Address;
import com.example.tuckbox.datamodel.entity.CartItem;
import com.example.tuckbox.datamodel.entity.Order;
import com.example.tuckbox.datamodel.entity.Store;
import com.example.tuckbox.datamodel.entity.Timeslot;
import com.example.tuckbox.datamodel.entity.User;
import com.example.tuckbox.datamodel.recycler.CartViewAdapter;
import com.example.tuckbox.datamodel.relations.CartItemWithFoodOption;
import com.example.tuckbox.datamodel.relations.OrderTotals;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.sql.Time;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderActivity extends AppCompatActivity {


    //todo: Prevent highlighting the first textbox on load
    //todo: Add a button to manage addresses
    //todo: Redirect to history upon placing order
    TuckBoxViewModel viewModel;

    ActivityOrderBinding binding;
    long userId = -1;
    int selectedAddressIndex;
    List<Address> addressList;
    int selectedStoreIndex;
    List<Store> storeList;
    int selectedTimeslotIndex;
    List<Timeslot> timeslotList;
    List<CartItemWithFoodOption> cartItemWithFoodOptionList = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //set viewmodel
        viewModel = TuckBoxViewModel.getViewModel(getApplication());
        //get user id
        SharedPreferences preferences = getSharedPreferences(
                TuckBoxViewModel.USER_PREF_DATA,
                Context.MODE_PRIVATE);
        userId = preferences.getLong(TuckBoxViewModel.USER_PREF_USER_ID, -1);
        //Set up connections
        connectDropdowns();
        connectCartItems();
        connectPlaceOrderButton();
    }


    @Override
    protected void onStart() {
        super.onStart();
        connectLoading();
    }

    private void connectLoading() {
        TuckBoxViewModel.isLoading.observe(this, isCurrentlyLoading -> {
            binding.orderProgress.setVisibility(
                    isCurrentlyLoading ? View.VISIBLE : View.INVISIBLE
            );
            //Timeslot dropdown
            binding.orderTimeslotLayout.setFocusable(!isCurrentlyLoading);
            binding.orderTimeslotLayout.setFocusableInTouchMode(!isCurrentlyLoading);
            binding.orderTimeslotLayout.setEnabled(!isCurrentlyLoading);
            binding.orderTimeslot.setCursorVisible(!isCurrentlyLoading);
            //Store dropdown
            binding.orderStoreLayout.setFocusable(!isCurrentlyLoading);
            binding.orderStoreLayout.setFocusableInTouchMode(!isCurrentlyLoading);
            binding.orderStoreLayout.setEnabled(!isCurrentlyLoading);
            binding.orderStore.setCursorVisible(!isCurrentlyLoading);
            //Address dropdown
            binding.orderAddressLayout.setFocusable(!isCurrentlyLoading);
            binding.orderAddressLayout.setFocusableInTouchMode(!isCurrentlyLoading);
            binding.orderAddressLayout.setEnabled(!isCurrentlyLoading);
            binding.orderAddress.setCursorVisible(!isCurrentlyLoading);
            //Submit button
            binding.orderSubmitButton.setEnabled(!isCurrentlyLoading);
        });
    }

    private void connectPlaceOrderButton() {
        binding.orderSubmitButton.setOnClickListener(v -> {
            if (userId != -1 &&
                    selectedAddressIndex != -1 &&
                    selectedStoreIndex != -1 &&
                    selectedTimeslotIndex != -1) {
                viewModel.getUserById(userId).observe(this, user -> {
                    Address address = addressList.get(selectedAddressIndex);
                    Store store = storeList.get(selectedStoreIndex);
                    Timeslot timeslot = timeslotList.get(selectedTimeslotIndex);
                    Order order = new Order(
                            null,
                            user.getId(),
                            address.getId(),
                            store.getId(),
                            timeslot.getId(),
                            new Date(),
                            "",
                            user.toString(),
                            address.toString(),
                            store.toString()
                    );
                    //Get Cart items
                    List<CartItem> cartItems = CartItemWithFoodOption.getCartItems(cartItemWithFoodOptionList);
                    //Set them to complete
                    for (CartItem cartItem : cartItems) {
                        cartItem.setComplete(true);
                    }
                    TuckBoxViewModel.setIsLoading(true);
                    viewModel.insertOrderAndCartItems(order, cartItems).addOnCompleteListener(
                            task -> {
                                TuckBoxViewModel.setIsLoading(false);
                                if (task.isSuccessful()) {
                                    Toast.makeText(getBaseContext(), "Order Placed Successfully", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(this, MainActivity.class);
                                    intent.putExtra(MainActivity.IS_FROM_ORDER, true);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(getBaseContext(), "Order Failed", Toast.LENGTH_SHORT).show();
                                }
                            });
                });
            } else {
                Toast.makeText(this, "Not everything was selected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void connectCartItems() {
        RecyclerView recyclerView = binding.orderRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        viewModel.getLiveCartItemsWithFoodOption().observe(this,
                cartItemWithFoodOptions -> {
                    cartItemWithFoodOptionList = cartItemWithFoodOptions;
                    //update the recycler
                    recyclerView.setAdapter(
                            new CartViewAdapter(
                                    R.layout.order_list_item_layout,
                                    cartItemWithFoodOptions
                            )
                    );
                    //Add up all the cart items
                    int count = 0;
                    double totalPrice = 0;
                    for (CartItemWithFoodOption item : cartItemWithFoodOptions) {
                        count += item.cartItem.getAmount();
                        totalPrice += item.getSubTotal();
                    }
                    //For cart total
                    NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
                    OrderTotals totals = new OrderTotals(
                            String.valueOf(count),
                            nf.format(totalPrice));
                    binding.setTotals(totals);
                });
    }

    private void connectDropdowns() {
        //User Addresses
        viewModel.getLiveAddressesByUserId(userId).observe(this,
                addresses -> {
                    selectedAddressIndex = -1;
                    addressList = addresses;
                    ArrayAdapter<?> adapter = new ArrayAdapter<>(getBaseContext(), R.layout.dropdown_list_item, addresses);
                    binding.orderAddress.setAdapter(adapter);
                    binding.orderAddress.setOnItemClickListener((parent, view, position, id) -> {
                        selectedAddressIndex = position;
                    });
                });

        //Store Names
        viewModel.getAllStores().observe(this,
                stores -> {
                    selectedStoreIndex = -1;
                    storeList = stores;
                    ArrayAdapter<?> adapter = new ArrayAdapter<>(getBaseContext(), R.layout.dropdown_list_item, storeList);
                    binding.orderStore.setAdapter(adapter);
                    binding.orderStore.setOnItemClickListener((parent, view, position, id) -> {
                        selectedStoreIndex = position;
                    });
                });

        //Timeslots
        viewModel.getAllTimeslots().observe(this,
                timeslots -> {
                    selectedTimeslotIndex = -1;
                    timeslotList = timeslots;
                    ArrayAdapter<?> adapter = new ArrayAdapter<>(getBaseContext(), R.layout.dropdown_list_item, timeslotList);
                    binding.orderTimeslot.setAdapter(adapter);
                    binding.orderTimeslot.setOnItemClickListener((parent, view, position, id) -> {
                        selectedTimeslotIndex = position;
                    });
                });
    }

}
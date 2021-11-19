package com.example.tuckbox;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tuckbox.databinding.ActivityAddressBinding;
import com.example.tuckbox.datamodel.TuckBoxViewModel;
import com.example.tuckbox.datamodel.entity.Address;
import com.example.tuckbox.datamodel.recycler.AddressViewAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Objects;

public class AddressActivity extends AppCompatActivity {

    long userId;
    ActivityAddressBinding binding;
    TuckBoxViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddressBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //set viewmodel
        viewModel = TuckBoxViewModel.getViewModel(getApplication());
        //get user id
        SharedPreferences preferences = getSharedPreferences(
                TuckBoxViewModel.USER_PREF_DATA,
                Context.MODE_PRIVATE);
        userId = preferences.getLong(TuckBoxViewModel.USER_PREF_USER_ID, -1);

        connectAddressList();
    }

    @Override
    protected void onStart() {
        super.onStart();
        connectLoading();
    }

    private void connectLoading() {
        TuckBoxViewModel.isLoading.observe(this, isCurrentlyLoading -> {
            binding.addressProgress.setVisibility(
                    isCurrentlyLoading ? View.VISIBLE : View.INVISIBLE
            );
            //Address
            binding.addressStreetAddress.setCursorVisible(!isCurrentlyLoading);
            binding.addressStreetAddressLayout.setEnabled(!isCurrentlyLoading);
            //Address
            binding.addressCity.setCursorVisible(!isCurrentlyLoading);
            binding.addressCityLayout.setEnabled(!isCurrentlyLoading);
            //Address
            binding.addressPostcode.setCursorVisible(!isCurrentlyLoading);
            binding.addressPostcodeLayout.setEnabled(!isCurrentlyLoading);
            //Submit button
            binding.addressSubmitButton.setEnabled(!isCurrentlyLoading);
            //Recycler
            binding.addressRecyclerView.setEnabled(!isCurrentlyLoading);
        });
    }

    private void connectAddressList() {
        RecyclerView recyclerView = binding.addressRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        viewModel.getLiveAddressesByUserId(userId).observe(this,
                addresses -> {
                    recyclerView.setAdapter(
                            new AddressViewAdapter(
                                    R.layout.address_list_item_layout,
                                    addresses
                            )
                    );
                    connectAddAddress();
                });
    }

    private void connectAddAddress() {
        binding.addressSubmitButton.setOnClickListener(
                v -> {
                    String streetAddress = Objects.requireNonNull(binding.addressStreetAddress.getText()).toString();
                    String city = Objects.requireNonNull(binding.addressCity.getText()).toString();
                    String postCode = Objects.requireNonNull(binding.addressPostcode.getText()).toString();
                    if (streetAddress.length() > 0) {
                        if (city.length() > 0) {
                            if (postCode.length() > 0) {
                                Address address = new Address(
                                        null,
                                        null,
                                        userId,
                                        streetAddress,
                                        city,
                                        city,
                                        postCode
                                );
                                TuckBoxViewModel.setIsLoading(true);
                                viewModel.insertAddress(address).addOnCompleteListener(task -> {
                                    TuckBoxViewModel.setIsLoading(false);
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "Address has been added!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Failed to add address!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                Toast.makeText(getApplicationContext(), "Please Enter a postcode", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Please Enter a city", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Please Enter an address", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @BindingAdapter("android:onClickDeleteItem")
    public static void setOnClickDeleteItem(View view, Address address) {
        view.setOnClickListener(
                v -> {
                    TuckBoxViewModel.setIsLoading(true);
                    //Should probably work fine even if null
                    TuckBoxViewModel.getViewModel(null).deleteAddress(address).addOnCompleteListener(
                            task -> {
                                TuckBoxViewModel.setIsLoading(false);
                                if (task.isSuccessful()) {
                                    Toast.makeText(v.getContext(), "Address Deleted!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(v.getContext(), "Address Deletion failed!", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
        );
    }

}
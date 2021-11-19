package com.example.tuckbox.datamodel;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tuckbox.AuthenticationActivity;
import com.example.tuckbox.datamodel.entity.Address;
import com.example.tuckbox.datamodel.entity.CartItem;
import com.example.tuckbox.datamodel.entity.Order;
import com.example.tuckbox.datamodel.entity.Store;
import com.example.tuckbox.datamodel.entity.Timeslot;
import com.example.tuckbox.datamodel.relations.CartItemWithFoodOption;
import com.example.tuckbox.datamodel.relations.FoodWithOptions;
import com.example.tuckbox.datamodel.entity.User;
import com.example.tuckbox.datamodel.relations.OrderWithHistory;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class TuckBoxViewModel extends ViewModel {

    TuckBoxDataModel dataModel = null;

    static TuckBoxViewModel viewModel;

    public static final String USER_PREF_DATA = "USER_PREF_DATA";
    public static final String USER_PREF_USER_ID = "USER_PREF_USER_ID";
    public static final String USER_PREF_USERNAME = "USER_PREF_USERNAME";
    public static final String USER_PREF_PASSWORD = "USER_PREF_PASSWORD";
    public static final String USER_OBJECT_INTENT_EXTRA = "USER_OBJECT_INTENT_EXTRA";

    public static MutableLiveData<Boolean> isLoading;

    private TuckBoxViewModel(Application application) {
        dataModel = TuckBoxDataModel.getInstance(application);
        isLoading = new MutableLiveData<>(false);
    }

    public static TuckBoxViewModel getViewModel(Application application) {
        if (viewModel == null) {
            viewModel = new TuckBoxViewModel(application);
        }
        return viewModel;
    }

    public void signOut(Activity activity) {
        Log.d("SIGNOUT", "Going back");

        SharedPreferences preferences = activity.getSharedPreferences(
                USER_PREF_DATA,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(USER_PREF_USER_ID);
        editor.remove(USER_PREF_USERNAME);
        editor.remove(USER_PREF_PASSWORD);
        editor.apply();

        activity.startActivity(new Intent(activity, AuthenticationActivity.class));
        activity.finish();
    }

    public static void setIsLoading(boolean _isLoading) {
        isLoading.setValue(_isLoading);
    }

    //Address
    public LiveData<List<Address>> getLiveAddressesByUserId(long userId) {
        return dataModel.getLiveAddressesByUserId(userId);
    }

    public Task<Object> insertAddress(Address address) {
        return dataModel.insertAddress(address);
    }

    public Task<Void> deleteAddress(Address address) {
        return dataModel.deleteAddress(address);
    }

    //CartItem
    public LiveData<List<CartItemWithFoodOption>> getLiveCartItemsWithFoodOption() {
        return dataModel.getLiveCartItemsWithFoodOption();
    }

    public Task<Long> insertCartItem(CartItem cartItem) {
        return dataModel.insertCartItem(cartItem);
    }

    public long reduceCartItem(CartItem cartItem) {
        return dataModel.reduceCartItem(cartItem);
    }

    public void clearCartItems() {
        dataModel.clearCartItems();
    }

    //Food With Options
    public LiveData<List<FoodWithOptions>> getFoodWithOptions() {
        return dataModel.getFoodWithOptions();
    }

    //Order
    public Task<Object> insertOrderAndCartItems(Order order, List<CartItem> cartItems) {
        return dataModel.insertOrderAndCartItems(order, cartItems);
    }

    public LiveData<List<OrderWithHistory>> getOrderHistoryByUserId(long userId) {
        return dataModel.getOrderHistoryByUserId(userId);
    }

    //Store
    public LiveData<List<Store>> getAllStores() {
        return dataModel.getAllStores();
    }

    //Timeslot
    public LiveData<List<Timeslot>> getAllTimeslots() {
        return dataModel.getAllTimeslots();
    }

    //User
    public Task<User> register(User user, Address address) {
        return dataModel.register(user, address);
    }

    public Task<Void> updateUser(User user) {
        return dataModel.updateUser(user);
    }

    public User getUserById(long userId) {
        return dataModel.getUserById(userId);
    }

    public Task<Void> deleteUser(User user) {
        return dataModel.deleteUser(user);
    }

    public Task<QuerySnapshot> login(String email, String password) {
        return dataModel.login(email, password);
    }
}

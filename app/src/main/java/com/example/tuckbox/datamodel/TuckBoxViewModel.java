package com.example.tuckbox.datamodel;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tuckbox.AuthenticationActivity;
import com.example.tuckbox.MainActivity;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Objects;

public class TuckBoxViewModel extends ViewModel {

    TuckBoxDataModel dataModel;

    static TuckBoxViewModel viewModel;

    public static final String USER_PREF_DATA = "USER_PREF_DATA";
    public static final String USER_PREF_USER_ID = "USER_PREF_USER_ID";
    public static final String USER_PREF_USERNAME = "USER_PREF_USERNAME";
    public static final String USER_PREF_PASSWORD = "USER_PREF_PASSWORD";
    public static final String USER_OBJECT_INTENT_EXTRA = "USER_OBJECT_INTENT_EXTRA";
    public static final String JUST_SIGNED_UP = "JUST_SIGNED_UP";

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

    public void loginUser(String email, String password, Activity activity) {
        TuckBoxViewModel.setIsLoading(true);
        viewModel.login(email, password).addOnCompleteListener(task -> {
            TuckBoxViewModel.setIsLoading(false);
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (Objects.requireNonNull(querySnapshot).size() == 1) {
                    QueryDocumentSnapshot doc = querySnapshot.iterator().next();
                    User user = doc.toObject(User.class);

                    Intent intent = new Intent(activity, MainActivity.class);
                    activity.startActivity(intent);

                    Toast.makeText(activity, "Login Success!", Toast.LENGTH_SHORT).show();

                    SharedPreferences preferences = activity.getSharedPreferences(TuckBoxViewModel.USER_PREF_DATA, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putLong(TuckBoxViewModel.USER_PREF_USER_ID, user.getId());
                    editor.putString(TuckBoxViewModel.USER_PREF_USERNAME, user.getEmail());
                    editor.putString(TuckBoxViewModel.USER_PREF_PASSWORD, user.getPassword());
                    editor.apply();
                } else {
                    Toast.makeText(activity, "Login Failed!", Toast.LENGTH_SHORT).show();
                    Log.d("F_LOGIN_CALLBACK", "Successful but did not return 1. n=" + querySnapshot.size());
                }
            } else {
                Toast.makeText(activity, "Login Failed!", Toast.LENGTH_SHORT).show();
                Log.d("F_LOGIN_CALLBACK", "Query unsuccessful");
            }
        });
    }

    //Probably a better way to do this
    public Task<Void> initialImport() {
        //Connect to database normally
        return dataModel.initialImport();
    }

    public Task<Void> secondImport() {
        //Connect to database normally
        return dataModel.secondImport();
    }

    public Task<Void> thirdImport() {
        //Connect to database normally
        return dataModel.thirdImport();
    }

    public Task<Void> addDummyData() {
        return dataModel.addDummyData();
    }

    public void setUpdateListeners() {
        dataModel.setUpdateListeners();
    }

    public void setUpdateListener(String dataCollection) {
        dataModel.setUpdateListener(dataCollection);
    }

    public Task<QuerySnapshot> getAllCollectionInfo() {
        return dataModel.getAllCollectionInfo();
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

    public int reduceCartItem(CartItem cartItem) {
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
    public Task<Order> insertOrderAndCartItems(Order order, List<CartItem> cartItems) {
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

    public LiveData<User> getUserById(long userId) {
        return dataModel.getUserById(userId);
    }

    public Task<Void> deleteUser(User user) {
        return dataModel.deleteUser(user);
    }

    public Task<QuerySnapshot> login(String email, String password) {
        return dataModel.login(email, password);
    }
}

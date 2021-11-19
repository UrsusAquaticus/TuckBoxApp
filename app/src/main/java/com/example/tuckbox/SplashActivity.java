package com.example.tuckbox;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;

import com.example.tuckbox.datamodel.TuckBoxViewModel;
import com.example.tuckbox.datamodel.entity.User;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class SplashActivity extends AppCompatActivity {
    //https://medium.com/geekculture/implementing-the-perfect-splash-screen-in-android-295de045a8dc

    TuckBoxViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkUserLoginPreference();
        viewModel = TuckBoxViewModel.getViewModel(getApplication());
        new CountDownTimer(50000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                finish();
            }
        }.start();
    }

    public void checkUserLoginPreference() {
        SharedPreferences preferences = getSharedPreferences(
                TuckBoxViewModel.USER_PREF_DATA,
                Context.MODE_PRIVATE);
        String username = preferences.getString(
                TuckBoxViewModel.USER_PREF_USERNAME,
                null);
        String password = preferences.getString(
                TuckBoxViewModel.USER_PREF_PASSWORD,
                null);
        if (username != null && password != null) {
            loginUser(username, password);
        } else {
            Log.d("SPLASH_LOGIN", "No saved login data");
            startActivity(new Intent(SplashActivity.this, AuthenticationActivity.class));
        }
    }

    private void loginUser(String email, String password) {
        viewModel.login(email, password).addOnCompleteListener(
                task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        assert querySnapshot != null;
                        if (querySnapshot.size() == 1) {
                            QueryDocumentSnapshot doc = querySnapshot.iterator().next();
                            User user = doc.toObject(User.class);

                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                            intent.putExtra(TuckBoxViewModel.USER_OBJECT_INTENT_EXTRA, user);
                            startActivity(intent);
                        } else {
                            Log.d("LOGIN_CALLBACK", "Successful but did not return 1");
                            startActivity(new Intent(SplashActivity.this, AuthenticationActivity.class));
                        }
                    } else {
                        Log.d("LOGIN_CALLBACK", "Query unsuccessful");
                        startActivity(new Intent(SplashActivity.this, AuthenticationActivity.class));
                    }
                });
    }
}
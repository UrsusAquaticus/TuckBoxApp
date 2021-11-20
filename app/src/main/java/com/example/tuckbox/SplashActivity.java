package com.example.tuckbox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;

import com.example.tuckbox.datamodel.RemoteDbHandler;
import com.example.tuckbox.datamodel.TuckBoxViewModel;
import com.example.tuckbox.datamodel.entity.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class SplashActivity extends AppCompatActivity {
    //https://medium.com/geekculture/implementing-the-perfect-splash-screen-in-android-295de045a8dc

    TuckBoxViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = TuckBoxViewModel.getViewModel(getApplication());

        Log.d("STARTUP", "Attempting to Contact Firestore");
        viewModel.getAllCollectionInfo().addOnCompleteListener(
                collectionInfo -> {
                    if (collectionInfo.isSuccessful()) {
                        QuerySnapshot snapshot = collectionInfo.getResult();
                        if (snapshot != null) {
                            if (snapshot.isEmpty()) {
                                //Development only
                                Log.d("STARTUP", "Firestore Empty, Attempting to populate");
                                viewModel.addDummyData().addOnCompleteListener(
                                        dummyData -> {
                                            if (dummyData.isSuccessful()) {
                                                Log.d("STARTUP", "Attempting Initial Import");
                                                viewModel.initialImport().addOnCompleteListener(
                                                        initialImport -> {
                                                            Log.d("STARTUP", "Attempting second Import");
                                                            viewModel.secondImport().addOnCompleteListener(
                                                                    secondImport -> {
                                                                        Log.d("STARTUP", "Attempting third Import");
                                                                        viewModel.thirdImport().addOnCompleteListener(
                                                                                thirdImport -> {
                                                                                    Log.d("STARTUP", "Successfully Populated Firestore");
                                                                                    viewModel.setUpdateListeners();
                                                                                    checkUserLoginPreference();
                                                                                }
                                                                        );
                                                                    }
                                                            );
                                                        }
                                                );
                                            } else {
                                                Log.e("STARTUP", "Failed to Populate Firestore");
                                            }
                                        });
                            } else {
                                Log.d("STARTUP", "Attempting Initial Import");
                                viewModel.initialImport().addOnCompleteListener(
                                        initialImport -> {
                                            Log.d("STARTUP", "Attempting second Import");
                                            viewModel.secondImport().addOnCompleteListener(
                                                    secondImport -> {
                                                        Log.d("STARTUP", "Attempting third Import");
                                                        viewModel.thirdImport().addOnCompleteListener(
                                                                thirdImport -> {
                                                                    Log.d("STARTUP", "Successfully Populated Firestore");
                                                                    viewModel.setUpdateListeners();
                                                                    checkUserLoginPreference();
                                                                }
                                                        );
                                                    }
                                            );
                                        }
                                );
                            }
                        } else {
                            Log.e("STARTUP", "Snapshot is null?");
                        }
                    } else {
                        Log.e("STARTUP", "Failed to Contact Firestore");
                        checkUserLoginPreference();
                    }
                });
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
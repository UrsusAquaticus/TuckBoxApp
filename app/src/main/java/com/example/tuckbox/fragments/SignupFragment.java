package com.example.tuckbox.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.service.autofill.Validator;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.tuckbox.AuthenticationActivity;
import com.example.tuckbox.MainActivity;
import com.example.tuckbox.databinding.FragmentSignupBinding;
import com.example.tuckbox.datamodel.TuckBoxViewModel;
import com.example.tuckbox.datamodel.entity.Address;
import com.example.tuckbox.datamodel.entity.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.Objects;

public class SignupFragment extends Fragment {

    public FragmentSignupBinding getBinding() {
        return binding;
    }

    FragmentSignupBinding binding;
    TuckBoxViewModel viewModel;

    public SignupFragment() {
        //Apparently need this
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSignupBinding.inflate(inflater, container, false);
        viewModel = AuthenticationActivity.getViewModel();
        connectSignupButton();
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        connectLoading();
    }

    private void connectLoading() {
        TuckBoxViewModel.isLoading.observe(getViewLifecycleOwner(), isCurrentlyLoading -> {
            binding.signupSubmitButton.setEnabled(!isCurrentlyLoading);

            binding.signupNameLayout.setEnabled(!isCurrentlyLoading);
            binding.signupName.setCursorVisible(!isCurrentlyLoading);
            binding.signupEmailLayout.setEnabled(!isCurrentlyLoading);
            binding.signupEmail.setCursorVisible(!isCurrentlyLoading);
            binding.signupPhoneLayout.setEnabled(!isCurrentlyLoading);
            binding.signupPhone.setCursorVisible(!isCurrentlyLoading);
            binding.signupAddressLayout.setEnabled(!isCurrentlyLoading);
            binding.signupAddress.setCursorVisible(!isCurrentlyLoading);
            binding.signupCityLayout.setEnabled(!isCurrentlyLoading);
            binding.signupCity.setCursorVisible(!isCurrentlyLoading);
            binding.signupPostcodeLayout.setEnabled(!isCurrentlyLoading);
            binding.signupPostcode.setCursorVisible(!isCurrentlyLoading);
            binding.signupPasswordLayout.setEnabled(!isCurrentlyLoading);
            binding.signupPassword.setCursorVisible(!isCurrentlyLoading);
            binding.signupConfirmPasswordLayout.setEnabled(!isCurrentlyLoading);
            binding.signupConfirmPassword.setCursorVisible(!isCurrentlyLoading);
        });
    }

    private void connectSignupButton() {
        binding.signupSubmitButton.setOnClickListener(
                v -> {
                    String name = Objects.requireNonNull(binding.signupName.getText()).toString();
                    String email = Objects.requireNonNull(binding.signupEmail.getText()).toString();
                    String phone = Objects.requireNonNull(binding.signupPhone.getText()).toString();
                    String streetAddress = Objects.requireNonNull(binding.signupAddress.getText()).toString();
                    String city = Objects.requireNonNull(binding.signupCity.getText()).toString();
                    String postCode = Objects.requireNonNull(binding.signupPostcode.getText()).toString();
                    String password = Objects.requireNonNull(binding.signupPassword.getText()).toString();
                    String confirmPassword = Objects.requireNonNull(binding.signupConfirmPassword.getText()).toString();
                    Log.d("SIGNUP", name + " " + email + " " + password + " " + confirmPassword);
                    if (!TextUtils.isEmpty(name)) {
                        if (isValidEmail(email)) {
                            if (!TextUtils.isEmpty(phone)) {
                                if (!TextUtils.isEmpty(streetAddress)) {
                                    if (!TextUtils.isEmpty(city)) {
                                        if (!TextUtils.isEmpty(postCode)) {
                                            if (!TextUtils.isEmpty(password) && password.length() >= 7) {
                                                if (password.equals(confirmPassword)) {
                                                    //Create New user
                                                    User user = new User(
                                                            null,
                                                            name,
                                                            email,
                                                            phone,
                                                            password
                                                    );
                                                    Address address = new Address(
                                                            null,
                                                            null,
                                                            null,
                                                            streetAddress,
                                                            city,
                                                            city,
                                                            postCode
                                                    );
                                                    TuckBoxViewModel.setIsLoading(true);
                                                    viewModel.register(user, address).addOnCompleteListener(
                                                            task -> {
                                                                TuckBoxViewModel.setIsLoading(false);
                                                                if (task.isSuccessful()) {
                                                                    User newUser = task.getResult();
                                                                    if (newUser != null) {
                                                                        //Go to main activity
                                                                        Intent intent = new Intent(requireActivity(), MainActivity.class);
                                                                        requireActivity().startActivity(intent);

                                                                        Toast.makeText(requireActivity(), "Registration Success!", Toast.LENGTH_SHORT).show();
                                                                        //Store Login session
                                                                        SharedPreferences preferences = requireActivity().getSharedPreferences(TuckBoxViewModel.USER_PREF_DATA, Context.MODE_PRIVATE);
                                                                        SharedPreferences.Editor editor = preferences.edit();
                                                                        editor.putLong(TuckBoxViewModel.USER_PREF_USER_ID, newUser.getId());
                                                                        editor.putString(TuckBoxViewModel.USER_PREF_USERNAME, email);
                                                                        editor.putString(TuckBoxViewModel.USER_PREF_PASSWORD, password);
                                                                        editor.apply();
                                                                    } else {
                                                                        Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                } else {
                                                                    Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                } else {
                                                    Toast.makeText(getContext(), "Passwords don't match, try again", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                Toast.makeText(getContext(), "Please Enter a password of 7 or greater characters", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(getContext(), "Please Enter a postcode", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(getContext(), "Please Enter a city", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(getContext(), "Please Enter an address", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getContext(), "Please Enter a phone number", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "Please Enter a valid email", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Please Enter a Name", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

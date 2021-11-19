package com.example.tuckbox.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.tuckbox.AddressActivity;
import com.example.tuckbox.MainActivity;
import com.example.tuckbox.R;
import com.example.tuckbox.datamodel.TuckBoxViewModel;
import com.example.tuckbox.databinding.FragmentAccountBinding;
import com.example.tuckbox.datamodel.entity.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Objects;

public class AccountFragment extends Fragment {

    FragmentAccountBinding binding;
    User user;
    TuckBoxViewModel viewModel;

    public AccountFragment() {
        //Apparently needed
    }

    public FragmentAccountBinding getBinding() {
        return binding;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAccountBinding.inflate(inflater, container, false);
        viewModel = MainActivity.getViewModel();
        Intent intent = requireActivity().getIntent();
        user = (User) intent.getExtras().get(TuckBoxViewModel.USER_OBJECT_INTENT_EXTRA);
        binding.accountName.setText(user.getName());
        binding.accountEmail.setText(user.getEmail());
        binding.accountPhone.setText(user.getPhone());
        connectButtons();
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        connectLoading();
    }

    private void connectLoading() {
        TuckBoxViewModel.isLoading.observe(getViewLifecycleOwner(), isCurrentlyLoading -> {
            binding.accountSubmitButton.setEnabled(!isCurrentlyLoading);
            binding.accountAddressButton.setEnabled(!isCurrentlyLoading);
            binding.accountDeleteButton.setEnabled(!isCurrentlyLoading);
            //Name
            binding.accountName.setCursorVisible(!isCurrentlyLoading);
            binding.accountNameLayout.setEnabled(!isCurrentlyLoading);
            //Email
            binding.accountEmail.setCursorVisible(!isCurrentlyLoading);
            binding.accountEmailLayout.setEnabled(!isCurrentlyLoading);
            //Phone
            binding.accountPhone.setCursorVisible(!isCurrentlyLoading);
            binding.accountPhoneLayout.setEnabled(!isCurrentlyLoading);
            //NewPassword
            binding.accountPassword.setCursorVisible(!isCurrentlyLoading);
            binding.accountPasswordLayout.setEnabled(!isCurrentlyLoading);
            //RepeatPassword
            binding.accountConfirmPassword.setCursorVisible(!isCurrentlyLoading);
            binding.accountConfirmPasswordLayout.setEnabled(!isCurrentlyLoading);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    private void connectButtons() {

        //Address Button
        binding.accountAddressButton.setOnClickListener(
                v -> {
                    Intent newIntent = new Intent(v.getContext(), AddressActivity.class);
                    v.getContext().startActivity(newIntent);
                }
        );
        //Delete Account button
        binding.accountDeleteButton.setOnClickListener(
                v -> new MaterialAlertDialogBuilder(v.getContext(), R.style.Picker)
                        .setTitle("Delete Account?")
                        .setPositiveButton("Confirm",
                                (dialog, which) -> {
                                    viewModel.deleteUser(user).addOnCompleteListener(
                                            task -> {
                                                if (task.isSuccessful()) {
                                                    viewModel.signOut((Activity) v.getContext());
                                                    Toast.makeText(v.getContext(), "Account Deleted Successfully!", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(v.getContext(), "Failed to Delete Account!", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                })
                        .show()
        );
        //Update account button
        binding.accountSubmitButton.setOnClickListener(
                v -> {
                    TuckBoxViewModel.setIsLoading(true);
                    String name = Objects.requireNonNull(binding.accountName.getText()).toString();
                    if (name.length() > 0) {
                        String emailAddress = Objects.requireNonNull(binding.accountEmail.getText()).toString();
                        if (emailAddress.length() > 0) {
                            String phone = Objects.requireNonNull(binding.accountPhone.getText()).toString();
                            if (phone.length() > 0) {
                                String password = Objects.requireNonNull(binding.accountPassword.getText()).toString();
                                if (password.length() >= 7 || password.length() == 0) {
                                    String confirmPassword = Objects.requireNonNull(binding.accountConfirmPassword.getText()).toString();
                                    if (password.equals(confirmPassword)) {
                                        user.setName(name);
                                        user.setEmail(emailAddress);
                                        user.setPhone(phone);
                                        if (password.length() >= 7) {
                                            user.setPassword(password);
                                        }
                                        //Update remote database
                                        MainActivity.getViewModel().updateUser(user).addOnCompleteListener(task -> {
                                            TuckBoxViewModel.setIsLoading(false);
                                            if (task.isSuccessful()) {
                                                Toast.makeText(v.getContext(), "User Updated", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(v.getContext(), "User Update Failed", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } else {
                                        Toast.makeText(v.getContext(), "Please enter the same password", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(v.getContext(), "Please enter a password with 7 or more characters", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(v.getContext(), "Please enter a Phone", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(v.getContext(), "Please enter an Email", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(v.getContext(), "Please enter a Name", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}

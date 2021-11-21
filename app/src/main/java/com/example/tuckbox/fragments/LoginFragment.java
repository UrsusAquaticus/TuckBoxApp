package com.example.tuckbox.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.tuckbox.AuthenticationActivity;
import com.example.tuckbox.MainActivity;
import com.example.tuckbox.datamodel.TuckBoxViewModel;
import com.example.tuckbox.databinding.FragmentLoginBinding;
import com.example.tuckbox.datamodel.entity.User;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class LoginFragment extends Fragment {

    //todo: make a class for input validators


    FragmentLoginBinding binding;
    TuckBoxViewModel viewModel;

    public FragmentLoginBinding getBinding() {
        return binding;
    }

    public LoginFragment() {
        //Apparently need this
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        viewModel = AuthenticationActivity.getViewModel();
        connectLoginButton();
        connectLoading();
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        connectLoading();
    }

    private void connectLoading() {
        TuckBoxViewModel.isLoading.observe(getViewLifecycleOwner(), isCurrentlyLoading -> {
            binding.loginSubmitButton.setEnabled(!isCurrentlyLoading);
            binding.loginEmail.setCursorVisible(!isCurrentlyLoading);
            binding.loginEmailLayout.setEnabled(!isCurrentlyLoading);
            binding.loginPassword.setCursorVisible(!isCurrentlyLoading);
            binding.loginPasswordLayout.setEnabled(!isCurrentlyLoading);
        });
    }

    private void connectLoginButton() {
        binding.loginSubmitButton.setOnClickListener(v -> {
            String email = Objects.requireNonNull(binding.loginEmail.getText()).toString();
            String password = Objects.requireNonNull(binding.loginPassword.getText()).toString();
            Log.d("LOGIN", email + " " + password);

            if (email.length() >= 2) { // replace this with registration's validator
                if (password.length() >= 7) {
                    viewModel.loginUser(email, password, requireActivity());
                } else {
                    Toast.makeText(getActivity(), "Password must be longer than 2 Characters", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "Email must be longer than 2 Characters", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

package com.example.tuckbox.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tuckbox.MainActivity;
import com.example.tuckbox.R;
import com.example.tuckbox.datamodel.TuckBoxViewModel;
import com.example.tuckbox.databinding.FragmentHistoryBinding;
import com.example.tuckbox.datamodel.recycler.HistoryViewAdapter;

public class HistoryFragment extends Fragment {

    //todo: Separate most recent order

    FragmentHistoryBinding binding;
    TuckBoxViewModel viewModel;

    public HistoryFragment() {
        //Apparently needed
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        viewModel = MainActivity.getViewModel();
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();

        RecyclerView recyclerView = binding.historyRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        SharedPreferences preferences = requireActivity().getSharedPreferences(
                TuckBoxViewModel.USER_PREF_DATA,
                Context.MODE_PRIVATE);
        long userId = preferences.getLong(TuckBoxViewModel.USER_PREF_USER_ID, -1);

        viewModel.getOrderHistoryByUserId(userId).observe(getViewLifecycleOwner(),
                orderWithHistories -> {
                    recyclerView.setAdapter(
                            new HistoryViewAdapter(
                                    R.layout.history_list_item_layout,
                                    orderWithHistories
                            )
                    );
                    binding.setIsEmpty(orderWithHistories.isEmpty());
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

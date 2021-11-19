package com.example.tuckbox.datamodel.recycler;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tuckbox.BR;

public class BaseViewHolder extends RecyclerView.ViewHolder {

    //https://medium.com/androiddevelopers/android-data-binding-recyclerview-db7c40d9f0e4

    private final ViewDataBinding binding;

    public BaseViewHolder(ViewDataBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(Object obj) {
        binding.setVariable(BR.obj, obj);
        binding.executePendingBindings();
    }

}

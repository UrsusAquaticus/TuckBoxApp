package com.example.tuckbox.datamodel.recycler;

import androidx.annotation.NonNull;

import com.example.tuckbox.datamodel.entity.Address;

import java.util.List;

public class AddressViewAdapter extends SingleLayoutAdapter {
    //https://medium.com/androiddevelopers/android-data-binding-recyclerview-db7c40d9f0e4

    List<Address> addressList = null;

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        Object obj = getObjForPosition(position);
        holder.bind(obj);
    }

    public AddressViewAdapter(int layoutId, List<Address> addressList) {
        super(layoutId);
        this.addressList = addressList;
    }

    @Override
    protected Object getObjForPosition(int position) {
        return addressList.get(position);
    }

    @Override
    public int getItemCount() {
        if (addressList == null) {
            return 0;
        } else {
            return addressList.size();
        }
    }
}

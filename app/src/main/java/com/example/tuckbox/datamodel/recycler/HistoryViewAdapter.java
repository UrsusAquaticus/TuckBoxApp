package com.example.tuckbox.datamodel.recycler;

import androidx.annotation.NonNull;

import com.example.tuckbox.datamodel.relations.OrderWithHistory;

import java.util.List;

public class HistoryViewAdapter extends SingleLayoutAdapter {
    //https://medium.com/androiddevelopers/android-data-binding-recyclerview-db7c40d9f0e4

    List<OrderWithHistory> orderList = null;

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        Object obj = getObjForPosition(position);
        holder.bind(obj);
    }

    public HistoryViewAdapter(int layoutId, List<OrderWithHistory> orderList) {
        super(layoutId);
        this.orderList = orderList;
    }

    @Override
    protected Object getObjForPosition(int position) {
        return orderList.get(position);
    }

    @Override
    public int getItemCount() {
        if (orderList == null) {
            return 0;
        } else {
            return orderList.size();
        }
    }
}

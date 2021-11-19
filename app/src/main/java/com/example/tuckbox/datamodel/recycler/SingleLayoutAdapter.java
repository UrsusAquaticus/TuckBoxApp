package com.example.tuckbox.datamodel.recycler;

public abstract class SingleLayoutAdapter extends BaseViewAdapter {
    //https://medium.com/androiddevelopers/android-data-binding-recyclerview-db7c40d9f0e4

    private final int layoutId; // eg. R.layout.object_list_item_layout

    public SingleLayoutAdapter(int layoutId) {
        this.layoutId = layoutId;
    }

    @Override
    protected int getLayoutIdForPosition(int position) {
        return layoutId;
    }
}

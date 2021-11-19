package com.example.tuckbox;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.example.tuckbox.databinding.OrderListHeaderLayoutBinding;

public class OrderListHeader extends LinearLayout {
    OrderListHeaderLayoutBinding binding;
    String name;
    //    String option;
    String amount;
    String subTotal;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        binding.headerItemName.setText(name);
        invalidate();
        requestLayout();
    }

//    public String getOption() {
//        return option;
//    }
//
//    public void setOption(String option) {
//        this.option = option;
//        invalidate();
//        requestLayout();
//    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
        binding.headerItemCount.setText(amount);
        invalidate();
        requestLayout();
    }

    public String getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(String subTotal) {
        this.subTotal = subTotal;
        binding.headerItemPrice.setText(subTotal);
        invalidate();
        requestLayout();
    }

    public OrderListHeader(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        binding = OrderListHeaderLayoutBinding.inflate(LayoutInflater.from(context),
                this,
                true);
        inflate(getContext(), R.layout.order_list_header_layout, this);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.OrderListHeader,
                0, 0);
        try {
            setName(a.getString(R.styleable.OrderListHeader_foodName));
//            setOption(a.getString(R.styleable.OrderListHeader_foodOption));
//            binding.headerItemOption.setText(option);
            setAmount(a.getString(R.styleable.OrderListHeader_amount));
            setSubTotal(a.getString(R.styleable.OrderListHeader_subTotal));
        } finally {
            a.recycle();
        }
    }
}

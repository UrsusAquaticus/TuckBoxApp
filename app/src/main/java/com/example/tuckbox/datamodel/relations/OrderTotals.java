package com.example.tuckbox.datamodel.relations;

public class OrderTotals {
    String itemCount;
    String totalPrice;

    public String getItemCount() {
        return itemCount;
    }

    public void setItemCount(String itemCount) {
        this.itemCount = itemCount;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public OrderTotals(String itemCount, String totalPrice) {
        this.itemCount = itemCount;
        this.totalPrice = totalPrice;
    }
}

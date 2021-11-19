package com.example.tuckbox.datamodel.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

@Entity(
        tableName = "Addresses"
)
public class Address extends BaseEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "address_id")
    Long id;
    @ColumnInfo(name = "store_id", index = true)
    Long storeId;
    @ColumnInfo(name = "user_id", index = true)
    Long userId;
    @ColumnInfo(name = "street_address")
    String streetAddress;
    String suburb;
    String city;
    @ColumnInfo(name = "post_code")
    String postcode;

    final public static String COLLECTION = "ADDRESSES";
//    final static String STORE_ID = "STORE_ID";
//    final static String USER_ID = "USER_ID";
//    final static String STREET_ADDRESS = "STREET_ADDRESS";
//    final static String SUBURB = "SUBURB";
//    final static String CITY = "CITY";
//    final static String POST_CODE = "POST_CODE";

    @Ignore
    public Address() {
    }

    public Address(Long id, Long storeId, Long userId, String streetAddress, String suburb, String city, String postcode) {
        this.id = id;
        this.storeId = storeId;
        this.userId = userId;
        this.streetAddress = streetAddress;
        this.suburb = suburb;
        this.city = city;
        this.postcode = postcode;
    }

//    @Override
//    public Address setEntityFromDoc(QueryDocumentSnapshot doc) {
//        //Create from firebase doc
//        Map<String, Object> map = doc.getData();
//        this.id = (Long) map.get(ENTITY_ID);
//        this.storeId = (Long) map.get(STORE_ID);
//        this.userId = (Long) map.get(USER_ID);
//        this.streetAddress = (String) map.get(STREET_ADDRESS);
//        this.suburb = (String) map.get(SUBURB);
//        this.city = (String) map.get(CITY);
//        this.postcode = (String) map.get(POST_CODE);
//        return this;
//    }

    @Exclude
    @Override
    public String getDataCollection() {
        return COLLECTION;
    }

//    @Override
//    public Map<String, Object> getMap() {
//        Map<String, Object> map = new HashMap<>();
//        map.put(ENTITY_ID, this.id);
//        map.put(STORE_ID, this.storeId);
//        map.put(USER_ID, this.userId);
//        map.put(STREET_ADDRESS, this.streetAddress);
//        map.put(SUBURB, this.suburb);
//        map.put(CITY, this.city);
//        map.put(POST_CODE, this.postcode);
//        return map;
//    }

    @NonNull
    @Override
    public String toString() {
        return streetAddress + ", " +
                city + ", " +
                postcode;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getSuburb() {
        return suburb;
    }

    public void setSuburb(String suburb) {
        this.suburb = suburb;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }
}

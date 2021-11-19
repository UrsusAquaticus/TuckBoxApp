package com.example.tuckbox.datamodel.entity;

import static androidx.room.ForeignKey.CASCADE;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Entity(
        tableName = "Stores",
        foreignKeys = {
                @ForeignKey(
                        entity = Address.class,
                        parentColumns = "address_id",
                        childColumns = "address_id",
                        onDelete = CASCADE
                ),
        }
)
public class Store extends BaseEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "store_id")
    Long id;
    @ColumnInfo(name = "address_id", index = true)
    Long addressId;
    String name;

    @Exclude
    final public static String COLLECTION = "STORES";
//    final public static String ADDRESS_ID = "ADDRESS_ID";
//    final public static String NAME = "NAME";

    @Ignore
    public Store() {
    }

    public Store(Long id, Long addressId, String name) {
        this.id = id;
        this.addressId = addressId;
        this.name = name;
    }

    @Exclude
    @Override
    public String getDataCollection() {
        return COLLECTION;
    }

//    @Override
//    public Map<String, Object> getMap() {
//        Map<String, Object> map = new HashMap<>();
//        map.put(ENTITY_ID, this.id);
//        map.put(ADDRESS_ID, this.addressId);
//        map.put(NAME, this.name);
//        return map;
//    }
//
//    @Override
//    public Store setEntityFromDoc(QueryDocumentSnapshot doc) {
//        //Create from firebase doc
//        Map<String, Object> map = doc.getData();
//        this.id = (Long) map.get(ENTITY_ID);
//        this.addressId = (Long) map.get(ADDRESS_ID);
//        this.name = (String) map.get(NAME);
//        return this;
//    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

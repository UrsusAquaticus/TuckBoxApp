package com.example.tuckbox.datamodel.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Entity(
        tableName = "Orders"
)

public class Order extends BaseEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "order_id")
    Long id;
    @ColumnInfo(name = "user_id", index = true)
    Long userId;
    @ColumnInfo(name = "address_id", index = true)
    Long addressId;
    @ColumnInfo(name = "store_id", index = true)
    Long storeId;
    @ColumnInfo(name = "timeslot_id")
    Long timeslotId;
    Date timestamp;
    String notes;

    //The values at the time the order was created
    @ColumnInfo(name = "snapshot_user")
    String snapshotUser;
    @ColumnInfo(name = "snapshot_address")
    String snapshotAddress;
    @ColumnInfo(name = "snapshot_store")
    String snapshotStore;

    @Exclude
    final public static String COLLECTION = "ORDERS";
//    final public static String USER_ID = "USER_ID";
//    final public static String ADDRESS_ID = "ADDRESS_ID";
//    final public static String STORE_ID = "STORE_ID";
//    final public static String TIMESLOT_ID = "TIMESLOT_ID";
//    final public static String TIMESTAMP = "TIMESTAMP";
//    final public static String NOTES = "NOTES";
//    final public static String SNAPSHOT_USER = "SNAPSHOT_USER";
//    final public static String SNAPSHOT_ADDRESS = "SNAPSHOT_ADDRESS";
//    final public static String SNAPSHOT_STORE = "SNAPSHOT_STORE";

    @Ignore
    public Order() {
    }

    public Order(Long id, Long userId, Long addressId, Long storeId, Long timeslotId, Date timestamp, String notes, String snapshotUser, String snapshotAddress, String snapshotStore) {
        this.id = id;
        this.userId = userId;
        this.addressId = addressId;
        this.storeId = storeId;
        this.timeslotId = timeslotId;
        this.timestamp = timestamp;
        this.notes = notes;
        this.snapshotUser = snapshotUser;
        this.snapshotAddress = snapshotAddress;
        this.snapshotStore = snapshotStore;
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
//        map.put(USER_ID, this.userId);
//        map.put(ADDRESS_ID, this.addressId);
//        map.put(STORE_ID, this.storeId);
//        map.put(TIMESLOT_ID, this.timeslotId);
//        map.put(TIMESTAMP, this.timestamp);
//        map.put(NOTES, this.notes);
//        map.put(SNAPSHOT_USER, this.snapshotUser);
//        map.put(SNAPSHOT_ADDRESS, this.snapshotAddress);
//        map.put(SNAPSHOT_STORE, this.snapshotStore);
//        return map;
//    }
//
//    @Override
//    public Order setEntityFromDoc(QueryDocumentSnapshot doc) {
//        //Create from firebase doc
//        Map<String, Object> map = doc.getData();
//        this.id = (Long) map.get(ENTITY_ID);
//        this.userId = (Long) map.get(USER_ID);
//        this.addressId = (Long) map.get(ADDRESS_ID);
//        this.storeId = (Long) map.get(STORE_ID);
//        this.timeslotId = (Long) map.get(TIMESLOT_ID);
//        this.timestamp = (Date) map.get(TIMESTAMP);
//        this.notes = (String) map.get(NOTES);
//        this.snapshotUser = (String) map.get(SNAPSHOT_USER);
//        this.snapshotAddress = (String) map.get(SNAPSHOT_ADDRESS);
//        this.snapshotStore = (String) map.get(SNAPSHOT_STORE);
//        return this;
//    }

    @NonNull
    @Override
    public String toString() {
        return "User: " + this.userId + "/" + this.timestamp;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public Long getTimeslotId() {
        return timeslotId;
    }

    public void setTimeslotId(Long timeslotId) {
        this.timeslotId = timeslotId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getSnapshotUser() {
        return snapshotUser;
    }

    public void setSnapshotUser(String snapshotUser) {
        this.snapshotUser = snapshotUser;
    }

    public String getSnapshotAddress() {
        return snapshotAddress;
    }

    public void setSnapshotAddress(String snapshotAddress) {
        this.snapshotAddress = snapshotAddress;
    }

    public String getSnapshotStore() {
        return snapshotStore;
    }

    public void setSnapshotStore(String snapshotStore) {
        this.snapshotStore = snapshotStore;
    }
}

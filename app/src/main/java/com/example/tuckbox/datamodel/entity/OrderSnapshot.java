package com.example.tuckbox.datamodel.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.firestore.Exclude;

import java.util.Date;

@Entity(
        tableName = "OrderSnapshots"
)
public class OrderSnapshot extends BaseEntity {
    //todo: finish implementing this to take over for displaying order history
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "order_snapshot_id")
    Long id;
    @ColumnInfo(name = "order_id")
    Long orderid;
    @ColumnInfo(name = "user_id", index = true)
    Long userId;
    Date timestamp;
    //The values at the time the order was created
    @ColumnInfo(name = "snapshot_user")
    String snapshotUser;
    @ColumnInfo(name = "snapshot_address")
    String snapshotAddress;
    @ColumnInfo(name = "snapshot_store")
    String snapshotStore;

    @Exclude
    final public static String COLLECTION = "ORDER_SNAPSHOTS";

    @Override
    public String getDataCollection() {
        return COLLECTION;
    }

    @Override
    public Long getId() {
        return null;
    }

    @Override
    public void setId(Long id) {

    }

    @NonNull
    @Override
    public String toString() {
        return null;
    }
}

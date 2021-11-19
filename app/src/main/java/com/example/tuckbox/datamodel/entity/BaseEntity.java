package com.example.tuckbox.datamodel.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.Serializable;
import java.util.Map;

public abstract class BaseEntity implements Serializable {
    final static String ENTITY_ID = "ENTITY_ID";

    @Ignore
    public BaseEntity() {
    }

    public abstract String getDataCollection();

//    public abstract Map<String, Object> getMap();
//
//    public abstract T setEntityFromDoc(QueryDocumentSnapshot doc);

    public abstract Long getId();

    public abstract void setId(Long id);

    @NonNull
    @Override
    public abstract String toString();
}

package com.example.tuckbox.datamodel.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Entity(
        tableName = "Timeslots"
)
public class Timeslot extends BaseEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "timeslot_id")
    Long id;
    String start;
    String end;

    @Exclude
    final public static String COLLECTION = "TIMESLOTS";
//    final public static String START = "START";
//    final public static String END = "END";

    @Ignore
    public Timeslot() {
    }

    public Timeslot(Long id, String start, String end) {
        this.id = id;
        this.start = start;
        this.end = end;
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
//        map.put(START, this.start);
//        map.put(END, this.end);
//        return map;
//    }
//
//    @Override
//    public Timeslot setEntityFromDoc(QueryDocumentSnapshot doc) {
//        //Create from firebase doc
//        Map<String, Object> map = doc.getData();
//        this.id = (Long) map.get(ENTITY_ID);
//        this.start = (String) map.get(START);
//        this.end = (String) map.get(END);
//        return this;
//    }


    @NonNull
    @Override
    public String toString() {
        return start + " - " + end;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }
}

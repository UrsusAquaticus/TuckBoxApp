package com.example.tuckbox.datamodel.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Entity(
        tableName = "Users",
        indices = {
                @Index(
                        value = "user_id",
                        unique = true
                ),
                @Index(
                        value = "email",
                        unique = true
                )
        }
)
public class User extends BaseEntity {

    // todo: Financial Info Update/Add

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "user_id")
    Long id;
    String name;
    String email;
    String phone;
    String password;

    @Exclude
    final public static String COLLECTION = "USERS";
//    final public static String NAME = "NAME";
//    final public static String EMAIL = "EMAIL";
//    final public static String PHONE = "PHONE";
//    final public static String PASSWORD = "PASSWORD";

    @Ignore
    public User() {
    }

    public User(Long id, String name, String email, String phone, String password) {
        this.id = id;
        this.name = name;
        this.email = email.toLowerCase(Locale.ROOT); //Ease querying
        this.phone = phone;
        this.password = password;
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
//        map.put(NAME, this.name);
//        map.put(EMAIL, this.email);
//        map.put(PHONE, this.phone);
//        map.put(PASSWORD, this.password);
//        return map;
//    }
//
//    @Override
//    public User setEntityFromDoc(QueryDocumentSnapshot doc) {
//        //Create from firebase doc
//        Map<String, Object> map = doc.getData();
//        this.id = (Long) map.get(ENTITY_ID);
//        this.name = (String) map.get(NAME);
//        this.email = (String) map.get(EMAIL);
//        this.phone = (String) map.get(PHONE);
//        this.password = (String) map.get(PASSWORD);
//        return this;
//    }


    @NonNull
    @Override
    public String toString() {
        return this.name + " | " + this.email;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

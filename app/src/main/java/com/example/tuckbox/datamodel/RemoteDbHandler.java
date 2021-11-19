package com.example.tuckbox.datamodel;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.tuckbox.datamodel.entity.Address;
import com.example.tuckbox.datamodel.entity.BaseEntity;
import com.example.tuckbox.datamodel.entity.CartItem;
import com.example.tuckbox.datamodel.entity.Food;
import com.example.tuckbox.datamodel.entity.FoodOption;
import com.example.tuckbox.datamodel.entity.Order;
import com.example.tuckbox.datamodel.entity.Store;
import com.example.tuckbox.datamodel.entity.Timeslot;
import com.example.tuckbox.datamodel.entity.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RemoteDbHandler {

    static RemoteDbHandler Instance = null;
    TuckBoxDataModel dataModel = null;

    static List<ListenerRegistration> listeners = null;
    static String COLLECTION_INFO = "COLLECTION_INFO";

    private RemoteDbHandler(TuckBoxDataModel dataModel) {
        this.dataModel = dataModel;
        listeners = new ArrayList<>();
    }

    public static RemoteDbHandler getInstance(TuckBoxDataModel dataModel) {
        if (Instance == null) {
            Instance = new RemoteDbHandler(dataModel);
        }
        return Instance;
    }

    public void connectDatabases() {
        //Set local from remote making sure parents are inserted first
        //Then add change listeners
        //todo: Insert parent and children as a single object and skip orphans
        setLocalFromRemote(User.COLLECTION).addOnCompleteListener(userTask -> {
            setAutoUpdateListener(User.COLLECTION);
            if (userTask.isSuccessful()) {
                setLocalFromRemote(Address.COLLECTION).addOnCompleteListener(addressTask -> {
                    setAutoUpdateListener(Address.COLLECTION);
                });
            }
        });
        //Food
        setLocalFromRemote(Food.COLLECTION).addOnCompleteListener(foodTask -> {
            setAutoUpdateListener(Food.COLLECTION);
            if (foodTask.isSuccessful()) {
                //Option
                setLocalFromRemote(FoodOption.COLLECTION).addOnCompleteListener(optionTask -> {
                    setAutoUpdateListener(FoodOption.COLLECTION);
                    if (optionTask.isSuccessful()) {
                        //Order
                        setLocalFromRemote(Order.COLLECTION).addOnCompleteListener(orderTask -> {
                            setAutoUpdateListener(Order.COLLECTION);
                            if (orderTask.isSuccessful()) {
                                //Cart Item
                                setLocalFromRemote(CartItem.COLLECTION).addOnCompleteListener(cartItemTask -> {
                                    setAutoUpdateListener(CartItem.COLLECTION);
                                });
                            }
                        });
                    }
                });
            }
        });
        setLocalFromRemote(Store.COLLECTION).addOnCompleteListener(foodTask -> {
            setAutoUpdateListener(Store.COLLECTION);
        });
        setLocalFromRemote(Timeslot.COLLECTION).addOnCompleteListener(foodTask -> {
            setAutoUpdateListener(Timeslot.COLLECTION);
        });
    }


    public Task<QuerySnapshot> getAll(String dataCollection) {
        FirebaseFirestore cloudDb = FirebaseFirestore.getInstance();
        return cloudDb.collection(dataCollection).get();
    }

    public void clearListeners() {
        for (ListenerRegistration registration :
                listeners) {
            registration.remove();
        }
    }

    public Task<Object> setAll(String dataCollection, List<BaseEntity> entities) {
        FirebaseFirestore cloudDb = FirebaseFirestore.getInstance();
        DocumentReference collectionInfo = cloudDb.collection(COLLECTION_INFO).document(dataCollection);
        return cloudDb.runTransaction(transaction -> {
            //Get and update count used for Long id
            Long count = transaction.get(collectionInfo).getLong("count");
            //Go through each entity
            for (final BaseEntity entity : entities) {
                //Get and update count used for id
                count = count == null ? (long) 100000000 : count + 1;// silly number so room auto increment shouldn't conflict
                //Set the entity using new id
                entity.setId(count);
                DocumentReference doc = cloudDb.collection(dataCollection).document(count.toString());
                transaction.set(doc, entity, SetOptions.merge());
            }
            //Update collection count
            Map<String, Object> data = new HashMap<>();
            data.put("count", count);
            transaction.set(collectionInfo, data);
            return null;
        });
    }

    public Task<Long> getId(String dataCollection) {
        FirebaseFirestore cloudDb = FirebaseFirestore.getInstance();
        DocumentReference collectionInfo = cloudDb.collection(COLLECTION_INFO).document(dataCollection);
        return cloudDb.runTransaction(transaction -> {
            //Get and update count used for Long id
            Long count = transaction.get(collectionInfo).getLong("count");
            count = count == null ? (long) 100000000 : count + 1;// silly number so room auto increment shouldn't conflict
            Map<String, Object> data = new HashMap<>();
            data.put("count", count);
            transaction.set(collectionInfo, data);
            Log.d("RDBH", "New ID: " + count);
            return count;
        });
    }

    public Task<Object> set(String dataCollection, BaseEntity entity) {
        FirebaseFirestore cloudDb = FirebaseFirestore.getInstance();
        DocumentReference collectionInfo = cloudDb.collection(COLLECTION_INFO).document(dataCollection);
        return cloudDb.runTransaction(transaction -> {
            //Get and update count used for Long id
            Long count = transaction.get(collectionInfo).getLong("count");
            count = count == null ? (long) 100000000 : count + 1;// silly number so room auto increment shouldn't conflict
            Map<String, Object> data = new HashMap<>();
            data.put("count", count);

            //Set the entity using new Id
            entity.setId(count);
            DocumentReference doc = cloudDb.collection(dataCollection).document(count.toString());

            //Execute setting
            transaction.set(collectionInfo, data);
            transaction.set(doc, entity, SetOptions.merge());
            return null;
        })
                .addOnSuccessListener(unused -> Log.d(dataCollection + "_SET", "Set Success"))
                .addOnFailureListener(e -> {
                    Log.e(dataCollection + "_SET", "Set Failure");
                    Log.e(dataCollection + "_SET", e.getMessage());
                });
    }

    public Task<Object> setWithChildren(String parentDataCollection, BaseEntity parentEntity,
                                        String childDataCollection, List<BaseEntity> childEntities,
                                        String parentIdFieldInChild) {
        FirebaseFirestore cloudDb = FirebaseFirestore.getInstance();
        DocumentReference parentCollectionInfo = cloudDb.collection(COLLECTION_INFO).document(parentDataCollection);
        DocumentReference childCollectionInfo = cloudDb.collection(COLLECTION_INFO).document(childDataCollection);
        return cloudDb.runTransaction(transaction -> {
            //Setup count used for parent id
            Long parentCount = transaction.get(parentCollectionInfo).getLong("count");
            parentCount = parentCount == null ? (long) 100000000 : parentCount + 1;
            Map<String, Object> data = new HashMap<>();
            data.put("count", parentCount);

            //Setup parent entity using new Id
            parentEntity.setId(parentCount);
            DocumentReference doc = cloudDb.collection(parentDataCollection).document(parentCount.toString());

            //Setup Children count
            Long childCount = transaction.get(childCollectionInfo).getLong("count");

            //Set parent
            transaction.set(parentCollectionInfo, data);
            transaction.set(doc, parentEntity, SetOptions.merge());
            //Set children
            for (BaseEntity childEntity : childEntities) {
                //Get and update count used for child id
                childCount = childCount == null ? (long) 100000000 : childCount + 1;
                //Set the entity using new id
                childEntity.setId(childCount);
                DocumentReference childDoc = cloudDb.collection(childDataCollection).document(childCount.toString());
                transaction.set(childDoc, childEntity, SetOptions.merge());
                //Update child with parent's Id
                transaction.update(childDoc, parentIdFieldInChild, parentCount);
            }
            //Update child collection count
            Map<String, Object> childData = new HashMap<>();
            childData.put("count", childCount);
            transaction.set(childCollectionInfo, childData);
            return null;
        })
                .addOnSuccessListener(unused -> Log.d(parentDataCollection + "_WITH_" +
                        childDataCollection + "_SET", "Set Success"))
                .addOnFailureListener(e -> {
                    Log.e(parentDataCollection + "_WITH_" +
                            childDataCollection + "_SET", "Set Failure");
                    Log.e(parentDataCollection + "_WITH_" +
                            childDataCollection + "_SET", e.getMessage());
                });
    }

    public Task<Object> setWithChild(String parentDataCollection, BaseEntity parentEntity,
                                     String childDataCollection, BaseEntity childEntity,
                                     String parentIdFieldInChild) {
        FirebaseFirestore cloudDb = FirebaseFirestore.getInstance();
        DocumentReference parentCollectionInfo = cloudDb.collection(COLLECTION_INFO).document(parentDataCollection);
        DocumentReference childCollectionInfo = cloudDb.collection(COLLECTION_INFO).document(childDataCollection);
        return cloudDb.runTransaction(transaction -> {
            //Setup count used for parent id
            Long parentCount = transaction.get(parentCollectionInfo).getLong("count");
            parentCount = parentCount == null ? (long) 100000000 : parentCount + 1;
            Map<String, Object> data = new HashMap<>();
            data.put("count", parentCount);

            //Setup parent entity using new Id
            parentEntity.setId(parentCount);
            DocumentReference doc = cloudDb.collection(parentDataCollection).document(parentCount.toString());

            //Setup Children count
            Long childCount = transaction.get(childCollectionInfo).getLong("count");

            //Set parent
            transaction.set(parentCollectionInfo, data);
            transaction.set(doc, parentEntity, SetOptions.merge());
            //Set child
            //Get and update count used for child id
            childCount = childCount == null ? (long) 100000000 : childCount + 1;
            //Set the entity using new id
            childEntity.setId(childCount);
            DocumentReference childDoc = cloudDb.collection(childDataCollection).document(childCount.toString());
            transaction.set(childDoc, childEntity, SetOptions.merge());
            //Update child with parent's Id
            transaction.update(childDoc, parentIdFieldInChild, parentCount);

            //Update child collection count
            Map<String, Object> childData = new HashMap<>();
            childData.put("count", childCount);
            transaction.set(childCollectionInfo, childData);
            return null;
        })
                .addOnSuccessListener(unused -> Log.d(parentDataCollection + "_WITH_" +
                        childDataCollection + "_SET", "Set Success"))
                .addOnFailureListener(e -> {
                    Log.e(parentDataCollection + "_WITH_" +
                            childDataCollection + "_SET", "Set Failure");
                    Log.e(parentDataCollection + "_WITH_" +
                            childDataCollection + "_SET", e.getMessage());
                });
    }

    public Task<Void> overwrite(String dataCollection, BaseEntity entity) {
        FirebaseFirestore cloudDb = FirebaseFirestore.getInstance();
        return cloudDb.collection(dataCollection)
                .document(entity.getId().toString())
                .set(entity)
                .addOnSuccessListener(unused -> Log.d(dataCollection + "_OVERWRITE", "Overwrite Success"))
                .addOnFailureListener(e -> {
                    Log.e(dataCollection + "_OVERWRITE", "OVERWRITE Failure");
                    Log.e(dataCollection + "_OVERWRITE", e.getMessage());
                });
    }

    public Task<Void> delete(String dataCollection, BaseEntity entity) {
        FirebaseFirestore cloudDb = FirebaseFirestore.getInstance();
        return cloudDb.collection(dataCollection)
                .document(entity.getId().toString())
                .delete()
                .addOnSuccessListener(unused -> Log.d(dataCollection + "_DELETE", "Delete Success"))
                .addOnFailureListener(e -> {
                    Log.e(dataCollection + "_DELETE", "Delete Failure");
                    Log.e(dataCollection + "_DELETE", e.getMessage());
                });
    }

    public Task<QuerySnapshot> setLocalFromRemote(String dataCollection) {
        FirebaseFirestore cloudDb = FirebaseFirestore.getInstance();
        return cloudDb.collection(dataCollection)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots != null) {
                            //create list of entities to add
                            List<BaseEntity> entities = new ArrayList<>();
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                BaseEntity entity = doc.toObject(
                                        (Class<BaseEntity>) dataModel.getClassFromCollection(dataCollection)
                                );
                                entities.add(entity);
                            }
                            //Insert all entities
                            Log.d("SET_LOCAL_FROM_REMOTE_" + dataCollection, "Delupsert " + entities.size() + " Entities");
                            dataModel.delupsertFromDataCollection(dataCollection, entities);
                        } else {
                            Log.d("SET_LOCAL_FROM_REMOTE_" + dataCollection, "No Documents Returned");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("SET_LOCAL_FROM_REMOTE_" + dataCollection, "Error getting documents: ", e);
                    }
                });
    }

    public void setAutoUpdateListener(String dataCollection) {
        FirebaseFirestore cloudDb = FirebaseFirestore.getInstance();
        ListenerRegistration listenerRegistration = cloudDb.collection(dataCollection)
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
                        Log.w(dataCollection + "_LISTENER", "Listen failed.", e);
                        return;
                    }
                    if (value != null) {
                        for (DocumentChange documentChange : value.getDocumentChanges()) {
                            BaseEntity entity = documentChange.getDocument().toObject(
                                    (Class<BaseEntity>) dataModel.getClassFromCollection(dataCollection)
                            );
                            switch (documentChange.getType()) {
                                case ADDED:
                                    Log.d(dataCollection + "_LISTENER", "New Id: " + entity.getId());
                                    dataModel.insertFromDataCollection(dataCollection, entity);
                                    break;
                                case MODIFIED:
                                    Log.d(dataCollection + "_LISTENER", "Modified Id: " + entity.getId());
                                    dataModel.updateFromDataCollection(dataCollection, entity);
                                    break;
                                case REMOVED:
                                    Log.d(dataCollection + "_LISTENER", "Removed Id: " + entity.getId());
                                    dataModel.deleteFromDataCollection(dataCollection, entity);
                                    break;
                            }
                        }
                    }
                });
        //add to listeners
        listeners.add(listenerRegistration);
    }

    public Task<QuerySnapshot> login(String email, String password) {
        FirebaseFirestore cloudDb = FirebaseFirestore.getInstance();
        return cloudDb.collection(User.COLLECTION)
                .whereEqualTo("email", email)
                .whereEqualTo("password", password)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> Log.d("LOGIN", "Login Success"))
                .addOnFailureListener(e -> Log.e("LOGIN", "Login Failed", e));
    }

    public Task<User> register(User user, Address address) {
        FirebaseFirestore cloudDb = FirebaseFirestore.getInstance();
        DocumentReference userCollectionInfo = cloudDb.collection(COLLECTION_INFO).document(User.COLLECTION);
        DocumentReference addressCollectionInfo = cloudDb.collection(COLLECTION_INFO).document(Address.COLLECTION);
        return cloudDb.runTransaction(transaction -> {
            //Setup count used for USER id
            Long userCount = transaction.get(userCollectionInfo).getLong("count");
            userCount = userCount == null ? (long) 100000000 : userCount + 1;
            Map<String, Object> userCountData = new HashMap<>();
            userCountData.put("count", userCount);

            //Setup the entity using new Id
            user.setId(userCount);
            DocumentReference userDoc = cloudDb.collection(User.COLLECTION).document(userCount.toString());

            //Setup count used for ADDRESS id
            Long addressCount = transaction.get(addressCollectionInfo).getLong("count");
            addressCount = addressCount == null ? (long) 100000000 : addressCount + 1;
            Map<String, Object> addressCountData = new HashMap<>();
            addressCountData.put("count", addressCount);

            //Setup the entity using new Id
            address.setId(addressCount);
            address.setUserId(userCount);
            DocumentReference addressDoc = cloudDb.collection(Address.COLLECTION).document(addressCount.toString());

            //Execute the set functions
            transaction.set(userCollectionInfo, userCountData);
            transaction.set(userDoc, user, SetOptions.merge());
            transaction.set(addressCollectionInfo, addressCountData);
            transaction.set(addressDoc, address, SetOptions.merge());
            return user;
        })
                .addOnSuccessListener(queryDocumentSnapshots -> Log.d("REGISTER", "Registration Success"))
                .addOnFailureListener(e -> Log.e("REGISTER", "Registration Failed", e));
    }

    //Cart items already have a firebase ID
    public Task<Object> placeOrder(Order order, List<CartItem> cartItemList) {
        FirebaseFirestore cloudDb = FirebaseFirestore.getInstance();
        DocumentReference orderCollectionInfo = cloudDb.collection(COLLECTION_INFO).document(Order.COLLECTION);
        DocumentReference cartItemCollectionInfo = cloudDb.collection(COLLECTION_INFO).document(CartItem.COLLECTION);
        return cloudDb.runTransaction(transaction -> {
            //Setup count used for parent id
            Long orderCount = transaction.get(orderCollectionInfo).getLong("count");
            orderCount = orderCount == null ? (long) 100000000 : orderCount + 1;
            Map<String, Object> data = new HashMap<>();
            data.put("count", orderCount);

            //Setup parent entity using new Id
            order.setId(orderCount);
            DocumentReference doc = cloudDb.collection(Order.COLLECTION).document(orderCount.toString());

            //Set parent
            transaction.set(orderCollectionInfo, data);
            transaction.set(doc, order, SetOptions.merge());

            //Set children
            for (CartItem cartItem : cartItemList) {
                DocumentReference childDoc = cloudDb.collection(CartItem.COLLECTION).document(cartItem.getId().toString());
                transaction.set(childDoc, cartItem, SetOptions.merge());
                //Update child with parent's Id
                transaction.update(childDoc, "orderId", orderCount);
            }
            return null;
        })
                .addOnFailureListener(e -> {
                    Log.e("PLACE_ORDER", "Set Failure", e);
                });
    }
}

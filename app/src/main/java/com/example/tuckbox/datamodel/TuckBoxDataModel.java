package com.example.tuckbox.datamodel;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.tuckbox.R;
import com.example.tuckbox.datamodel.dao.AddressDao;
import com.example.tuckbox.datamodel.dao.CartItemDao;
import com.example.tuckbox.datamodel.dao.FoodDao;
import com.example.tuckbox.datamodel.dao.FoodOptionDao;
import com.example.tuckbox.datamodel.dao.OrderDao;
import com.example.tuckbox.datamodel.dao.StoreDao;
import com.example.tuckbox.datamodel.dao.TimeslotDao;
import com.example.tuckbox.datamodel.dao.UserDao;
import com.example.tuckbox.datamodel.entity.Address;
import com.example.tuckbox.datamodel.entity.BaseEntity;
import com.example.tuckbox.datamodel.entity.CartItem;
import com.example.tuckbox.datamodel.entity.Food;
import com.example.tuckbox.datamodel.entity.FoodOption;
import com.example.tuckbox.datamodel.entity.Order;
import com.example.tuckbox.datamodel.relations.CartItemWithFoodOption;
import com.example.tuckbox.datamodel.relations.FoodWithOptions;
import com.example.tuckbox.datamodel.entity.Store;
import com.example.tuckbox.datamodel.entity.Timeslot;
import com.example.tuckbox.datamodel.entity.User;
import com.example.tuckbox.datamodel.relations.OrderWithHistory;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TuckBoxDataModel {

    static TuckBoxDataModel Instance = null;

    private final AddressDao addressDao;
    private final CartItemDao cartItemDao;
    private final FoodDao foodDao;
    private final FoodOptionDao foodOptionDao;
    private final OrderDao orderDao;
    private final StoreDao storeDao;
    private final TimeslotDao timeslotDao;
    private final UserDao userDao;

    private final RemoteDbHandler remoteDbHandler;

    private TuckBoxDataModel(Application application) {
        TuckBoxDatabase db = TuckBoxDatabase.createInstance(application);
        addressDao = db.getAddressDao();
        cartItemDao = db.getCartItemDao();
        foodDao = db.getFoodDao();
        foodOptionDao = db.getFoodOptionDao();
        orderDao = db.getOrderDao();
        storeDao = db.getStoreDao();
        timeslotDao = db.getTimeslotDao();
        userDao = db.getUserDao();
        remoteDbHandler = RemoteDbHandler.getInstance(this);
    }

    public static TuckBoxDataModel getInstance(Application application) {
        if (Instance == null) {
            Instance = new TuckBoxDataModel(application);
        }
        return Instance;
    }

    //Probably a better way to do this
    public Task<Void> initialImport() {
        //Connect to database normally
        return remoteDbHandler.initialImport();
    }

    public Task<Void> secondImport() {
        //Connect to database normally
        return remoteDbHandler.secondImport();
    }

    public Task<Void> thirdImport() {
        //Connect to database normally
        return remoteDbHandler.thirdImport();
    }

    public Task<Void> addDummyData() {
        List<Task<?>> tasks = new ArrayList<>();
        //Add dummy data
        tasks.addAll(dummyFoodTasks());
        tasks.addAll(dummyStoreTasks());
        tasks.add(dummyUserTask());
        tasks.add(dummyTimeslotTask());
        return Tasks.whenAll(tasks);
    }

    public void setUpdateListeners() {
        remoteDbHandler.setAutoUpdateListeners();
    }

    public void setUpdateListener(String dataCollection) {
        remoteDbHandler.setAutoUpdateListener(dataCollection);
    }

    public Task<QuerySnapshot> getAllCollectionInfo() {
        return remoteDbHandler.getAll(RemoteDbHandler.COLLECTION_INFO);
    }

    public Class<?> getClassFromCollection(String dataCollection) {
        switch (dataCollection) {
            case Address.COLLECTION:
                return Address.class;
            case CartItem.COLLECTION:
                return CartItem.class;
            case Food.COLLECTION:
                return Food.class;
            case FoodOption.COLLECTION:
                return FoodOption.class;
            case Order.COLLECTION:
                return Order.class;
            case Store.COLLECTION:
                return Store.class;
            case Timeslot.COLLECTION:
                return Timeslot.class;
            case User.COLLECTION:
                return User.class;
            default:
                Log.e("DATAMODEL_GET_CLASS", "Unrecognised Datacollection");
                return null;
        }
    }

    public void delupsertFromDataCollection(String
                                                    dataCollection, List<BaseEntity> entities) {
        //delete update insert
        //upsert and delsert
        //https://www.py4u.net/discuss/668276
        switch (dataCollection) {
            case Address.COLLECTION:
                addressDao.delupsert(new ArrayList<>((List<Address>) (List<?>) entities));
                break;
            case CartItem.COLLECTION:
                cartItemDao.upsert(new ArrayList<>((List<CartItem>) (List<?>) entities));
                break;
            case Food.COLLECTION:
                foodDao.delupsert(new ArrayList<>((List<Food>) (List<?>) entities));
                break;
            case FoodOption.COLLECTION:
                foodOptionDao.delupsert(new ArrayList<>((List<FoodOption>) (List<?>) entities));
                break;
            case Order.COLLECTION:
                orderDao.delupsert(new ArrayList<>((List<Order>) (List<?>) entities));
                break;
            case Store.COLLECTION:
                storeDao.delupsert(new ArrayList<>((List<Store>) (List<?>) entities));
                break;
            case Timeslot.COLLECTION:
                timeslotDao.delupsert(new ArrayList<>((List<Timeslot>) (List<?>) entities));
                break;
            case User.COLLECTION:
                userDao.delupsert(new ArrayList<>((List<User>) (List<?>) entities));
                break;
            default:
                Log.e("DATAMODEL_REMOTE_UPSERT", "Unrecognised Datacollection");
                break;
        }
    }

    public long insertFromDataCollection(String dataCollection, BaseEntity entity) {
        //todo: Read this https://docs.oracle.com/javase/tutorial/java/generics/subtyping.html
        switch (dataCollection) {
            case Address.COLLECTION:
                return addressDao.insert((Address) entity);
            case CartItem.COLLECTION:
                return cartItemDao.upsert((CartItem) entity);
            case Food.COLLECTION:
                return foodDao.insert((Food) entity);
            case FoodOption.COLLECTION:
                return foodOptionDao.insert((FoodOption) entity);
            case Order.COLLECTION:
                return orderDao.insert((Order) entity);
            case Store.COLLECTION:
                return storeDao.insert((Store) entity);
            case Timeslot.COLLECTION:
                return timeslotDao.insert((Timeslot) entity);
            case User.COLLECTION:
                return userDao.insert((User) entity);
            default:
                Log.e("DATAMODEL_REMOTE_SET", "Unrecognised Datacollection");
                return -1;
        }
    }

    public long updateFromDataCollection(String dataCollection, BaseEntity entity) {
        switch (dataCollection) {
            case Address.COLLECTION:
                return addressDao.upsert((Address) entity);
            case CartItem.COLLECTION:
                return cartItemDao.upsert((CartItem) entity);
            case Food.COLLECTION:
                return foodDao.upsert((Food) entity);
            case FoodOption.COLLECTION:
                return foodOptionDao.upsert((FoodOption) entity);
            case Order.COLLECTION:
                return orderDao.upsert((Order) entity);
            case Store.COLLECTION:
                return storeDao.upsert((Store) entity);
            case Timeslot.COLLECTION:
                return timeslotDao.upsert((Timeslot) entity);
            case User.COLLECTION:
                return userDao.upsert((User) entity);
            default:
                Log.e("DATAMODEL_REMOTE_SET", "Unrecognised Datacollection");
                return -1;
        }
    }

    public long deleteFromDataCollection(String dataCollection, BaseEntity entity) {
        switch (dataCollection) {
            case Address.COLLECTION:
                return addressDao.delete((Address) entity);
            case CartItem.COLLECTION:
                return cartItemDao.delete((CartItem) entity);
            case Food.COLLECTION:
                return foodDao.delete((Food) entity);
            case FoodOption.COLLECTION:
                return foodOptionDao.delete((FoodOption) entity);
            case Order.COLLECTION:
                return orderDao.delete((Order) entity);
            case Store.COLLECTION:
                return storeDao.delete((Store) entity);
            case Timeslot.COLLECTION:
                return timeslotDao.delete((Timeslot) entity);
            case User.COLLECTION:
                return userDao.delete((User) entity);
            default:
                Log.e("DATAMODEL_REMOTE_SET", "Unrecognised Datacollection");
                return -1;
        }
    }

    //Address
    public LiveData<List<Address>> getLiveAddressesByUserId(long userId) {
        return addressDao.getLiveAddressesByUserId(userId);
    }

    public Task<Object> insertAddress(Address address) {
        return remoteDbHandler.set(Address.COLLECTION, address);

    }

    public Task<Void> deleteAddress(Address address) {
        return remoteDbHandler.delete(Address.COLLECTION, address);
    }

    //CartItem
    public LiveData<List<CartItemWithFoodOption>> getLiveCartItemsWithFoodOption() {
        return cartItemDao.getLiveAllCartItems();
    }

    public Task<Long> insertCartItem(CartItem cartItem) {
        //Check if Food with option is already in the cart
        CartItem existingCartItem = cartItemDao.getCartItemByFoodOptionId(cartItem.getFoodOptionId());
        if (existingCartItem != null) {
            //Increase amount
            existingCartItem.addAmount();
            cartItemDao.update(existingCartItem);
            return null;
        } else {
            long oldId = cartItemDao.insert(cartItem);
            cartItem.setId(oldId);
            Log.d("TDM", cartItem.toString());
            return remoteDbHandler.getId(CartItem.COLLECTION)
                    .addOnSuccessListener(
                            newId -> cartItemDao.updateId(oldId, newId)
                    )
                    .addOnFailureListener(
                            e -> Log.e("CART_ITEM_INSERT", "Failed to get a new ID for Cart Item", e));
        }
    }

    public int reduceCartItem(CartItem cartItem) {
        //Check if Food with option is already in the cart
        CartItem existingCartItem = cartItemDao.getCartItemByFoodOptionId(cartItem.getFoodOptionId());
        if (existingCartItem != null) {
            if (existingCartItem.getAmount() > 1) {
                //Reduce amount
                existingCartItem.reduceAmount();
                cartItemDao.update(existingCartItem);
                return -2;
            } else {
                return cartItemDao.delete(cartItem);
            }
        } else {
            return -1;
        }
    }

    public void clearCartItems() {
        cartItemDao.nukeTable();
    }

    //Food
    public LiveData<List<Food>> getLiveFood() {
        return foodDao.getLiveAll();
    }

    //Food With Options
    public LiveData<List<FoodWithOptions>> getFoodWithOptions() {
        return foodDao.getFoodWithOptions();
    }

    //Order
    public Task<Order> insertOrderAndCartItems(Order order, List<CartItem> cartItems) {
        //Clear listeners and add them back in so that cart items shouldn't? be updated
        //before order comes back to be inserted
        remoteDbHandler.removeListener(CartItem.COLLECTION);
        return remoteDbHandler.placeOrder(
                order,
                new ArrayList<>(cartItems));
    }

    public LiveData<List<OrderWithHistory>> getOrderHistoryByUserId(long userId) {
        return orderDao.getOrderHistory(userId);
    }

    //Store
    public LiveData<List<Store>> getAllStores() {
        return storeDao.getLiveAll();
    }

    //Timeslot
    public LiveData<List<Timeslot>> getAllTimeslots() {
        return timeslotDao.getLiveAll();
    }

    //User
    public Task<Void> updateUser(User user) {
        user.setEmail(user.getEmail().toLowerCase(Locale.ROOT));//Force lowercase to ease query
        return remoteDbHandler.overwrite(User.COLLECTION, user);
    }

    public LiveData<User> getUserById(long userId) {
        return userDao.getUserById(userId);
    }

    public Task<Void> deleteUser(User user) {
        return remoteDbHandler.delete(User.COLLECTION, user);
    }

    public Task<QuerySnapshot> login(String email, String password) {
        email = email.toLowerCase(Locale.ROOT); //Force lowercase to ease query
        return remoteDbHandler.login(email, password);
    }

    public Task<User> register(User user, Address address) {
        return remoteDbHandler.register(user, address);
    }

    //Dummy
    private Task<Object> dummyUserTask() {
        User user = new User(
                null,
                "Name",
                "Test@Email.com",
                "0210210210",
                "password"
        );
        List<BaseEntity> addresses = new ArrayList<>();
        addresses.add(new Address(
                null,
                null,
                null,
                "123 Test St",
                "Test Suburb",
                "Test City",
                "0001"
        ));
        addresses.add(new Address(
                null,
                null,
                null,
                "1 Testing Pl",
                "Other Test Suburb",
                "Other Test City",
                "0002"
        ));
        return remoteDbHandler.setWithChildren(
                User.COLLECTION, user,
                Address.COLLECTION, addresses,
                "userId");
    }

    private List<Task<?>> dummyFoodTasks() {
        List<Task<?>> tasks = new ArrayList<>();
        List<BaseEntity> foodOptions = new ArrayList<>();
        //Salad
        foodOptions.add(new FoodOption(
                null,
                null,
                "None",
                "Dressing",
                0
        ));
        foodOptions.add(new FoodOption(
                null,
                null,
                "Ranch",
                "Dressing",
                0
        ));
        foodOptions.add(new FoodOption(
                null,
                null,
                "Vinaigrette",
                "Dressing",
                0f
        ));
        tasks.add(remoteDbHandler.setWithChildren(
                Food.COLLECTION,
                new Food(
                        null,
                        "Green Salad Lunch",
                        "This sweet salad is perfect teamed with Thai, " +
                                "Chinese and Japanese meals to give a bit " +
                                "of colour and crunch",
                        "Salad",
                        R.drawable.greensalad,
                        10.00
                ),
                FoodOption.COLLECTION,
                new ArrayList<>(foodOptions),
                "foodId"));
        //Curry
        foodOptions.clear();
        foodOptions.add(new FoodOption(
                null,
                null,
                "Mild",
                "Spice",
                0f
        ));
        foodOptions.add(new FoodOption(
                null,
                null,
                "Medium",
                "Spice",
                0f
        ));
        foodOptions.add(new FoodOption(
                null,
                null,
                "Hot",
                "Spice",
                0f
        ));
        tasks.add(remoteDbHandler.setWithChildren(
                Food.COLLECTION,
                new Food(
                        null,
                        "Lamb Korma",
                        "Korma or qorma is a dish originating in the " +
                                "Indian subcontinent, consisting of meat or " +
                                "vegetables braised with yogurt (dahi) or cream, " +
                                "water or stock, and spices to produce a thick " +
                                "sauce or gravy.",
                        "Curry",
                        R.drawable.curry,
                        16.00
                ),
                FoodOption.COLLECTION,
                new ArrayList<>(foodOptions),
                "foodId"));
        //Sandwich
        foodOptions.clear();
        foodOptions.add(new FoodOption(
                null,
                null,
                "White",
                "Bread",
                0f
        ));
        foodOptions.add(new FoodOption(
                null,
                null,
                "Rye",
                "Bread",
                0f
        ));
        foodOptions.add(new FoodOption(
                null,
                null,
                "Wholemeal",
                "Bread",
                0f
        ));
        tasks.add(remoteDbHandler.setWithChildren(
                Food.COLLECTION,
                new Food(
                        null,
                        "Open Chicken Sandwich",
                        "A chicken sandwich is a sandwich that typically " +
                                "consists of boneless, skinless chicken breast " +
                                "served between slices of bread, on a bun, or " +
                                "on a roll. Variations on the 'chicken sandwich'" +
                                " include the chicken burger or chicken on a bun, " +
                                "hot chicken, and chicken salad sandwich.",
                        "Sandwich",
                        R.drawable.sandw,
                        7.50
                ),
                FoodOption.COLLECTION,
                new ArrayList<>(foodOptions),
                "foodId"));
        //Beef noodle salad
        foodOptions.clear();
        foodOptions.add(new FoodOption(
                null,
                null,
                "None",
                "Chilli Flakes",
                0f
        ));
        foodOptions.add(new FoodOption(
                null,
                null,
                "Regular",
                "Chilli Flakes",
                0f
        ));
        foodOptions.add(new FoodOption(
                null,
                null,
                "Extra",
                "Chilli Flakes",
                0f
        ));
        tasks.add(remoteDbHandler.setWithChildren(
                Food.COLLECTION,
                new Food(
                        null,
                        "Beef Noodle Salad",
                        "Turn up the heat with this tasty Thai classic – " +
                                "it's fast, fresh and seriously fabulous, plus most " +
                                "of it can be made ahead.",
                        "Salad",
                        R.drawable.noodle,
                        13.00
                ),
                FoodOption.COLLECTION,
                new ArrayList<>(foodOptions),
                "foodId"));
        //Sushi
        foodOptions.clear();
        foodOptions.add(new FoodOption(
                null,
                null,
                "Salmon",
                "Sushi",
                0f
        ));
        foodOptions.add(new FoodOption(
                null,
                null,
                "Chicken",
                "Sushi",
                0f
        ));
        foodOptions.add(new FoodOption(
                null,
                null,
                "Tofu",
                "Sushi",
                0f
        ));
        tasks.add(remoteDbHandler.setWithChildren(
                Food.COLLECTION,
                new Food(
                        null,
                        "Sushi 12pc",
                        "Sushi is a traditional Japanese dish of" +
                                " prepared vinegared rice, usually with some " +
                                "sugar and salt, accompanied by a variety of " +
                                "ingredients, such as seafood, often raw, and vegetables.",
                        "Sushi",
                        R.drawable.sushi,
                        12.00
                ),
                FoodOption.COLLECTION,
                new ArrayList<>(foodOptions),
                "foodId"));
        //Kebab
        foodOptions.clear();
        foodOptions.add(new FoodOption(
                null,
                null,
                "Lamb",
                "Kebab",
                0f
        ));
        foodOptions.add(new FoodOption(
                null,
                null,
                "Beef",
                "Kebab",
                0f
        ));
        foodOptions.add(new FoodOption(
                null,
                null,
                "Chicken",
                "Kebab",
                0f
        ));
        foodOptions.add(new FoodOption(
                null,
                null,
                "Falafel",
                "Kebab",
                0f
        ));
        tasks.add(remoteDbHandler.setWithChildren(
                Food.COLLECTION,
                new Food(
                        null,
                        "Doner Kebab",
                        "Doner kebab is a type of kebab, made of meat " +
                                "cooked on a vertical rotisserie. Seasoned meat " +
                                "stacked in the shape of an inverted cone is turned " +
                                "slowly on the rotisserie, next to a vertical cooking element. ",
                        "Kebab",
                        R.drawable.kebab,
                        10.75
                ),
                FoodOption.COLLECTION,
                new ArrayList<>(foodOptions),
                "foodId"));
        return tasks;
    }

    private List<Task<?>> dummyStoreTasks() {
        //#1
        List<Task<?>> tasks = new ArrayList<>();
        tasks.add(remoteDbHandler.setWithChild(
                Store.COLLECTION,
                new Store(
                        null,
                        null,
                        "Palmerston North"
                ),
                Address.COLLECTION,
                new Address(
                        null,
                        null,
                        null,
                        "123 Church St",
                        "Central",
                        "Palmerston North",
                        "4410"
                ),
                "storeId"));
        //#2
        tasks.add(remoteDbHandler.setWithChild(
                Store.COLLECTION,
                new Store(
                        null,
                        null,
                        "Feilding"
                ),
                Address.COLLECTION,
                new Address(
                        null,
                        null,
                        null,
                        "123 Fergusson St",
                        "Central",
                        "Feilding",
                        "4702"
                ),
                "storeId"));
        //#3
        tasks.add(remoteDbHandler.setWithChild(
                Store.COLLECTION,
                new Store(
                        null,
                        null,
                        "Ashurst"
                ),
                Address.COLLECTION,
                new Address(
                        null,
                        null,
                        null,
                        "123 Cambridge Ave",
                        "Central",
                        "Ashurst",
                        "4810"
                ),
                "storeId"));
        //#4
        tasks.add(remoteDbHandler.setWithChild(
                Store.COLLECTION,
                new Store(
                        null,
                        null,
                        "Longburn"
                ),
                Address.COLLECTION,
                new Address(
                        null,
                        null,
                        null,
                        "123 State Highway 56",
                        "Central",
                        "Longburn",
                        "2985"
                ),
                "storeId"));
        return tasks;
    }

    private Task<Object> dummyTimeslotTask() {
        List<BaseEntity> timeslots = new ArrayList<>();
        timeslots.add(new Timeslot(
                null,
                "11:45",
                "12:15"
        ));
        timeslots.add(new Timeslot(
                null,
                "12:15",
                "12:45"
        ));
        timeslots.add(new Timeslot(
                null,
                "12:45",
                "13:15"
        ));
        timeslots.add(new Timeslot(
                null,
                "13:15",
                "13:45"
        ));
        return remoteDbHandler.setAll(Timeslot.COLLECTION, timeslots);
    }

}

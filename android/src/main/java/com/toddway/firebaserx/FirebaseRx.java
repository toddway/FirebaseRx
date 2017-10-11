package com.toddway.firebaserx;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.util.Map;

import io.reactivex.Observable;


/**
 * Created by tway on 2/7/17.
 */

public class FirebaseRx {

    public static <T> Observable<Optional<T>> observeTask(Task<T> task) {
        return FirebaseRxKt.observeTask(task);
    }

    public static Observable<FirebaseUser> observeCurrentUid(FirebaseAuth auth) {
        return FirebaseRxKt.observeCurrentUser(auth);
    }

    public static Observable<Optional<Void>> observeAddChild(DatabaseReference ref, Object object) {
        return FirebaseRxKt.observeAddChild(ref, object);
    }

    public static Observable<DataSnapshot> observeValue(Query query) {
        return FirebaseRxCommonKt.observeValue(query);
    }

    public static <T> Observable<Optional<T>> observeValue(Query query, Class<T> type) {
        return FirebaseRxCommonKt.observeValue(query, type);
    }

    public static <T> Observable<Map<String, T>> observeChildMap(Query query, Class<T> type) {
        return FirebaseRxCommonKt.observeChildMap(query, type);
    }

    public static <T> Observable<Map<String, T>> observeChildMap(Query query, Class<T> type, Iterable<String> keys) {
        return FirebaseRxCommonKt.observeChildMap(query, type, keys);
    }
}
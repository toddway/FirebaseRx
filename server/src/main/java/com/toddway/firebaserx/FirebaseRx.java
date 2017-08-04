package com.toddway.firebaserx;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.tasks.Task;

import java.util.Map;

import rx.Observable;

/**
 * Created by tway on 2/7/17.
 */

public class FirebaseRx {

    public static <T> Observable<T> observeTask(Task<T> task) {
        return FirebaseRxKt.observeTask(task);
    }

    public static Observable<Void> observeAddChild(DatabaseReference ref, Object object) {
        return FirebaseRxKt.observeAddChild(ref, object);
    }

    public static Observable<DataSnapshot> observeValue(Query query) {
        return FirebaseRxCommonKt.observeValue(query);
    }

    public static <T> Observable<T> observeValue(Query query, Class<T> type) {
        return FirebaseRxCommonKt.observeValue(query, type);
    }

    public static <T> Observable<Map<String, T>> observeChildMap(Query query, Class<T> type) {
        return FirebaseRxCommonKt.observeChildMap(query, type);
    }

    public static <T> Observable<Map<String, T>> observeChildMap(Query query, Class<T> type, Iterable<String> keys) {
        return FirebaseRxCommonKt.observeChildMap(query, type, keys);
    }
}
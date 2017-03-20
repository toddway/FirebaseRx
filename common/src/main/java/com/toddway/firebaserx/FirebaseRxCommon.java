package com.toddway.firebaserx;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.subscriptions.Subscriptions;

/**
 * Created by tway on 2/7/17.
 */

public class FirebaseRxCommon {

    public static Observable<DataSnapshot> observeValue(Query query) {
        return Observable.create(subscriber -> {
            final ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(snapshot);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    int code = databaseError.getCode();
                    //if (code == -3) ; //permission denied, do nothing
                    subscriber.onError(databaseError.toException());
                }
            };

            subscriber.add(Subscriptions.create(() -> {
                query.removeEventListener(listener);
            }));

            query.addValueEventListener(listener);
        });
    }

    public static <T> Observable<T> observeValue(Query query, Class<T> type) {
        return observeValue(query).map(dataSnapshot -> dataSnapshot.getValue(type));
    }

    public static <T> Observable<Map<String, T>> observeChildMap(Query query, Class<T> type) {
        return observeValue(query)
                .flatMap(dataSnapshot -> toMap(dataSnapshot.getChildren(), type));
    }


    public static <T> Observable<List<T>> observeChildList(Query query, Class<T> type) {
        return observeValue(query)
                .flatMap(dataSnapshot -> toList(dataSnapshot.getChildren(), type));
    }

    private static <T> Observable<Map<String, T>> toMap(Iterable<DataSnapshot> dataSnapshots, Class<T> type) {
        return Observable.from(dataSnapshots)
                .toMap(
                        dataSnapshot1 -> dataSnapshot1.getKey(),
                        dataSnapshot1 -> dataSnapshot1.getValue(type)
                );
    }

    private static <T> Observable<List<T>> toList(Iterable<DataSnapshot> dataSnapshots, Class<T> type) {
        return Observable.from(dataSnapshots)
                .map(dataSnapshot2 -> dataSnapshot2.getValue(type))
                .toList();
    }

    public static <T> Observable<Map<String, T>> observeChildMap(Query query, Class<T> type, Iterable<String> keys) {
        return observeEqualTos(query, keys)
                .switchMap(snapshots -> toMap(snapshots, type));
    }

    private static Observable<List<DataSnapshot>> observeEqualTos(Query query, Iterable<String> equalTos) {
        List<Observable<DataSnapshot>> observables = new ArrayList<>();
        if (equalTos != null) {
            for (String equalTo : equalTos) {
                Observable<DataSnapshot> o = observeValue(query.equalTo(equalTo));
                observables.add(o);
            }
        }

        if (observables.isEmpty()) return Observable.just(new ArrayList<>());
        else return Observable
                .combineLatest(observables, args -> {
                    List<DataSnapshot> list = new ArrayList<>();
                    for (Object arg : args) list.add((DataSnapshot) arg);
                    return list;
                })
                .flatMap(dataSnapshots -> Observable
                        .from(dataSnapshots)
                        .flatMap(dataSnapshot -> Observable.from(dataSnapshot.getChildren()))
                        .toList()
                );
    }
}
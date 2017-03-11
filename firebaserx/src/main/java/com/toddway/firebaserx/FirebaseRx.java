package com.toddway.firebaserx;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.subscriptions.Subscriptions;

/**
 * Created by tway on 2/7/17.
 */

public class FirebaseRx {

    public static <T> Observable<T> observeTask(Task<T> task) {
        return Observable.create(subscriber -> {
            final OnCompleteListener<T> listener = task1 -> handleComplete(subscriber, task1);
            task.addOnCompleteListener(listener);
        });
    }

    private static <T> void handleComplete(Subscriber<? super T> subscriber, Task<T> task1) {
        if (!subscriber.isUnsubscribed()) {
            if (task1.isSuccessful()) {
                subscriber.onNext(task1.getResult());
                subscriber.onCompleted();
            } else {
                subscriber.onError(task1.getException());
            }
        }
    }

    public static Observable<String> observeCurrentUid(FirebaseAuth auth) {
        return Observable.create(subscriber -> {
            final FirebaseAuth.AuthStateListener listener = firebaseAuth -> {
                if (!subscriber.isUnsubscribed()) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    subscriber.onNext(user == null ? null : user.getUid());
                }
            };

            auth.addAuthStateListener(listener);

            subscriber.add(Subscriptions.create(() -> {
                auth.removeAuthStateListener(listener);
            }));
        });
    }

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

    public static <T> Observable<Map<String, T>> toMap(Iterable<DataSnapshot> dataSnapshots, Class<T> type) {
        return Observable.from(dataSnapshots)
                .toMap(
                        dataSnapshot1 -> dataSnapshot1.getKey(),
                        dataSnapshot1 -> dataSnapshot1.getValue(type)
                );
    }

    public static <T> Observable<List<T>> toList(Iterable<DataSnapshot> dataSnapshots, Class<T> type) {
        return Observable.from(dataSnapshots)
                .map(dataSnapshot2 -> dataSnapshot2.getValue(type))
                .toList();
    }

    public static Observable<List<DataSnapshot>> observeEqualTos(Query query, Iterable<String> equalTos) {
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

    public static Observable<Void> observeAddChild(DatabaseReference ref, Object object) {
        return Observable.create(subscriber -> {
            final String key = ref.push().getKey();
            ref.child(key).setValue(object)
                    .addOnCompleteListener(task -> handleComplete(subscriber, task));
        });
    }

    public static Observable<Void> observeSetValue(DatabaseReference ref, Object object) {
        return Observable.create(subscriber -> ref.setValue(object)
                .addOnCompleteListener(task -> handleComplete(subscriber, task)));
    }

//    public static Observable<DataSnapshot> observeRemoveValue(Query query) {
//        return observeValue(query)
//                .doOnNext(snapshot -> snapshot.getRef().removeValue());
//    }

}
package com.toddway.firebaserx;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import rx.Observable;
import rx.Subscriber;
import rx.subscriptions.Subscriptions;

/**
 * Created by tway on 2/7/17.
 */

public class FirebaseRx extends FirebaseRxCommon {

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
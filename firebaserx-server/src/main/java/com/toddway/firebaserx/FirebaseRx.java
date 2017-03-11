package com.toddway.firebaserx;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.tasks.OnCompleteListener;
import com.google.firebase.tasks.Task;

import rx.Observable;
import rx.Subscriber;

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
}
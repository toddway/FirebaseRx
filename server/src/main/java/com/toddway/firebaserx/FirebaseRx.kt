package com.toddway.firebaserx

import com.google.firebase.database.DatabaseReference
import com.google.firebase.tasks.Task
import rx.Observable
import rx.Subscriber

/**
 * Created by tway on 8/4/17.
 */

fun <T> Task<T>.observeTask(): Observable<T> {
    return Observable.create<T> { subscriber ->
        val listener = { task : Task<T> -> taskComplete(subscriber, task) }
        this.addOnCompleteListener(listener)
    }
}

fun DatabaseReference.observeAddChild(value: Any): Observable<Void> {
    return Observable.create<Void> { subscriber ->
        val key = push().key
        child(key).setValue(value).addOnCompleteListener { task -> taskComplete(subscriber, task) }
    }
}

fun DatabaseReference.observeCopyValue(targetRef: DatabaseReference): Observable<Void> {
    return observeValue().take(1)
            .flatMap {
                if (it.exists()) targetRef.setValue(it.value).observeTask()
                else Observable.just(null)
            }
}

fun DatabaseReference.observeMoveValue(targetRef: DatabaseReference): Observable<Void> {
    return observeCopyValue(targetRef)
            .doOnNext { removeValue().observeTask().subscribe() }
}

private fun <T> taskComplete(subscriber: Subscriber<in T>, task: Task<T>) {
    if (!subscriber.isUnsubscribed) {
        if (task.isSuccessful) {
            subscriber.onNext(task.result)
            subscriber.onCompleted()
        } else {
            subscriber.onError(task.exception)
        }
    }
}
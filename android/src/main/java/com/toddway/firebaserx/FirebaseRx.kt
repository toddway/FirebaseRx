package com.toddway.firebaserx

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import io.reactivex.Observable
import io.reactivex.ObservableEmitter


/**
 * Created by tway on 8/4/17.
 */

fun <T> Task<T>.observeTask(): Observable<Optional<T>> {
    return Observable.create<Optional<T>> { subscriber ->
        val listener = { task : Task<T> -> taskComplete(subscriber, task) }
        this.addOnCompleteListener(listener)
    }
}

fun DatabaseReference.observeAddChild(value: Any): Observable<Optional<Void>> {
    return Observable.create<Optional<Void>> { subscriber ->
        val key = push().key
        child(key).setValue(value).addOnCompleteListener { task -> taskComplete(subscriber, task) }
    }
}

fun DatabaseReference.observeCopyValue(targetRef: DatabaseReference): Observable<Any> {
    return observeValue().take(1)
            .flatMap {
                if (it.exists()) targetRef.setValue(it.value).observeTask()
                else Observable.just(Any())
            }
}

fun DatabaseReference.observeMoveValue(targetRef: DatabaseReference): Observable<Any> {
    return observeCopyValue(targetRef)
            .doOnNext { removeValue().observeTask().subscribe() }
}

private fun <T> taskComplete(subscriber: ObservableEmitter<Optional<T>>, task: Task<T>) {
    if (!subscriber.isDisposed) {
        if (task.isSuccessful) {
            subscriber.onNext(Optional(task.result))
            subscriber.onComplete()
        } else {
            task.exception?.let { subscriber.onError(it) }
        }
    }
}

fun observeCurrentUser(auth: FirebaseAuth): Observable<FirebaseUser?> {
    return Observable.create { subscriber ->
        val listener = { auth : FirebaseAuth ->
            if (!subscriber.isDisposed) {
                auth.currentUser?.let { subscriber.onNext(it) }
            }
        }

        auth.addAuthStateListener(listener)
        subscriber.setCancellable { auth.removeAuthStateListener(listener) }
    }
}

fun Query.observeValue(): Observable<QuerySnapshot> {
    return Observable.create<QuerySnapshot> { emitter ->
        val registration = this.addSnapshotListener({ snap, exception ->
            when {
                exception != null -> emitter.onError(exception)
                snap == null -> emitter.onError(kotlin.NullPointerException())
                else -> emitter.onNext(snap)
            }
        })
        emitter.setCancellable { registration.remove() }
    }
}
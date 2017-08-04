package com.toddway.firebaserx

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import rx.Observable
import rx.subscriptions.Subscriptions
import java.util.*

/**
 * Created by tway on 2/7/17.
 */


fun Query.observeValue(): Observable<DataSnapshot> {
    return Observable.create<DataSnapshot> { subscriber ->
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!subscriber.isUnsubscribed) {
                    subscriber.onNext(snapshot)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                val code = databaseError.code
                //if (code == -3) ; //permission denied, do nothing
                subscriber.onError(databaseError.toException())
            }
        }

        subscriber.add(Subscriptions.create { this.removeEventListener(listener) })

        this.addValueEventListener(listener)
    }
}

fun <T> Query.observeValue(type: Class<T>): Observable<T> {
    return observeValue().map { snap -> snap.getValue(type) }
}

fun <T> Query.observeChildMap(type: Class<T>): Observable<Map<String, T>> {
    return observeValue().map { toMap(it.children, type) }
}

fun <T> Query.observeChildMap(type: Class<T>, keys: Iterable<String>): Observable<Map<String, T>> {
    return observeEqualTos(this, keys).map { toMap(it, type) }
}

private fun <T> toMap(snaps: Iterable<DataSnapshot>, type: Class<T>): Map<String, T> {
    val map = LinkedHashMap<String, T>()
    snaps.forEach { snap ->
        snap.getValue(type)?.let { value ->
            map.put(snap.key, value)
        }
    }
    return map
}


private fun observeEqualTos(query: Query, equalTos: Iterable<String>?): Observable<List<DataSnapshot>> {
    val observables = ArrayList<Observable<DataSnapshot>>()
    if (equalTos != null) {
        for (equalTo in equalTos) {
            val o = query.equalTo(equalTo).observeValue()
            observables.add(o)
        }
    }

    if (observables.isEmpty())
        return Observable.just(ArrayList<DataSnapshot>())
    else
        return Observable
                .combineLatest(observables) { args ->
                    args.map { it as DataSnapshot }
                }
                .flatMap { dataSnapshots ->
                    Observable
                            .from(dataSnapshots)
                            .flatMap({ dataSnapshot : DataSnapshot -> Observable.from(dataSnapshot.children) })
                            .toList()
                }
}
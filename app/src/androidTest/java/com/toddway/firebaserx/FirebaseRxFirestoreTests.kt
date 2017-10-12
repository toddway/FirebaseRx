package com.toddway.firebaserx

import com.google.firebase.firestore.FirebaseFirestore
import org.junit.Assert
import org.junit.Test

/**
 * Created by tway on 10/12/17.
 */
class FirebaseRxFirestoreTests {

    @Test
    fun testFirestoreQuery() {
        val ref = FirebaseFirestore.getInstance().collection("things")

        val value = ref.observeValue().blockingFirst()

        Assert.assertNotNull(value)
    }
}
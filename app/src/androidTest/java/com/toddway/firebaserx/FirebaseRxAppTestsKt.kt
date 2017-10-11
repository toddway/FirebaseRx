package com.toddway.firebaserx

import android.support.test.runner.AndroidJUnit4
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import junit.framework.Assert
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FirebaseRxAppTestsKt {

    companion object {
        @BeforeClass @JvmStatic fun before() {
            if (FirebaseAuth.getInstance().currentUser == null)
                    FirebaseAuth.getInstance().signInAnonymously().observeTask().blockingFirst()
        }
    }

    @Test fun testSetAndGetValue() {
        val ref = FirebaseDatabase.getInstance().getReference("testSetAndGetValue")
        val beforeString = ref.push().key

        ref.removeValue().observeTask().blockingFirst(null)
        ref.setValue(beforeString).observeTask().blockingFirst(null)
        val afterString = ref.observeValue(String::class.java).blockingFirst().get()

        Assert.assertEquals(beforeString, afterString)
    }

    @Test fun testSetAndGetChildMap() {
        val ref = FirebaseDatabase.getInstance().getReference("testSetAndGetChildMap")
        val beforeMap = mapOf(Pair("first key", "first value"), Pair("second key", "second value"), Pair("third key", "third value"))

        ref.removeValue().observeTask().blockingFirst(null)
        ref.setValue(beforeMap).observeTask().blockingFirst(null)
        val afterMap = ref.observeChildMap(String::class.java).blockingFirst(null)

        println(afterMap.values) //prints [first value, second value, third value]
        Assert.assertEquals(beforeMap, afterMap)
    }

    @Test fun testSetAndRemoveValue() {
        val ref = FirebaseDatabase.getInstance().getReference("testSetAndRemoveValue")

        ref.removeValue().observeTask().blockingFirst(null)
        ref.setValue("something").observeTask().blockingFirst(null)
        ref.removeValue().observeTask().blockingFirst(null)
        val afterString = ref.observeValue(String::class.java).blockingFirst(null).get()

        Assert.assertNull(afterString)
    }

    @Test fun testMoveValue() {
        val ref = FirebaseDatabase.getInstance().getReference("testMoveValue")
        val targetRef = FirebaseDatabase.getInstance().getReference("testMoveValueTarget")
        val sourceBeforeString = ref.push().key

        ref.setValue(sourceBeforeString).observeTask().blockingFirst(null)
        ref.observeMoveValue(targetRef).blockingFirst(null)
        val targetAfterString = targetRef.observeValue(String::class.java).blockingFirst().get()
        val sourceAfterString = ref.observeValue(String::class.java).blockingFirst().get()

        Assert.assertEquals(targetAfterString, sourceBeforeString)
        Assert.assertNull(sourceAfterString)
    }

    @Test fun testCopyValue() {
        val ref = FirebaseDatabase.getInstance().getReference("testMoveValue")
        val targetRef = FirebaseDatabase.getInstance().getReference("testMoveValueTarget")
        val sourceBeforeString = ref.push().key

        ref.setValue(sourceBeforeString).observeTask().blockingFirst(null)
        ref.observeCopyValue(targetRef).blockingFirst(null)
        val targetAfterString = targetRef.observeValue(String::class.java).blockingFirst().get()
        val sourceAfterString = ref.observeValue(String::class.java).blockingFirst().get()

        Assert.assertEquals(targetAfterString, sourceBeforeString)
        Assert.assertEquals(sourceBeforeString, sourceAfterString)
    }

    @Test fun testAddKey() {
        val ref = FirebaseDatabase.getInstance().getReference("items")

        ref.observeValue()
    }

}
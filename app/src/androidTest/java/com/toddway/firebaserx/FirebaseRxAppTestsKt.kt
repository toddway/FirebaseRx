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
                    FirebaseAuth.getInstance().signInAnonymously().observeTask().toBlocking().first()
        }
    }

    @Test fun testSetAndGetValue() {
        val ref = FirebaseDatabase.getInstance().getReference("testSetAndGetValue")
        val beforeString = ref.push().key

        ref.removeValue().observeTask().toBlocking().first()
        ref.setValue(beforeString).observeTask().toBlocking().first()
        val afterString = ref.observeValue(String::class.java).toBlocking().first()

        Assert.assertEquals(beforeString, afterString)
    }

    @Test fun testSetAndGetChildMap() {
        val ref = FirebaseDatabase.getInstance().getReference("testSetAndGetChildMap")
        val beforeMap = mapOf(Pair("first key", "first value"), Pair("second key", "second value"), Pair("third key", "third value"))

        ref.removeValue().observeTask().toBlocking().first()
        ref.setValue(beforeMap).observeTask().toBlocking().first()
        val afterMap = ref.observeChildMap(String::class.java).toBlocking().first()

        println(afterMap.values) //prints [first value, second value, third value]
        Assert.assertEquals(beforeMap, afterMap)
    }

    @Test fun testSetAndRemoveValue() {
        val ref = FirebaseDatabase.getInstance().getReference("testSetAndRemoveValue")

        ref.removeValue().observeTask().toBlocking().first()
        ref.setValue("something").observeTask().toBlocking().first()
        ref.removeValue().observeTask().toBlocking().first()
        val afterString = ref.observeValue(String::class.java).toBlocking().first()

        Assert.assertNull(afterString)
    }

    @Test fun testMoveValue() {
        val ref = FirebaseDatabase.getInstance().getReference("testMoveValue")
        val targetRef = FirebaseDatabase.getInstance().getReference("testMoveValueTarget")
        val sourceBeforeString = ref.push().key

        ref.setValue(sourceBeforeString).observeTask().toBlocking().first()
        ref.observeMoveValue(targetRef).toBlocking().first()
        val targetAfterString = targetRef.observeValue(String::class.java).toBlocking().first()
        val sourceAfterString = ref.observeValue(String::class.java).toBlocking().first()

        Assert.assertEquals(targetAfterString, sourceBeforeString)
        Assert.assertNull(sourceAfterString)
    }

    @Test fun testCopyValue() {
        val ref = FirebaseDatabase.getInstance().getReference("testMoveValue")
        val targetRef = FirebaseDatabase.getInstance().getReference("testMoveValueTarget")
        val sourceBeforeString = ref.push().key

        ref.setValue(sourceBeforeString).observeTask().toBlocking().first()
        ref.observeCopyValue(targetRef).toBlocking().first()
        val targetAfterString = targetRef.observeValue(String::class.java).toBlocking().first()
        val sourceAfterString = ref.observeValue(String::class.java).toBlocking().first()

        Assert.assertEquals(targetAfterString, sourceBeforeString)
        Assert.assertEquals(sourceBeforeString, sourceAfterString)
    }

    @Test fun testAddKey() {
        val ref = FirebaseDatabase.getInstance().getReference("items")

        ref.observeValue()
    }

}
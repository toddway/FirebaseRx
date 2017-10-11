package com.toddway.firebaserx;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;


@RunWith(AndroidJUnit4.class)
public class FirebaseRxAppTests {
    @Test
    public void useAppContext() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        assertEquals("com.toddway.firebaserx.app", appContext.getPackageName());
    }

    @Test
    public void testSignInAnonymously() {
        AuthResult result = FirebaseRx.observeTask(FirebaseAuth.getInstance().signInAnonymously()).blockingFirst(null).get();
        Assert.assertNotNull(result.getUser().getUid());
    }

    @Test
    public void testSetAndGetValue() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("testSetAndGetValue");
        String beforeString = ref.push().getKey();
        FirebaseRx.observeTask(ref.setValue(beforeString)).blockingFirst();
        String afterString = FirebaseRx.observeValue(ref, beforeString.getClass()).blockingFirst(null).get();
        Assert.assertEquals(beforeString, afterString);
    }
}

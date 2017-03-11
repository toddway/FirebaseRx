package com.toddway.firebaserx;

import android.support.test.runner.AndroidJUnit4;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ObserveTaskTests {


    @Test
    public void testSignInAnonymously() {
        AuthResult result = FirebaseRx.observeTask(FirebaseAuth.getInstance().signInAnonymously()).toBlocking().first();
        Assert.assertNotNull(result.getUser().getUid());
    }
}

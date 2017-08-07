
import android.support.test.runner.AndroidJUnit4
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.toddway.firebaserx.observeTask
import com.toddway.firebaserx.observeValue
import junit.framework.Assert
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class Tests {

    companion object {
        @BeforeClass @JvmStatic fun before() {
            if (FirebaseAuth.getInstance().currentUser == null)
                FirebaseAuth.getInstance().signInAnonymously().observeTask().toBlocking().first()
        }
    }

    @Test
    fun testSetAndGetValue() {
        //val app = FirebaseApp.initializeApp(InstrumentationRegistry.getTargetContext())
        val ref = FirebaseDatabase.getInstance().getReference("testSetAndGetValue")
        val beforeString = ref.push().key

        ref.removeValue().observeTask().toBlocking().first()
        ref.setValue(beforeString).observeTask().toBlocking().first()
        val afterString = ref.observeValue(String::class.java).toBlocking().first()

        Assert.assertEquals(beforeString, afterString)
    }

}
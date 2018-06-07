package paradise.ccclxix.projectparadise;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class RegistrationActivityTest {
    @Test
    public void test() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        assertEquals("paradise.ccclxix.projectparadise", appContext.getPackageName());
    }
}

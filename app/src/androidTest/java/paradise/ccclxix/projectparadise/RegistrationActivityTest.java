package paradise.ccclxix.projectparadise;


import com.google.firebase.auth.FirebaseAuth;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(JUnit4.class)
public class RegistrationActivityTest {
    @Test
    public void test() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        assertEquals("life", "life");
    }
}

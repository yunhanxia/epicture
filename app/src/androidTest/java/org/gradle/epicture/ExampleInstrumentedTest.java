package org.gradle.epicture;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.Fragment;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("org.gradle.epicture", appContext.getPackageName());
    }
        @Test (expected = Test.None.class)
        public void onCreate() {
            AccountActivity A = new AccountActivity();
            FavoriteActivity f = new FavoriteActivity();
            HomeActivity h = new HomeActivity();
            LoginActivity l = new LoginActivity();
            NavigationActivity n = new NavigationActivity();
            SearchActivity s = new SearchActivity();
            UploadActivity u = new UploadActivity();

            Launch_Test start = new Launch_Test();
        }

}

 class Launch_Test {

    @Before
    public void setUp() throws Exception {
    }

    @Test(expected = Test.None.class)
    public void onCreate() {
        MainActivity activity = new MainActivity();
    }

    @Test(expected = Test.None.class)
    public void goToAccountPage() {
        MainActivity activity = new MainActivity();
    }
}

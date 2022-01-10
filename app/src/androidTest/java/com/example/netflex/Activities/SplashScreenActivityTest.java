package com.example.netflex.Activities;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertNotNull;

import android.app.Activity;
import android.app.Instrumentation;
import android.view.View;

import androidx.test.rule.ActivityTestRule;

import com.example.netflex.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class SplashScreenActivityTest {
    @Rule
    public ActivityTestRule<SplashScreenActivity> activityTestRule = new ActivityTestRule<>(SplashScreenActivity.class);
    Instrumentation.ActivityMonitor monitor = getInstrumentation().addMonitor(SignInActivity.class.getName(), null, false);
    private SplashScreenActivity screen = null;

    @Before
    public void setUp() throws Exception {
        screen = activityTestRule.getActivity();
    }

    @Test
    public void test() {
        View v = screen.findViewById(R.id.forgotPasswordID);
        Activity sigin = getInstrumentation().waitForMonitorWithTimeout(monitor, 5000);
        assertNotNull(sigin);
    }

    @After
    public void tearDown() throws Exception {
        screen = null;
    }
}
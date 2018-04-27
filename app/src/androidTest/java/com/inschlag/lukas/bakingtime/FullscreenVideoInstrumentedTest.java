package com.inschlag.lukas.bakingtime;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.inschlag.lukas.bakingtime.data.Constants;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class FullscreenVideoInstrumentedTest {

    @Before
    public void init(){
        Intents.init();
    }

    @Rule
    public ActivityTestRule<RecipeStepDetailActivity> mActivityRule =
            new ActivityTestRule<RecipeStepDetailActivity>(RecipeStepDetailActivity.class){
                @Override
                protected Intent getActivityIntent() {
                    Context targetContext = getInstrumentation()
                            .getTargetContext();
                    Intent result = new Intent(targetContext, RecipeStepDetailActivity.class);
                    result.putExtra(Constants.ARG_ITEM_ID, 2);
                    result.putExtra(Constants.ARG_STEP, 4);
                    return result;
                }
            };

    @Test
    public void checkVideoBehaviour(){

        onView(withText("Description")).check(matches(isDisplayed()));

        mActivityRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getInstrumentation().waitForIdleSync();

        intended(hasComponent(FullScreenVideoActivity.class.getName()));
        onView(withText("Description")).check(doesNotExist());
    }

    @After
    public void finish(){
        Intents.release();
    }
}

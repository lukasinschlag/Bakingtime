package com.inschlag.lukas.bakingtime;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class RecipeInstrumentedTest {

    @Before
    public void init(){
        Intents.init();
    }

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.inschlag.lukas.bakingtime", appContext.getPackageName());
    }

    @Rule
    public ActivityTestRule<RecipeListActivity> mActivityRule =
            new ActivityTestRule<>(RecipeListActivity.class);

    @Test
    public void openRecipe(){
        onView(withId(R.id.recipe_list))
                .perform(RecyclerViewActions
                        .actionOnItemAtPosition(1, click()));

        intended(hasComponent(RecipeStepListActivity.class.getName()));

        onView(withText("Step 6: Add eggs.")).check(matches(isDisplayed()));

        onView(withText("Step 5: Mix together dry ingredients.")).perform(click());

        intended(hasComponent(RecipeStepDetailActivity.class.getName()));
    }

    @Test
    public void confirmAllViewsInDetailScreen(){
        onView(withId(R.id.recipe_list))
                .perform(RecyclerViewActions
                        .actionOnItemAtPosition(0, click()));

        onView(withId(R.id.recipe_list))
                .perform(RecyclerViewActions
                        .actionOnItemAtPosition(3, click()));

        onView(withId(R.id.videoPlayer)).check(matches(isDisplayed()));
        onView(withId(R.id.stepDesc)).check(matches(isDisplayed()));
        onView(withId(R.id.btnPrevious)).check(matches(isDisplayed()));
        onView(withId(R.id.btnNext)).check(matches(isDisplayed()));
    }

    @Test
    public void checkNavigationThroughDetails(){
        onView(withId(R.id.recipe_list))
                .perform(RecyclerViewActions
                        .actionOnItemAtPosition(3, click()));

        onView(withId(R.id.recipe_list))
                .perform(RecyclerViewActions
                        .actionOnItemAtPosition(0, click()));

        onView(withText("Ingredients")).check(matches(isDisplayed()));
        onView(withText("SHOW STEPS")).check(matches(isDisplayed()));
        onView(withId(R.id.btnNext)).perform(click());
        onView(withId(R.id.btnNext)).perform(click());
        onView(withId(R.id.btnPrevious)).perform(click());
        onView(withText("SHOW INGREDIENTS")).perform(click());
        onView(withId(R.id.btnNext)).perform(click());
        onView(withId(R.id.btnNext)).perform(click());
        onView(withId(R.id.btnNext)).perform(click());
        onView(withId(R.id.btnNext)).perform(click());
        onView(withId(R.id.btnNext)).perform(click());
        onView(withId(R.id.btnNext)).perform(click());
        onView(withId(R.id.btnNext)).perform(click());
        onView(withId(R.id.btnNext)).perform(click());
        onView(withId(R.id.btnNext)).perform(click());
        onView(withId(R.id.btnNext)).perform(click());
        onView(withId(R.id.btnNext)).perform(click());
        onView(withId(R.id.btnNext)).perform(click());
        onView(withId(R.id.btnNext)).perform(click());
        onView(withId(R.id.btnNext)).check(matches(not(isEnabled())));
    }

    @Test
    public void checkFullscreenVideo(){
        onView(withId(R.id.recipe_list))
                .perform(RecyclerViewActions
                        .actionOnItemAtPosition(2, click()));

        onView(withId(R.id.recipe_list))
                .perform(RecyclerViewActions
                        .actionOnItemAtPosition(3, click()));


    }

    @After
    public void finish(){
        Intents.release();
    }
}

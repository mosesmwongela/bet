package com.betmwitu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

/**
 * Created by mwongela on 12/26/16.
 */
public class IntroActivity extends AppIntro {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Note here that we DO NOT use setContentView();

        // Add your slide fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.

        // addSlide(firstFragment);
        // addSlide(secondFragment);
        // addSlide(thirdFragment);
        // addSlide(fourthFragment);

        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest.

        addSlide(AppIntroFragment.newInstance(
                "Bet Mwitu",
                "Because gambling is an investment.",
                R.drawable.my_logo,
                getResources().getColor(R.color.primary_dark)));

        addSlide(AppIntroFragment.newInstance(
                "Quality Betting Tips",
                "On a daily basis, we will provide you with quality betting tips. Some are free, " +
                        "but the big boys roll with the max-profit premium tips.",
                R.drawable.this_is_how_you_dance,
                getResources().getColor(R.color.primary_dark)));

        addSlide(AppIntroFragment.newInstance(
                "Free Tips",
                "You love free stuff? We will grow your betting skills with a set of free tips.",
                R.drawable.free_stuff,
                getResources().getColor(R.color.primary_dark)));

        addSlide(AppIntroFragment.newInstance(
                "Premium Tips",
                "This is how you maximize your profits. This is how you get rich. " +
                        "We will show you how to top up your account and take advantage of this product.",
                R.drawable.premium_tips,
                getResources().getColor(R.color.primary_dark)));

        addSlide(AppIntroFragment.newInstance(
                "How to top-up",
                "Your registered m-pesa number is your account number. We will ask you for it during account creation.",
                R.drawable.lipa_na_mpesa,
                getResources().getColor(R.color.primary_dark)));

        setZoomAnimation();

        // OPTIONAL METHODS
        // Override bar/separator color.
        setBarColor(getResources().getColor(R.color.primary));
        setSeparatorColor(getResources().getColor(R.color.divider));

        // Hide Skip/Done button.
        showSkipButton(false);
        setProgressButtonEnabled(true);

        // Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permission in Manifest.
        setVibrate(true);
        setVibrateIntensity(30);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
}

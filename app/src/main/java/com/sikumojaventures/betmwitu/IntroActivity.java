package com.sikumojaventures.betmwitu;

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
                "The best betting tips in town",
                R.drawable.my_logo,
                getResources().getColor(R.color.primary_dark)));

        addSlide(AppIntroFragment.newInstance(
                "Quality Betting Tips",
                "We sell each tip at an average of KES 30 only - but we also have a lot of free tips.",
                R.drawable.this_is_how_you_dance,
                getResources().getColor(R.color.primary_dark)));



        addSlide(AppIntroFragment.newInstance(
                "Single Bet vs Multibets",
                "The safest tips, usually with very low odds, they are perfect to accumulators. Tips with higher odds. Use them as single bets.",
                R.drawable.premium_tips,
                getResources().getColor(R.color.primary_dark)));

        addSlide(AppIntroFragment.newInstance(
                "How to top-up",
                "When you create an account, we'll require you to use your safaricom phone number so that your account is credited as soon as you send money to our till number: 560921.",
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

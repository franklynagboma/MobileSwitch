package com.franklyn.mobileswitch;


import android.content.Intent;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.franklyn.mobileswitch.activities.MobileSwitchActivity;
import com.viksaa.sssplash.lib.activity.AwesomeSplash;
import com.viksaa.sssplash.lib.cnst.Flags;
import com.viksaa.sssplash.lib.model.ConfigSplash;

/**
 * Created by AGBOMA franklyn on 1/25/17.
 */

public class SplashActivity extends AwesomeSplash {

    private final String LOG_TAG = SplashActivity.class.getSimpleName();

    @Override
    public void initSplash(ConfigSplash configSplash) {

        //circular reveal
        configSplash.setBackgroundColor(R.color.background);
        configSplash.setAnimCircularRevealDuration(1500);
        configSplash.setRevealFlagX(Flags.REVEAL_RIGHT);
        configSplash.setRevealFlagY(Flags.REVEAL_BOTTOM);

        //logo
        configSplash.setLogoSplash(R.mipmap.oau_logo);
        configSplash.setAnimLogoSplashDuration(1500);
        configSplash.setAnimLogoSplashTechnique(Techniques.Bounce);

        //path
        //configSplash.setPathSplash(Constant.DROID_LOGO);

        //title
        configSplash.setTitleSplash(getResources().getString(R.string.app_name));
        configSplash.setTitleTextColor(android.R.color.white);
        configSplash.setTitleTextSize(35f);
        configSplash.setAnimTitleDuration(1500);
        configSplash.setAnimTitleTechnique(Techniques.Flash);
        //configSplash.setTitleFont();
    }

    @Override
    public void animationsFinished() {
        //start new activity here
        //Toast.makeText(SplashActivity.this, LOG_TAG + " finished", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(SplashActivity.this, MobileSwitchActivity.class));
    }
}

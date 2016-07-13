package com.tiago.onlyjokes;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdView;

public class GoogleAdListener extends AdListener {
    private Context _context;
    private AdView mAdView;

    public GoogleAdListener(Context context, AdView adView) {
        this._context = context;
        mAdView = adView;
        //Hide the AdView on creation
        mAdView.setVisibility(View.GONE);
    }
    @Override
    public void onAdLoaded() {
        //if (BuildConfig.DEBUG) {Log.d(Constants.LOG, "onAdLoaded: cheguei");}
        //Display the AdView if an Ad is loaded
        mAdView.setVisibility(View.VISIBLE);


    }
    @Override
    public void onAdFailedToLoad(int errorCode) {
        // Code to be executed when an ad request fails.
    }

    @Override
    public void onAdOpened() {
        // Code to be executed when an ad opens an overlay that
        // covers the screen.
        // Tracking Event //v2.0.3
        //MyApplication.getInstance().trackEvent("Ads", "Banner clicked", "Monetizing");
    }

    @Override
    public void onAdLeftApplication() {
        // Code to be executed when the user has left the app.
    }

    @Override
    public void onAdClosed() {
        // Code to be executed when when the user is about to return
        // to the app after tapping on an ad.
    }

    public interface Constants {
        String LOG = "GoogleAdListener --- ";
    }
}

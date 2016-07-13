package com.tiago.onlyjokes;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

public class GoToMarket {

    private final static int DAYS_UNTIL_PROMPT = 3;//Min number of days
    private final static int DAYS_UNTIL_ADS_APPEAR = 7;//Min number of days
    private final static int LAUNCHES_UNTIL_PROMPT = 5;//Min number of launches

    public static void app_launched(Context mContext) {

        SharedPreferences prefs = mContext.getSharedPreferences("apprater", 0);
        if (prefs.getBoolean("dontshowagain", false)) { return ; }

        SharedPreferences.Editor editor = prefs.edit();
        //showRateDialog(mContext, editor); TODO enable when testing


        // Increment launch counter
        long launch_count = prefs.getLong("launch_count", 0) + 1;
        editor.putLong("launch_count", launch_count);

        // Get date of first launch
        Long date_firstLaunch = prefs.getLong("date_firstlaunch", 0);
        if (date_firstLaunch == 0) {
            date_firstLaunch = System.currentTimeMillis();
            editor.putLong("date_firstlaunch", date_firstLaunch);
        }

        // Wait at least n days before opening
        if (launch_count >= LAUNCHES_UNTIL_PROMPT) {
            if (System.currentTimeMillis() >= date_firstLaunch +
                    (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000)) {
                showRateDialog(mContext, editor);
            }
        }
        editor.apply();
    }

    /**public static boolean adsGracePeriod(Context mContext){
        SharedPreferences prefs = mContext.getSharedPreferences("apprater", 0);
        SharedPreferences.Editor editor = prefs.edit();

        Long date_firstLaunch = prefs.getLong("date_first_launch_ads", 0);
        if (date_firstLaunch == 0) {
            date_firstLaunch = System.currentTimeMillis();
            editor.putLong("date_first_launch_ads", date_firstLaunch);
            editor.apply();
        }

        // Wait at least n days before showing ads
        if (System.currentTimeMillis() >= date_firstLaunch +
                (DAYS_UNTIL_ADS_APPEAR * 24 * 60 * 60 * 1000)) {
            //showRateDialog(mContext, editor);//TODO maybe do something here (ads)
            if (BuildConfig.DEBUG) {
                Log.d(Constants.LOG, "adsGracePeriod: TRUE " + date_firstLaunch);
            }
            return true;
        } else{
            if (BuildConfig.DEBUG) {
                Log.d(Constants.LOG, "adsGracePeriod: TRUE " + date_firstLaunch);
            }
            return false;
        }

    }*/

    public void goToAppStore (final Context mContext, String packageName){ //2.1 substitute for all the individual goToApp methods
        Uri uri = Uri.parse("market://details?id=" + packageName);
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            mContext.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id="+packageName)));
        }
    }

    public void goToDevPage (final Context mContext){ //2.1 substitute for all the individual goToApp methods
        Uri uri = Uri.parse("https://play.google.com/store/apps/dev?id=7729843282518254108");
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            mContext.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/dev?id=7729843282518254108")));
        }
    }

    public static void showRateDialog (final Context mContext, final SharedPreferences.Editor editor){
        AlertDialog ad = new AlertDialog.Builder(mContext).create();
        if (editor != null) {
            editor.putBoolean("dontshowagain", true);
            editor.commit();
        }
        ad.setTitle("Rate Only Jokes!");
        ad.setMessage("If you like Only Jokes, please take a moment to rate it and comment on Google Play.\n\nThanks for your support!!");
        ad.setButton(AlertDialog.BUTTON_NEUTRAL, "Later", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Tracking Event
                //MyApplication.getInstance().trackEvent("App Rater", "Is not rating now", "Track event");
                SharedPreferences prefs = mContext.getSharedPreferences("apprater", 0);
                SharedPreferences.Editor editor = prefs.edit();
                editor.apply();
                //show again
                editor.putBoolean("dontshowagain", false);
                editor.apply();

                // Increment launch counter
                long launch_count = 3;
                editor.putLong("launch_count", launch_count);
                dialogInterface.dismiss();

            }
        });
        ad.setButton(AlertDialog.BUTTON_POSITIVE, "Rate", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Tracking Event
                //MyApplication.getInstance().trackEvent("App Rater", "Is rating the app", "Track event");
                //mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.colombo.tiago.esldailyshot")));
                Uri uri = Uri.parse("market://details?id=com.tiago.onlyjokes");
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    mContext.startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.tiago.onlyjokes")));
                }

                //v1.8 new method only postpone it for a long time
                SharedPreferences prefs = mContext.getSharedPreferences("apprater", 0);
                SharedPreferences.Editor editor = prefs.edit();
                editor.apply();
                //show again
                editor.putBoolean("dontshowagain", false);
                editor.apply();

                // Increment launch counter
                long launch_count = 12;
                editor.putLong("launch_count", launch_count);
                dialogInterface.dismiss();
            }
        });
        ad.setButton(AlertDialog.BUTTON_NEGATIVE, "No, thanks", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Tracking Event
                //MyApplication.getInstance().trackEvent("App Rater", "Is not rating now", "Track event");
                dialogInterface.dismiss();
            }
        });

        ad.show();
    }


    public interface Constants {
        String LOG = "GoToMarket --- ";
    }

}

package com.tiago.onlyjokes;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.analytics.FirebaseAnalytics.Param;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import com.tiago.onlyjokes.util.IabHelper;
import com.tiago.onlyjokes.util.IabResult;
import com.tiago.onlyjokes.util.Inventory;
import com.tiago.onlyjokes.util.Purchase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements CategoriesRecyclerAdapter.OnItemClickListener{
    private static final String TAG = "MainActivity";
    static Typeface jokeFont, myFont2;
    static String screen = "home";
    static String currentJoke="";
    static String currentId="";
    static int listPos = 0;
    static int COOLDOWN_MAX = 18;
    static int interstitialCooldown = COOLDOWN_MAX;
    //static int totalItems = 0;
    static String chosenCategory ="";
    static String filterText ="";
    static List<DatabaseModel> dbList;

    static boolean showBanner = true;
    static boolean showInterstitial = true;
    static ArrayList<String> favsList = new ArrayList<>();
    //static ArrayList<String> seenList = new ArrayList<>();
    boolean nightModeSet = false;

    static Float newVersion = 1.3F; //TODO increment every update

    TTSManager ttsManager = null;

    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    InterstitialAd mInterstitialAd;
    private FirebaseAnalytics mFirebaseAnalytics;
    AdView mAdView;

    private String ONLINE_VERSION_NUMBER = "version_only_jokes";
    int latestBuildNumber = BuildConfig.VERSION_CODE;
    //String latestBuildName = BuildConfig.VERSION_NAME;

    //v1.3 in app billing
    IabHelper mHelper;
    static boolean isPro;
    static final String SKU_PREMIUM = "premium";
    final String myUniqueID = "fQ4x0";
    final String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAn/+0+90F5aGV/c41CJfH2hgcdkMXY4Rr3GV1HVlN9yXMu4GDRnh+LpsvFszzdVeuollDDZ2Qctb6MtVV+vLRzcV0AMOckAEQbahXujksCN8YJ8vjA2MuIfDTUYMBJ8Sd6Iqz3MSpWyLbNLvZi3/+nZkYewCFGP4TBbrdpdrxuRVRHPMMaD42IT1SVUr9YxKA1ja83jmhvpuQMXBsK9G+qUu5FrTO3e468735WTPxGzaVdr1Sft+GDfHMg39DVRmyPLzzfpj+e8A4ZfZEA0QRVKhNFane0mKydBKcETT6uNQII14ZhggrQC2KsLz+TxsO/K4WUg2lYLbZGm7OxFxa8wIDAQAB";
    String payloadPremium;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);//This will change theme back to original (boot logo)
        loadTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //recreate();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        inflateFragment(screen);

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-9563082889139022~4549708599");

        myFont2 = Typeface.createFromAsset(getAssets(), "mailraystuff.ttf");
        jokeFont = Typeface.createFromAsset(getAssets(), "HelvetiHand.ttf");

        //init TTS
        ttsManager = new TTSManager();
        ttsManager.init(this);
        mAdView = (AdView) findViewById(R.id.adView);
        //mAdView.setAdListener(new GoogleAdListener(this, mAdView));
        /**
         // Add code to print out the key hash
         try {
         PackageInfo info = getPackageManager().getPackageInfo(
         "com.tiago.onlyjokes",
         PackageManager.GET_SIGNATURES);
         for (Signature signature : info.signatures) {
         MessageDigest md = MessageDigest.getInstance("SHA");
         md.update(signature.toByteArray());
         Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
         }
         } catch (PackageManager.NameNotFoundException e) {

         } catch (NoSuchAlgorithmException e) {

         }*/

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        if(BuildConfig.DEBUG){
            mFirebaseAnalytics.setUserProperty( "user", "tester");
        }else{
            mFirebaseAnalytics.setUserProperty( "user", "normal");
        }

        //remote config
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();

        // Define default config values. Defaults are used when fetched config values are not
        // available. Eg: if an error occurred fetching values from the server.
        Map<String, Object> defaultConfigMap = new HashMap<>();
        defaultConfigMap.put("version_only_jokes", 1L);

        // Apply config settings and default values.
        mFirebaseRemoteConfig.setConfigSettings(configSettings);
        mFirebaseRemoteConfig.setDefaults(defaultConfigMap);
        //mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);


        GoToMarket.app_launched(this);
        fetchConfig();
        showChangeLog();


        loadFavs();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        isPro = sp.getBoolean("isPro", false);
        initIAB();
        //if(!isPro){
        //    initIAB();//1.3
        //}else{
        //    checkProHandler.post(checkProRunnable);
        //}
        //isPro = true; //use for debugging
    }

    /**In app billing*/
    public void initIAB(){
        Log.d(TAG, "initIAB: aqui");
        //init payloads
        payloadPremium = myUniqueID +SKU_PREMIUM;

        mHelper = new IabHelper(this, base64EncodedPublicKey);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {

            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    Log.d(TAG, "In-app Billing setup failed: " + result);
                } else {
                    Log.d(TAG, "In-app Billing is set up OK");
                    mHelper.queryInventoryAsync(mReceivedInventoryListener);

                }
            }
        });
        mHelper.enableDebugLogging(true);
    }

    public boolean shouldConsume = false;//TODO for debugging purposes
    IabHelper.QueryInventoryFinishedListener mReceivedInventoryListener
            = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory) {
            Log.d(TAG, "onQueryInventoryFinished: aqui");
            // Do we have the premium upgrade?
            Purchase purchasePremium = inventory.getPurchase(SKU_PREMIUM);
            isPro = (purchasePremium != null && verifyDeveloperPayload(purchasePremium));
            Log.d(TAG, "Query inventory finished.");
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("isPro", isPro);
            editor.apply();
            if(isPro) mFirebaseAnalytics.setUserProperty( "user", "pro");

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;

            // Is it a failure?
            if (result.isFailure()) {
                Log.d(TAG, "Failed to query inventory: " + result);
                //showToastMessage("xeguei fail");
                return;
            }else if(shouldConsume){ //not necessary to consume, so It knows premium is bought when initialize app
                shouldConsume = false;
                mHelper.consumeAsync(inventory.getPurchase(SKU_PREMIUM),
                        mConsumeFinishedListener);
            }

            checkProHandler.post(checkProRunnable);//finally show or hide ads accordingly
            //updateUI();
            //Log.d(TAG, "Query inventory was successful.");
        }
    };

    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener =
            new IabHelper.OnConsumeFinishedListener() {
                public void onConsumeFinished(Purchase purchase,
                                              IabResult result) {
                    //showToastMessage("xeguei yeah");
                    if (result.isSuccess()) {
                        Log.d(TAG, "onConsumeFinished: aqui");
                        //isPremium = true;
                        //clickButton.setEnabled(true);
                    } else {
                        // handle error
                        showToastMessage("Oh noes");
                        //updateUI();
                    }
                }
            };

    boolean verifyDeveloperPayload(Purchase p) {
        Log.d(TAG, "verifyDeveloperPayload: aqui");
        String responsePayload = p.getDeveloperPayload();
        return responsePayload != null && (responsePayload.equals(payloadPremium));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data)
    {
        if (!mHelper.handleActivityResult(requestCode,
                resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void showProDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Buy pro version?");
        alertDialog.setMessage(getString(R.string.message_buy_pro));
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "buy",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        buyPremium();
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public void buyPremium() {
        mHelper.launchPurchaseFlow(this, SKU_PREMIUM, 10001,
                mPurchaseFinishedListener, payloadPremium);
    }

    public void consumePremium(){
        shouldConsume = true;
        mHelper.queryInventoryAsync(mReceivedInventoryListener);
        showToastMessage("you consumed this crap");
    }

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener
            = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result,
                                          Purchase purchase) {
            Log.d(TAG, "onIabPurchaseFinished: aqui");
            if (result.isFailure()) {
                // Handle error
                //return;
            } else if (purchase.getSku().equals(SKU_PREMIUM)) {
                //consumeItem(); //the line below is from this method, which is useless now
                mHelper.queryInventoryAsync(mReceivedInventoryListener);
                isPro = true;
                //buyButton.setEnabled(false);
            }
        }
    };
    /**In app billing*/


    public void sendDataAnalytics(){//v1.4 sends data after a while (only if the user is READING the joke)
        mAnalyticsHandler.removeCallbacks(mAnalyticsRunnable);
        mAnalyticsHandler.postDelayed(mAnalyticsRunnable, 1500);
    }

    private final Handler mAnalyticsHandler = new Handler();
    private final Runnable mAnalyticsRunnable = new Runnable() {
        public void run() {
            // Obtain the FirebaseAnalytics instance.
            FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(MainActivity.this);
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "category");
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "selected_category");
            bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, MainActivity.chosenCategory);
            //bundle.putString(FirebaseAnalytics.Param.VALUE, MainActivity.chosenCategory);
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, MainActivity.chosenCategory);//this is the correct way to show analytics
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
            Log.d(TAG, "sendDataAnalytics: SENT");        }
    };


    @Override
    public void onResume() {
        super.onResume();
        //if (mAdView != null) {
        //    mAdView.resume();
        //}
        if (mAdView != null) {
            if(isPro){
                if(mAdView.getVisibility()==View.VISIBLE) mAdView.setVisibility(View.GONE);
            }else{
                mAdView.resume();
            }
        }
        //loadSeen();
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        //saveSeen();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ttsManager.shutDown();
        if (mAdView != null) {
            mAdView.resume();
        }
        //1.3 IAB
        if (mHelper != null) mHelper.dispose();
        mHelper = null;

    }

    public void clickStarButton(View v){
        v = findViewById(R.id.favsBT);
        if(!favsList.contains(currentId)){ //it is not a favorite
            addFavs();
            if (v != null) {
                v.setBackground(getResources().getDrawable(R.drawable.ic_star_checked));
                //System.out.println("xeguei clickStarButton - favsList.contains FALSE");
            }
        }else{ //it is a favorite
            deleteFav();
            if (v != null) {
                v.setBackground(getResources().getDrawable(R.drawable.ic_star_normal));
                //System.out.println("xeguei clickStarButton - favsList.contains TRUE");
            }
        }
    }



    public void deleteFav(){
        favsList.remove(new String(currentId));//don't change this line
        showToastMessage("Favorite deleted");
        saveFavs();
    }

    public void addFavs(){
        if(favsList!=null){
            favsList.add(currentId);
            showToastMessage("Favorite saved");
            //System.out.println("cheguei clickStarButton - favsList: "+favsList);
            saveFavs();
        }else{
            showToastMessage("Something happened.");
        }
    }
    /**static boolean shouldInstantiate = false; //fix for calling this many times
    public void addSeen(){
        ImageView newIV = (ImageView) findViewById(R.id.newIV);
        if(!seenList.contains(currentId)){
            if(seenList!=null){
                seenList.add(currentId);
                System.out.println("cheguei addSeen - NEW seenList: "+seenList);
                //saveSeen();

                if (newIV != null) {
                    newIV.setVisibility(View.VISIBLE);
                    shouldInstantiate = true;
                    FullscreenFragment.viewPager.getAdapter().notifyDataSetChanged();
                }

            }else{
                showToastMessage("Something happened.");
            }
        }else{
            System.out.println("cheguei addSeen - OLD seenList: "+seenList);
            if (newIV != null) {
                newIV.setVisibility(View.INVISIBLE);
                shouldInstantiate = true;
                FullscreenFragment.viewPager.getAdapter().notifyDataSetChanged();
            }
        }
        //FullscreenFragment.viewPager.getAdapter().notifyDataSetChanged();
    }

    public void saveSeen(){
        Set<String> set = new HashSet<String>();
        set.addAll(seenList);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        SharedPreferences.Editor editor = sp.edit();
        editor.putStringSet("seenJokes", set);
        editor.apply();
        loadSeen();
        //FullscreenFragment.viewPager.getAdapter().notifyDataSetChanged();
        //FullscreenFragment.viewPager.destroyDrawingCache();
    }

    public void loadSeen(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        Set<String> set = sp.getStringSet("seenJokes", null);
        if( set != null){
            seenList.clear();
            for (String str : set)
                seenList.add(str);
            System.out.println("cheguei loadSeen - seenList: "+seenList);
        }
    }*/
    public void saveFavs(){
        Set<String> set = new HashSet<String>();
        set.addAll(favsList);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        SharedPreferences.Editor editor = sp.edit();
        editor.putStringSet("favorites", set);
        editor.apply();
        loadFavs();
        FullscreenFragment.viewPager.getAdapter().notifyDataSetChanged();
    }

    public void loadFavs(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        Set<String> set = sp.getStringSet("favorites", null);
        if( set != null){
            favsList.clear();
            for (String str : set)
                favsList.add(str);
            System.out.println("cheguei loadFavs - favsList: "+favsList);
        }
    }

    private void fetchConfig() {
        //System.out.println("cheguei - fetchDiscount");

        long cacheExpiration = 1000; // 1 hour in seconds.
        // If in developer mode cacheExpiration is set to 0 so each fetch will retrieve values from the server.
        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }

        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //System.out.println("cheguei - Fetch Succeeded");
                            mFirebaseRemoteConfig.activateFetched();
                        } else {
                            //System.out.println("cheguei - Fetch Failed");
                        }

                        long initialPrice = mFirebaseRemoteConfig.getLong(ONLINE_VERSION_NUMBER);
                        if(initialPrice>latestBuildNumber){
                            sendNotification();
                        }
                    }
                });
    }

    public void sendNotification() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.tiago.onlyjokes"));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder nBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("An update is available!")
                .setContentText("Click to update Only Jokes.")
                .setContentIntent(pendingIntent)
                .addAction(R.mipmap.ic_launcher, "Open", pendingIntent);
        nBuilder.setAutoCancel(true);
        int notificationId = 1;
        nBuilder.setVibrate(new long[]{1000});
        nBuilder.setLights(Color.RED, 3000, 3000);//LED
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);//Sound
        nBuilder.setSound(alarmSound);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, nBuilder.build());
    }

    static boolean shouldShowProVersionToast = true; //runs pro version toast only once
    private final Handler checkProHandler = new Handler();
    private final Runnable checkProRunnable = new Runnable() {
        public void run() {
            if(isPro){
                if(shouldShowProVersionToast){
                    showToastMessage("You have pro version! Thanks for supporting this app.");
                    shouldShowProVersionToast = false;
                    FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(MainActivity.this);
                    analytics.setUserProperty( "user", "pro");
                }
            }else{
                if(showInterstitial){
                    mInterstitialAd = new InterstitialAd(MainActivity.this);
                    mInterstitialAd.setAdUnitId("ca-app-pub-9563082889139022/2285742992");

                    mInterstitialAd.setAdListener(new AdListener() {
                        @Override
                        public void onAdClosed() {
                            requestNewInterstitial();
                        }
                    });

                    requestNewInterstitial();
                }
                if(showBanner){
                    mAdView.setAdListener(new GoogleAdListener(MainActivity.this, mAdView));
                    AdRequest adRequest = new AdRequest.Builder()
                            .addTestDevice("7826F88C35A0EFF3FFB01D2CCA5B2D87")
                            .build();
                    mAdView.loadAd(adRequest);
                }
                //if(isPro && mAdView!=null && mAdView.getVisibility()==View.VISIBLE){//1.3 it will remove banner right after user bought pro version
                //    mAdView.setVisibility(View.GONE);
                //}
            }
        }
    };

    private void requestNewInterstitial() {
        if(showInterstitial && !isPro){
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice("7826F88C35A0EFF3FFB01D2CCA5B2D87")
                    .build();
            mInterstitialAd.loadAd(adRequest);
        }

    }

    public void showInterstitial(){
        if(showInterstitial && !isPro && mInterstitialAd.isLoaded()) {// it will check for interstitials and display it
            if(MainActivity.interstitialCooldown >0){
                MainActivity.interstitialCooldown--;
            }else{
                mInterstitialAd.show();
                MainActivity.interstitialCooldown = COOLDOWN_MAX;
            }
            toggleAdCountdown();
        }

    }

    public void toggleAdCountdown(){
        TextView countdownTV = (TextView)findViewById(R.id.countdownTV);
        Animation appear = AnimationUtils.loadAnimation(this, R.anim.fadeout);
        appear.setStartOffset(2000);

        if(countdownTV!=null){
            if(MainActivity.interstitialCooldown == 2){
                countdownTV.setText("showing ad in: 3");
                countdownTV.startAnimation(appear);
            }else if(MainActivity.interstitialCooldown == 1){
                countdownTV.setText("showing ad in: 2");
                countdownTV.startAnimation(appear);
            }else if(MainActivity.interstitialCooldown == 0){
                countdownTV.setText("showing ad in: 1");
                countdownTV.startAnimation(appear);
            }else {
                countdownTV.setVisibility(View.GONE);
            }
        }

    }
    public void inflateFragment(String screen) {
        Fragment objFragment = null;
        switch (screen) {
            case "home"://Home screen
                objFragment = new MainActivityFragment();
                break;
            case "jokes"://Game screen
                objFragment = new JokesListFragment();
                break;
            case "search"://Game screen
                objFragment = new JokesListFragment();
                break;
            case "favorites"://Favorites screen
                screen = "favorites";
                objFragment = new JokesListFragment();
                break;

            case "fullscreen":
                objFragment = new FullscreenFragment();
                break;
            }
        System.out.println("-- cheguei inflateFragment1 - screen: "+screen);
        MainActivity.screen = screen;
        System.out.println("-- cheguei inflateFragment2 - screen: "+screen);
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        //transaction.setCustomAnimations(R.anim.push_up_in, R.anim.fadeout, R.anim.pop_enter, R.anim.pop_exit);
        transaction.replace(R.id.container, objFragment, String.valueOf(screen));
        transaction.addToBackStack(null);
        transaction.commit();
    }

    static boolean shouldShuffleList= true;
    public void initialiseDatabase(){
        final DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        try {
            databaseAccess.open();
            dbList = DatabaseAccess.getInstance(this).getDataFromDBnew(chosenCategory,filterText);
            if(shouldShuffleList){
                Collections.shuffle(dbList);//it will shuffle the list
                shouldShuffleList = false;
                System.out.println("-- cheguei initialiseDatabase: shuffled");
            }else{
                System.out.println("-- cheguei initialiseDatabase: not shuffled");
            }
            //initialiseList();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            databaseAccess.close();
        }
    }



    @Override
    public void onItemClick(CategoriesRecyclerAdapter.ItemHolder item, int position) {
        chosenCategory = item.getItemName().toString();
        inflateFragment("jokes");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            goBack();
        }
        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
        //    return true;
        //}
        if (id == R.id.action_about) {
            showAboutDialog();
            return true;
        }
        if (id == R.id.action_list) {
            if ("".equals(filterText)) {
                inflateFragment("jokes");
            }else{
                inflateFragment("search");
            }

            return true;
        }
        if (id == R.id.action_speak) {
            speakJoke(currentJoke);
            return true;
        }
        if (id == R.id.action_share) {
            shareApp();
            //showToastMessage("share app");
            return true;
        }
        if (id == R.id.action_search) {
           inflateFragment("search");
            return true;
        }
        if (id == R.id.action_buy_pro) {
            if(!isPro){
                //buyPremium();
                showProDialog();
            }else{
                showToastMessage("You already have pro version.");
            }
            return true;
        }

        if (id == R.id.action_sendjoke) {
            sendEmail();
            //showToastMessage("share app");
            return true;
        }
        //if (id == R.id.test) {
        //    consumePremium();
        //    showToastMessage("you are no longer premium");
        //    //showToastMessage("share app");
        //    return true;
        //}

        //if (id == R.id.action_settings) {
        //    //chosenCategory = "All";
        //    //inflateFragment("jokes");
        //    //showToastMessage("share app");
        //    return true;
        //}
        if (id == R.id.action_theme) {
            changeTheme();
            //recreate();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    public void showChangeLog() {
        //changeLogIsOpen = true;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        Float lastVersion = sp.getFloat("Last_version_number", 1.0F);
        //if (BuildConfig.DEBUG) {
        //    Log.d(Constants.LOG, "lastVersion: " + lastVersion + "newVersion:" + newVersion);
        //}
        if (lastVersion < newVersion) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putFloat("Last_version_number", newVersion);
            editor.apply();

            AlertDialog ad = new AlertDialog.Builder(this).create();
            ad.setTitle("What's new!      v" + newVersion);
            ad.setMessage(getString(R.string.message_changelog));
            ad.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //changeLogIsOpen = false;
                    dialogInterface.dismiss();
                }
            });
            ad.show();
        }
    }

    /**Day-Night Theme*/
    public void loadTheme(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        nightModeSet = sp.getBoolean("nightModeSet", false);
        //System.out.println("xeguei loadTheme - nightModeSet: "+nightModeSet);
        if(nightModeSet){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        //System.out.println("xeguei loadTheme - nightModeSet: "+nightModeSet);
    }

    public void changeTheme(){
        //System.out.println("xeguei1 changeTheme - nightModeSet: "+nightModeSet);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();
        if(nightModeSet){
            nightModeSet=false;
        }else {
            nightModeSet=true;
        }
        //System.out.println("xeguei2 changeTheme - nightModeSet: "+nightModeSet);
        editor.putBoolean("nightModeSet", nightModeSet);
        editor.apply();
        recreate();
    }

    private void shareApp() {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.tiago.onlyjokes");
        //startActivity(Intent.createChooser(sharingIntent, "Share using"));
        startActivityForResult(Intent.createChooser(sharingIntent, "Share this app:"), 1);

        //Bundle bundle = new Bundle();
        //bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "share");
        //bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "share app");
        //bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "button");
        //mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        Bundle payload = new Bundle();
        payload.putString(FirebaseAnalytics.Param.VALUE, "app shared");
        payload.putString(Param.ITEM_NAME, "app_shared");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE, payload);
    }

    public void shareJoke() {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String text = currentJoke+"\n\nJoke sent from \"Only Jokes\" for Android. Download free: https://play.google.com/store/apps/details?id=com.tiago.onlyjokes";
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, text);
        //startActivity(Intent.createChooser(sharingIntent, "Share using"));
        System.out.println("currentJoke: "+currentJoke);
        startActivityForResult(Intent.createChooser(sharingIntent, "Share this joke:"), 2);

        //analytics
        //Bundle bundle = new Bundle();
        //bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "share");
        //bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "share joke");
        //bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "button");
        //mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        Bundle payload = new Bundle();
        payload.putString(FirebaseAnalytics.Param.VALUE, "joke shared");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE,
                payload);

        //copy to clipboard and notify user
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("label", currentJoke);
        clipboard.setPrimaryClip(clip);
        showToastMessage("To share on Facebook, just paste the joke from the clipboard. (Long click --> Paste)");
    }

    public void speakJoke(String chosenWord){
        if(!ttsManager.checkVolume(this)) showToastMessage("Low volume detected, automatically adjusted to 70%.");
        ttsManager.initQueue(chosenWord);
    }

    public void showAboutDialog() {
        android.app.FragmentManager manager = getFragmentManager();
        MoreFragment dialog = new MoreFragment();
        dialog.show(manager, "dialog");

    }
    public void showToastMessage(String message){//
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG); // first tap will show toast
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 400);
        toast.show();
    }
    public void sendEmail(){
        Intent send = new Intent(Intent.ACTION_SENDTO);
        String uriText = "mailto:" + Uri.encode("tienglishtutor@gmail.com") +
                "?subject=" + Uri.encode("Only Jokes -  ") +
                "&body=" + Uri.encode("");
        Uri uri = Uri.parse(uriText);
        send.setData(uri);
        startActivity(Intent.createChooser(send, "Send mail..."));
        // Tracking Event
        //MyApplication.getInstance().trackEvent("Suggestion", "Suggested: " + countSugg, "Help");
        //editor.apply();
    }

    /**press back twice to exit*/
    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long mBackPressed;
    private void goBack() {
        if(ttsManager.isSpeaking()){
            speakJoke("");
        }else {
            if ("home".equals(screen)) {//you are in topics screen
                if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()){ //second tap will close app
                    finish();
                } else {
                    showToastMessage("Tap back again to exit");
                }
                mBackPressed = System.currentTimeMillis();
            } else if ("jokes".equals(screen)) {
                inflateFragment("fullscreen");
            } else if ("fullscreen".equals(screen)||"favorites".equals(screen)) {
                inflateFragment("home");
            } else if ("search".equals(screen)) {
                if("".equals(filterText)){//filter is empty
                    inflateFragment("home");
                }else{
                    filterText="";
                    inflateFragment("search");
                }
            }
        }
    }
    @Override
    public void onBackPressed() {
        goBack();
    }
}

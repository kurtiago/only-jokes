package com.tiago.onlyjokes;
/**custom class v 1.0*/
import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.HashMap;
import java.util.Locale;

public class TTSManager { //Taken from http://javatechig.com/android/android-texttospeech-example
    private TextToSpeech mTts = null;

    static boolean isLoaded = false;
    static float speechRate = 0.0f;
    static float pitch = 0.0f;
    static String locale;
    static String lastLocale;
    static String LOCALE_US = "US";
    static String LOCALE_UK = "UK";
    static String LOCALE_AUSTRALIA = "en_AU";

    public void init(Context context) {
        try {
            mTts = new TextToSpeech(context, onInitListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private TextToSpeech.OnInitListener onInitListener = new TextToSpeech.OnInitListener() {
        @Override
        public void onInit(int status) {
            if (status == TextToSpeech.SUCCESS) {
                updateTextToSpeech();
            } else {
                Log.e("error", "Initialization Failed!");
            }
        }
    };

    public void updateTextToSpeech(){
        //
        int result;
        if (LOCALE_US.equals(locale)) {
            result = mTts.setLanguage(Locale.US);
        } else if (LOCALE_UK.equals(locale)){
            result = mTts.setLanguage(Locale.UK);
        } else if (LOCALE_AUSTRALIA.equals(locale)){
            result = mTts.setLanguage(Locale.CANADA);
        } else{
            result = mTts.setLanguage(Locale.UK);
        }
        mTts.setPitch(pitch);
        mTts.setSpeechRate(speechRate);

        if(result == TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE) {
            System.out.println("updateTextToSpeech - Denotes the language is available exactly as specified by the locale.");
        }
        if(result == TextToSpeech.LANG_COUNTRY_AVAILABLE) {
            System.out.println("updateTextToSpeech - Denotes the language is available for the language and country specified by the locale, but not the variant.");
        }
        if(result == TextToSpeech.LANG_MISSING_DATA) {
            System.out.println("updateTextToSpeech - Denotes the language data is missing.");
        }
        if(result == TextToSpeech.LANG_NOT_SUPPORTED) {
            System.out.println("updateTextToSpeech - Denotes the language is not supported.");
        }
        if(result == TextToSpeech.LANG_AVAILABLE) {
            System.out.println("updateTextToSpeech - Denotes the language is available for the language by the locale, but not the country and variant.");
        }

        isLoaded = true;
    }

    public void shutDown() {
        mTts.shutdown();
    }

    public boolean isSpeaking(){
        return mTts.isSpeaking();
    }

    public void addQueue(String text) {
        if (isLoaded)
            mTts.speak(text, TextToSpeech.QUEUE_ADD, null);
        else
            Log.e("error", "TTS Not Initialized");
    }

    public void initQueue(String text) {
        // Tracking Event
        //MyApplication.getInstance().trackEvent("Text to speech", "Speaking " + mTitle, "Track speaking"); //v.19 test
        if (isLoaded){
            mTts.setSpeechRate(speechRate);
            mTts.setPitch(pitch);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ttsGreater21(text);
            } else {
                ttsUnder20(text);
            }
        }
        //set speech rate adjusted by user

    }

    @SuppressWarnings("deprecation")
    private void ttsUnder20(String text) {
        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
        mTts.speak(text, TextToSpeech.QUEUE_FLUSH, map);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void ttsGreater21(String text) {

        String utteranceId = this.hashCode() + "";
        //System.out.println(utteranceId);
        mTts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }

    public boolean isGoogleTtsDefault() { //v1.9
        boolean isDefault = false;
        try {
            final String engine = mTts.getDefaultEngine();
            if ("com.google.android.tts".equals(engine)) {
                isDefault = true;

            } else {
                isDefault = false;
            }
            // Check it's != null before doing anything with it.
        } catch (final Exception ignored) { //2.0 "ignored added"

        }
        return isDefault;
    }

    /**public void openTtsSettings(Context context) { // not working here
        //Open Android Text-To-Speech Settings
        if (Build.VERSION.SDK_INT >= 14) {
            Intent intent = new Intent(context,MainActivity.class);
            intent.setAction("com.android.settings.TTS_SETTINGS");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else {
            Intent intent = new Intent(context,MainActivity.class);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.TextToSpeechSettings"));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }*/

    public boolean checkVolume(Context ctx){
        AudioManager audio = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
        int currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
        int maxVolume = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        if(currentVolume<=(maxVolume*0.2f)){
            float percent = 0.7f;
            int seventyVolume = (int) (maxVolume*percent);
            audio.setStreamVolume(AudioManager.STREAM_MUSIC, seventyVolume, 0);
            return false;

        }else{
            return true;
        }
    }

}

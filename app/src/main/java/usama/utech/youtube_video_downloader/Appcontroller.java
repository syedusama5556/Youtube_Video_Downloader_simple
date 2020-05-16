package usama.utech.youtube_video_downloader;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import usama.utech.youtube_video_downloader.utils.LocaleHelper;

public class Appcontroller extends Application {


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, "en"));
    }

    @Override
    public void onCreate() {
        super.onCreate();

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });


        List<Locale> locales = new ArrayList<>();
        locales.add(Locale.ENGLISH);
        locales.add(new Locale("tr", "Turkish"));
        LocaleHelper.setLocale(getApplicationContext(), "en");

        Random random = new Random();
        int num = random.nextInt(2);
    }
}

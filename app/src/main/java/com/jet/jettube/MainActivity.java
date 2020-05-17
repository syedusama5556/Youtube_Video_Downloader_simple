package com.jet.jettube;

import android.Manifest;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.jet.jettube.databinding.ActivityMainBinding;
import com.jet.jettube.service.ClipboardMonitor;
import com.jet.jettube.service.Receiver;
import com.jet.jettube.tasks.downloadVideo;
import com.jet.jettube.utils.Constants;
import com.jet.jettube.utils.LocaleHelper;

import static com.jet.jettube.utils.Constants.PREF_CLIP;
import static com.jet.jettube.utils.Constants.STARTFOREGROUND_ACTION;
import static com.jet.jettube.utils.Constants.STOPFOREGROUND_ACTION;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private SharedPreferences pref;
    private SharedPreferences.Editor prefEditor;
    private boolean csRunning;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        setSupportActionBar(binding.toolbarD);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        pref = getSharedPreferences(PREF_CLIP, 0);// 0 - for private mode
        prefEditor = pref.edit();
        csRunning = pref.getBoolean("csRunning", false);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.AdmobInterstitial));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());


        binding.watchVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();


                    if (isPackageInstalled(MainActivity.this, "com.google.android.youtube")) {
                        Intent intent = new Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://www.youtube.com/watch?v=5X7WWVTrBvM"));
                        intent.setPackage("com.google.android.youtube");
                        startActivity(intent);
                    }
                } else {
                    mInterstitialAd.loadAd(new AdRequest.Builder().build());


                    if (isPackageInstalled(MainActivity.this, "com.google.android.youtube")) {
                        Intent intent = new Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://www.youtube.com/watch?v=5X7WWVTrBvM"));
                        intent.setPackage("com.google.android.youtube");
                        startActivity(intent);
                    }
                }


            }
        });

        binding.gotoYoutubeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();

                    if (isPackageInstalled(MainActivity.this, "com.google.android.youtube")) {
                        Intent intent = new Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://www.youtube.com/watch?v=5X7WWVTrBvM"));
                        intent.setPackage("com.google.android.youtube");
                        startActivity(intent);
                    }


                } else {
                    mInterstitialAd.loadAd(new AdRequest.Builder().build());


                    if (isPackageInstalled(MainActivity.this, "com.google.android.youtube")) {
                        Intent intent = new Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://www.youtube.com/watch?v=5X7WWVTrBvM"));
                        intent.setPackage("com.google.android.youtube");
                        startActivity(intent);
                    }
                }



                //  Toast.makeText(MainActivity.this, "work", Toast.LENGTH_SHORT).show();


            }
        });


        binding.chkAutoDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = binding.chkAutoDownload.isChecked();


                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();


                    if (checked) {
                        Log.e("loged", "testing checked!");
                        startClipboardMonitor();
                    } else {
                        Log.e("loged", "testing unchecked!");
                        stopClipboardMonitor();
                        // setNofication(false);
                    }

                } else {
                    mInterstitialAd.loadAd(new AdRequest.Builder().build());


                    if (checked) {
                        Log.e("loged", "testing checked!");
                        startClipboardMonitor();
                    } else {
                        Log.e("loged", "testing unchecked!");
                        stopClipboardMonitor();
                        // setNofication(false);
                    }

                }


            }
        });


//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            binding.autodownloadLayoutCard.setVisibility(View.GONE);
//
//        } else {
//
//            binding.autodownloadLayoutCard.setVisibility(View.VISIBLE);
//        }


        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!checkIfAlreadyhavePermission()) {
                requestForSpecificPermission();
            }

            if (android.os.Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(this)) {   //Android M Or Over
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 123);
                return;
            }

        }


        createNotificationChannel(
                this,
                NotificationManagerCompat.IMPORTANCE_LOW,
                true,
                getString(R.string.app_name),
                getString(R.string.auto)
        );


        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView);


        AdRequest adRequest = new AdRequest.Builder().build();


        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                //      Toast.makeText(MainActivity.this, "ad loddd", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                //     Toast.makeText(MainActivity.this, "ad loddd err "+errorCode, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });

        mAdView.loadAd(adRequest);
    }


    public void shareApp(Context context) {
        final String appPackageName = context.getPackageName();
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.chge) + " " + " https://play.google.com/store/apps/details?id=" + appPackageName);
        sendIntent.setType("text/plain");
        context.startActivity(sendIntent);
    }

    private boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS);
        return result == PackageManager.PERMISSION_GRANTED;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mymenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.exit_m:
                System.exit(0);
                return true;
            case R.id.share_m:
                shareApp(MainActivity.this);

                return true;

            case R.id.action_language:

                showlanguageDialog();
                return true;
            case R.id.star_m:

                launchMarket();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showlanguageDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_change_language);

        TextView l_english = dialog.findViewById(R.id.l_english);
        l_english.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocaleHelper.setLocale(MainActivity.this, "en");

                recreate();
                dialog.dismiss();
            }
        });

        TextView l_turkish = dialog.findViewById(R.id.l_turkey);
        l_turkish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocaleHelper.setLocale(MainActivity.this, "tr");

                recreate();
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    @Override
    protected void attachBaseContext(Context newBase) {

        Context newBase2 = newBase;
        newBase2 = LocaleHelper.onAttach(newBase2);

        super.attachBaseContext(newBase2);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                    //    Toast.makeText(this, "permission granted", Toast.LENGTH_SHORT).show();

                } else {
                    //not granted
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
    }

    void createNotificationChannel(
            Context context,
            int importance,
            Boolean showBadge,
            String name,
            String description
    ) {
        // 1
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // 2
            String channelId = getPackageName() + name;
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);
            channel.setShowBadge(showBadge);

            // 3
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
            Log.e("loged112211", "Notificaion Channel Created!");
        }
    }

    private void launchMarket() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.unable, Toast.LENGTH_LONG).show();
        }
    }

    public boolean isPackageInstalled(Context context, String packageName) {

        boolean found = true;

        try {

            context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {

            found = false;
        }

        return found;
    }


    void startClipboardMonitor() {
        prefEditor.putBoolean("csRunning", true);
        prefEditor.commit();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(
                    new Intent(
                            this,
                            ClipboardMonitor.class
                    ).setAction(STARTFOREGROUND_ACTION)
            );
        } else {
            startService(
                    new Intent(
                            this,
                            ClipboardMonitor.class
                    )
            );
        }

    }

    void stopClipboardMonitor() {
        prefEditor.putBoolean("csRunning", false);
        prefEditor.commit();

        stopService(new Intent(MainActivity.this, ClipboardMonitor.class).setAction(STOPFOREGROUND_ACTION));


    }

    PendingIntent makePendingIntent(String name) {
        Intent intent = new Intent(this, Receiver.class);
        intent.setAction(name);
        return PendingIntent.getBroadcast(this, 0, intent, 0);
    }

    void DownloadVideo(String url) {

        //if (iUtils.checkURL(url)) {
        if (url.equals("") && Constants.checkURL(url)) {
            Toast.makeText(this, R.string.enterv, Toast.LENGTH_SHORT).show();
        } else {
            downloadVideo.Start(this, url, false);

        }
    }


}

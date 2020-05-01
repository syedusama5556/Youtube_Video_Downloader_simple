package usama.utech.youtube_video_downloader;

import android.Manifest;
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
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import usama.utech.youtube_video_downloader.databinding.ActivityMainBinding;
import usama.utech.youtube_video_downloader.service.ClipboardMonitor;
import usama.utech.youtube_video_downloader.service.Receiver;
import usama.utech.youtube_video_downloader.tasks.downloadVideo;
import usama.utech.youtube_video_downloader.utils.Constants;

import static usama.utech.youtube_video_downloader.utils.Constants.PREF_CLIP;
import static usama.utech.youtube_video_downloader.utils.Constants.STARTFOREGROUND_ACTION;
import static usama.utech.youtube_video_downloader.utils.Constants.STOPFOREGROUND_ACTION;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private SharedPreferences pref;
    private SharedPreferences.Editor prefEditor;
    private boolean csRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        pref = getSharedPreferences(PREF_CLIP, 0);// 0 - for private mode
        prefEditor = pref.edit();
        csRunning = pref.getBoolean("csRunning", false);


        binding.watchVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isPackageInstalled(MainActivity.this, "com.google.android.youtube")) {
                    Intent intent = new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://www.youtube.com/watch?v=5X7WWVTrBvM"));
                    intent.setPackage("com.google.android.youtube");
                    startActivity(intent);
                }

            }
        });

        binding.gotoYoutubeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //  Toast.makeText(MainActivity.this, "work", Toast.LENGTH_SHORT).show();

                if (isPackageInstalled(MainActivity.this, "com.google.android.youtube")) {
                    Intent intent = new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://www.youtube.com/watch?v=5X7WWVTrBvM"));
                    intent.setPackage("com.google.android.youtube");
                    startActivity(intent);
                }
            }
        });

        binding.rateUsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(MainActivity.this, "work", Toast.LENGTH_SHORT).show();

                launchMarket();
            }
        });
        binding.chkAutoDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = binding.chkAutoDownload.isChecked();

                if (checked) {
                    Log.e("loged", "testing checked!");
                    startClipboardMonitor();
                } else {
                    Log.e("loged", "testing unchecked!");
                    stopClipboardMonitor();
                    // setNofication(false);
                }
            }
        });


        if (android.os.Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(this)) {   //Android M Or Over
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 123);
            return;
        }
        
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!checkIfAlreadyhavePermission()) {
                requestForSpecificPermission();
            }
        }

        createNotificationChannel(
                this,
                NotificationManagerCompat.IMPORTANCE_LOW,
                true,
                getString(R.string.app_name),
                "Auto Download"
        );

    }
    private boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "permission granted", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, " unable to find market app", Toast.LENGTH_LONG).show();
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
            Toast.makeText(this, "Enter Valid Url", Toast.LENGTH_SHORT).show();
        } else {
            downloadVideo.Start(this, url, false);

        }
    }


}

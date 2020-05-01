package usama.utech.youtube_video_downloader.tasks;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;


import java.io.File;

import static usama.utech.youtube_video_downloader.utils.Constants.DOWNLOAD_DIRECTORY;
import static usama.utech.youtube_video_downloader.utils.Constants.PREF_APPNAME;


public class downloadFile {
    public static DownloadManager downloadManager;
    public static long downloadID;
    private static String mBaseFolderPath;


    public static void Downloading(final Context context, String url, String title, String ext) {
        String cutTitle = title;
        String characterFilter = "[^\\p{L}\\p{M}\\p{N}\\p{P}\\p{Z}\\p{Cf}\\p{Cs}\\s]";
        cutTitle = cutTitle.replaceAll(characterFilter, "");
        cutTitle = cutTitle.replaceAll("['+.^:,#\"]", "");
        cutTitle = cutTitle.replace(" ", "-").replace("!", "").replace(":", "") + ext;
        if (cutTitle.length() > 100)
            cutTitle = cutTitle.substring(0, 100) + ext;
        downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        //  Uri mainUri = FileProvider.get(context, context.getApplicationContext().getPackageName() + ".provider", url);


        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setTitle(title);
        request.setDescription("downloasd Des");

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        String folderName = DOWNLOAD_DIRECTORY;
        SharedPreferences preferences = context.getSharedPreferences(PREF_APPNAME, Context.MODE_PRIVATE);

        if (!preferences.getString("path", "DEFAULT").equals("DEFAULT")) {

            mBaseFolderPath = preferences.getString("path", "DEFAULT");
        } else {

            mBaseFolderPath = context.getExternalFilesDir(null).getAbsolutePath() + File.separator + folderName;
            System.out.println("myerroris5555555555 " + context.getExternalFilesDir(null).getAbsolutePath() + File.separator + folderName);
            // mBaseFolderPath = android.os.Environment.getDataDirectory() + File.separator + folderName;
        }

        File mBaseFolderPathfile = new File(mBaseFolderPath);

        if (!mBaseFolderPathfile.exists()) {
            mBaseFolderPathfile.mkdir();
        }
        String[] bits = mBaseFolderPath.split("/");
        String Dir = bits[bits.length - 1];
        //  request.setDestinationUri(new File(mBaseFolderPath).);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, cutTitle);

        } else {
            request.setDestinationInExternalPublicDir(Dir, cutTitle);

        }



        request.allowScanningByMediaScanner();
        downloadID = downloadManager.enqueue(request);
        Log.e("downloadFileName", cutTitle);

        Handler mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {

                Toast.makeText(context, "Started", Toast.LENGTH_SHORT).show();

            }
        };

    }
}

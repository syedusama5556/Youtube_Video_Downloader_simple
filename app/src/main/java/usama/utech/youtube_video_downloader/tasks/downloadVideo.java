package usama.utech.youtube_video_downloader.tasks;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;
import usama.utech.youtube_video_downloader.R;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.WINDOW_SERVICE;

public class downloadVideo {

    public static Context Mcontext;
    public static ProgressDialog pd;
    public static Dialog dialog;
    public static SharedPreferences prefs;
    public static Boolean fromService;
    static String SessionID, Title;
    static int error = 1;
    static LinearLayout mainLayout;
    static Dialog dialogquality;
    static WindowManager windowManager2;
    static WindowManager.LayoutParams params;
    static View mChatHeadView;
    static ImageView img_dialog;

    public static void Start(final Context context, String url, Boolean service) {

        Mcontext = context;
        fromService = service;
        Log.i("LOGClipboard111111 clip", "work 2");
//SessionID=title;
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }
        if (!fromService) {
            pd = new ProgressDialog(context);
            pd.setMessage("genarating download link");
            pd.setCancelable(false);
            pd.show();
        }
         if (url.contains("youtube.com") || url.contains("youtu.be")) {
            //  String youtubeLink = "https://www.youtube.com/watch?v=668nUCeBHyY";
            Log.i("LOGClipboard111111 clip", "work 3");
            getYoutubeDownloadUrl(url);

        } else {
            if (!fromService) {
                pd.dismiss();

                Handler mHandler = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message message) {
                        Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();



                    }
                };
            }
        }

//iUtils.ShowToast(Mcontext,url);
//iUtils.ShowToast(Mcontext,SessionID);


        prefs = Mcontext.getSharedPreferences("AppConfig", MODE_PRIVATE);
    }

    //TODO
    private static void getYoutubeDownloadUrl(String youtubeLink) {

        new YouTubeExtractor(Mcontext) {

            @Override
            public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {
                //    mainProgressBar.setVisibility(View.GONE);

                if (ytFiles != null) {

                    if (!fromService) {
                        pd.dismiss();
                    }


                    windowManager2 = (WindowManager) Mcontext.getSystemService(WINDOW_SERVICE);
                    LayoutInflater layoutInflater = (LayoutInflater) Mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                    mChatHeadView = layoutInflater.inflate(R.layout.dialog_quality_ytd, null);

                    mainLayout = mChatHeadView.findViewById(R.id.linlayout_dialog);

                    img_dialog = mChatHeadView.findViewById(R.id.img_dialog);


                    dialogquality = new Dialog(Mcontext);
                    dialogquality.setContentView(R.layout.dialog_quality_ytd);
                    mainLayout = dialogquality.findViewById(R.id.linlayout_dialog);
                    img_dialog = dialogquality.findViewById(R.id.img_dialog);


                    int size = 0;

                    try {
                        DisplayMetrics displayMetrics = new DisplayMetrics();
                        ((Activity) Mcontext).getWindowManager()
                                .getDefaultDisplay()
                                .getMetrics(displayMetrics);

                        int height = displayMetrics.heightPixels;
                        int width = displayMetrics.widthPixels;

                        size = width / 2;

                    } catch (Exception e) {
                        size = WindowManager.LayoutParams.WRAP_CONTENT;
                    }

                        params = new WindowManager.LayoutParams(
                                size,
                                WindowManager.LayoutParams.WRAP_CONTENT,
                                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                                        | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                                PixelFormat.TRANSLUCENT);

                        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
                        params.x = 0;
                        params.y = 100;

                    // mainLayout.setLayoutParams(params);


                    for (int i = 0, itag; i < ytFiles.size(); i++) {
                        itag = ytFiles.keyAt(i);
                        // ytFile represents one file with its url and meta data
                        YtFile ytFile = ytFiles.get(itag);

                        // Just add videos in a decent format => height -1 = audio
                        if (ytFile.getFormat().getHeight() == -1 || ytFile.getFormat().getHeight() >= 360) {
                            addButtonToMainLayouttest(vMeta.getTitle(), ytFile);
                        }
                    }

                    img_dialog.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogquality.dismiss();
                        }
                    });

                    dialogquality.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
                    dialogquality.getWindow().setAttributes(params);
                    dialogquality.show();


                } else {
                    if (!fromService) {
                        pd.dismiss();
                    }

                    Handler mHandler = new Handler(Looper.getMainLooper()) {
                        @Override
                        public void handleMessage(Message message) {
                            Toast.makeText(Mcontext, "Error", Toast.LENGTH_SHORT).show();

                        }
                    };

                }


            }
        }.extract(youtubeLink, true, false);
    }

    private static void addButtonToMainLayouttest(final String videoTitle, final YtFile ytfile) {


        // Display some buttons and let the user choose the format
        String btnText = (ytfile.getFormat().getHeight() == -1) ? "Audio " +
                ytfile.getFormat().getAudioBitrate() + " kbit/s" :
                ytfile.getFormat().getHeight() + "p";
        btnText += (ytfile.getFormat().isDashContainer()) ? " dash" : "";
        Button btn = new Button(Mcontext);

        btn.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));


        btn.setText(btnText);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (windowManager2 != null) {
                    try {
                        windowManager2.removeView(mChatHeadView);
                    } catch (Exception e) {
                        Log.i("LOGClipboard111111", "error is " + e.getMessage());

                    }
                }

                String filename;
                if (videoTitle.length() > 55) {
                    filename = videoTitle.substring(0, 55) + "." + ytfile.getFormat().getExt();
                } else {
                    filename = videoTitle + "." + ytfile.getFormat().getExt();
                }
                filename = filename.replaceAll("[\\\\><\"|*?%:#/]", "");

//                downloadFromUrl(ytfile.getUrl(), videoTitle, filename);
//
//
//                String downloadUrl = ytFiles.get(itag).getUrl();


                new downloadFile().Downloading(Mcontext, ytfile.getUrl(), filename, ".mp4");


                dialogquality.dismiss();
            }
        });
        mainLayout.addView(btn);
    }


}

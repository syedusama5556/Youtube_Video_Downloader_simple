package com.jet.jettube;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.jet.jettube.databinding.ActivityDownloadVideoBinding;
import com.jet.jettube.tasks.downloadFile;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;

import static com.jet.jettube.tasks.downloadVideo.Mcontext;

public class DownloadVideoActivity extends AppCompatActivity {
    ActivityDownloadVideoBinding binding;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDownloadVideoBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();
        setContentView(view);

        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();


        progress = new ProgressDialog(DownloadVideoActivity.this);
        progress.setMessage(getString(R.string.lod));


        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent); // Handle text being sent
            }

        }
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadVideoActivity.super.onBackPressed();
            }
        });

    }


    void handleSendText(Intent intent) {
        String url = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (url != null) {

            Log.e("loged112211 txt ", url);


            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "http://" + url;
            }

            if (url.contains("youtube.com") || url.contains("youtu.be")) {
                //  String youtubeLink = "https://www.youtube.com/watch?v=668nUCeBHyY";
                Log.i("LOGClipboard111111 clip", "work 3");


                getYoutubeDownloadUrlfor(url);
                progress.show();
            } else {

                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();


            }
        }


    }


    @SuppressLint("StaticFieldLeak")
    void getYoutubeDownloadUrlfor(String youtubeLink) {

        new YouTubeExtractor(DownloadVideoActivity.this) {

            @Override
            public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {
                //    mainProgressBar.setVisibility(View.GONE);

                if (ytFiles != null) {


                    for (int i = 0, itag; i < ytFiles.size(); i++) {
                        itag = ytFiles.keyAt(i);
                        // ytFile represents one file with its url and meta data
                        YtFile ytFile = ytFiles.get(itag);

                        // Just add videos in a decent format => height -1 = audio
                        if (ytFile.getFormat().getHeight() == -1 || ytFile.getFormat().getHeight() >= 360) {
                            addButtonToMainLayouttest(vMeta.getTitle(), ytFile);
                        }
                    }

                    progress.dismiss();

                } else {
                    progress.dismiss();
                    Toast.makeText(Mcontext, "Error", Toast.LENGTH_SHORT).show();


                }


            }
        }.extract(youtubeLink, true, false);
    }

    void addButtonToMainLayouttest(final String videoTitle, final YtFile ytfile) {


        // Display some buttons and let the user choose the format
        String btnText = (ytfile.getFormat().getHeight() == -1) ? "MP3 " +
                ytfile.getFormat().getAudioBitrate() + " kbit/s" :
                ytfile.getFormat().getHeight() + "p";
        btnText += (ytfile.getFormat().isDashContainer()) ? " No Audio" : "";
        Button btn = new Button(this);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(8, 8, 8, 8);
        btn.setLayoutParams(params);

        // btn.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        btn.setBackground(getResources().getDrawable(R.drawable.btn_bg_download_screen));
        btn.setTextColor(Color.WHITE);

        btn.setText(btnText);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String filename;
                if (videoTitle.length() > 55) {
                    filename = videoTitle.substring(0, 55);
                } else {
                    filename = videoTitle;
                }
                filename = filename.replaceAll("[\\\\><\"|*?%:#/]", "");

//                downloadFromUrl(ytfile.getUrl(), videoTitle, filename);
//
//
//                String downloadUrl = ytFiles.get(itag).getUrl();

                if (ytfile.getFormat().getExt().equals("m4a")) {
                    new downloadFile().Downloading(DownloadVideoActivity.this, ytfile.getUrl(), filename, ".mp3");
                } else {
                    new downloadFile().Downloading(DownloadVideoActivity.this, ytfile.getUrl(), filename, "." + ytfile.getFormat().getExt());

                }

            }
        });
        binding.linlayoutBtns.addView(btn);
    }


}

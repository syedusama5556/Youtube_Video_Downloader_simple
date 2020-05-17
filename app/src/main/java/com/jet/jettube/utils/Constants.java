package com.jet.jettube.utils;

import android.text.TextUtils;
import android.util.Patterns;
import android.webkit.URLUtil;

import java.net.URL;
import java.util.regex.Pattern;

public class Constants {
    public static final String STARTFOREGROUND_ACTION = "usama.utech.youtube_video_downloader.action.startforeground";
    public static final String STOPFOREGROUND_ACTION = "usama.utech.youtube_video_downloader.action.stopforeground";
    public static String PREF_CLIP = "ytdVideoDownloader";
    public static String DOWNLOAD_DIRECTORY = "ytdVideoDownloader";
    public static String PREF_APPNAME = "ytdVideoDownloader";

    public static boolean checkURL(CharSequence input) {
        if (TextUtils.isEmpty(input)) {
            return false;
        }
        Pattern URL_PATTERN = Patterns.WEB_URL;
        boolean isURL = URL_PATTERN.matcher(input).matches();
        if (!isURL) {
            String urlString = input + "";
            if (URLUtil.isNetworkUrl(urlString)) {
                try {
                    new URL(urlString);
                    isURL = true;
                } catch (Exception e) {
                }
            }
        }
        return isURL;
    }

}

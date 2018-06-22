package paradise.ccclxix.projectparadise.utils;

import android.text.TextUtils;

public class YoutubeHelpers {

    public static String getVideoID(String link){
        if (link.contains("v=")){
            String[] linkA = link.split("v=");
            return linkA[linkA.length-1];
        }
        if (link.contains("https://youtu.be")){
            String[] linkA = link.split("/");
            return linkA[linkA.length-1];
        }
        return "";
    }

    public static String getVideoThumbnail(String id){
        if (!TextUtils.isEmpty(id)){
            return String.format("http://img.youtube.com/vi/%s/0.jpg", id);
        }
        return "";
    }
}

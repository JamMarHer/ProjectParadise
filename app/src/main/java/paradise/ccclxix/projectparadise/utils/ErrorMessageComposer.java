package paradise.ccclxix.projectparadise.utils;

import android.util.Log;

public class ErrorMessageComposer {

    public static void loadingPost(String source, String waveID, String postID){
        Log.d(source, String.format("WaveID: %s \nPostID: %s", waveID, postID));
    }
}

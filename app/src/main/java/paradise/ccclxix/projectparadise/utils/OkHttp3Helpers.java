package paradise.ccclxix.projectparadise.utils;

import android.content.Context;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

public class OkHttp3Helpers {


    // TODO passing tag for future implementation of types of downloaders.
    public static OkHttpClient getOkHttpClient(String tag, Context context) {
        // TODO if Tag = blah blah then etc.limit the cache etc.
        return new OkHttpClient.Builder()
                .cache(new Cache(context.getCacheDir(), 250000000))
                .build();
    }
}

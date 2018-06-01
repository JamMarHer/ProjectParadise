package paradise.ccclxix.projectparadise.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.View;

import com.squareup.picasso.Transformation;

public class Transformations {

    private static View wavePostImage;
    private static float WIDTH;

    public static Transformation getScaleDownWithView(View view){
        wavePostImage = view;
        return transformationWithView;
    }

    public static Transformation getScaleDownWithMaxWidthDP(Context context){
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        WIDTH = displayMetrics.widthPixels / displayMetrics.density;
        return transformationWithMaxDP;
    }

    // TODO This method downsizes the image too much when the resolution is too high.
    private static Transformation transformationWithMaxDP = new Transformation() {

        @Override
        public Bitmap transform(Bitmap source) {
            int targetWidth = (int) WIDTH;

            double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
            int targetHeight = (int) (targetWidth * aspectRatio);
            Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
            if (result != source) {
                // Same bitmap is returned if sizes are the same
                source.recycle();
            }
            return result;
        }

        @Override
        public String key() {
            return "transformation" + " desiredWidth";
        }
    };

    private static Transformation transformationWithView = new Transformation() {

        @Override
        public Bitmap transform(Bitmap source) {
            int targetWidth = wavePostImage.getWidth();

            double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
            int targetHeight = (int) (targetWidth * aspectRatio);
            Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
            if (result != source) {
                // Same bitmap is returned if sizes are the same
                source.recycle();
            }
            return result;
        }

        @Override
        public String key() {
            return "transformation" + " desiredWidth";
        }
    };
}

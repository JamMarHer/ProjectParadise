package paradise.ccclxix.projectparadise.utils;

import android.graphics.Bitmap;
import android.view.View;

import com.squareup.picasso.Transformation;

public class Transformations {

    private static View wavePostImage;

    public static Transformation getScaleDown(View view){
        wavePostImage = view;
        return transformation;
    }

    private static Transformation transformation = new Transformation() {

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

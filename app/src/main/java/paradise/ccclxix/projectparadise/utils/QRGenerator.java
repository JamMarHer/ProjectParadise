package paradise.ccclxix.projectparadise.utils;

import android.graphics.Bitmap;

import net.glxn.qrgen.android.QRCode;

public class QRGenerator {
    public static Bitmap getEventQR(String eventID){
        return QRCode.from(eventID).bitmap();
    }

}

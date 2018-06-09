package paradise.ccclxix.projectparadise.utils;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.androidadvance.topsnackbar.TSnackbar;

public class SnackBar extends AppCompatActivity {

    private static int BACKGROUND_COLOR = Color.parseColor("#CC000000");

    public void showErrorBar(View view){
        showEmojiBar(view, "Something went wrong.", Icons.POOP);
    }

    public void showEmojiBar(String message, int emoji) {
        TSnackbar snackbar = TSnackbar.make(findViewById(android.R.id.content), message, TSnackbar.LENGTH_LONG);
        snackbar.setActionTextColor(Color.WHITE);
        if(Icons.isIcon(emoji)){
            snackbar.setIconLeft(emoji, 24);
        }
        else{
            Log.e("SNACK", "Icon was not an icon");
        }
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(BACKGROUND_COLOR);
        TextView textView = snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    public void showEmojiBar(View view, String message, int emoji) {
        TSnackbar snackbar = TSnackbar.make(view, message, TSnackbar.LENGTH_LONG);
        snackbar.setActionTextColor(Color.WHITE);
        if(Icons.isIcon(emoji)){
            snackbar.setIconLeft(emoji, 24);
        }
        else{
            Log.e("SNACK", "Icon was not an icon");
        }
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(BACKGROUND_COLOR);
        TextView textView = snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    public void showDefaultBar(String message){
        TSnackbar snackbar = TSnackbar.make(findViewById(android.R.id.content), message, TSnackbar.LENGTH_LONG);
        snackbar.setActionTextColor(Color.WHITE);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(BACKGROUND_COLOR);
        TextView textView = snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    public void showWhiteBar(String message){
        TSnackbar snackbar = TSnackbar.make(findViewById(android.R.id.content), message, TSnackbar.LENGTH_LONG);
        snackbar.setActionTextColor(Color.WHITE);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(Color.parseColor("#27000000"));
        TextView textView = snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }
}

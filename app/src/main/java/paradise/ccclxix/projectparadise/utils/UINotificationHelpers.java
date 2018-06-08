package paradise.ccclxix.projectparadise.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.widget.TextView;

import com.androidadvance.topsnackbar.TSnackbar;

public class UINotificationHelpers {

    public static void showTopSnackBar(View view, String message, int icon){
        TSnackbar snackbar = TSnackbar.make(view, message, TSnackbar.LENGTH_SHORT);
        snackbar.setActionTextColor(Color.WHITE);
        if(Icons.isIcon(icon))
            snackbar.setIconLeft(icon, 24);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(Color.parseColor("#CC000000"));
        TextView textView = snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public static void showProgress(final boolean show, final View viewToHide, final View mProgressView, final int aniTime) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {

            viewToHide.setVisibility(show ? View.GONE : View.VISIBLE);
            viewToHide.animate().setDuration(aniTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    viewToHide.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(aniTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            viewToHide.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
package paradise.ccclxix.projectparadise.Attending;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory;
import com.androidadvance.topsnackbar.TSnackbar;
import com.google.gson.Gson;
import com.google.zxing.Result;

import iDaeAPI.IDaeClient;
import iDaeAPI.model.EventAttenEnterRequest;
import iDaeAPI.model.EventAttenEnterResponse;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import paradise.ccclxix.projectparadise.BackendVals.MessageCodes;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.CredentialsManager;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.EventManager;
import paradise.ccclxix.projectparadise.InitialAcitivity;
import paradise.ccclxix.projectparadise.Login.LoginActivity;
import paradise.ccclxix.projectparadise.MainActivity;
import paradise.ccclxix.projectparadise.Network.NetworkHandler;
import paradise.ccclxix.projectparadise.Network.NetworkResponse;
import paradise.ccclxix.projectparadise.R;

import static android.Manifest.permission.CAMERA;

public class QRScannerActivity extends AppCompatActivity  implements ZXingScannerView.ResultHandler{

    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView scannerView;

    ApiClientFactory apiClientFactory;
    IDaeClient iDaeClient;

    EventAttenEnterResponse eventAttenEnterResponse;
    CredentialsManager credentialsManager;
    EventManager eventManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);
        apiClientFactory = new ApiClientFactory();
        iDaeClient = apiClientFactory.build(IDaeClient.class);

        credentialsManager = new CredentialsManager(getApplicationContext());
        eventManager = new EventManager(getApplicationContext());

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkPermission()){
                showSnackbar("Scan the QR code from one of the event hosts.");
            }else {
                requestPermission();
            }
        }
    }

    private boolean checkPermission()
    {
        return (ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission()
    {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA);
    }

    @Override
    public void onResume() {
        super.onResume();

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.M) {
            if (checkPermission()) {
                if(scannerView == null) {
                    scannerView = new ZXingScannerView(this);
                    setContentView(scannerView);
                }
                scannerView.setResultHandler(this);
                scannerView.startCamera();
            } else {
                requestPermission();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        scannerView.stopCamera();
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (grantResults.length > 0) {

                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted){
                        Toast.makeText(getApplicationContext(), "Permission Granted, Now you can access camera", Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(getApplicationContext(), "Permission Denied, You cannot access and camera", Toast.LENGTH_LONG).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(CAMERA)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{CAMERA},
                                                            REQUEST_CAMERA);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new android.support.v7.app.AlertDialog.Builder(QRScannerActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void handleResult(Result result) {
        final String scanResult = result.getText();
        final EventAttenEnterRequest eventAttenEnterRequest = new EventAttenEnterRequest();
        eventAttenEnterRequest.setEventID(scanResult);
        eventAttenEnterRequest.setUsername(credentialsManager.getUsername());
        eventAttenEnterRequest.setToken(credentialsManager.getToken());

        Thread loginEvent = new Thread() {
            @Override
            public void run() {
                eventAttenEnterResponse = iDaeClient.idaeEventAttendantEntereventPost(eventAttenEnterRequest);
                try {
                    super.run();
                    while (eventAttenEnterResponse == null) {
                        sleep(100);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            switch (eventAttenEnterResponse.getStatus()){

                                case MessageCodes.OK:
                                    if (eventAttenEnterResponse.getAgeTarget().equals("all")){
                                        startMainActivity(eventAttenEnterResponse);
                                        break;
                                    }
                                    createSuccessDialog(scanResult, eventAttenEnterResponse);
                                    break;
                                case MessageCodes.INCORRECT_TOKEN:
                                    showSnackbar("You have been logged out.");
                                    try {
                                        sleep(300);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    Intent intentOut = new Intent(QRScannerActivity.this, InitialAcitivity.class);
                                    intentOut.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    credentialsManager.clear();
                                    QRScannerActivity.this.startActivity(intentOut);
                                    break;
                                case MessageCodes.SERVER_ERROR:
                                    scannerView.resumeCameraPreview(QRScannerActivity.this);
                                    showSnackbar("Server didn't respond, please try again later.");
                                    break;
                            }
                        }
                    });
                }
            }
        };
        loginEvent.start();

    }

    public void startMainActivity(EventAttenEnterResponse event){
        Intent intent = new Intent(QRScannerActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("source", "qr_code_scanned");
        eventManager.updateEventAttendant(event);
        eventManager.setAttendantMode();

        finish();
        startActivity(intent);
    }

    private void createSuccessDialog(final String scanResult, final EventAttenEnterResponse event){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);

        builder.setTitle(String.format("Event: %s", event.getName()));

        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                scannerView.resumeCameraPreview(QRScannerActivity.this);
            }
        });
        builder.setNegativeButton("Nope", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                scannerView.resumeCameraPreview(QRScannerActivity.this);
            }
        });
        builder.setPositiveButton("Join", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(QRScannerActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("source", "qr_code_scanned");
                eventManager.updateEventAttendant(event);
                eventManager.setAttendantMode();
                finish();
                startActivity(intent);
            }
        });
        builder.setMessage(String.format("This event is intended for %s+ years old. \n If you are %s+" +
                " years old and want to join hit that join button!", event.getAgeTarget(),event.getAgeTarget()));

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showSnackbar(final String message) {
        TSnackbar snackbar = TSnackbar.make(findViewById(android.R.id.content), message, TSnackbar.LENGTH_LONG);
        snackbar.setActionTextColor(Color.WHITE);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(Color.parseColor("#27000000"));
        TextView textView = (TextView) snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }
}

package paradise.ccclxix.projectparadise.Attending;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import paradise.ccclxix.projectparadise.BackendVals.MessageCodes;
import paradise.ccclxix.projectparadise.Login.LoginActivity;
import paradise.ccclxix.projectparadise.MainActivity;
import paradise.ccclxix.projectparadise.Network.NetworkHandler;
import paradise.ccclxix.projectparadise.Network.NetworkResponse;
import paradise.ccclxix.projectparadise.R;

import static android.Manifest.permission.CAMERA;

public class QRScannerActivity extends AppCompatActivity  implements ZXingScannerView.ResultHandler{

    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView scannerView;
    private NetworkHandler networkHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);
        networkHandler =  new NetworkHandler(getApplicationContext());

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkPermission()){
                Toast.makeText(QRScannerActivity.this, "Persmission is granted", Toast.LENGTH_SHORT).show();
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



        Thread checkEvent = new Thread() {
            @Override
            public void run() {
                networkHandler.isEventValidNetworkRequest(scanResult);
                try {
                    super.run();
                    while (networkHandler.isRunning()) {
                        sleep(100);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            NetworkResponse networkResponse = networkHandler.getNetworkResponse();
                            switch (networkResponse.getStatus()){
                                case 100:
                                    createSuccessDialog(scanResult, networkResponse.getResponse().getEvent_id());
                                    break;
                                case MessageCodes.INVALID_EVENT:
                                    scannerView.resumeCameraPreview(QRScannerActivity.this);
                                    showSnackbar("Event is no longer valid.");
                                    break;
                                case MessageCodes.INCORRECT_FORMAT:
                                    scannerView.resumeCameraPreview(QRScannerActivity.this);
                                    showSnackbar("There has been a problem with the server response.");
                                    break;
                                case MessageCodes.FAILED_CONNECTION:
                                    scannerView.resumeCameraPreview(QRScannerActivity.this);
                                    showSnackbar("Server didn't respond.");
                                    break;
                                case MessageCodes.NO_INTERNET_CONNECTION:
                                    scannerView.resumeCameraPreview(QRScannerActivity.this);
                                    showSnackbar("No internet connection.");
                                    break;
                            }
                        }
                    });
                }
            }
        };
        checkEvent.start();

    }

    private void createSuccessDialog(final String scanResult, final String eventName){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);

        builder.setTitle("Event found!");

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
                intent.putExtra("event_id", scanResult);
                finish();
                startActivity(intent);
            }
        });
        builder.setMessage(eventName);
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showSnackbar(final String message) {
        Snackbar.make(findViewById(android.R.id.content),message,
                Snackbar.LENGTH_LONG).show();
    }
}

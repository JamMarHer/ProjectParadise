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
import android.support.annotation.NonNull;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.zxing.Result;


import java.util.HashMap;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import paradise.ccclxix.projectparadise.BackendVals.MessageCodes;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.CredentialsManager;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.EventManager;
import paradise.ccclxix.projectparadise.InitialAcitivity;
import paradise.ccclxix.projectparadise.MainActivity;
import paradise.ccclxix.projectparadise.Models.Event;
import paradise.ccclxix.projectparadise.R;

import static android.Manifest.permission.CAMERA;

public class QRScannerActivity extends AppCompatActivity  implements ZXingScannerView.ResultHandler{

    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView scannerView;

    ValueEventListener valueEventListener;
    DatabaseReference databaseReference;

    ApiClientFactory apiClientFactory;


    Event eventAttenEnterResponse;
    CredentialsManager credentialsManager;
    EventManager eventManager;


    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);
        apiClientFactory = new ApiClientFactory();

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
    public void handleResult(final Result result) {
        final HashMap<String, Object> event = new HashMap<>();
        try{
            final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference =firebaseDatabase.getReference()
                    .child("events_us")
                    .child(result.getText());
            valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    event.put("name_event", dataSnapshot.child("name_event").getValue().toString());
                    event.put("event_id", eventManager.getEventID());
                    event.put("privacy", dataSnapshot.child("privacy").getValue().toString());
                    event.put("latitude", dataSnapshot.child("latitude").getValue().toString());
                    event.put("longitude", dataSnapshot.child("longitude").getValue().toString());
                    event.put("age_target", dataSnapshot.child("age_target").getValue().toString());
                    HashMap<String, HashMap<String, Long>> attending = new HashMap<>();
                    HashMap<String, HashMap<String, Long>> attended = new HashMap<>();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.child("attending").getChildren()) {
                        HashMap<String, Long> inOut = new HashMap<>();
                        inOut.put("in", Long.valueOf(dataSnapshot.child("attending").child(dataSnapshot1.getKey()).child("in").getValue().toString()));
                        attending.put(dataSnapshot1.getKey(), inOut);
                    }
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.child("attended").getChildren()) {
                        HashMap<String, Long> inOut = new HashMap<>();
                        inOut.put("in", Long.valueOf(dataSnapshot.child("attended").child(dataSnapshot1.getKey()).child("in").getValue().toString()));
                        inOut.put("out", Long.valueOf(dataSnapshot.child("attended").child(dataSnapshot1.getKey()).child("out").getValue().toString()));
                        attended.put(dataSnapshot1.getKey(), inOut);
                    }


                    event.put("attended", attended);
                    event.put("attending", attending);
                    createSuccessDialog(event, result.getText());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    showSnackbar("Something went wrong");
                    scannerView.resumeCameraPreview(QRScannerActivity.this);
                    System.out.println(databaseError.getMessage());
                }
            });

        }catch (Exception e){
            showSnackbar("Invalid Event.");
            scannerView.resumeCameraPreview(QRScannerActivity.this);
        }
    }




    private void createSuccessDialog(final HashMap<String, Object> event, final String eventID){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        builder.setTitle(String.format("Event: %s", (String)event.get("name_event")));

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
            public void onClick(final DialogInterface dialogInterface, int i) {


                FirebaseDatabase database = FirebaseDatabase.getInstance();
                eventManager.updateEventID(eventID);
                DatabaseReference eventDatabaseReference = database.getReference().child("events_us").child(eventID).child("attending").child(credentialsManager.getUsername());
                HashMap<String, Long> in = new HashMap<>();
                final long timeIn = System.currentTimeMillis();
                in.put("in", timeIn);
                eventDatabaseReference.setValue(in).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){

                            databaseReference.removeEventListener(valueEventListener);
                            final Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("source", "qr_code_scanned");
                            dialogInterface.dismiss();
                            eventManager.updatePersonalTimein(timeIn);

                            finish();
                            QRScannerActivity.this.startActivity(intent);
                        }else{
                            showSnackbar("Something went wrong");
                            scannerView.resumeCameraPreview(QRScannerActivity.this);
                        }

                    }
                });
            }
        });
        String ageTarget = (String)event.get("age_target");
        if(ageTarget.equals("all")){
            builder.setMessage("Hit that join button!");
        }else {
            builder.setMessage(String.format("This event is intended for %s+ years old. \nIf you are %s+" +
                    " years old and want to join hit that join button!", ageTarget,ageTarget));
        }
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        
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

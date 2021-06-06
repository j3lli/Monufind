package com.opsc19003852.monufind;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class MainActivity extends AppCompatActivity {

    //context string and error string
    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //checking the google play service version to ensure that the
        //device is able to use map services
        if (checkServiceVersion()) {
            init();
        }
    }

    //initialization method
    private void init() {
        Button btnMap = (Button) findViewById(R.id.btnMap);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });
    }

    //boolean method that validates if a user has the correct play services
    //to run the map service (this is a requirements as per googles' documentation)
    public boolean checkServiceVersion() {
        Log.d(TAG, "checkServiceVersion: checking google services version!");

        int available = GoogleApiAvailability.getInstance()
                .isGooglePlayServicesAvailable(MainActivity.this);

        //if the connection service happens, then the user can use the map
        if (available == ConnectionResult.SUCCESS) {
            Log.d(TAG, "checkServiceVersion: google play services are up to date!");
            return true;
        }
        //if the connection service experiences a resolvable error, resolve it and retry
        else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Log.d(TAG, "checkServiceVersion: and error occurred, but it is resolvable!");

            //generating an error dialog
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this,
                    available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        //if the connection service experiences a fatal error
        else {
            Toast.makeText(this, "your device is unable to make map requests!", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}
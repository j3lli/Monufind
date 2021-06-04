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

        if(checkServiceVersion()){
            init();
        }
    }

    private void init(){
        Button btnMap = (Button) findViewById(R.id.btnMap);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });
    }

    //boolean method that validates if a user has the correct play services
    //to run the map service (this is a requirements as per googles' documentation)
    public boolean checkServiceVersion (){
        Log.d(TAG, "checkServiceVersion: validating the current devices' google services version");

        int checkAvailability = GoogleApiAvailability.getInstance()
                .isGooglePlayServicesAvailable(MainActivity.this);

        //if the connection service happens, then the user can use the map
        if(checkAvailability == ConnectionResult.SUCCESS){
            Log.d(TAG, "checkServiceVersion: google play services are working!");
            return true;
        }
        //if the connection error experiences a resolvable error, resolve it and retry
        else if (GoogleApiAvailability.getInstance()
                .isUserResolvableError(checkAvailability)){
            Log.d(TAG, "checkServiceVersion: an error occurred but it is resolvable");

            //generating an error dialog
            Dialog errDialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this,
                    checkAvailability, ERROR_DIALOG_REQUEST);
            errDialog.show();
        }else{
            Toast.makeText(this,
                    "your device is unable to make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

}

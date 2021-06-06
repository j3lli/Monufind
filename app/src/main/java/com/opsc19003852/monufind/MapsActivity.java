package com.opsc19003852.monufind;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    //using the onMapReadyCallback to generate a map once the user agrees
    //to all permissions
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready!", Toast.LENGTH_SHORT).show();
        gMap = googleMap;
        Log.d(TAG, "onMapReady: map is ready!");

        if (locationPermissionGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            gMap.setMyLocationEnabled(true);
            gMap.getUiSettings().setMyLocationButtonEnabled(false);

            init();
        }
    }


    private static final String TAG = "MapActivity";

    //variables used when explicitly checking user permissions
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 16f;

    //widgets
    private EditText mSearchText;
    private ImageView mGps;

    //user location permission flag
    private boolean locationPermissionGranted = false;

    //map object
    private GoogleMap gMap;

    //location provider
    private FusedLocationProviderClient fusedLPClient;
    private Marker mMarker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mSearchText = (EditText) findViewById(R.id.input_search);
        mGps = (ImageView) findViewById(R.id.ic_gps);

        getLocationPermission();
    }

    //initialization method
    private void init() {
        Log.d(TAG, "init: initializing!");

        //setting the action listener for the search bar
        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textview, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
                    //execute the search method
                    geoLocate();
                }
                return false;
            }
        });

        //setting the gps button on click listener
        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked gps icon!");
                getDeviceLocation();
            }
        });

        //hiding the keyboard
        HideSoftKeyboard();
    }

    //the geoLocation method
    private void geoLocate() {
        Log.d(TAG, "geoLocate: using the geo-location service!");

        String searchString = mSearchText.getText().toString();

        Geocoder geocoder = new Geocoder(MapsActivity.this);
        List<Address> addrList = new ArrayList<>();
        try {
            addrList = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            Log.e(TAG, "geoLocate: IOException: " + e.getMessage());
        }

        if (addrList.size() > 0) {
            Address address = addrList.get(0);
            Log.d(TAG, "geoLocate: found a location" + address.toString());

            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, address);
            /*MarkerOptions options = new MarkerOptions().position(new LatLng(address.getLatitude(),address.getLongitude())).title(address.getAddressLine(0));
            mMap.addMarker(options);*/


        }
    }

    //getting the devices location
    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the devices current location!");

        fusedLPClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (locationPermissionGranted) {

                Task location = fusedLPClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();

                            //moving the camera object to centre over a user's location
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM, "My Location");
                        } else {
                            Log.d(TAG, "onComplete: current location is null!");
                            Toast.makeText(MapsActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        } catch (SecurityException e) {
            Log.d(TAG, "getDeviceLocation: SecurityException" + e.getMessage());
        }
    }


    private void moveCamera(LatLng latLng, float zoom, Address address) {
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        gMap.clear();

        if (address != null) {
            try {
                String snippet = "Address: " + address.getAddressLine(0) + "\n" +
                        "Phone Number: " + address.getPhone() + "\n" +
                        "Website: " + address.getUrl() + "\n";
                // "Price Rating: "+address.getRating(0)+"\n";
                Log.d(TAG, "moveCamera: snippet" + snippet);
                //    Phone Number: null
                //    Website: null

                //adding a marker to the map
                MarkerOptions options = new MarkerOptions()
                        .position(latLng)
                        .title(address.getFeatureName())
                        .snippet(snippet);
                mMarker = gMap.addMarker(options);

            } catch (NullPointerException e) {
                Log.d(TAG, "moveCamera: NullPointerException: " + e.getMessage());
            }
        } else {
            gMap.addMarker(new MarkerOptions().position(latLng));
        }

        HideSoftKeyboard();
    }

    //method to move the camera (view window of the map)
    private void moveCamera(LatLng latLng, float zoom, String title) {
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if (!title.equals("My Location")) {
            MarkerOptions options = new MarkerOptions().position(latLng).title(title);
            gMap.addMarker(options);
        }

        HideSoftKeyboard();
    }

    //initializing the map
    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        //using the callback super to generate the map
        mapFragment.getMapAsync(MapsActivity.this);
    }

    //checking user permissions
    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions!");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION};

        //checking if permission has been granted for fine location
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //checking if permission has been granted for course location
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this, permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
            @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.d(TAG, "onRequestPermissionsResult: has been called");
        locationPermissionGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                //if the first permission has been granted
                if (grantResults.length > 0) {
                    //looping through all of the grant results
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            locationPermissionGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: perm failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: Perm granted");
                    locationPermissionGranted = true;
                    //initialize the map
                    initMap();
                }
            }
        }
    }

    //method to hide the keyboard
    private void HideSoftKeyboard() {
        //doesnt work
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

}
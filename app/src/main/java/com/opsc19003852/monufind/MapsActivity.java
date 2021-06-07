package com.opsc19003852.monufind;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Spinner;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.location.places.Places;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.opsc19003852.monufind.models.PlaceInfo;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener {

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        mMap = googleMap;
        Log.d(TAG, "onMapReady: map is ready here");

        if (mLocationPermissionsGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            init();
        }
    }


    private static final String TAG = "MapActivity";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 16f;
    private static final int PLACE_PICKER_REQUEST = 1;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));

    //widgets
    private AutoCompleteTextView mSearchText;
    private ImageView mGps, mInfo, mPlacePicker;


    //vars
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    private GoogleApiClient mGoogleApiClient;
    private PlaceInfo mPlace;
    private Marker mMarker;
    private String apiKey = "AIzaSyDWPY9SZbin4-1t-Xq3ZbwQPLGHJrN7kNU";
    private DatabaseReference mDatabase;
    Spinner mLandmark;
    Button mbtnLandmark;

    private String[][] arrEnt;
    private String[][] arrFood;
    private String[][] arrHis;
    private String[][] arrSports;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mSearchText = (AutoCompleteTextView) findViewById(R.id.input_search);
        mGps = (ImageView) findViewById(R.id.ic_gps);
        mInfo = (ImageView) findViewById(R.id.place_info);
        mPlacePicker = (ImageView) findViewById(R.id.place_picker);
        mLandmark = findViewById(R.id.spnLandmark);
        mbtnLandmark = (Button) findViewById(R.id.btnLandmark);




        /*
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child("JMeV000HgkPcFwWJjsnoMT82QRb2").child("landmark").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                }
            }
        });*/


        arrEnt = new String[][]{
                {"Lakeside Mall", "18b Tom Jones St, Benoni, 1501", "+27114271801", "-26.18413410789464", "28.315627708697136"},
                {"East Rand Mall", "Bentel Ave, Jansen Park, Boksburg, 1459", "+27118231110", "-26.180671228055335", "28.24254497895984"},
                {"Formula-K (Pty) LTD", "7 Golden Dr, Morehill, Benoni, 1501", "+27728900103", "-26.176940368117258", "28.336907493789077"},
                {"El Ridge Corner", "144 Ridge Rd, Bartlett AH, Boksburg", "+27119188467", "-26.16712508831256", "28.2755424500296"},
                {"Summer Place Boksburg", "Elizabeth Rd &, Leith Rd, Bartlett AH, Boksburg, 1472", "+27118943614", "-26.169666754154566", "28.275365454160436"},
                {"Monte Cristp", "Beyers Park, Boksburg, 1459", "NA", "-26.18269192192591", "28.26318813836189"},
        };

        arrFood=new String[][]{
                {"Dingo's Pub & Restaurant","Shop 4, Lakedene Centre, Lakefield Ave, Benoni, Johannesburg, 1501","+27106150898","-26.17837695922995","28.285933290069632"},
                {"Burger King Northmead (Drive-thru)","2 10th Ave, Northmead, Benoni, 1501","+27214175830","-26.169779741626378","28.32888929613805"},
                {"McDonald's Benoni","Cnr & Streets, Bunyan St, Benoni, 1500","+27114209612","-26.184261244560602","28.311697224701412"},
                {"Cielo Restaurant","1 Country St, Lakefield, Benoni, 1501","+27118457070","-26.181882947178355","28.30427102533554"},
                {"Grapes Pub & Restaurant","Cnr Edward &, Divot St, Benoni, 1501","+27114224276","-26.182923458337616","28.29811473418057"},
                {"Cheers Bar and Bistro","17 Russel St, Western extension, Benoni, 1501","+27834010045","-26.19119766810416","28.303387611699847"},
                {"Gas Monkey Pub & Grill Boksburg","Bartlett AH, Boksburg","+27118971930","-26.176483004886233","28.279124774998476"},

        };
        arrHis=new String[][]{
                {"Benoni Museum","60 Elston Ave, Benoni, 1500","+27119996835","-26.1894515","28.3116752"},
                {"Homestead Dam Recreation Park","56 Wilge Rd, Kleinfontein AH, Benoni, 1501","+27839404249","-26.17099","28.28958"},
                {"CR Swart Park","Vlakfontein 69-Ir, Benoni","NA","-26.18848","28.32078"},
                {"Korsman Conservancy","The Dr, Westdene, Benoni, 1501","+27826902832","-26.19127838000901","28.29065804748345"},

        };
        arrSports=new String[][]{
                {"Benoni Northern Sports Club","1 Brodigan St, Northmead, Benoni, 1501","+27118493410","-26.15540983022986","28.317489168604517"},
                {"Planet Fitness Benoni Lakefiield","Cnr of Main Road and, Windemere Dr, Lakefield, Benoni, 1501","+27119187424","-26.17920892720237","28.290058183556464"},
                {"Old Benonians Sports Club","Corner of President Brand Rd &, President Boshoff Rd, Rynfield AH, Benoni, 1514","+27723769637","-26.141503256246978","28.321120583945472"},
                {"Benoni Lake Club","Cnr Edward &, Divot St, Benoni, 1501","NA","-26.18364842587627","28.298561726521722"},
                {"Virgin Active Benoni","Cnr Pioneer Drive &, Woburn Ave, Benoni, 1501","+27114221155","-26.184334460596364","28.328998013583877"},
                {"Fit Xtreme Gym","2 Malherbe Rd, Rynfield, Benoni, 1514","+27114256390","-26.163959468757117","28.329233148340197"},
                {"VIP Boxing Academy","87 Bedford Ave, Benoni, 1500","+27100208327","-26.18847547754615","28.318946446648294"},

        };


        mbtnLandmark.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // your code here
                String landmark=mLandmark.getSelectedItem().toString();
                Log.d(TAG, "onItemSelected: landmark "+landmark);
                if (landmark=="Historical"){
                    mMap.clear();
                    for (int i = 0; i < arrHis.length; i++) {
                        String snippet ="";
                        snippet = "Address: " + arrHis[i][1] + "\n" +
                                "Phone Number: " + arrHis[i][2]; //
                        LatLng location = new LatLng(Double.valueOf(arrHis[i][3]),Double.valueOf(arrHis[i][4]));
                        MarkerOptions options = new MarkerOptions()
                                .position(location)
                                .title(arrHis[i][0])
                                .snippet(snippet);
                        mMarker = mMap.addMarker(options);

                    }
                }


            }

            /*@Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }*/

        });


        getLocationPermission();

    }

    private void init() {
        Log.d(TAG, "init: initializing");

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        mSearchText.setOnItemClickListener(mAutoCompleteClickListener);

        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient,
                LAT_LNG_BOUNDS, null);

        mSearchText.setAdapter(mPlaceAutocompleteAdapter);

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textview, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
                    //Hides keyboard after enter
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mSearchText.getWindowToken(), 0);
                    //execute our method for searching
                    geoLocate();
                }
                return false;
            }
        });

        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked gps icon");
                getDeviceLocation();
            }
        });

        mInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked place info");
                try {
                    if (mMarker.isInfoWindowShown()) {
                        mMarker.hideInfoWindow();
                    } else {
                        Log.d(TAG, "onClick: Place info: " + mPlace.toString());
                        mMarker.showInfoWindow();
                    }
                } catch (NullPointerException e) {
                    Log.d(TAG, "onClick: NullPointerException: " + e.getMessage());
                }
            }
        });

        mPlacePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {
                    startActivityForResult(builder.build(MapsActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    Log.e(TAG, "onClick: GooglePlayServicesRepairableException: " + e.getMessage());
                } catch (GooglePlayServicesNotAvailableException e) {
                    Log.e(TAG, "onClick: GooglePlayServicesNotAvailableException: " + e.getMessage());
                }
            }
        });
    }

    //might throw an error
    @SuppressLint("MissingSuperCall")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);

                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                        .getPlaceById(mGoogleApiClient, place.getId());
                placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            }
        }
    }

    private void geoLocate() {
        Log.d(TAG, "geoLocate: geolocating");

        String searchString = mSearchText.getText().toString();

        Geocoder geocoder = new Geocoder(MapsActivity.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            Log.e(TAG, "geoLocate: IOException: " + e.getMessage());
        }

        if (list.size() > 0) {
            Address address = list.get(0);
            Log.d(TAG, "geoLocate: found a location" + address.toString());

            moveCameraS(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, mPlace);
            /*MarkerOptions options = new MarkerOptions().position(new LatLng(address.getLatitude(),address.getLongitude())).title(address.getAddressLine(0));
            mMap.addMarker(options);*/


        }


    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (mLocationPermissionsGranted) {

                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found location");
                            Location currentLocation = (Location) task.getResult();

                           /* CameraPosition cameraPosition = CameraPosition.builder()
                            .target(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
                            .zoom(DEFAULT_ZOOM)
                            .bearing(0)
                            .tilt(0)
                            .build();
                            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));*/

                            moveCameraU(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM, "My Location");
                        } else {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(MapsActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        } catch (SecurityException e) {
            Log.d(TAG, "getDeviceLocation: SecurityException" + e.getMessage());
        }

        /*for (int i = 0; i < arrEnt.length; i++) {
            String snippet ="";
            snippet = "Address: " + arrEnt[i][1] + "\n" +
                    "Phone Number: " + arrEnt[i][2]; //
            LatLng location = new LatLng(Double.valueOf(arrEnt[i][3]),Double.valueOf(arrEnt[i][4]));
            MarkerOptions options = new MarkerOptions()
                    .position(location)
                    .title(arrEnt[i][0])
                    .snippet(snippet);
            mMarker = mMap.addMarker(options);

        }

        for (int i = 0; i < arrFood.length; i++) {
            String snippet ="";
            snippet = "Address: " + arrFood[i][1] + "\n" +
                    "Phone Number: " + arrFood[i][2]; //
            LatLng location = new LatLng(Double.valueOf(arrFood[i][3]),Double.valueOf(arrFood[i][4]));
            MarkerOptions options = new MarkerOptions()
                    .position(location)
                    .title(arrFood[i][0])
                    .snippet(snippet);
            mMarker = mMap.addMarker(options);

        }



        for (int i = 0; i < arrSports.length; i++) {
            String snippet ="";
            snippet = "Address: " + arrSports[i][1] + "\n" +
                    "Phone Number: " + arrSports[i][2]; //
            LatLng location = new LatLng(Double.valueOf(arrSports[i][3]),Double.valueOf(arrSports[i][4]));
            MarkerOptions options = new MarkerOptions()
                    .position(location)
                    .title(arrSports[i][0])
                    .snippet(snippet);
            mMarker = mMap.addMarker(options);

        }*/

       

    }

    //moves camera to searched location
    private void moveCameraS(LatLng latLng, float zoom, PlaceInfo placeInfo) {
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        mMap.clear();

        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(MapsActivity.this));

        if (placeInfo != null) {
            try {
                String snippet = "Address: " + placeInfo.getAddress() + "\n" +
                        "Phone Number: " + placeInfo.getPhoneNumber() + "\n" +
                        "Website: " + placeInfo.getWebsiteUri() + "\n" +
                        "Price Rating: " + placeInfo.getRating() + "\n";
                Log.d(TAG, "moveCamera: snippet" + snippet);


                MarkerOptions options = new MarkerOptions()
                        .position(latLng)
                        .title(placeInfo.getName())
                        .snippet(snippet);
                mMarker = mMap.addMarker(options);


            } catch (NullPointerException e) {
                Log.d(TAG, "moveCamera: NullPointerException: " + e.getMessage());
            }
        } else {
            mMap.addMarker(new MarkerOptions().position(latLng));
        }


    }

    //moves camera to users location
    private void moveCameraU(LatLng latLng, float zoom, String title) {
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if (!title.equals("My Location")) {
            MarkerOptions options = new MarkerOptions().position(latLng).title(title);
            mMap.addMarker(options);
        }


    }

    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(MapsActivity.this);
    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: Getting location perm");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
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

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called");
        mLocationPermissionsGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: perm failed");
                            return;
                        }

                    }
                    Log.d(TAG, "onRequestPermissionsResult: Perm granted");
                    mLocationPermissionsGranted = true;

                    initMap();
                }
            }
        }
    }

    //============================ GOOGLE PLACES API AUTOCOMPLETE SUGGESTIONS ==================

    private AdapterView.OnItemClickListener mAutoCompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            final AutocompletePrediction item = mPlaceAutocompleteAdapter.getItem(i);
            final String placeID = item.getPlaceId();

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeID);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                //did not get place
                Log.d(TAG, "onResult: place query did not complete successfully: " + places.getStatus().toString());
                places.release();
                return;
            }
            final Place place = places.get(0);

            Log.d(TAG, "onResult: Place details: " + place.getAddress());

            try {
                //mPlace.setAttributions(place.getAttributions().toString());
                mPlace = new PlaceInfo();
                mPlace.setName(place.getName().toString());
                mPlace.setAddress(place.getAddress().toString());
                mPlace.setId(place.getId());
                mPlace.setLatlng(place.getLatLng());
                mPlace.setRating(place.getRating());
                mPlace.setWebsiteUri(place.getWebsiteUri());

                Log.d(TAG, "onResult: place: " + mPlace.toString());
            } catch (NullPointerException e) {
                Log.e(TAG, "onResult: NullPointerException: " + e.getMessage());
            }

            moveCameraU(new LatLng(place.getViewport().getCenter().latitude,
                    place.getViewport().getCenter().longitude), DEFAULT_ZOOM, mPlace.getName());

            places.release();
        }
    };

}
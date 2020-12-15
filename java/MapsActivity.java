package com.example.a906771.projectmeetingapp;
/**
 * MapActivity.java
 * A user can select and view meeting locations on the map
 * selecting a location can be done by search, selecting a longitude latitude location
 * or a point of interest
 *
 * @author Luke Cook (906771)
 */


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;

import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    //this may have to be FragmentActivity in extends
    private GoogleMap mMap;
    PlaceAutocompleteFragment search;
    String locationName;
    double lat,lng;

    /**
     * creates a map fragment
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //allows a user to select a location by searching
        search = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.search);
        setSearch();
        //A user can select points of interests for locations
        setPOI();
        //A user can select any point on the map
        setAnyLoc();
        //If a location is set for a meeting it can be viewed here
        Intent intent = getIntent();
        if(intent.hasExtra("Lng")) {
            Bundle extras = getIntent().getExtras();
            locationName = extras.getString("Name");
            lat = extras.getDouble("Lat");
            lng = extras.getDouble("Lng");
            //Toast.makeText(this, locationName + lat + lng, Toast.LENGTH_SHORT).show();
            Log.d("Maps", locationName + " " + lat + " " + lng);
            if (!(lat == 0 && lng == 0)) {
                LatLng location = new LatLng(lat, lng);
                String snippet = String.format(Locale.getDefault(), "Lat: %1$.5f, Long: %2$.5f", location.latitude, location.longitude);
                mMap.addMarker(new MarkerOptions().position(location).title(locationName).snippet(snippet));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
            }
            // used to show all meetings locations
        } else if(intent.hasExtra("map_op")){
            DBHandle get = new DBHandle(MapsActivity.this);
            for(int j = 1 ; j<= get.getLastId() ; j++){
                MeetingDB collect = new MeetingDB();
                collect = get.getMeetingById(j);
                if (!(collect.lat == 0 && collect.lng == 0)) {
                    LatLng location = new LatLng(collect.lat, collect.lng);
                    String snippet = String.format(Locale.getDefault(), "Lat: %1$.5f, Long: %2$.5f", location.latitude, location.longitude);
                    mMap.addMarker(new MarkerOptions().position(location).title(collect.location).snippet(snippet));
                }
            }
        }
    }

    /**
     * a user can use a search to set a marker
     */
    private void setSearch(){
        search.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                Log.d("Maps", "Place selected: " + place.getName() + place.getLatLng());
                //display only one marker on the screen
                mMap.clear();
                // create marker
                String snippet = String.format(Locale.getDefault(), "Lat: %1$.5f, Long: %2$.5f", place.getLatLng().latitude, place.getLatLng().longitude);
                mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(String.valueOf(place.getName())).snippet(snippet));
                locationName = String.valueOf(place.getName());
                lat = place.getLatLng().latitude;
                lng = place.getLatLng().longitude;

                //change camera location over point
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(),15));
            }

            @Override
            public void onError(Status status) {
                Log.d("Maps", "An error occurred: " + status);
            }
        });
    }

    /**
     * A user can select points of interests for locations and use the name
     */
    private void setPOI() {
        mMap.setOnPoiClickListener(new GoogleMap.OnPoiClickListener() {
            @Override
            public void onPoiClick(PointOfInterest place) {
                mMap.clear();
                Marker marker = mMap.addMarker(new MarkerOptions().position(place.latLng).title(place.name));
                marker.showInfoWindow();
                locationName = place.name;
                lat = place.latLng.latitude;
                lng = place.latLng.longitude;
            }
        });
    }

    /**
     * a user can select any location if they press and hold
     */
    private void setAnyLoc(){
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                mMap.clear();
                String snippet = String.format(Locale.getDefault(), "Lat: %1$.5f, Long: %2$.5f", latLng.latitude, latLng.longitude);
                locationName = "See location";
                lat = latLng.latitude;
                lng = latLng.longitude;

                mMap.addMarker(new MarkerOptions().position(latLng).title("Location").snippet(snippet));

            }
        });
    }

    /**
     * once a location has been set then it can be passed back
     */
   public void finish(){
       Intent data = new Intent();
       data.putExtra("returnLat", lat);
       data.putExtra("returnLng", lng);
       data.putExtra("returnName", locationName);

       setResult(RESULT_OK, data);
       super.finish();
   }
}

package com.example.memorableplaces;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    MemorablePlace returnablePlace;
    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    private int requestCode;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 1, locationListener);
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        requestCode = getIntent().getIntExtra("requestCode", 0);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if(requestCode == 1) {

            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    Log.i("Location", location.toString());
                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                    String locationName;
                    LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    String address = "";
                    try {
                        List<Address> listAddress = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                        if (listAddress.get(0).getAddressLine(0) != null) {
                            address += listAddress.get(0).getAddressLine(0) + " ";
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    returnablePlace = new MemorablePlace(location.getLongitude(), location.getLatitude(), address);
                    mMap.addMarker(new MarkerOptions().position(userLocation).title(address)).showInfoWindow();
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(12));

                }
            };
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(requestCode == 2) {
            MemorablePlace placeToShow = (MemorablePlace) getIntent().getSerializableExtra("placeToShow");
            LatLng locationToShow = new LatLng(placeToShow.getLatitude(), placeToShow.getLongitude());
            mMap.addMarker(new MarkerOptions().position(locationToShow).title(placeToShow.getName())).showInfoWindow();
            mMap.moveCamera(CameraUpdateFactory.newLatLng(locationToShow));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
        }
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener(){
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(requestCode == 1) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("place", returnablePlace);
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
                return false;
            }
        });
        // Add a marker in Sydney and move the camera
       /* LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
    }

    public void cancel(View view){
        setResult(Activity.RESULT_CANCELED);
        finish();
    }
}
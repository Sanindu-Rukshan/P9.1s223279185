package com.example.lostandfound;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.example.lostandfound.DataClasses.Advert;
import com.example.lostandfound.database.AdvertDatabaseHelper;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;



import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private GoogleMap googleMap;
    private List<Advert> adverts; // List of adverts with location data

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Create a new SupportMapFragment
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();

        // Replace the fragment container with the new SupportMapFragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.map_container, mapFragment)
                .commit();

        // Set the callback for when the map is ready
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;

        // Check location permission
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        googleMap.setMyLocationEnabled(true);

        // Retrieve the list of adverts from the database
        AdvertDatabaseHelper databaseHelper = new AdvertDatabaseHelper(this);
        adverts = databaseHelper.getAllAdverts();

        showAdvertLocations();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            googleMap.setMyLocationEnabled(true);
            showAdvertLocations();
        }
    }

    private void showAdvertLocations() {
        // Iterate through the adverts list and add markers on the map
        for (Advert advert : adverts) {
            LatLng latLng = new LatLng(advert.getLatitude(), advert.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .title(advert.getName())
                    .snippet(advert.getDescription());
            Marker marker = googleMap.addMarker(markerOptions);
            marker.setTag(advert.getId());
        }

        // Move the camera to the first advert location
        if (!adverts.isEmpty()) {
            Advert firstAdvert = adverts.get(0);
            LatLng firstAdvertLatLng = new LatLng(firstAdvert.getLatitude(), firstAdvert.getLongitude());
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstAdvertLatLng, 12f));
        }
    }
}
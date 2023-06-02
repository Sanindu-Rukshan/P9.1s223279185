package com.example.lostandfound;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.lostandfound.Adapters.AdvertAdapter;
import com.example.lostandfound.DataClasses.Advert;
import com.example.lostandfound.database.AdvertDatabaseHelper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class CreateAdvert extends AppCompatActivity {
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 2;


    private EditText etName;
    private EditText etPhoneNumber;
    private EditText etDescription;
    private EditText etDate;
    private EditText etLocation;

    private AdvertDatabaseHelper dbHelper;
    private FusedLocationProviderClient fusedLocationClient;

    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_advert);

        dbHelper = new AdvertDatabaseHelper(this);
//        etPostType = findViewById(R.id.etPostType);
        etName = findViewById(R.id.etName);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etDescription = findViewById(R.id.etDescription);
        etDate = findViewById(R.id.etDate);
        etLocation = findViewById(R.id.etLocation);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        // Initialize your EditText fields

        Button btnSave = findViewById(R.id.btnSave);
        final RadioGroup radioGroup = findViewById(R.id.radioGroup);

        radioGroup.clearCheck();
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String postType;
                // Save advert data to the SQLite database here
                int selectedId = radioGroup.getCheckedRadioButtonId();
                if (selectedId == R.id.rbLost) {
                    postType = "Lost";
                } else if (selectedId == R.id.rbFound) {
                    postType = "Found";
                } else {
                    // No post type selected
                    Toast.makeText(CreateAdvert.this, "Please select a post type", Toast.LENGTH_SHORT).show();
                    return;
                }
                String name = etName.getText().toString();
                String phoneNumber = etPhoneNumber.getText().toString();
                String description = etDescription.getText().toString();
                String date = etDate.getText().toString();
                String location = etLocation.getText().toString();

                // Create an Advert object with the entered data and the ID as 0 (placeholder)
                Advert advert = new Advert(0, postType, name, phoneNumber, description, date, location, latitude, longitude);

                // Save the advert object to the database and get the generated ID
                long id = dbHelper.saveAdvert(advert);

                // Update the ID of the advert object
                advert.setId((int) id);

                Toast.makeText(CreateAdvert.this, "Advert saved successfully ", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        Button btnGetCurrentLocation = findViewById(R.id.btnGetCurrentLocation);
        btnGetCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocation();
            }
        });

        // Check location permissions
        checkLocationPermissions();
    }

    private void checkLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request location permissions
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            // Permissions already granted, enable location features
            enableLocationFeatures();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults); // Call superclass implementation

        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissions granted, enable location features
                enableLocationFeatures();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void enableLocationFeatures() {
//        etLocation.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(CreateAdvert.this, AutocompleteActivity.class);
//                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
//            }
//        });

        Button btnGetCurrentLocation = findViewById(R.id.btnGetCurrentLocation);
        btnGetCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocation();
            }
        });
    }
    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            return;
        }

        Task<Location> locationTask = fusedLocationClient.getLastLocation();
        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    Geocoder geocoder = new Geocoder(CreateAdvert.this, Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        if (!addresses.isEmpty()) {
                            Address address = addresses.get(0);
                            String locationString = address.getAddressLine(0);
                            etLocation.setText(locationString);

                            // Update latitude and longitude fields
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        } else {
                            Toast.makeText(CreateAdvert.this, "Unable to retrieve address", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(CreateAdvert.this, "Unable to retrieve address", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CreateAdvert.this, "Unable to retrieve location", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String selectedLocation = data.getStringExtra("selected_location");
                etLocation.setText(selectedLocation);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Autocomplete canceled", Toast.LENGTH_SHORT).show();
            }
        }
    }
    }




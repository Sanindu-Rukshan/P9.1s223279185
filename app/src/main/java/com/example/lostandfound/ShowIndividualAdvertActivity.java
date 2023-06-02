package com.example.lostandfound;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lostandfound.DataClasses.Advert;
import com.example.lostandfound.R;
import com.example.lostandfound.ShowAdvertsActivity;
import com.example.lostandfound.database.AdvertDatabaseHelper;

public class ShowIndividualAdvertActivity extends AppCompatActivity {

    private TextView tvType;
    private TextView tvName;
    private TextView tvDate;
    private TextView tvDescription;
    private Button btnDelete;

    private AdvertDatabaseHelper dbHelper;
    private int advertId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_individual_advert);

        tvType = findViewById(R.id.tvType);
        tvName = findViewById(R.id.tvName);
        tvDate = findViewById(R.id.tvDate);
        tvDescription = findViewById(R.id.tvDescription);
        btnDelete = findViewById(R.id.btnDelete);

        dbHelper = new AdvertDatabaseHelper(this);

        Intent intent = getIntent();
        if (intent != null) {
            advertId = intent.getIntExtra("advertId", -1);
            if (advertId != -1) {
                Advert advert = getAdvertFromDatabase(advertId);
                if (advert != null) {
                    displayAdvertDetails(advert);
                }
            }
        }

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAdvert();
            }
        });
    }

    private void displayAdvertDetails(Advert advert) {
        tvType.setText(advert.getPostType());
        tvName.setText(advert.getName());
        tvDate.setText(advert.getDate());
        tvDescription.setText(advert.getDescription());
    }

    private Advert getAdvertFromDatabase(int advertId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + AdvertDatabaseHelper.TABLE_ADVERTS + " WHERE " +
                AdvertDatabaseHelper.COLUMN_ID + " = ?", new String[]{String.valueOf(advertId)});

        Advert advert = null;
        if (cursor.moveToFirst()) {
            int postTypeColumnIndex = cursor.getColumnIndex(AdvertDatabaseHelper.COLUMN_POST_TYPE);
            int nameColumnIndex = cursor.getColumnIndex(AdvertDatabaseHelper.COLUMN_NAME);
            int phoneNumberColumnIndex = cursor.getColumnIndex(AdvertDatabaseHelper.COLUMN_PHONE_NUMBER);
            int descriptionColumnIndex = cursor.getColumnIndex(AdvertDatabaseHelper.COLUMN_DESCRIPTION);
            int dateColumnIndex = cursor.getColumnIndex(AdvertDatabaseHelper.COLUMN_DATE);
            int locationColumnIndex = cursor.getColumnIndex(AdvertDatabaseHelper.COLUMN_LOCATION);
            int latitudeColumnIndex = cursor.getColumnIndex(AdvertDatabaseHelper.COLUMN_LATITUDE);
            int longitudeColumnIndex = cursor.getColumnIndex(AdvertDatabaseHelper.COLUMN_LONGITUDE);


            String postType = cursor.getString(postTypeColumnIndex);
            String name = cursor.getString(nameColumnIndex);
            String phoneNumber = cursor.getString(phoneNumberColumnIndex);
            String description = cursor.getString(descriptionColumnIndex);
            String date = cursor.getString(dateColumnIndex);
            String location = cursor.getString(locationColumnIndex);
            double latitude = cursor.getDouble(latitudeColumnIndex);
            double longitude = cursor.getDouble(longitudeColumnIndex);

            advert = new Advert(advertId, postType, name, phoneNumber, description, date, location, latitude, longitude);
        }

        cursor.close();
        db.close();

        return advert;
    }

    private void deleteAdvert() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsDeleted = db.delete(AdvertDatabaseHelper.TABLE_ADVERTS,
                AdvertDatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(advertId)});

        db.close();

        if (rowsDeleted > 0) {
            Toast.makeText(this, "Advert deleted", Toast.LENGTH_SHORT).show();
        }

        // Redirect to ShowAdvertsActivity
        Intent intent = new Intent(ShowIndividualAdvertActivity.this, ShowAdvertsActivity.class);
        startActivity(intent);
        finish();
    }
}

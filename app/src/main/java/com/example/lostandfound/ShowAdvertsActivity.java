package com.example.lostandfound;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lostandfound.Adapters.AdvertAdapter;
import com.example.lostandfound.DataClasses.Advert;
import com.example.lostandfound.database.AdvertDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class ShowAdvertsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdvertAdapter adapter;
    private AdvertDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_adverts);

        dbHelper = new AdvertDatabaseHelper(this);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Fetch all adverts from the SQLite database here and pass them to the adapter
        List<Advert> adverts = getAllAdverts();
//        adapter = new AdvertAdapter(adverts);

        adapter = new AdvertAdapter(adverts, new AdvertAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Advert advert) {
                int advertId = advert.getId();
                Intent intent = new Intent(ShowAdvertsActivity.this, ShowIndividualAdvertActivity.class);
                intent.putExtra("advertId", advertId);
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(adapter);
    }

    private List<Advert> getAllAdverts() {
        List<Advert> adverts = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + AdvertDatabaseHelper.TABLE_ADVERTS, null);
        if (cursor.moveToFirst()) {
            int idColumnIndex = cursor.getColumnIndex(AdvertDatabaseHelper.COLUMN_ID);
            int postTypeColumnIndex = cursor.getColumnIndex(AdvertDatabaseHelper.COLUMN_POST_TYPE);
            int nameColumnIndex = cursor.getColumnIndex(AdvertDatabaseHelper.COLUMN_NAME);
            int phoneNumberColumnIndex = cursor.getColumnIndex(AdvertDatabaseHelper.COLUMN_PHONE_NUMBER);
            int descriptionColumnIndex = cursor.getColumnIndex(AdvertDatabaseHelper.COLUMN_DESCRIPTION);
            int dateColumnIndex = cursor.getColumnIndex(AdvertDatabaseHelper.COLUMN_DATE);
            int locationColumnIndex = cursor.getColumnIndex(AdvertDatabaseHelper.COLUMN_LOCATION);
            int latitudeColumnIndex = cursor.getColumnIndex(AdvertDatabaseHelper.COLUMN_LATITUDE);
            int longitudeColumnIndex = cursor.getColumnIndex(AdvertDatabaseHelper.COLUMN_LONGITUDE);
            do {
                int id = cursor.getInt(idColumnIndex);
                String postType = cursor.getString(postTypeColumnIndex);
                String name = cursor.getString(nameColumnIndex);
                String phoneNumber = cursor.getString(phoneNumberColumnIndex);
                String description = cursor.getString(descriptionColumnIndex);
                String date = cursor.getString(dateColumnIndex);
                String location = cursor.getString(locationColumnIndex);
                double latitude = cursor.getDouble(latitudeColumnIndex);
                double longitude = cursor.getDouble(longitudeColumnIndex);

                Advert advert = new Advert(id, postType, name, phoneNumber, description, date, location, latitude, longitude);
                adverts.add(advert);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return adverts;
    }

}

package com.pdr.veigris;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.room.Room;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.pdr.veigris.databinding.ActivityMapsBinding;

import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private TripDao tripDao;
    private boolean locationPermissionGranted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tripDao = new DbHelper(getApplicationContext()).GetTripDao();

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
            updateLocationUI();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        if (requestCode == 1) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        updateLocationUI();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.upload_takeout:
                Intent intent = new Intent(this, TakeoutUploadActivity.class);
                startActivity(intent);
                return true;
            case R.id.bust_cache:
                new Thread(() -> {
                    tripDao.nukeTrips();
                    runOnUiThread(()->{
                        Toast.makeText(getApplicationContext(), "Cache busted!", Toast.LENGTH_SHORT).show();
                    });
                }).start();
                return true;
            case R.id.initiate_takeout:
                new Thread(() -> {
                    // do background stuff here
                    Trip arne = new Trip();
                    arne.date = "2022-01-01";

                    List<LatLng> coordinates = new ArrayList<>();
                    coordinates.add(new LatLng(59,10));
                    coordinates.add(new LatLng(60,11));
                    coordinates.add(new LatLng(60.5,12));
                    arne.SetCoordinateList(coordinates);

                    tripDao.insertAll(arne);
                    runOnUiThread(()->{
                        // OnPostExecute stuff here
                    });
                }).start();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        LatLng start = new LatLng(59, 10);
        mMap.moveCamera(CameraUpdateFactory.zoomTo(7));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(start));
        getLocationPermission();
        drawRoutes();
    }
    @SuppressLint("MissingPermission")
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        if (locationPermissionGranted) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            mMap.setMyLocationEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            getLocationPermission();
        }
    }
    private void drawRoutes(){
        new Thread(() -> {
            List<Trip> trips = tripDao.getAll();
            runOnUiThread(()->{
                for (Trip trip : trips){
                    PolylineOptions polylineOptions= new PolylineOptions();
                    polylineOptions.addAll(trip.GetCoordinateList());
                    mMap.addPolyline(polylineOptions.color(Color.RED));
                }
            });
        }).start();
    }
}
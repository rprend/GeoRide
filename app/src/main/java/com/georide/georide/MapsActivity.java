package com.georide.georide;

import android.app.DownloadManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnCircleClickListener,
        GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;

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
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng foellinger = new LatLng(40.10595, -88.22719);
        LatLng bikeRack = new LatLng(40.10616, -88.22813);

        mMap.setOnCircleClickListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.addMarker(new MarkerOptions().position(foellinger).title("The GOD house"));
        com.google.android.gms.maps.model.Circle firstBikeRack = mMap.addCircle(new CircleOptions()
                .center(bikeRack)
                .radius(14)
                .fillColor(4388081)
                .clickable(true)
                .visible(true));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(17));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(foellinger));

        Log.i("Test","BOIIII " + firstBikeRack.getFillColor());
    }

    /**
     * Called when circles are clicked
     * @param circle the circle instance which has been clicked.
     */
    @Override
    public void onCircleClick(Circle circle) {
        Log.i("Click", "Circle has been clicked");
    }

    /**
     * Handles clicking of markers on the map.
     * @param marker The marker instance which was clicked
     * @return False to continue with default marker click behavior (center, zoom, etc), true to cancel.
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.i("Marker click", "Marker has been clicked");
        mMap.animateCamera(CameraUpdateFactory.zoomTo(25));
        String url = "http://localhost:5000/";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("Response: ", response.toString());
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("error: ", error.toString());

                    }
                });

    // Access the RequestQueue through your singleton class.
        VolleyNetworking.getInstance(this).addToRequestQueue(jsonObjectRequest);
        return false;
    }
}

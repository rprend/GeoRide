package com.georide.georide;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnCircleClickListener,
        GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private String coordinateString = "-88.22719,40.10595\n" +
            "-88.22813,40.10616\n" +
            "-88.2254,40.1136\n" +
            "-88.226,40.108\n" +
            "-88.227,40.108";
    private List<LatLng> coordinates = new ArrayList<>();
    private List<Marker> markerList = new ArrayList<>();
    private HashMap<Integer, String> bikeRackInfo = new HashMap<>();

    private final String RACK_TEXT = "Occupancy: Loading...";
    private final String LINK = "http://10.0.2.2:5000/racks/";
    private BroadcastReceiver firebaseBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            Log.i("Receiver", "Broadcast received");
            String receivedAction = intent.getAction();
            if (receivedAction.equals(MyFirebaseMessagingService.INTENT_FILTER)) {
                Log.i("Receiver", "Received correct broadcast");
//                Log.d("Test extras", intent.getExtras().toString());
                String broadcastMessage = intent.getExtras().getString("firebaseUpdate");
//                Log.i("Receiver", broadcastMessage);
                JsonElement jElement = new JsonParser().parse(broadcastMessage);
                JsonObject jObject = jElement.getAsJsonObject();
                for (Map.Entry entry : jObject.entrySet()) {
                    String key = entry.getKey().toString();
                    if (Integer.parseInt(key) >= markerList.size()) {
                        Log.e("Update", "Told to update a bike rack that does not exist!");
                    } else {
                        String result = jObject.get(key).toString()
                                .replaceAll("\"", "");
                        Marker marker = markerList.get(Integer.parseInt(key));
                        updateMarkerIfResultKnown(marker, result);
                    }
                }
            }
        }
    };

    private void updateMarkerIfResultKnown(final Marker marker, String result) {
        marker.setTitle(RACK_TEXT.split(" ")[0] + result);
        if (marker.isInfoWindowShown()) {
            marker.showInfoWindow();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        final LatLng FOELLINGER = new LatLng(40.10595, -88.22719);

        mMap.setOnCircleClickListener(this);
        mMap.setOnMarkerClickListener(this);
//        mMap.setInfoWindowAdapter(new BikeInfoWindow(this));

        for (LatLng coord : coordinates) {
            markerList.add(mMap.addMarker(new MarkerOptions()
                    .position(coord)
                    .title(RACK_TEXT)
                    .snippet("Max Occupancy: 66")
            ));
        }
        //.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_splash_foreground))
        mMap.moveCamera(CameraUpdateFactory.zoomTo(17));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(FOELLINGER));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(firebaseBroadcastReceiver);
    }

    /**
     * This creates a new request to add to the volley queue.
     * @param link The link to go to
     * @param marker Which marker is being updated
     * @return The JSON object request which updates marker with the correct value
     */
    private void updateRackRequest(final String link, final Marker marker, final int number) {
        final String numberString = Integer.toString(number);
        JsonObjectRequest request = new JsonObjectRequest
                (Request.Method.GET, link, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("Response: ", response.toString());
                        String resp = response.toString();
                        JsonElement jElement = new JsonParser().parse(resp);
                        JsonObject jObject = jElement.getAsJsonObject();
                        String result = jObject.get(numberString).toString()
                                .replaceAll("\"", "");
                        marker.setTitle(RACK_TEXT.split(" ")[0] + result);
                        if (marker.isInfoWindowShown()) {
                            marker.showInfoWindow();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("error: ", error.toString());

                    }
                });
        VolleyNetworking.getInstance(this).addToRequestQueue(request);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        registerReceiver(firebaseBroadcastReceiver, new IntentFilter(MyFirebaseMessagingService.INTENT_FILTER));
        FirebaseMessaging.getInstance().subscribeToTopic("all");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        for (String coord : coordinateString.split("\n")) {
            String[] xyCoords = coord.split(",");
//            Log.i("Coordinate", "sd");
//            Log.i("Coordinate", xyCoords[0] + " " + xyCoords[1]);
            coordinates.add(new LatLng(Double.parseDouble(xyCoords[1]),
                            Double.parseDouble(xyCoords[0])));
        }
//        start_map_button.setVisibility(View.GONE);

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
        if (marker.getTitle().equals(RACK_TEXT)) {
            int indexLoc = markerList.indexOf(marker);
            updateRackRequest(LINK + Integer.toString(indexLoc), marker, indexLoc);
        }

        Log.i("Marker click", "Marker has been clicked");
        mMap.animateCamera(CameraUpdateFactory.zoomTo(32));

        return false;
    }

}

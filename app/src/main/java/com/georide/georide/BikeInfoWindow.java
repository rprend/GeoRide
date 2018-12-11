package com.georide.georide;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;

public class BikeInfoWindow implements InfoWindowAdapter {
    private Context context;
    LayoutInflater inflater;
    private String infoTitle;
    private String infoSnippet;

    public BikeInfoWindow(Context context) {
        this.context = context;
    }
    public View getInfoContents(Marker marker) {
        return null;
    }

    public View getInfoWindow(Marker marker) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View  infoView = inflater.inflate(R.layout.info_winder, null);
        TextView title = infoView.findViewById(R.id.info_window_title);
        TextView snippet = infoView.findViewById(R.id.snippet);
        title.setText(marker.getTitle());
        snippet.setText(marker.getSnippet());
        return infoView;
    }
}

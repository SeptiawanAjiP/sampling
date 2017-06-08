package org.odk.collect.android.augmentedreality.formisian;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;

import org.odk.collect.android.R;


public class TagTempatInfoWindow implements InfoWindowAdapter {

    private final View myContentsView;

    public TagTempatInfoWindow(Activity activity) {
        myContentsView = activity.getLayoutInflater().inflate(R.layout.custom_info_contents, null);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        TextView tvTitle = ((TextView)myContentsView.findViewById(R.id.title_info));
        tvTitle.setText(marker.getTitle());
        return myContentsView;
    }
}
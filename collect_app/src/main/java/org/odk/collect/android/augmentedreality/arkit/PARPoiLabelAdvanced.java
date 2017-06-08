/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.location.Location
 *  android.widget.TextView
 */
package org.odk.collect.android.augmentedreality.arkit;

import android.location.Location;

public class PARPoiLabelAdvanced
extends PARPoiLabel {
    public PARPoiLabelAdvanced(Location location, String title, String description, int layoutId, int radarResourceId) {
        super(location, title, description, layoutId, radarResourceId);
    }

    public float getAltitude() {
        return (float)this.getLocation().getAltitude();
    }

    public void setAltitude(float altitude) {
        this.getLocation().setAltitude((double)altitude);
    }

    @Override
    public void updateContent() {
        if (!this.hasCreatedView) {
            return;
        }
        super.updateContent();
        if (this.isAltitudeEnabled && this.altitudeTextView != null) {
            this.altitudeTextView.setText((CharSequence)("" + FORMATTER_DISTANCE_SMALL.format(this.getAltitude()) + " altitude"));
        }
    }
}


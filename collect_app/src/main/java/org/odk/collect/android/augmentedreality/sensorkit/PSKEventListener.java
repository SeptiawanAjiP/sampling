/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.location.Location
 */
package org.odk.collect.android.augmentedreality.sensorkit;

import android.location.Location;

import org.odk.collect.android.augmentedreality.sensorkit.enums.PSKDeviceOrientation;


public interface PSKEventListener {
    public void onLocationChangedEvent(Location var1);

    public void onDeviceOrientationChanged(PSKDeviceOrientation var1);
}


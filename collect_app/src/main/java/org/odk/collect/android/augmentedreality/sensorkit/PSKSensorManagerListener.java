/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.location.GpsStatus
 *  android.location.GpsStatus$Listener
 *  android.location.LocationListener
 */
package org.odk.collect.android.augmentedreality.sensorkit;

import android.location.GpsStatus;
import android.location.LocationListener;

public interface PSKSensorManagerListener
extends LocationListener,
GpsStatus.Listener {
    public void onRotationVectorChanged(float[] var1, long var2);

    public void onRotationVectorAccuracyChanged(int var1);

    public void onGravityChanged(float[] var1, long var2);

    public void onGravityAccuracyChanged(int var1);

    public void onOrientationChanged(int var1);
}


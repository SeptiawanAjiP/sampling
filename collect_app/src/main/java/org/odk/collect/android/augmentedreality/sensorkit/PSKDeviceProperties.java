/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  android.content.pm.PackageManager
 *  android.graphics.Point
 *  android.hardware.Camera
 *  android.hardware.Camera$Parameters
 *  android.hardware.Sensor
 *  android.hardware.SensorManager
 *  android.os.Build
 *  android.os.Build$VERSION
 *  android.util.Log
 *  android.view.Display
 *  android.view.WindowManager
 */
package org.odk.collect.android.augmentedreality.sensorkit;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.os.Build;
import android.util.Log;
import android.view.WindowManager;

import org.odk.collect.android.augmentedreality.sensorkit.enums.PSKGPSAvailabilityStatus;


public class PSKDeviceProperties {
    private static final String TAG = "PSKDeviceProperties";
    private static PSKDeviceProperties devicePropertiesSingleton;
    private final Context context = PSKApplication.getAppContext();
    private String deviceName = Build.MODEL;
    private String osVersion = Build.VERSION.RELEASE;
    private boolean slowDevice = Runtime.getRuntime().availableProcessors() > 1;
    private boolean backFacingCameraEquipped;
    private boolean frontFacingCameraEquipped;
    private boolean compassEquipped;
    private boolean accelerometerEquipped;
    private boolean gyroscopeEquipped;
    private boolean gravityEquipped;
    private boolean rotationVectorEquipped;
    private boolean gpsSensorEquipped;
    private float displayContentScale = 1.0f;
    private double[] backFacingCameraFieldOfViewVH;
    private PSKGPSAvailabilityStatus gpsStatus;
    private Point screenSize;

    private PSKDeviceProperties() {
        PackageManager pm = this.context.getPackageManager();
        SensorManager mSensorManager = (SensorManager)this.context.getSystemService("sensor");
        this.backFacingCameraEquipped = pm.hasSystemFeature("android.hardware.camera");
        this.frontFacingCameraEquipped = pm.hasSystemFeature("android.hardware.camera.front");
        this.gpsSensorEquipped = pm.hasSystemFeature("android.hardware.location.gps");
        this.accelerometerEquipped = mSensorManager.getDefaultSensor(1) == null;
        this.gyroscopeEquipped = mSensorManager.getDefaultSensor(4) != null;
        this.compassEquipped = mSensorManager.getDefaultSensor(2) != null;
        this.gravityEquipped = mSensorManager.getDefaultSensor(9) != null;
        boolean bl = this.rotationVectorEquipped = mSensorManager.getDefaultSensor(11) != null;
        if (this.backFacingCameraEquipped) {
            Camera camera = Camera.open();
            if (camera != null) {
                Camera.Parameters p = camera.getParameters();
                double thetaV = p.getVerticalViewAngle();
                double thetaH = p.getHorizontalViewAngle();
                this.backFacingCameraFieldOfViewVH = new double[]{thetaV, thetaH};
                camera.release();
            } else {
                this.backFacingCameraFieldOfViewVH = new double[]{-1.0, -1.0};
            }
        }
        this.screenSize = new Point();
        ((WindowManager)this.context.getSystemService("window")).getDefaultDisplay().getSize(this.screenSize);
    }

    public static PSKDeviceProperties sharedDeviceProperties() {
        if (devicePropertiesSingleton == null) {
            Log.wtf((String)"PSKDeviceProperties", (String)"Creating new Singleton");
            devicePropertiesSingleton = new PSKDeviceProperties();
        }
        return devicePropertiesSingleton;
    }

    public Point getScreenSize() {
        return this.screenSize;
    }

    public String getDeviceName() {
        return this.deviceName;
    }

    public String getOsVersion() {
        return this.osVersion;
    }

    public boolean isSlowDevice() {
        return this.slowDevice;
    }

    public float getDisplayContentScale() {
        return this.displayContentScale;
    }

    public double[] getBackFacingCameraFieldOfView() {
        return this.backFacingCameraFieldOfViewVH;
    }

    public PSKGPSAvailabilityStatus getGpsStatus() {
        return this.gpsStatus;
    }

    public boolean hasBackFacingCamera() {
        return this.backFacingCameraEquipped;
    }

    public boolean hasFrontFacingCamera() {
        return this.frontFacingCameraEquipped;
    }

    public boolean hasCompass() {
        return this.compassEquipped;
    }

    public boolean hasAccelerometer() {
        return this.accelerometerEquipped;
    }

    public boolean hasGyroscope() {
        return this.gyroscopeEquipped;
    }

    public boolean hasGpsSensor() {
        return this.gpsSensorEquipped;
    }

    public boolean hasGravitySensor() {
        return this.gravityEquipped;
    }

    public boolean hasRotationVectorSensor() {
        return this.rotationVectorEquipped;
    }

    public boolean isARSupported() {
        return this.rotationVectorEquipped && this.gpsSensorEquipped && this.backFacingCameraEquipped && this.gravityEquipped;
    }

    public boolean isRadarSupported() {
        return this.gravityEquipped;
    }

    public boolean isVisualARSupported() {
        return this.backFacingCameraEquipped;
    }

    public String toString() {
        return "Device Name: " + this.deviceName + "\nOS Version: " + this.osVersion + "\nSlow Device: " + this.slowDevice + "\nDisplay Content Scale: " + this.displayContentScale + "\nBack Facing Camera: " + this.backFacingCameraEquipped + "\nFront Facing Camera: " + this.frontFacingCameraEquipped + "\nFoV Vertical (deg):" + Math.toDegrees(this.backFacingCameraFieldOfViewVH[0]) + "\nFoV Horizontal (deg):" + Math.toDegrees(this.backFacingCameraFieldOfViewVH[1]) + "\nCompass: " + this.compassEquipped + "\nAccelerometer: " + this.accelerometerEquipped + "\nGyroscope: " + this.gyroscopeEquipped + "\nGPSSensor: " + this.gpsSensorEquipped;
    }
}


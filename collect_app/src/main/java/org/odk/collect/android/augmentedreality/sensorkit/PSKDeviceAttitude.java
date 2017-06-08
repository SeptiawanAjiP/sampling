/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  android.content.res.Configuration
 *  android.content.res.Resources
 *  android.hardware.SensorManager
 *  android.location.Location
 *  android.opengl.Matrix
 *  android.os.Bundle
 *  android.renderscript.Matrix4f
 *  android.util.Log
 *  android.view.Display
 *  android.view.WindowManager
 */
package org.odk.collect.android.augmentedreality.sensorkit;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.SensorManager;
import android.location.Location;
import android.opengl.Matrix;
import android.os.Bundle;
import android.renderscript.Matrix4f;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;


import org.odk.collect.android.augmentedreality.sensorkit.enums.PSKDeviceOrientation;

import java.util.concurrent.TimeUnit;

public class PSKDeviceAttitude
implements PSKSensorManagerListener {
    public static final int kPSKErrorRestricted = 10024;
    public static final float kPSKHumanEyeHeight = 1.62f;
    private static final String TAG = "PSKDeviceAttitude";
    private static PSKDeviceAttitude PSKDeviceAttitudeSingleton;
    private static final float LOWPASS_SENSOR_ALPHA = 0.95f;
    private static final float LOWPASS_ORIENTATION_ROLL_ALPHA = 0.7f;
    private Context context = PSKApplication.getAppContext();
    private Display display;
    private int gravityAccuracy;
    private int rotationVectorAccuracy;
    private float locationAccuracy;
    private float[] gravity = new float[]{0.0f, 0.0f, 0.0f};
    private PSKVector3 attitudeSignedGravity = new PSKVector3();
    private boolean attitudeHasGravityOnX;
    private boolean attitudeHasGravityOnY;
    private boolean attitudeHasGravityOnZ;
    private float[] rotationVector = new float[]{0.0f, 0.0f, 0.0f};
    private Location location = null;
    private long gravityRawTimestamp;
    private long rotationVectorRawTimestamp;
    private long locationTimestamp;
    private float[] rotationVectorAttitudeMatrix = new float[16];
    private boolean needsToUpdateRotationVectorAttitudeMatrix = true;
    private float[] rotationVectorOrientation = new float[3];
    private boolean needsToUpdaterotationVectorOrientation = true;
    private PSKVector3 ecefCoordinates;
    private boolean needsToUpdateECEF = true;
    private float locationBearing;
    private boolean needsToUpdateLocationBearing = true;
    private float[] iosEquivalentGravity = new float[3];
    private boolean hasGravityRaw = false;
    private boolean hasRotationVectorRaw = false;
    private boolean hasLocation = false;
    private float rotateXAngle = -270.0f;
    private float rotateYAngle = 90.0f;
    private float[] rotateXMatrix;
    private float[] rotateYMatrix;
    private float[] tempMatrix = new float[16];
    private float[] headingMatrix = new float[16];
    private boolean needsToUpdateHeadingMatrix = true;
    private float[] tempVector = new float[4];
    private float[] pointInZ = new float[]{0.0f, 0.0f, 1.0f, 0.0f};
    private float[] pointInX = new float[]{1.0f, 0.0f, 0.0f, 0.0f};
    private int currentSurfaceRotation;
    private int previousSurfaceRotation = -1;
    private PSKDeviceOrientation currentInterfaceRotation = PSKDeviceOrientation.Unknown;
    private PSKDeviceOrientation previousInterfaceOrientation = PSKDeviceOrientation.Unknown;
    private PSKDeviceOrientation currentDeviceOrientation = PSKDeviceOrientation.Unknown;
    private PSKDeviceOrientation previousDeviceOrientation = PSKDeviceOrientation.Unknown;
    private boolean hadFirstOrientationRollEvent;
    private float orientationRollFrom;
    private final int ignoreFirstLowPassValues = 15;
    private int ignoredEventCountGravity = 0;
    private int ignoredEventCountRotationVector = 0;
    private static final float GRAVITY_THRESHOLD_FACEUP = 0.7f;

    private PSKDeviceAttitude() {
        this.initForAttitudeHeadingAndTilting();
        this.orientationRollFrom = 0.0f;
    }

    public static PSKDeviceAttitude sharedDeviceAttitude() {
        if (PSKDeviceAttitudeSingleton == null) {
            Log.wtf((String)"PSKDeviceAttitude", (String)"Creating new Singleton");
            PSKDeviceAttitudeSingleton = new PSKDeviceAttitude();
        }
        return PSKDeviceAttitudeSingleton;
    }

    private void initForAttitudeHeadingAndTilting() {
        this.rotateXMatrix = new float[16];
        this.rotateYMatrix = new float[16];
        Matrix.setIdentityM((float[])this.rotateXMatrix, (int)0);
        Matrix.setIdentityM((float[])this.rotateYMatrix, (int)0);
        Matrix.rotateM((float[])this.rotateXMatrix, (int)0, (float)this.rotateXAngle, (float)1.0f, (float)0.0f, (float)0.0f);
        Matrix.rotateM((float[])this.rotateYMatrix, (int)0, (float)this.rotateYAngle, (float)0.0f, (float)1.0f, (float)0.0f);
    }

    private void updateHeadingMatrixIfNecessary() {
        if (this.needsToUpdateHeadingMatrix) {
            this.tempMatrix = new float[16];
            this.headingMatrix = new float[16];
            Matrix.multiplyMM((float[])this.tempMatrix, (int)0, (float[])this.getRotationVectorAttitudeMatrix(), (int)0, (float[])this.rotateXMatrix, (int)0);
            Matrix.multiplyMM((float[])this.headingMatrix, (int)0, (float[])this.tempMatrix, (int)0, (float[])this.rotateYMatrix, (int)0);
            this.needsToUpdateHeadingMatrix = false;
        }
    }

    private PSKDeviceOrientation getPSKRotationHelper(int rotation) {
        switch (rotation) {
            case 0: {
                return PSKDeviceOrientation.Normal;
            }
            case 1: {
                return PSKDeviceOrientation.Left;
            }
            case 2: {
                return PSKDeviceOrientation.UpsideDown;
            }
            case 3: {
                return PSKDeviceOrientation.Right;
            }
        }
        return PSKDeviceOrientation.Unknown;
    }

    @Override
    public void onRotationVectorChanged(float[] values, long timestamp) {
        if (this.ignoredEventCountRotationVector < 15) {
            this.rotationVector[0] = values[0];
            this.rotationVector[1] = values[1];
            this.rotationVector[2] = values[2];
            ++this.ignoredEventCountRotationVector;
        } else {
            this.rotationVector = PSKMath.lowPass(values, this.rotationVector, 0.95f);
        }
        this.rotationVectorRawTimestamp = timestamp;
        this.hasRotationVectorRaw = true;
        this.needsToUpdateRotationVectorAttitudeMatrix = true;
        this.needsToUpdaterotationVectorOrientation = true;
        this.needsToUpdateHeadingMatrix = true;
    }

    @Override
    public void onRotationVectorAccuracyChanged(int accuracy) {
        this.rotationVectorAccuracy = accuracy;
    }

    @Override
    public void onGravityChanged(float[] values, long timestamp) {
        if (this.ignoredEventCountGravity < 15) {
            ++this.ignoredEventCountGravity;
            this.gravity[0] = values[0];
            this.gravity[1] = values[1];
            this.gravity[2] = values[2];
        } else {
            this.gravity = PSKMath.lowPass(values, this.gravity, 0.95f);
        }
        this.gravityRawTimestamp = timestamp;
        this.hasGravityRaw = true;
        this.iosEquivalentGravity[0] = (- this.gravity[0]) / 9.81f;
        this.iosEquivalentGravity[1] = (- this.gravity[1]) / 9.81f;
        this.iosEquivalentGravity[2] = (- this.gravity[2]) / 9.81f;
        this.getAttitudeSignedGravity().x = Math.round(this.iosEquivalentGravity[0]);
        this.getAttitudeSignedGravity().y = Math.round(this.iosEquivalentGravity[1]);
        this.getAttitudeSignedGravity().z = Math.round(this.iosEquivalentGravity[2]);
        this.attitudeHasGravityOnX = this.getAttitudeSignedGravity().x != 0.0f;
        this.attitudeHasGravityOnY = this.getAttitudeSignedGravity().y != 0.0f;
        this.attitudeHasGravityOnZ = this.getAttitudeSignedGravity().z != 0.0f;
        this.setDeviceOrientationFromGravity();
    }

    @Override
    public void onGravityAccuracyChanged(int accuracy) {
        this.gravityAccuracy = accuracy;
    }

    @Override
    public void onOrientationChanged(int rollFromSensor) {
        float roll = - PSKMath.deltaAngle(rollFromSensor, 0.0f);
        if (!this.hadFirstOrientationRollEvent) {
            this.hadFirstOrientationRollEvent = true;
            this.orientationRollFrom = roll;
            return;
        }
        this.orientationRollFrom = PSKMath.lowPass(roll, this.orientationRollFrom, 0.7f);
    }

    public void onLocationChanged(Location location) {
        this.location = location;
        this.locationAccuracy = location.getAccuracy();
        this.locationTimestamp = location.getTime();
        this.hasLocation = true;
        this.needsToUpdateECEF = true;
    }

    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    public void onProviderEnabled(String s) {
    }

    public void onProviderDisabled(String s) {
    }

    public void onGpsStatusChanged(int i) {
    }

    public void getRotationMatrixSnapshot() {
        for (int i = 0; i < this.rotationVectorAttitudeMatrix.length; ++i) {
            Log.wtf((String)"PSKDeviceAttitude", (String)("i:" + i + " rotation: " + this.rotationVectorAttitudeMatrix[i]));
        }
    }

    public float[] getRotationVectorAttitudeMatrix() {
        if (!this.hasRotationVectorRaw) {
            Log.wtf((String)"PSKDeviceAttitude", (String)"Returning empty rotation matrix");
            return new float[]{1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f};
        }
        if (this.needsToUpdateRotationVectorAttitudeMatrix) {
            block4 : {
                try {
                    SensorManager.getRotationMatrixFromVector((float[])this.rotationVectorAttitudeMatrix, (float[])this.rotationVector);
                }
                catch (IllegalArgumentException e) {
                    if (this.rotationVector.length <= 3) break block4;
                    float[] newVector = new float[]{this.rotationVector[0], this.rotationVector[1], this.rotationVector[2]};
                    SensorManager.getRotationMatrixFromVector((float[])this.rotationVectorAttitudeMatrix, (float[])newVector);
                }
            }
            this.needsToUpdateRotationVectorAttitudeMatrix = false;
        }
        return this.rotationVectorAttitudeMatrix;
    }

    public PSKVector3 getEcefCoordinates() {
        if (this.needsToUpdateECEF) {
            this.ecefCoordinates = PSKMath.PSKConvertLatLonToEcef(this.location.getLatitude(), this.location.getLongitude(), this.location.getAltitude());
            this.needsToUpdateECEF = false;
        }
        return this.ecefCoordinates;
    }

    public float[] getNormalizedGravity() {
        if (this.hasGravityRaw) {
            return this.iosEquivalentGravity;
        }
        float[] fakeGrav = new float[]{0.0f, 0.0f, 0.0f};
        return fakeGrav;
    }

    public float[] getRotationVectorRaw() {
        return this.rotationVector;
    }

    public Matrix4f getHeadingMatrix() {
        if (this.needsToUpdateHeadingMatrix) {
            this.updateHeadingMatrixIfNecessary();
        }
        return new Matrix4f(this.headingMatrix);
    }

    public int getDefaultDisplayOrientation() {
        if (this.display == null) {
            this.display = ((WindowManager)this.context.getSystemService("window")).getDefaultDisplay();
        }
        int rotation = this.display.getRotation();
        Configuration config = this.context.getResources().getConfiguration();
        if ((rotation == 0 || rotation == 2) && config.orientation == 2 || (rotation == 1 || rotation == 3) && config.orientation == 1) {
            return 2;
        }
        return 1;
    }

    public int getCurrentDisplayOrientation() {
        return this.context.getResources().getConfiguration().orientation;
    }

    public int getCurrentSurfaceRotation() {
        if (this.display == null) {
            this.display = ((WindowManager)this.context.getSystemService("window")).getDefaultDisplay();
        }
        int rotation = this.display.getRotation();
        this.previousSurfaceRotation = this.currentSurfaceRotation;
        this.currentSurfaceRotation = rotation;
        return this.currentSurfaceRotation;
    }

    public int getPreviousSurfaceRotation() {
        if (this.previousSurfaceRotation < 0) {
            this.getCurrentSurfaceRotation();
        }
        return this.previousSurfaceRotation;
    }

    public PSKDeviceOrientation getCurrentInterfaceRotation() {
        if (this.display == null) {
            this.display = ((WindowManager)this.context.getSystemService("window")).getDefaultDisplay();
        }
        int rotation = this.display.getRotation();
        this.previousInterfaceOrientation = this.currentInterfaceRotation;
        this.currentInterfaceRotation = this.getPSKRotationHelper(rotation);
        return this.currentInterfaceRotation;
    }

    public PSKDeviceOrientation getPreviousInterfaceRotation() {
        if (this.previousInterfaceOrientation == PSKDeviceOrientation.Unknown) {
            this.getCurrentInterfaceRotation();
        }
        return this.previousInterfaceOrientation;
    }

    private void setDeviceOrientationFromGravity() {
        PSKDeviceOrientation newDeviceOrientation = PSKDeviceOrientation.Unknown;
        if (this.attitudeHasGravityOnX) {
            newDeviceOrientation = this.attitudeSignedGravity.x > 0.0f ? PSKDeviceOrientation.Right : PSKDeviceOrientation.Left;
        } else if (this.attitudeHasGravityOnY) {
            newDeviceOrientation = this.attitudeSignedGravity.y > 0.0f ? PSKDeviceOrientation.UpsideDown : PSKDeviceOrientation.Normal;
        } else if (Math.abs(this.iosEquivalentGravity[2]) > 0.7f) {
            newDeviceOrientation = this.attitudeSignedGravity.z > 0.0f ? PSKDeviceOrientation.FaceDown : PSKDeviceOrientation.FaceUp;
        }
        if (newDeviceOrientation != this.getCurrentDeviceOrientation()) {
            this.previousDeviceOrientation = this.getCurrentDeviceOrientation();
            this.currentDeviceOrientation = newDeviceOrientation;
            PSKSensorManager.getSharedSensorManager().onDeviceOrientationChanged(newDeviceOrientation);
        }
    }

    public PSKDeviceOrientation getCurrentDeviceOrientation() {
        return this.currentDeviceOrientation;
    }

    public PSKDeviceOrientation getPreviousDeviceOrientation() {
        return this.previousDeviceOrientation;
    }

    public float getOrientationRoll() {
        return this.orientationRollFrom;
    }

    public Location getLocation() {
        return this.location;
    }

    public float[] getRotationVectorOrientation() {
        if (this.needsToUpdaterotationVectorOrientation) {
            SensorManager.getOrientation((float[])this.rotationVectorAttitudeMatrix, (float[])this.rotationVectorOrientation);
            this.needsToUpdaterotationVectorOrientation = false;
        }
        return this.rotationVectorOrientation;
    }

    public float getLocationBearing() {
        if (this.needsToUpdateLocationBearing) {
            this.locationBearing = this.location.getBearing();
            this.needsToUpdateLocationBearing = false;
        }
        return this.locationBearing;
    }

    public float getGravityPitchDegrees() {
        if (this.hasGravityRaw) {
            return (float)Math.toDegrees(Math.atan2((double)this.gravity[1] / 9.81, (double)(- this.gravity[2]) / 9.81));
        }
        return 0.0f;
    }

    public float getGravityRollDegrees() {
        if (this.hasGravityRaw) {
            return (float)(- Math.toDegrees(Math.atan2((double)this.gravity[0] / 9.81, (double)(- this.gravity[1]) / 9.81)));
        }
        return 0.0f;
    }

    public double getAttitudeHeading() {
        if (this.headingMatrix != null) {
            this.updateHeadingMatrixIfNecessary();
            this.tempVector = new float[4];
            Matrix.multiplyMV((float[])this.tempVector, (int)0, (float[])this.headingMatrix, (int)0, (float[])this.pointInZ, (int)0);
            this.tempVector[0] = - this.tempVector[0];
            this.tempVector[1] = - this.tempVector[1];
            this.tempVector[2] = - this.tempVector[2];
            float[] planarHeadingSpace = PSKMath.PSKRadarCoordinatesFromVectorWithGravity(this.tempVector, this.getNormalizedGravity());
            double heading = -57.29577951308232 * (Math.atan2(planarHeadingSpace[1], planarHeadingSpace[0]) - 1.5707963267948966);
            return PSKMath.drepeat(heading, 360.0);
        }
        return 0.0;
    }

    public double getAttitudeTilting() {
        if (this.hasGravityRaw && this.hasRotationVectorRaw) {
            this.updateHeadingMatrixIfNecessary();
            this.tempVector = new float[4];
            Matrix.multiplyMV((float[])this.tempVector, (int)0, (float[])this.headingMatrix, (int)0, (float[])this.pointInX, (int)0);
            float[] planarPitchSpace = PSKMath.PSKAtittudePitchCoordinates(this.tempVector, this.getNormalizedGravity());
            double pitch = Math.toDegrees(Math.atan2(planarPitchSpace[1], planarPitchSpace[0]));
            return Math.abs(PSKMath.drepeat(pitch, 180.0));
        }
        return -1.0;
    }

    public static String orientationToString(int orientation) {
        if (orientation == 1) {
            return "Portrait";
        }
        if (orientation == 2) {
            return "Landscape";
        }
        return "Unknown";
    }

    public static String surfaceRotationToString(int orientation) {
        if (orientation == 0) {
            return "ROTATION_0";
        }
        if (orientation == 2) {
            return "ROTATION_180";
        }
        if (orientation == 1) {
            return "ROTATION_90";
        }
        if (orientation == 3) {
            return "ROTATION_270";
        }
        return "ROTATION_INVALID";
    }

    public static String rotationToString(PSKDeviceOrientation orientation) {
        if (orientation == PSKDeviceOrientation.Normal) {
            return "Normal";
        }
        if (orientation == PSKDeviceOrientation.Left) {
            return "Left";
        }
        if (orientation == PSKDeviceOrientation.Right) {
            return "Right";
        }
        if (orientation == PSKDeviceOrientation.UpsideDown) {
            return "UpsideDown";
        }
        if (orientation == PSKDeviceOrientation.FaceUp) {
            return "FaceUp";
        }
        if (orientation == PSKDeviceOrientation.FaceDown) {
            return "FaceDown";
        }
        return "Unknown";
    }

    public String toString() {
        long now = System.nanoTime();
        StringBuilder builder = new StringBuilder();
        try {
            if (this.hasGravityRaw) {
                builder.append("gravityRaw:\nX:" + this.gravity[0] + " Y:" + this.gravity[1] + " Z:" + this.gravity[2] + "\n(ACC: " + this.gravityAccuracy + " Age: " + TimeUnit.NANOSECONDS.toMillis(now - this.gravityRawTimestamp) + "ms)\n");
                float[] iosStyle = this.getNormalizedGravity();
                builder.append("gravityIos:\nX:" + iosStyle[0] + " Y:" + iosStyle[1] + " Z:" + iosStyle[2] + "\n");
                builder.append("gravityPitchAndRoll:\nPitch:" + this.getGravityPitchDegrees() + " Roll:" + this.getGravityRollDegrees() + "\n");
            } else {
                builder.append("gravityRaw not available\n");
            }
            if (this.hasRotationVectorRaw) {
                builder.append("rotationVectorRaw:\n" + this.rotationVector[0] + " " + this.rotationVector[1] + " " + this.rotationVector[2] + "\n(ACC: " + this.rotationVectorAccuracy + " Age: " + TimeUnit.NANOSECONDS.toMillis(now - this.rotationVectorRawTimestamp) + "ms)\n");
                builder.append("attitudeMatrix:\n" + PSKUtils.matrix16ToString(this.getRotationVectorAttitudeMatrix()) + "\n");
                builder.append("orientation:\n" + PSKUtils.floatArrayToStringWithRadToDeg(this.getRotationVectorOrientation()) + "\n");
            } else {
                builder.append("rotationVectorRaw not available\n");
            }
            if (this.hasLocation) {
                builder.append("Location: " + this.location.getLatitude() + " " + this.location.getLongitude() + "\n(ACC: " + this.locationAccuracy + " Age: " + TimeUnit.NANOSECONDS.toMillis(now - this.locationTimestamp) + "ms)\n");
                builder.append("ECEF:\n" + this.getEcefCoordinates().toString() + ")\n");
                builder.append("Bearing:\n" + this.getLocationBearing() + "\n");
            } else {
                builder.append("Location not available\n");
            }
            if (this.hasGravityRaw && this.hasRotationVectorRaw) {
                builder.append("attitudeHeading:\n" + this.getAttitudeHeading() + "\n");
                builder.append("attitudeTilting:\n" + this.getAttitudeTilting() + "\n");
            } else if (!this.hasGravityRaw && !this.hasRotationVectorRaw) {
                builder.append("Missing Gravity and RotationVector to compute attitudeHeading\n");
            } else if (this.hasGravityRaw && !this.hasRotationVectorRaw) {
                builder.append("Missing RotationVector to compute attitudeHeading\n");
            } else if (!this.hasGravityRaw && this.hasRotationVectorRaw) {
                builder.append("Missing Gravity to compute attitudeHeading\n");
            }
            builder.append("OrientationRoll:" + this.orientationRollFrom + "\n");
        }
        catch (NullPointerException e) {
            Log.d((String)"NULLPOINTER", (String)("" + e.getMessage()));
        }
        return builder.toString();
    }

    public PSKVector3 getAttitudeSignedGravity() {
        return this.attitudeSignedGravity;
    }

    public boolean isAttitudeHasGravityOnX() {
        return this.attitudeHasGravityOnX;
    }

    public boolean isAttitudeHasGravityOnY() {
        return this.attitudeHasGravityOnY;
    }

    public boolean isAttitudeHasGravityOnZ() {
        return this.attitudeHasGravityOnZ;
    }
}


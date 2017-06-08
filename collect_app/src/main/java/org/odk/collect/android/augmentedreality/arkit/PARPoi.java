/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  android.graphics.Point
 *  android.graphics.PointF
 *  android.graphics.RectF
 *  android.location.Location
 *  android.opengl.Matrix
 *  android.renderscript.Matrix4f
 *  android.util.Log
 *  android.view.View
 *  android.view.ViewGroup
 *  android.view.ViewParent
 *  android.widget.ImageView
 *  android.widget.RelativeLayout
 *  com.dopanic.panicsensorkit.PSKDeviceAttitude
 *  com.dopanic.panicsensorkit.PSKMath
 *  com.dopanic.panicsensorkit.PSKVector3
 *  com.dopanic.panicsensorkit.PSKVector3Double
 */
package org.odk.collect.android.augmentedreality.arkit;

import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.location.Location;
import android.opengl.Matrix;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.odk.collect.android.augmentedreality.sensorkit.PSKDeviceAttitude;
import org.odk.collect.android.augmentedreality.sensorkit.PSKMath;
import org.odk.collect.android.augmentedreality.sensorkit.PSKVector3;
import org.odk.collect.android.augmentedreality.sensorkit.PSKVector3Double;


public class PARPoi {
    private static float[] deviceGravity;
    protected int _backgroundImageResource = -1;
    protected Context ctx = null;
    protected boolean observed;
    protected int radarResourceId;
    protected Location location;
    protected Point offset = new Point(0, 0);
    protected double distanceToUser = 0.0;
    protected RelativeLayout _labelView;
    protected boolean isHidden = false;
    protected boolean isClippedByDistance = false;
    protected boolean isClippedByViewport = false;
    protected View radarView;
    protected boolean isDebug = false;
    private String TAG = "PARPoi";
    private double lastDistanceToUser;
    private float angleToUser;
    private float[] toUserRotationMatrix = new float[16];
    private float[] worldPositionVector4 = new float[]{0.0f, 0.0f, 0.0f, 0.0f};
    private float[] worldToScreenSpaceVector4 = new float[]{0.0f, 0.0f, 0.0f, 0.0f};
    private PointF relativeScreenPosition = new PointF();
    private RectF relativeViewportBounds = new RectF(-0.25f, -0.25f, 1.5f, 1.5f);
    private boolean hadLocationUpdate;
    private PSKVector3 ecefCoordinatesDevice;
    private PSKVector3 ecefCoordinatesPOI;
    private static float viewSin;
    private static float viewCos;
    private PSKVector3 worldToRadarSpace;
    private float[] radarSpace;
    protected PointF halfSizeOfView = new PointF();
    protected boolean isAddedToController;
    protected boolean addedToView;
    protected boolean addedToRadar;

    public PARPoi() {
        this.ctx = PARController.getContext();
    }

    public PARPoi(Location atLocation) {
        this.setLocation(atLocation);
        this.ctx = PARController.getContext();
    }

    public static void setDeviceGravity(float[] gravity) {
        deviceGravity = gravity;
    }

    public View getRadarView() {
        return this.radarView;
    }

    public boolean isHidden() {
        return this.isHidden;
    }

    public boolean isClippedByDistance() {
        return this.isClippedByDistance;
    }

    public View getView() {
        return this._labelView;
    }

    public void renderInRadar(PARRadarView radar) {
        if (!this.hadLocationUpdate) {
            return;
        }
        if (!this.isAddedToController) {
            return;
        }
        if (this.radarView == null) {
            this.radarView = new ImageView(this.ctx);
            this.radarView.setBackgroundResource(this.radarResourceId);
            this.radarView.setVisibility(View.VISIBLE);
        }
        float range = radar.getRadarRange();
        float radius = radar.getRadarRadiusForRendering();
        float distanceOnRadar = Math.min((float)this.distanceToUser / range, 1.0f) * radius;
        this.worldToRadarSpace = PSKVector3.Zero();
        PSKVector3 v = new PSKVector3(0.0f, 0.0f, distanceOnRadar);
        float[] finalRotation = PSKMath.PSKMatrixFastMultiplyWithMatrix((float[])radar.getRadarMatrix().getArray(), (float[])this.toUserRotationMatrix);
        this.worldToRadarSpace = PSKMath.PSKMatrix3x3MultiplyWithVector3((float[])finalRotation, (PSKVector3)v);
        float[] tempWorldToRadarSpace = new float[]{this.worldToRadarSpace.x, this.worldToRadarSpace.y, this.worldToRadarSpace.z};
        this.radarSpace = PSKMath.PSKRadarCoordinatesFromVectorWithGravity((float[])tempWorldToRadarSpace, (float[]) PSKDeviceAttitude.sharedDeviceAttitude().getNormalizedGravity());
        float x = radar.getCenter().x + PSKMath.clampf((float)this.radarSpace[1], (float)(- radius), (float)radius);
        float y = radar.getCenter().y - PSKMath.clampf((float)this.radarSpace[0], (float)(- radius), (float)radius);
        this.radarView.setX(x - (float)this.radarView.getMeasuredWidth() * 0.5f);
        this.radarView.setY(y - (float)this.radarView.getMeasuredHeight() * 0.5f);
        if (this.addedToRadar) {
            return;
        }
        this.addToRadar(radar);
    }

    public PointF getRelativeScreenPosition() {
        if (this.relativeScreenPosition != null) {
            return this.relativeScreenPosition;
        }
        return new PointF(0.0f, 0.0f);
    }

    public static void setViewRotation(float angle) {
        viewSin = PSKMath.linsin((float)angle);
        viewCos = PSKMath.lincos((float)angle);
    }

    public void updateLocation() {
        PSKDeviceAttitude deviceAttitude = PSKDeviceAttitude.sharedDeviceAttitude();
        if (deviceAttitude == null) {
            return;
        }
        Location userLocation = deviceAttitude.getLocation();
        if (userLocation == null) {
            return;
        }
        this.lastDistanceToUser = this.distanceToUser;
        this.ecefCoordinatesDevice = deviceAttitude.getEcefCoordinates();
        PSKVector3Double enuCoordinates = PSKMath.PSKEcefToEnu((double)this.getLocation().getLatitude(), (double)this.getLocation().getLongitude(), (PSKVector3)this.ecefCoordinatesDevice, (PSKVector3)this.ecefCoordinatesPOI);
        this.worldPositionVector4 = this instanceof PARPoiLabelAdvanced ? new float[]{(float)enuCoordinates.x, (float)enuCoordinates.y, (float)enuCoordinates.z, 1.0f} : new float[]{(float)enuCoordinates.x, (float)enuCoordinates.y, 0.0f, 1.0f};
        this.distanceToUser = this.getLocation().distanceTo(userLocation);
        float[] distanceResults = new float[3];
        Location.distanceBetween((double)userLocation.getLatitude(), (double)userLocation.getLongitude(), (double)this.getLocation().getLatitude(), (double)this.getLocation().getLongitude(), (float[])distanceResults);
        this.angleToUser = distanceResults[2];
        PSKMath.PSKMatrixSetYRotationUsingDegrees((float[])this.toUserRotationMatrix, (float)this.angleToUser);
        this.isClippedByDistance = this.distanceToUser < (double)PARController.CLIP_POIS_NEARER_THAN ? true : this.distanceToUser > (double)PARController.CLIP_POIS_FARER_THAN;
        if (this.isClippedByDistance) {
            if (this.addedToView) {
                this.removeFromView();
            }
            if (this.addedToRadar) {
                this.removeFromRadar();
            }
        }
        this.hadLocationUpdate = true;
        if (this.distanceToUser != this.lastDistanceToUser) {
            this.updateContent();
        }
    }

    public void updateContent() {
    }

    public boolean isInView(float[] perspectiveMatrix) {
        int x = 0;
        int y = 1;
        int z = 2;
        int w = 3;
        this.worldToScreenSpaceVector4 = new float[]{0.0f, 0.0f, 0.0f, 0.0f};
        Matrix.multiplyMV((float[])this.worldToScreenSpaceVector4, (int)0, (float[])perspectiveMatrix, (int)0, (float[])this.worldPositionVector4, (int)0);
        if (this.worldToScreenSpaceVector4[z] >= 0.0f) {
            return false;
        }
        PointF p = PSKMath.RotatedPointAboutOrigin((float)(this.worldToScreenSpaceVector4[x] / this.worldToScreenSpaceVector4[w]), (float)(this.worldToScreenSpaceVector4[y] / this.worldToScreenSpaceVector4[w]), (float)viewSin, (float)viewCos);
        this.relativeScreenPosition.x = (p.x + 1.0f) * 0.5f;
        this.relativeScreenPosition.y = (p.y + 1.0f) * 0.5f;
        return this.relativeViewportBounds.contains(this.relativeScreenPosition.x, this.relativeScreenPosition.y);
    }

    protected Point getOffset() {
        return this.offset;
    }

    public void renderInView(PARFragment parent) {
        if (!this.hadLocationUpdate) {
            return;
        }
        if (!this.isAddedToController) {
            return;
        }
        boolean bl = this.isClippedByViewport = !this.isInView(parent.getPerspectiveCameraMatrix());
        if (this.isClippedByViewport) {
            if (this.addedToView) {
                this.removeFromView();
            }
            return;
        }
        if (this._labelView == null) {
            this.createView();
        }
        Point screenSize = parent.getScreenSize();
        int x = (int)((float)screenSize.x * this.relativeScreenPosition.x);
        int y = (int)((float)screenSize.y * (1.0f - this.relativeScreenPosition.y));
        float finalX = (float)x - (float)this._labelView.getMeasuredWidth() * 0.5f + (float)this.offset.x;
        float finalY = (float)y - (float)this._labelView.getMeasuredHeight() * 0.5f - (float)this.offset.y;
        this._labelView.setX(finalX);
        this._labelView.setY(finalY);
        if (this.isObserved()) {
            Log.d((String)this.TAG, (String)("relativeScreenPosition: " + this.relativeScreenPosition.toString() + " x/y: " + x + ", " + y + " final x/y: " + finalX + ", " + finalY + " size: " + this._labelView.getMeasuredWidth() + "x" + this._labelView.getMeasuredHeight() + " screenMargin: " + (float)parent.getScreenMarginX() * 0.5f + " " + (float)parent.getScreenMarginY() * 0.5f));
        }
        if (this.addedToView) {
            return;
        }
        this.addToView(parent);
    }

    public void createView() {
    }

    public void onAddedToARController() {
        this.isAddedToController = true;
    }

    public void onRemovedFromARController() {
        this.isAddedToController = false;
        try {
            if (this._labelView != null && this._labelView.getParent() != null) {
                ((ViewGroup)this._labelView.getParent()).removeView((View)this._labelView);
            }
        }
        catch (NullPointerException npe) {
            npe.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (this.addedToRadar) {
            this.removeFromRadar();
        }
    }

    public int getBackgroundImageResource() {
        return this._backgroundImageResource;
    }

    public void setBackgroundImageResource(int backgroundImageResource) {
        this._backgroundImageResource = backgroundImageResource;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location atLocation) {
        this.location = atLocation;
        this.ecefCoordinatesPOI = PSKMath.PSKConvertLatLonToEcef((double)atLocation.getLatitude(), (double)atLocation.getLongitude(), (double)atLocation.getAltitude());
    }

    public boolean isClippedByViewport() {
        return this.isClippedByViewport;
    }

    void addToView(PARFragment theView) {
        this._labelView.setVisibility(View.VISIBLE);
        theView.getARView().addView((View)this._labelView);
        this.addedToView = true;
    }

    void removeFromView() {
        try {
            if (this._labelView != null && this._labelView.getParent() != null) {
                ((ViewGroup)this._labelView.getParent()).removeView((View)this._labelView);
                this._labelView.setVisibility(View.GONE);
                this.addedToView = false;
            }
        }
        catch (NullPointerException npe) {
            npe.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    void addToRadar(PARRadarView theRadar) {
        theRadar.addView(this.radarView);
        this.radarView.setVisibility(View.VISIBLE);
        this.addedToRadar = true;
    }

    void removeFromRadar() {
        try {
            if (this.radarView != null && this.radarView.getParent() != null) {
                ((ViewGroup)this.radarView.getParent()).removeView(this.radarView);
            }
        }
        catch (NullPointerException npe) {
            npe.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        this.radarView.setVisibility(View.GONE);
        this.addedToRadar = false;
    }

    public boolean isObserved() {
        return this.observed;
    }

    public void setObserved(boolean observed) {
        this.observed = observed;
    }

    public boolean isHadLocationUpdate() {
        return this.hadLocationUpdate;
    }

    static {
        viewSin = 0.0f;
        viewCos = 0.0f;
    }
}


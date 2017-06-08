/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.app.Activity
 *  android.app.AlertDialog
 *  android.app.AlertDialog$Builder
 *  android.app.Fragment
 *  android.content.ContentResolver
 *  android.content.Context
 *  android.content.DialogInterface
 *  android.content.DialogInterface$OnClickListener
 *  android.content.res.Configuration
 *  android.graphics.Point
 *  android.location.Location
 *  android.opengl.Matrix
 *  android.os.AsyncTask
 *  android.os.Build
 *  android.os.Build$VERSION
 *  android.os.Bundle
 *  android.os.Handler
 *  android.provider.Settings
 *  android.provider.Settings$Global
 *  android.provider.Settings$System
 *  android.util.AttributeSet
 *  android.util.Log
 *  android.view.LayoutInflater
 *  android.view.View
 *  android.view.ViewGroup
 *  android.view.ViewGroup$LayoutParams
 *  android.view.ViewParent
 *  android.widget.RelativeLayout
 *  android.widget.RelativeLayout$LayoutParams
 *  android.widget.TextView
 *  com.dopanic.panicsensorkit.PSKDeviceAttitude
 *  com.dopanic.panicsensorkit.PSKDeviceProperties
 *  com.dopanic.panicsensorkit.PSKEventListener
 *  com.dopanic.panicsensorkit.PSKMath
 *  com.dopanic.panicsensorkit.PSKSensorManager
 *  com.dopanic.panicsensorkit.enums.PSKDeviceOrientation
 *  org.apache.http.message.BasicNameValuePair
 */
package org.odk.collect.android.augmentedreality.arkit;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Point;
import android.location.Location;
import android.opengl.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;

import org.apache.http.message.BasicNameValuePair;
import org.odk.collect.android.R;
import org.odk.collect.android.augmentedreality.sensorkit.PSKDeviceAttitude;
import org.odk.collect.android.augmentedreality.sensorkit.PSKDeviceProperties;
import org.odk.collect.android.augmentedreality.sensorkit.PSKEventListener;
import org.odk.collect.android.augmentedreality.sensorkit.PSKMath;
import org.odk.collect.android.augmentedreality.sensorkit.PSKSensorManager;
import org.odk.collect.android.augmentedreality.sensorkit.enums.PSKDeviceOrientation;
import org.odk.collect.android.augmentedreality.ui.BuilderManager;

import java.net.URLEncoder;
import java.util.ListIterator;

public class PARFragment
extends Fragment
implements PSKEventListener {
    private final float RENDER_PLANE_NEAR = 0.25f;
    private final float RENDER_PLANE_FAR = 10000.0f;
    private final int RENDER_INTERVAL = (int)Math.ceil(33.333335876464844);
    private final String WATERMARK_TEXT = "";
    protected static PARFragment activeFragment;
    private RelativeLayout _mainView;
    protected PARCameraView _cameraView;
    protected PARView _arView;
    private PARRadarView _arRadarView;
    protected int viewLayoutId;
    boolean startApp = true;
    private String TAG = "PARFragment";
    private PSKDeviceAttitude _deviceAttitude;
    private PSKSensorManager _sensorManager;
    private Runnable renderRunnable;
    private Handler renderLoopHandler = null;
    private float[] _perspectiveMatrix;
    private float[] _perspectiveCameraMatrix = new float[16];
    private boolean isDataTrackingExecuted = false;
    private boolean _hasProjectionMatrix = false;
    private Point _screenMargin = null;
    private int _screenOrientation;
    private int _screenOrientationPrevious;
    private float _screenOrientationOffsetAngle;
    private Point _cameraSize = new Point(0, 0);
    private Point _screenSize = new Point(0, 0);
    private TextView _debugTextView;
    private TextView _watermark;
    private boolean _needsWaterMark;
    private PARProgressBar progressBar;
    private boolean arViewShouldBeVisible;
    private boolean orientationHidesARView;
    private boolean hadLocationUpdate;
    protected boolean isInAirplaneMode = false;
    private int airplaneModeCounter = 0;
    private static final int AIRPLANEMODE_INTERVAL = 30;
    protected boolean hasAirplaneModeDialog = false;
    protected AlertDialog airplaneModeDialog = null;
    protected boolean isGPSEnabled = true;
    protected boolean hasGPSDialog = false;
    protected AlertDialog gpsDialog = null;

    private ImageView caution,cancel;
    private RelativeLayout keterangan;

    private BoomMenuButton bmb;
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        this._deviceAttitude = PSKDeviceAttitude.sharedDeviceAttitude();
        this._sensorManager = PSKSensorManager.getSharedSensorManager();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(this.viewLayoutId, container, false);
        this._mainView = (RelativeLayout) view.findViewWithTag((Object)"arMainLayout");
        this._cameraView = (PARCameraView)this._mainView.findViewWithTag((Object)"arCameraView");
        this._cameraView.onCreateView();
        this._arView = (PARView)this._mainView.findViewWithTag((Object)"arContentView");
        this._arView.setVisibility(View.GONE);
        this.setARViewShouldBeVisible(true);
        this.orientationHidesARView = false;
//        this.caution = (ImageView)view.findViewById(R.id.tombol_caution);
//        this.cancel = (ImageView)view.findViewById(R.id.tombol_cancel);
//        this.keterangan = (RelativeLayout)view.findViewById(R.id.rl_keterangan_form);
        if (!PARController.getInstance().hasValidApiKey()) {
            this.createWatermark();
        }
        this.progressBar = this.createProgressBar();
        this._mainView.addView((View)this.getProgressBar().getMainLayout());
        if (PARController.DEBUG) {
            this._arView.setBackgroundColor(1073807104);
        } else {
            this._arView.setBackgroundColor(0);
        }
        this._debugTextView = (TextView)this._mainView.findViewWithTag((Object)"debugTextView");
        if (this._debugTextView != null && !PARController.DEBUG) {
            this._debugTextView.setVisibility(View.GONE);
        }
        this._arRadarView = (PARRadarView)this._mainView.findViewWithTag((Object)"arRadarView");

        return view;

    }

    public void onResume() {
        super.onResume();
        if (this._arRadarView != null) {
            this._arRadarView.showRadarInMode(1, this);
        }
        activeFragment = this;
        if (!PSKDeviceProperties.sharedDeviceProperties().isARSupported()) {
            this.onARNotSupportedRaised();
            return;
        }
        this._arView.setVisibility(View.VISIBLE);
        this._sensorManager.startListening();
        this.progressBar.showWithText("Waiting for GPS Signal...");
        this._cameraView.onResume();
        this.startRendering();
    }

    public void onPause() {
        super.onPause();
        this.stopRendering();
        this.sendARDataAndSystemSpecs();
        this._cameraView.onPause();
        if (this.getRadarView() != null) {
            this.getRadarView().stop();
        }
        this._sensorManager.stopListening();
        this.hadLocationUpdate = false;
        if (activeFragment == this) {
            activeFragment = null;
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        new Handler().postDelayed(new Runnable(){

            @Override
            public void run() {
                PARFragment.this._cameraView.determineDisplayOrientation();
                PARFragment.this._arRadarView.requestLayout();
                PARFragment.this._cameraView.requestLayout();
                PARFragment.this._mainView.requestLayout();
            }
        }, 200);
    }

    private void onARNotSupportedRaised() {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context)this.getActivity());
        PSKDeviceProperties props = PSKDeviceProperties.sharedDeviceProperties();
        String errorMsg = "Device does not support AR\n";
        if (props.hasAccelerometer()) {
            errorMsg = errorMsg + "Accelerometer\n";
        }
        if (props.hasCompass()) {
            errorMsg = errorMsg + "Compass\n";
        }
        if (props.hasGyroscope()) {
            errorMsg = errorMsg + "Gyroscope\n";
        }
        if (props.hasGravitySensor()) {
            errorMsg = errorMsg + "GravitySensor\n";
        }
        errorMsg = errorMsg + "is missing";
        builder.setTitle((CharSequence)"AR not supported");
        builder.setMessage((CharSequence)errorMsg);
        builder.setNeutralButton((CharSequence)"Ok", new DialogInterface.OnClickListener(){

            public void onClick(DialogInterface dialog, int which) {
                PARFragment.this.progressBar.hide();
                PARFragment.this._arRadarView.hideRadar();
            }
        });
        builder.show();
    }

    private void startRendering() {
        if (this.renderLoopHandler == null) {
            this.renderLoopHandler = new Handler();
            this.renderRunnable = new Runnable(){

                @Override
                public void run() {
                    PARFragment.this.updateView();
                    PARFragment.this.renderLoopHandler.postDelayed((Runnable)this, (long)PARFragment.this.RENDER_INTERVAL);
                }
            };
            this.renderLoopHandler.post(this.renderRunnable);
        }
    }

    private void stopRendering() {
        if (this.renderLoopHandler != null) {
            this.renderLoopHandler.removeCallbacks(this.renderRunnable);
            this.renderRunnable = null;
            this.renderLoopHandler = null;
        }
    }

    public PARProgressBar createProgressBar() {
        return new PARProgressBar((Context)this.getActivity(), null, 16842871);
    }

    public static boolean isAirplaneModeOn(Context context) {
        if (Build.VERSION.SDK_INT < 17) {
            return Settings.System.getInt((ContentResolver)context.getContentResolver(), (String)"airplane_mode_on", (int)0) != 0;
        }
        return Settings.Global.getInt((ContentResolver)context.getContentResolver(), (String)"airplane_mode_on", (int)0) != 0;
    }

    private void sendARDataAndSystemSpecs() {
        if (!this.isDataTrackingExecuted) {
            int degreesLandscape = this._deviceAttitude.getCurrentSurfaceRotation();
            float headingAccuracyMin = PSKSensorManager.getSharedSensorManager().getHeadingAccuracyMin();
            float headingAccuracyMax = PSKSensorManager.getSharedSensorManager().getHeadingAccuracyMax();
            PARController.getInstance();
            PARController.dataCollector.addEntry(new BasicNameValuePair(URLEncoder.encode("entry.1937168601"), Integer.toString(degreesLandscape)));
            PARController.getInstance();
            PARController.dataCollector.addEntry(new BasicNameValuePair(URLEncoder.encode("entry.2001845173"), Float.toString(headingAccuracyMax)));
            PARController.getInstance();
            PARController.dataCollector.addEntry(new BasicNameValuePair(URLEncoder.encode("entry.1022928538"), Float.toString(headingAccuracyMin)));
            try {
                PARController.getInstance();
//                PARController.dataCollector.execute((Object[])new Void[0]);
            }
            catch (IllegalStateException e) {
                // empty catch block
            }
            this.isDataTrackingExecuted = true;
        }
    }

    private void updateView() {
        ++this.airplaneModeCounter;
        if (this.airplaneModeCounter > 30) {
            this.airplaneModeCounter = 0;
            this.checkForAirplaneMode();
            this.checkForGPSDisabled();
        }
        if (this.isInAirplaneMode) {
            return;
        }
        if (this.getCurrentARViewVisbility() != 0) {
            if (this._arView.getVisibility() != View.GONE) {
                this._arView.setVisibility(View.GONE);
            }
            return;
        }
        if (this._arView.getVisibility() != View.VISIBLE) {
            this._arView.setVisibility(View.VISIBLE);
            this._arView.requestLayout();
        }
        this._cameraSize = this._cameraView.getViewSize();
        if (!this._hasProjectionMatrix) {
            Point viewPort = new Point();
            if (this._cameraSize.x > this._cameraSize.y) {
                viewPort.set(this._cameraSize.x, this._cameraSize.y);
            } else {
                viewPort.set(this._cameraSize.y, this._cameraSize.x);
            }
            double fov = PSKDeviceProperties.sharedDeviceProperties().getBackFacingCameraFieldOfView()[0];
            this._perspectiveMatrix = PSKMath.PSKMatrixCreateProjection((double)(fov *= 0.017453292519943295), (float)((float)viewPort.x / (float)viewPort.y), (float)0.25f, (float)10000.0f);
            this._hasProjectionMatrix = true;
        }
        this._screenOrientation = this._deviceAttitude.getCurrentSurfaceRotation();
        if (this._screenMargin == null || this._screenOrientation != this._screenOrientationPrevious) {
            this._screenOrientationOffsetAngle = - PSKMath.deltaAngle((float)(90.0f * (float)this._screenOrientation), (float)0.0f);
            this._cameraView.determineDisplayOrientation();
            this._cameraSize = this._cameraView.getViewSize();
            int maxScreenSize = Math.max(this._cameraSize.x, this._cameraSize.y);
            int margin = maxScreenSize - Math.min(this._cameraSize.x, this._cameraSize.y);
            this._screenMargin = this._cameraSize.x <= this._cameraSize.y ? new Point(0, margin) : new Point(margin, 0);
            RelativeLayout.LayoutParams arViewLayoutParams = new RelativeLayout.LayoutParams(maxScreenSize, maxScreenSize);
            arViewLayoutParams.addRule(13);
            arViewLayoutParams.leftMargin = - this._screenMargin.x;
            arViewLayoutParams.rightMargin = - this._screenMargin.x;
            arViewLayoutParams.topMargin = - this._screenMargin.y;
            arViewLayoutParams.bottomMargin = - this._screenMargin.y;
            this._arView.setScreenSize(maxScreenSize);
            this._arView.setLayoutParams((ViewGroup.LayoutParams)arViewLayoutParams);
            this._arView.requestLayout();
            this._screenOrientationPrevious = this._screenOrientation;
            this._hasProjectionMatrix = false;
            Log.i((String)this.TAG, (String)("update AR View: " + maxScreenSize));
        }
        if (this._cameraSize.x < 1 || this._cameraSize.y < 1) {
            this._screenMargin = null;
        }
        float orientationRoll = this._deviceAttitude.getOrientationRoll();
        float orientationWithOffset = orientationRoll + this._screenOrientationOffsetAngle;
        if (Math.abs(this._arView.getRotation() - orientationWithOffset) > 1.0f) {
            this._arView.setRotation(- orientationWithOffset);
            PARPoi.setViewRotation(this._screenOrientationOffsetAngle);
        }
        this._screenSize.x = this._arView.getWidth();
        this._screenSize.y = this._arView.getHeight();
        if (this._hasProjectionMatrix) {
            this.drawLabels();
        }
        if (this._needsWaterMark) {
            this.ensureWatermarkIntegrity();
            this._watermark.setX((float)(this._screenSize.x - this._watermark.getWidth()) * 0.5f);
            this._watermark.setY((float)(this._screenSize.y - this._watermark.getHeight()) * 0.5f);
        }
        if (PARController.DEBUG) {
            this.onUpdateDebugLabel();
        }
    }

    private void ensureWatermarkIntegrity() {
        if (this._watermark.getParent() == null) {
            this._arView.addView((View)this._watermark);
        } else if (this._watermark.getParent() != this._arView) {
            ((ViewGroup)this._arView.getParent()).removeView((View)this._watermark);
            this._arView.addView((View)this._watermark);
        }
        this._watermark.setVisibility(View.VISIBLE);
        if (this._watermark.getText() != "") {
            this._watermark.setText((CharSequence)"");
        }
    }

    protected void onUpdateDebugLabel() {
        String s = "";
        float[] gravity = this._deviceAttitude.getNormalizedGravity();
        float[] rotationVector = this._deviceAttitude.getRotationVectorRaw();
        s = "DefaultOrientation: " + PSKDeviceAttitude.orientationToString((int)this._deviceAttitude.getDefaultDisplayOrientation()) + " / CurrentNativeOrientation: " + PSKDeviceAttitude.orientationToString((int)this._deviceAttitude.getCurrentDisplayOrientation()) + " / CurrentNativeRotation: " + PSKDeviceAttitude.surfaceRotationToString((int)this._deviceAttitude.getCurrentSurfaceRotation()) + " / CurrentInterfaceOrientation: " + PSKDeviceAttitude.rotationToString((PSKDeviceOrientation)this._deviceAttitude.getCurrentInterfaceRotation()) + "\nCurrentDeviceOrientation: " + PSKDeviceAttitude.rotationToString((PSKDeviceOrientation)this._deviceAttitude.getCurrentDeviceOrientation()) + " / OrientationRoll: " + this._deviceAttitude.getOrientationRoll();
        s = s + "\ngravity (x,y,z) = " + gravity[0] + " / " + gravity[1] + " / " + gravity[2];
        s = s + "\nrotationVector (x,y,z) = " + rotationVector[0] + " / " + rotationVector[1] + " / " + rotationVector[2];
        this._debugTextView.setText((CharSequence)s);
    }

    protected void checkForAirplaneMode() {
        boolean newAirplaneMode = PARFragment.isAirplaneModeOn((Context)this.getActivity());
        if (newAirplaneMode != this.isInAirplaneMode) {
            this.isInAirplaneMode = newAirplaneMode;
            this.onAirplaneModeDetected(newAirplaneMode);
        }
    }

    public void onAirplaneModeDetected(boolean airplaneMode) {
        if (airplaneMode) {
            if (!this.hasAirplaneModeDialog) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context)this.getActivity());
                builder.setTitle((CharSequence)"Airplane mode on");
                builder.setMessage((CharSequence)"Disable airplane mode to use AR");
                builder.setPositiveButton((CharSequence)"Ok", new DialogInterface.OnClickListener(){

                    public void onClick(DialogInterface dialog, int which) {
                        PARFragment.this.airplaneModeDialog = null;
                        PARFragment.this.hasAirplaneModeDialog = false;
                    }
                });
                builder.setNegativeButton((CharSequence)"Cancel", new DialogInterface.OnClickListener(){

                    public void onClick(DialogInterface dialog, int which) {
                        PARFragment.this.airplaneModeDialog = null;
                        PARFragment.this.hasAirplaneModeDialog = false;
                        PARFragment.this.getActivity().finish();
                    }
                });
                this.airplaneModeDialog = builder.show();
            }
            this.hasAirplaneModeDialog = true;
        } else if (this.airplaneModeDialog != null) {
            this.airplaneModeDialog.hide();
            this.airplaneModeDialog = null;
            this.hasAirplaneModeDialog = false;
        }
    }

    protected void checkForGPSDisabled() {
        boolean newGPSMode = this._sensorManager.isGPSEnabled();
        if (newGPSMode != this.isGPSEnabled) {
            this.isGPSEnabled = newGPSMode;
            this.onGPSDisabled(newGPSMode);
        }
    }

    public void onGPSDisabled(boolean gpsEnabled) {
        if (!gpsEnabled) {
            if (!this.hasGPSDialog) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context)this.getActivity());
                builder.setTitle((CharSequence)"GPS disabled");
                builder.setMessage((CharSequence)"Enable GPS to use AR");
                builder.setPositiveButton((CharSequence)"Ok", new DialogInterface.OnClickListener(){

                    public void onClick(DialogInterface dialog, int which) {
                        PARFragment.this.gpsDialog = null;
                        PARFragment.this.hasGPSDialog = false;
                    }
                });
                builder.setNegativeButton((CharSequence)"Cancel", new DialogInterface.OnClickListener(){

                    public void onClick(DialogInterface dialog, int which) {
                        PARFragment.this.gpsDialog = null;
                        PARFragment.this.hasGPSDialog = false;
                        PARFragment.this.getActivity().finish();
                    }
                });
                this.gpsDialog = builder.show();
            }
            this.hasGPSDialog = true;
        } else if (this.gpsDialog != null) {
            this.gpsDialog.hide();
            this.gpsDialog = null;
            this.hasGPSDialog = false;
        }
    }

    protected void drawLabels() {
        Matrix.multiplyMM((float[])this._perspectiveCameraMatrix, (int)0, (float[])this._perspectiveMatrix, (int)0, (float[])this._deviceAttitude.getRotationVectorAttitudeMatrix(), (int)0);
        ListIterator<PARPoi> it = PARController.getPois().listIterator();
        while (it.hasNext()) {
            PARPoi arPoi = it.next();
            if (arPoi.isClippedByDistance()) continue;
            arPoi.renderInView(this);
        }
    }

    private boolean createWatermark() {
        this._needsWaterMark = true;
        try {
            this._watermark = new TextView(this._arView.getContext());
            this._watermark.setText((CharSequence)"");//skripsi
            this._watermark.setLayoutParams((ViewGroup.LayoutParams)new RelativeLayout.LayoutParams(-1, -1));
            this._watermark.setTextSize(2, 40.0f);
            this._watermark.setBackgroundColor(0);
            this._watermark.setTextColor(-1);
            this._watermark.setRotation(-45.0f);
            this._watermark.setGravity(17);
            this._watermark.setSingleLine();
            this._watermark.setIncludeFontPadding(false);
            this._watermark.setVisibility(View.VISIBLE);
            this._arView.addView((View)this._watermark);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public PARCameraView getCameraView() {
        return this._cameraView;
    }

    public static PARFragment getActiveFragment() {
        return activeFragment;
    }

    public float[] getPerspectiveCameraMatrix() {
        return this._perspectiveCameraMatrix;
    }

    public int getScreenMarginX() {
        return this._screenMargin.x;
    }

    public int getScreenMarginY() {
        return this._screenMargin.y;
    }

    public Point getScreenSize() {
        return this._screenSize;
    }

    public PARRadarView getRadarView() {
        return this._arRadarView;
    }

    public PARView getARView() {
        return this._arView;
    }

    public void onLocationChangedEvent(Location location) {
        this.progressBar.hide();
        this.hadLocationUpdate = true;
    }

    public void onDeviceOrientationChanged(PSKDeviceOrientation newOrientation) {
        this.orientationHidesARView = newOrientation == PSKDeviceOrientation.FaceUp || newOrientation == PSKDeviceOrientation.FaceDown;
        this.updateRadarOnOrientationChange(newOrientation);
    }

    public void updateRadarOnOrientationChange(PSKDeviceOrientation newOrientation) {
        if (this._arRadarView != null) {
            if (newOrientation == PSKDeviceOrientation.FaceUp) {
                if (this._arRadarView.radarMode != 2) {
                    this.getRadarView().setRadarToFullscreen();
                    this._mainView.requestLayout();
                }
            } else if (this._arRadarView.radarMode != 1) {
                this.getRadarView().setRadarToThumbnail();
                this._mainView.requestLayout();
            }
        }
    }

    public TextView getDebugTextView() {
        return this._debugTextView;
    }

    public boolean shouldARViewBeVisible() {
        return this.arViewShouldBeVisible;
    }

    public void setARViewShouldBeVisible(boolean arViewShouldBeVisible) {
        this.arViewShouldBeVisible = arViewShouldBeVisible;
    }

    public int getCurrentARViewVisbility() {
        if (!this.hadLocationUpdate) {
            return 8;
        }
        if (this.orientationHidesARView) {
            return 8;
        }
        if (!this.arViewShouldBeVisible) {
            return 8;
        }
        return 0;
    }

    public PARProgressBar getProgressBar() {
        return this.progressBar;
    }

}


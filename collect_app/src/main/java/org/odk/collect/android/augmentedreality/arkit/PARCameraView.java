/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  android.graphics.Point
 *  android.hardware.Camera
 *  android.hardware.Camera$CameraInfo
 *  android.hardware.Camera$Parameters
 *  android.hardware.Camera$Size
 *  android.os.Build
 *  android.os.Build$VERSION
 *  android.util.AttributeSet
 *  android.util.DisplayMetrics
 *  android.util.Log
 *  android.view.Display
 *  android.view.SurfaceHolder
 *  android.view.SurfaceHolder$Callback
 *  android.view.SurfaceView
 *  android.view.View
 *  android.view.View$MeasureSpec
 *  android.view.ViewGroup
 *  android.view.ViewParent
 *  android.view.WindowManager
 */
package org.odk.collect.android.augmentedreality.arkit;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.io.IOException;
import java.util.List;

public class PARCameraView
extends SurfaceView
implements SurfaceHolder.Callback {
    private static final String TAG = "PARCameraView";
    private static final int PICTURE_SIZE_MAX_WIDTH = 1280;
    private static final int PREVIEW_SIZE_MAX_WIDTH = 640;
    private int cameraId = 0;
    private Camera camera;
    private SurfaceHolder surfaceHolder;
    private Camera.Size bestPreviewSize;
    private float displaySizeW = 16.0f;
    private float displaySizeH = 9.0f;
    private float resolvedAspectRatioW = this.displaySizeW;
    private float resolvedAspectRatioH = this.displaySizeH;
    private float parentWidth;
    private float parentHeight;
    private DisplayMetrics displaymetrics = new DisplayMetrics();

    public PARCameraView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public PARCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PARCameraView(Context context) {
        super(context);
    }

    public void onCreateView() {
        this.getHolder().addCallback((SurfaceHolder.Callback)this);
        Display display = ((WindowManager)this.getContext().getSystemService("window")).getDefaultDisplay();
        try {
            if (Build.VERSION.SDK_INT >= 17) {
                display.getRealMetrics(this.displaymetrics);
            } else {
                display.getMetrics(this.displaymetrics);
            }
            this.parentWidth = this.displaymetrics.widthPixels;
            this.parentHeight = this.displaymetrics.heightPixels;
        }
        catch (Exception e) {
            e.printStackTrace();
            this.parentWidth = 1280.0f;
            this.parentHeight = 1280.0f;
        }
    }

    public Point getViewSize() {
        return new Point((int)this.parentWidth, (int)this.parentHeight);
    }

    public void onResume() {
        try {
            this.camera = Camera.open((int)this.cameraId);
            this.startCameraPreview();
        }
        catch (Exception exception) {
            Log.e((String)"PARCameraView", (String)("Can't open camera with id " + this.cameraId), (Throwable)exception);
            return;
        }
    }

    public void onPause() {
        try {
            if (this.camera != null) {
                this.stopCameraPreview();
                this.camera.release();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized void startCameraPreview() {
        this.determineDisplayOrientation();
        this.setupCamera();
        try {
            this.camera.setPreviewDisplay(this.surfaceHolder);
            this.camera.startPreview();
        }
        catch (IOException exception) {
            Log.e((String)"PARCameraView", (String)"Can't start camera preview due to IOException", (Throwable)exception);
        }
        catch (NullPointerException npe) {
            npe.printStackTrace();
        }
    }

    private synchronized void stopCameraPreview() {
        try {
            this.camera.stopPreview();
        }
        catch (Exception exception) {
            Log.i((String)"PARCameraView", (String)"Exception during stopping camera preview");
        }
    }

    public void determineDisplayOrientation() {
        int displayOrientation;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo((int)this.cameraId, (Camera.CameraInfo)cameraInfo);
        int rotation = ((WindowManager)this.getContext().getSystemService("window")).getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case 0: {
                this.resolvedAspectRatioW = this.displaySizeW;
                this.resolvedAspectRatioH = this.displaySizeH;
                this.parentWidth = this.displaymetrics.widthPixels;
                this.parentHeight = this.displaymetrics.heightPixels;
                degrees = 0;
                break;
            }
            case 1: {
                this.resolvedAspectRatioW = this.displaySizeH;
                this.resolvedAspectRatioH = this.displaySizeW;
                this.parentWidth = this.displaymetrics.heightPixels;
                this.parentHeight = this.displaymetrics.widthPixels;
                degrees = 90;
                break;
            }
            case 2: {
                this.resolvedAspectRatioW = this.displaySizeW;
                this.resolvedAspectRatioH = this.displaySizeH;
                this.parentWidth = this.displaymetrics.widthPixels;
                this.parentHeight = this.displaymetrics.heightPixels;
                degrees = 180;
                break;
            }
            case 3: {
                this.resolvedAspectRatioW = this.displaySizeH;
                this.resolvedAspectRatioH = this.displaySizeW;
                this.parentWidth = this.displaymetrics.heightPixels;
                this.parentHeight = this.displaymetrics.widthPixels;
                degrees = 270;
            }
        }
        if (cameraInfo.facing == 1) {
            displayOrientation = (cameraInfo.orientation + degrees) % 360;
            displayOrientation = (360 - displayOrientation) % 360;
        } else {
            displayOrientation = (cameraInfo.orientation - degrees + 360) % 360;
        }
        if (this.camera != null) {
            this.camera.setDisplayOrientation(displayOrientation);
        }
    }

    public void setupCamera() {
        if (this.camera != null) {
            Camera.Parameters parameters = this.camera.getParameters();
            this.bestPreviewSize = this.determineBestPreviewSize(parameters);
            Camera.Size bestPictureSize = this.determineBestPictureSize(parameters);
            parameters.setPreviewSize(this.bestPreviewSize.width, this.bestPreviewSize.height);
            parameters.setPictureSize(bestPictureSize.width, bestPictureSize.height);
            this.camera.setParameters(parameters);
        }
    }

    private Camera.Size determineBestPreviewSize(Camera.Parameters parameters) {
        List sizes = parameters.getSupportedPreviewSizes();
        return this.determineBestSize(sizes, 640);
    }

    private Camera.Size determineBestPictureSize(Camera.Parameters parameters) {
        List sizes = parameters.getSupportedPictureSizes();
        return this.determineBestSize(sizes, 1280);
    }

    protected Camera.Size determineBestSize(List<Camera.Size> sizes, int widthThreshold) {
        Camera.Size bestSize = null;
        for (Camera.Size currentSize : sizes) {
            boolean isInBounds;
            boolean isDesiredRatio = (float)currentSize.width / this.resolvedAspectRatioW == (float)currentSize.height / this.resolvedAspectRatioH;
            boolean isBetterSize = bestSize == null || currentSize.width > bestSize.width;
            boolean bl = isInBounds = currentSize.width <= 1280;
            if (!isDesiredRatio || !isInBounds || !isBetterSize) continue;
            bestSize = currentSize;
        }
        if (bestSize == null) {
            return sizes.get(0);
        }
        return bestSize;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        float originalWidth = MeasureSpec.getSize((int)((int)this.parentWidth));
        float originalHeight = MeasureSpec.getSize((int)((int)this.parentHeight));
        float width = originalWidth;
        float height = originalHeight;
        float parentWidth = ((ViewGroup)this.getParent()).getMeasuredWidth();
        float parentHeight = ((ViewGroup)this.getParent()).getMeasuredHeight();
        if (width > height * this.getResolvedAspectRatio()) {
            width = height / this.getResolvedAspectRatio() + 0.5f;
        } else {
            height = width * this.getResolvedAspectRatio() + 0.5f;
        }
        this.setX((parentWidth - width) * 0.5f);
        this.setY((parentHeight - height) * 0.5f);
        this.setMeasuredDimension((int)width, (int)height);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        this.surfaceHolder = holder;
        try {
            this.startCameraPreview();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    private float getResolvedAspectRatio() {
        return this.resolvedAspectRatioW / this.resolvedAspectRatioH;
    }
}


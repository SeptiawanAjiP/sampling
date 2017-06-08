/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.app.Activity
 *  android.content.Context
 *  android.content.res.Resources
 *  android.graphics.PointF
 *  android.renderscript.Matrix4f
 *  android.util.AttributeSet
 *  android.util.DisplayMetrics
 *  android.util.TypedValue
 *  android.view.ViewGroup
 *  android.view.ViewGroup$LayoutParams
 *  android.view.ViewGroup$MarginLayoutParams
 *  android.view.ViewParent
 *  android.widget.RelativeLayout
 *  com.dopanic.panicsensorkit.PSKDeviceAttitude
 */
package org.odk.collect.android.augmentedreality.arkit;

import android.content.Context;
import android.graphics.PointF;
import android.renderscript.Matrix4f;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import org.odk.collect.android.augmentedreality.sensorkit.PSKDeviceAttitude;

import java.util.ListIterator;
import java.util.Timer;
import java.util.TimerTask;

public class PARRadarView
extends RelativeLayout {
    private final String TAG = "PARRadarView";
    public static final int RADAR_MODE_THUMBNAIL = 1;
    public static final int RADAR_MODE_FULLSCREEN = 2;
    public static final float RADAR_RANGE_DEFAULT = 1000.0f;
    public static final int RADAR_SIZE_DEFAULT = 256;
    public static final long RENDER_RADAR_TIME_INTERVAL = 66;
    public static final int RENDER_RADAR_TIME_DELAY = 10;
    protected static PARRadarView activeView;
    protected float radarRange;
    protected int radarMode;
    protected float radarInset = 32.0f;
    protected int radarRadius = -1;
    protected Matrix4f radarMatrix;
    protected PARFragment arViewController;
    protected Timer renderTimer;
    private PointF center;
    protected PointF fullscreenMargin;
    protected float fullscreenSizeOffset;

    public PARRadarView(Context context) {
        super(context);
        this.init();
    }

    public PARRadarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public PARRadarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.init();
    }

    private void init() {
        this.radarRange = 1000.0f;
        this.radarMode = -1;
        this.radarRadius = -1;
        this.radarMatrix = new Matrix4f();
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = ((ViewGroup)this.getParent()).getMeasuredWidth();
        int height = ((ViewGroup)this.getParent()).getMeasuredHeight();
        MarginLayoutParams params = (MarginLayoutParams)this.getLayoutParams();
        if (this.radarMode == 1) {
            int size = (int)TypedValue.applyDimension((int)1, (float)150.0f, (DisplayMetrics)this.getResources().getDisplayMetrics());
//            int x = width - size - params.rightMargin;
//            int y = height - size - params.bottomMargin;
            int x = params.leftMargin;
            int y = height - size - params.bottomMargin;
            this.setX((float)x);
            this.setY((float)y);
            params.width = size;
            params.height = size;
            this.setLayoutParams((ViewGroup.LayoutParams)params);
        } else if (this.radarMode == 2) {
            width = (int)((float)width + this.fullscreenMargin.x);
            height = (int)((float)height + this.fullscreenMargin.y);
            int size = Math.min(width, height) - (int)this.fullscreenSizeOffset;
            float x = (width - size) / 2;
            float y = (height - size) / 2;
            this.setX(x);
            this.setY(y);
            params.width = size;
            params.height = size;
            this.setLayoutParams((ViewGroup.LayoutParams)params);
        }
        this.setMeasuredDimension(params.width, params.height);
    }

    private void start(PARFragment arViewController) {
        this.arViewController = arViewController;
        PARRadarView.setActiveView(this);
        this.renderTimer = new Timer("PARRadarViewTimer");
        this.renderTimer.schedule(new TimerTask(){

            @Override
            public void run() {
                PARRadarView.this.drawRadar();
            }
        }, 10, 66);
    }

    public void stop() {
        this.renderTimer.cancel();
        this.renderTimer.purge();
        this.renderTimer = null;
        PARRadarView.setActiveView(null);
    }

    public void showRadarInMode(int radarMode, PARFragment controller) {
        this.radarMode = radarMode;
        switch (this.radarMode) {
            case 2: {
                this.setRadarToFullscreen();
                break;
            }
            case 1: {
                this.setRadarToThumbnail();
                break;
            }
            default: {
                this.hideRadar();
            }
        }
        this.start(controller);
        this.setVisibility(VISIBLE);
    }

    public void hideRadar() {
        this.setVisibility(GONE);
        this.stop();
    }

    public void drawRadar() {
        float[] g = PSKDeviceAttitude.sharedDeviceAttitude().getNormalizedGravity();
        PARPoi.setDeviceGravity(g);
        this.radarMatrix.load(PSKDeviceAttitude.sharedDeviceAttitude().getHeadingMatrix());
        this.radarRadius = this.getHeight() < this.getWidth() ? this.getHeight() / 2 : this.getWidth() / 2;
        this.center = new PointF((float)this.radarRadius, (float)this.radarRadius);
        ListIterator<PARPoi> it = PARController.getPois().listIterator();
        final PARRadarView r = this;
        while (it.hasNext()) {
            final PARPoi poi = it.next();
            if (poi.isHidden || poi.isClippedByDistance) continue;
            this.arViewController.getActivity().runOnUiThread(new Runnable(){

                @Override
                public void run() {
                    poi.renderInRadar(r);
                }
            });
        }
        this.refreshDrawableState();
    }

    public static PARRadarView getActiveView() {
        return activeView;
    }

    public Matrix4f getRadarMatrix() {
        return this.radarMatrix;
    }

    public boolean isRadarVisible() {
        return this.getVisibility() == VISIBLE;
    }

    public int getRadarMode() {
        return this.radarMode;
    }

    public float getRadarRange() {
        return this.radarRange;
    }

    public float getRadarInset() {
        return this.radarInset;
    }

    public PointF getCenter() {
        return this.center;
    }

    public float getRadarRadiusForRendering() {
        return (float)this.radarRadius - this.radarInset;
    }

    public int getRadarRadius() {
        return this.radarRadius;
    }

    private static void setActiveView(PARRadarView activeView) {
          PARRadarView.activeView = activeView;
    }

    public void setRadarToFullscreen() {
        this.setRadarToFullscreen(new PointF(0.0f, 0.0f), 0.0f);
    }

    public void setRadarToFullscreen(PointF offset, float sizeOffset) {
        this.fullscreenMargin = offset;
        this.fullscreenSizeOffset = sizeOffset;
        this.radarMode = 2;
        this.requestLayout();
    }

    public void setRadarToThumbnail() {
        this.radarMode = 1;
        this.requestLayout();
    }

    public void setRadarRange(float range) {
        this.radarRange = range;
    }

    public void setRadarInset(float inset) {
        this.radarInset = inset;
    }

    public void setRadarRadius(int radius) {
        this.radarRadius = radius;
    }

}


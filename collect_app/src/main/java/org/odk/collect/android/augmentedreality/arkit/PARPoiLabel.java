/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  android.content.res.Resources
 *  android.graphics.Point
 *  android.graphics.PointF
 *  android.location.Location
 *  android.util.DisplayMetrics
 *  android.util.Log
 *  android.util.TypedValue
 *  android.view.LayoutInflater
 *  android.view.View
 *  android.view.View$OnClickListener
 *  android.view.ViewGroup
 *  android.view.ViewGroup$LayoutParams
 *  android.widget.ImageView
 *  android.widget.RelativeLayout
 *  android.widget.TextView
 */
package org.odk.collect.android.augmentedreality.arkit;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.PointF;
import android.location.Location;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DecimalFormat;

public class PARPoiLabel
extends PARPoi {
    protected static final DecimalFormat FORMATTER_DISTANCE_LARGEST = new DecimalFormat("#### km");
    protected static final DecimalFormat FORMATTER_DISTANCE_LARGE = new DecimalFormat("###,## km");
    protected static final DecimalFormat FORMATTER_DISTANCE_SMALL = new DecimalFormat("### m");
    protected final float SMALL_DISTANCE_INTERVAL = 5.0f;
    protected final float LARGE_DISTANCE_INTERVAL = 1000.0f;
    protected static Point defaultSize = new Point(256, 128);
    protected Point size = null;
    protected boolean hasCreatedView;
    protected int layoutId;
    private String TAG = "PARPoiLabel";
    protected String _title;
    protected String _description;
    protected String _distance;
    protected int _iconImageViewResource = -1;
    protected TextView distanceTextView;
    protected TextView altitudeTextView;
    protected TextView titleTextView;
    protected TextView descriptionTextView;
    protected ImageView iconImageView;
    private View.OnClickListener onClickListener;
    protected float _lastUpdateAtDistance;
    protected boolean isAltitudeEnabled = false;
    protected Point offset = new Point();

    protected String pathFoto;

    public PARPoiLabel() {
    }

    public PARPoiLabel(Location location, String title, int layoutId, int radarResourceId) {
        super(location);
        this._title = title;
        this.offset.set(0, 0);
        this.layoutId = layoutId;
        this.radarResourceId = radarResourceId;
    }

    public PARPoiLabel(Location location, String title, String description, int layoutId, int radarResourceId) {
        super(location);
        this._title = title;
        this._description = description;
        this.offset.set(0, 0);
        this.layoutId = layoutId;
        this.radarResourceId = radarResourceId;
    }

    public PARPoiLabel(Location location, String title, String description, int layoutId, int radarResourceId,String pathFoto) {
        super(location);
        this._title = title;
        this._description = description;
        this.offset.set(0, 0);
        this.layoutId = layoutId;
        this.radarResourceId = radarResourceId;
        this.pathFoto = pathFoto;
    }

    public PARPoiLabel(Location atLocation) {
        super(atLocation);
    }

    public static Point getDefaultSize() {
        return defaultSize;
    }

    public static void setDefaultSize(Point defaultSize) {
        PARPoiLabel.defaultSize = defaultSize;
    }

    public String getTitle() {
        return this._title;
    }

    public void setTitle(String title) {
        if (this._title != title) {
            this._title = title;
            if (this.titleTextView != null) {
                this.titleTextView.setText((CharSequence)this._title);
            }
        }
    }

    public String getDescription() {
        return this._description;
    }

    public void setDescription(String description) {
        if (this._description != description) {
            this._description = description;
            if (this.descriptionTextView != null) {
                this.descriptionTextView.setText((CharSequence)this._description);
            }
        }
    }

//    public int getIconImageViewResource() {
//        return this._iconImageViewResource;
//    }
//
//    public void setIconImageViewResource(int iconImageViewResource) {
//        this._iconImageViewResource = iconImageViewResource;
//        if (this.iconImageView != null) {
//            if (this._iconImageViewResource > -1) {
//                this.iconImageView.setImageResource(R.drawable.ic_placeholder);
//                this.iconImageView.setVisibility(View.VISIBLE);
//            } else {
//                this.iconImageView.setVisibility(View.GONE);
//            }
//        }
//    }

    public boolean getIsAltitudeEnabled() {
        return this.isAltitudeEnabled;
    }

    public void setIsAltitudeEnabled(boolean isAltitudeEnabled) {
        this.isAltitudeEnabled = isAltitudeEnabled;
    }

    @Override
    public Point getOffset() {
        return this.offset;
    }

    public void setOffset(Point leftTop) {
        this.offset = leftTop;
    }

    @Override
    public void createView() {
        if (this.ctx == null) {
            Log.e((String)this.TAG, (String)"context is NULL");
            return;
        }
        LayoutInflater inflater = (LayoutInflater)this.ctx.getSystemService("layout_inflater");
        if (inflater == null) {
            Log.e((String)this.TAG, (String)"Layout inflater is null");
            return;
        }
        this._labelView = (RelativeLayout)inflater.inflate(this.layoutId, null);
        if (this.onClickListener != null) {
            this._labelView.setOnClickListener(this.onClickListener);
        }
        if (this.size == null) {
            this.size = new Point(PARPoiLabel.defaultSize.x, PARPoiLabel.defaultSize.y);
        }
        Resources r = this._labelView.getResources();
        int width = (int)TypedValue.applyDimension((int)1, (float)this.size.x, (DisplayMetrics)r.getDisplayMetrics());
        int height = (int)TypedValue.applyDimension((int)1, (float)this.size.y, (DisplayMetrics)r.getDisplayMetrics());
        this._labelView.setLayoutParams(new ViewGroup.LayoutParams(width, height));
        this.halfSizeOfView = new PointF((float)(width / 2), (float)(height / 2));
        if (this._backgroundImageResource > -1) {
            this._labelView.setBackgroundResource(this._backgroundImageResource);
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;



        this.titleTextView = (TextView)this._labelView.findViewWithTag((Object)"title");
        this.titleTextView.setText((CharSequence)this._title);
        this.descriptionTextView = (TextView)this._labelView.findViewWithTag((Object)"description");
        this.descriptionTextView.setText((CharSequence)this._description);
        this.distanceTextView = (TextView)this._labelView.findViewWithTag((Object)"distance");
        this.iconImageView = (ImageView)this._labelView.findViewWithTag((Object)"icon");

        if(!pathFoto.equals("")){
            final Bitmap bitmap = BitmapFactory.decodeFile(pathFoto, options);
            iconImageView.setImageBitmap(bitmap);
        }

//        this.setIconImageViewResource(this._iconImageViewResource);
        this.altitudeTextView = (TextView)this._labelView.findViewWithTag((Object)"altitude");
        if (this.altitudeTextView != null) {
            if (this.isAltitudeEnabled) {
                this.altitudeTextView.setVisibility(View.VISIBLE);
            } else {
                this.altitudeTextView.setVisibility(View.GONE);
            }
        }
        this.hasCreatedView = true;
        this.updateContent();
    }

    @Override
    public void updateContent() {
        if (!this.hasCreatedView) {
            return;
        }
        double distance = this.distanceToUser;
        if (distance >= 10000.0) {
            if (Math.abs(distance - (double)this._lastUpdateAtDistance) < 1000.0) {
                return;
            }
            distance = Math.floor(distance / 1000.0);
            this._distance = FORMATTER_DISTANCE_LARGEST.format(distance);
        } else if (distance > 1000.0) {
            if (Math.abs(distance - (double)this._lastUpdateAtDistance) < 100.0) {
                return;
            }
            distance = Math.floor(distance / 1000.0);
            this._distance = FORMATTER_DISTANCE_LARGE.format(distance);
        } else {
            if (Math.abs(distance - (double)this._lastUpdateAtDistance) < 10.0) {
                return;
            }
            distance = Math.floor(distance / 5.0) * 5.0;
            this._distance = FORMATTER_DISTANCE_SMALL.format(distance);
        }
        if (this.distanceTextView != null) {
            this.distanceTextView.setText((CharSequence)(this._distance + " away"));
        }
        this._lastUpdateAtDistance = (float)this.distanceToUser;
    }

    public Point getSize() {
        return this.size;
    }

    public void setSize(Point size) {
        this.size = size;
    }

    public void setSize(int w, int h) {
        this.size = new Point(w, h);
    }

    public View.OnClickListener getOnClickListener() {
        return this.onClickListener;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        if (this._labelView != null) {
            this._labelView.setOnClickListener(onClickListener);
        }
    }
}


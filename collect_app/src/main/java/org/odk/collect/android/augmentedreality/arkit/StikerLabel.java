package org.odk.collect.android.augmentedreality.arkit;

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


import org.odk.collect.android.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Septiawan Aji Pradan on 5/21/2017.
 */

public class StikerLabel extends PARPoi {
    private TextView atasTv;
    private TextView kolom1Tv;
    private TextView kolom2Tv;
    private TextView kolom3Tv;
    private TextView jarakTv;
    private TextView judulKolom1,judulKolom2,judulKolom3;
    private ImageView fotoBangunan;
    private int layoutId;
    private ArrayList<String> keterangan;
    private ArrayList<String> key;
    protected static Point defaultSize = new Point(220, 60);
    private View.OnClickListener onClickListener;
    protected Point size = null;
    protected boolean hasCreatedView;
    protected float _lastUpdateAtDistance;
    protected boolean isAltitudeEnabled = false;
    protected static final DecimalFormat FORMATTER_DISTANCE_LARGEST = new DecimalFormat("#### km");
    protected static final DecimalFormat FORMATTER_DISTANCE_LARGE = new DecimalFormat("###,## km");
    protected static final DecimalFormat FORMATTER_DISTANCE_SMALL = new DecimalFormat("### m");

    public StikerLabel(){

    }

    public StikerLabel(ArrayList<String> key,Location location, ArrayList<String> keterangan, int layoutId, int radarResourceId){
        super(location);
        Log.d("cinta_stiker_ket",keterangan.toString());
        this.keterangan = keterangan;
        this.layoutId = layoutId;
        this.radarResourceId = radarResourceId;
        this.offset.set(0,0);
        this.key  = key;
    }

    public StikerLabel(Location location){
        super(location);
    }

    public static Point getDefaultSize() {
        return defaultSize;
    }

    public static void setDefaultSize(Point defaultSize) {
        PARPoiLabel.defaultSize = defaultSize;
    }

    @Override
    protected Point getOffset() {
        return this.getOffset();
    }

    public void setOffset(Point leftTop) {
        this.offset = leftTop;
    }

    @Override
    public void createView() {
        Log.d("array",keterangan.toString());
        if(this.ctx == null){
            return;
        }

        LayoutInflater inflater = (LayoutInflater)this.ctx.getSystemService("layout_inflater");
        if(inflater == null){
            return;
        }

        this._labelView = (RelativeLayout)inflater.inflate(this.layoutId,null);
        if (this.onClickListener != null) {
            this._labelView.setOnClickListener(this.onClickListener);
        }

        if (this.size == null) {
            this.size = new Point(StikerLabel.defaultSize.x, StikerLabel.defaultSize.y);
        }

        Resources r = this._labelView.getResources();
        int width = (int) TypedValue.applyDimension((int)1, (float)this.size.x, (DisplayMetrics)r.getDisplayMetrics());
        int height = (int)TypedValue.applyDimension((int)1, (float)this.size.y, (DisplayMetrics)r.getDisplayMetrics());
        this._labelView.setLayoutParams(new ViewGroup.LayoutParams(width, height));
        this.halfSizeOfView = new PointF((float)(width / 2), (float)(height / 2));
        if (this._backgroundImageResource > -1) {
            this._labelView.setBackgroundResource(this._backgroundImageResource);
        }



        this.atasTv= (TextView)this._labelView.findViewById(R.id.tv_atas_scan);
        this.kolom1Tv= (TextView)this._labelView.findViewById(R.id.tv_kolom_1_scan);
        this.kolom2Tv = (TextView)this._labelView.findViewById(R.id.tv_kolom_2_scan);
        this.kolom3Tv = (TextView)this._labelView.findViewById(R.id.tv_kolom_3_scan);
        this.jarakTv = (TextView)this._labelView.findViewById(R.id.tv_jarak);

        this.judulKolom1 = (TextView)this._labelView.findViewById(R.id.judul_kolom_1);
        this.judulKolom2 = (TextView)this._labelView.findViewById(R.id.judul_kolom_2);
        this.judulKolom3 = (TextView)this._labelView.findViewById(R.id.judul_kolom_3);
        this.fotoBangunan = (ImageView)this._labelView.findViewById(R.id.foto_bangunan);

        atasTv.setText(keterangan.get(2));
        kolom1Tv.setText(keterangan.get(3));
        kolom2Tv.setText(keterangan.get(4));
        kolom3Tv.setText(keterangan.get(5));

        judulKolom1.setText(key.get(1));
        judulKolom2.setText(key.get(2));
        judulKolom3.setText(key.get(3));

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        final Bitmap bitmap = BitmapFactory.decodeFile(keterangan.get(0), options);
        this.fotoBangunan.setImageBitmap(bitmap);


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
            this.keterangan.set(1,FORMATTER_DISTANCE_LARGEST.format(distance));
        } else if (distance > 1000.0) {
            if (Math.abs(distance - (double)this._lastUpdateAtDistance) < 100.0) {
                return;
            }
            distance = Math.floor(distance / 1000.0);
            this.keterangan.set(1,FORMATTER_DISTANCE_LARGE.format(distance));
        } else {
            if (Math.abs(distance - (double)this._lastUpdateAtDistance) < 10.0) {
                return;
            }
            distance = Math.floor(distance / 5.0) * 5.0;
            this.keterangan.set(1,FORMATTER_DISTANCE_SMALL.format(distance));;
        }
        if (this.jarakTv != null) {
            this.jarakTv.setText((CharSequence)(this.keterangan.get(1)));
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

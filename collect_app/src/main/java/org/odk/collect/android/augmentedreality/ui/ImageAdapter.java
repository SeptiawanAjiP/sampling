package org.odk.collect.android.augmentedreality.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import org.odk.collect.android.R;

/**
 * Created by Septiawan Aji Pradan on 6/7/2017.
 */

public class ImageAdapter extends BaseAdapter {
    private Context context;

    public Integer[] gambar = {
            R.drawable.img_rumah,
            R.drawable.img_ar,
            R.drawable.ic_input_data,
            R.drawable.img_rumah,
            R.drawable.img_rumah
    };

    public ImageAdapter(Context context){
        this.context = context;
    }

    @Override
    public int getCount() {
        return gambar.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View grid;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            grid = inflater.inflate(R.layout.grid_view_halaman_utama, null);
        } else {
            grid = (View) convertView;
        }

        ImageView imageView = (ImageView)grid.findViewById(R.id.foto_bangunan_hal_utama);
        imageView.setImageResource(gambar[position]);

        return grid;
    }
}

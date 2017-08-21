package org.odk.collect.android.gede.listsampel;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.odk.collect.android.R;
import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Septiawan Aji Pradan on 4/21/2017.
 */

public class SampelAdapter extends RecyclerView.Adapter<SampelAdapter.MyViewHolder>{

    private Activity activity;
    private ArrayList<String> keterangan;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView keteranganView,nomorView;
        public CardView cardView;
        public MyViewHolder(View view){
            super(view);
            keteranganView = (TextView)view.findViewById(R.id.keterangan);
            nomorView = (TextView)view.findViewById(R.id.nomor);
            cardView = (CardView) view.findViewById(R.id.card);
        }
    }

    public SampelAdapter(ArrayList<String> keterangan, Activity activity){
        this.keterangan= keterangan;
        this.activity = activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_sampel,null);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String order = keterangan.get(position);

        holder.nomorView.setText(Integer.toString(position+1));
        holder.keteranganView.setText(order);

    }

    @Override
    public int getItemCount() {
        return keterangan.size();
    }


}

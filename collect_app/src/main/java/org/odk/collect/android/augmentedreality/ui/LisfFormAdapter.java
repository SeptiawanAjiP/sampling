package org.odk.collect.android.augmentedreality.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;

import org.odk.collect.android.R;
import org.odk.collect.android.augmentedreality.aksesdata.Form;

import java.util.ArrayList;

/**
 * Created by Septiawan Aji Pradan on 6/4/2017.
 */

public class LisfFormAdapter extends RecyclerView.Adapter<LisfFormAdapter.MyViewHolder>{
    private ArrayList<Form> forms;
    private Context context;

    public ImageLoader imageLoader;

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView namaForm,totalIsian;
        public MyViewHolder(View view){
            super(view);
            namaForm = (TextView) view.findViewById(R.id.nama_form);
            totalIsian = (TextView) view.findViewById(R.id.total_isian);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Yoia", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public LisfFormAdapter(ArrayList<Form> forms){
        this.forms= forms;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_form_main,null);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Form form = forms.get(position);
//
        holder.namaForm.setText(form.getDisplayName());
        holder.totalIsian.setText(""+form.getTotalIsian());
    }

    @Override
    public int getItemCount() {
        return forms.size();
    }
}
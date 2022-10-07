package com.entrolabs.apsacs.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.entrolabs.apsacs.Bean.TwoBean;
import com.entrolabs.apsacs.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class BeneficiaryAdapter extends RecyclerView.Adapter<BeneficiaryAdapter.BeneficiaryViewHolder>{
    ArrayList<HashMap<String,String>> arrayList;
    Context context;
    OnItemClickListner onItemClickListner;

    public BeneficiaryAdapter(ArrayList<HashMap<String, String>> arrayList, Context context, OnItemClickListner onItemClickListner) {
        this.arrayList = arrayList;
        this.context = context;
        this.onItemClickListner = onItemClickListner;
    }

    @NonNull
    @Override
    public BeneficiaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.beneficiary_item, parent, false);
        return new BeneficiaryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BeneficiaryViewHolder holder, int position) {
        HashMap<String,String> hm = arrayList.get(position);
        String s = hm.get("name").substring(0,1);
        holder.f_letter.setText(s.toUpperCase(Locale.ROOT));
        holder.name.setText(hm.get("name"));
        holder.HRG_TI.setText(hm.get("hrg_ti_id_no"));
        //holder.gender.setText(hm.get("gender"));
        if (hm.get("gender").equals("m") || hm.get("gender").equals("1")){
            holder.gender.setText("Male");
        }else if (hm.get("gender").equals("f")|| hm.get("gender").equals("2")){
            holder.gender.setText("Female");
        }else if (hm.get("gender").equals("t")|| hm.get("gender").equals("3")) {
            holder.gender.setText("Transgender");
        }
        holder.soch_id.setText(hm.get("soch_uid"));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListner.onItemClick(hm);
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList==null?0:arrayList.size();
    }

    public class BeneficiaryViewHolder extends RecyclerView.ViewHolder{
        AppCompatTextView name,HRG_TI,gender,soch_id,f_letter;
        public BeneficiaryViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.TV_name);
            HRG_TI = itemView.findViewById(R.id.TV_HRG_TI_ID);
            gender = itemView.findViewById(R.id.TV_Gender);
            soch_id = itemView.findViewById(R.id.TV_Soch_Id);
            f_letter = itemView.findViewById(R.id.f_letter);
        }
    }

    public interface OnItemClickListner {
        void onItemClick(HashMap<String,String> hm);
    }
}

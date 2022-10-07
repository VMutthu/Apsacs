package com.entrolabs.apsacs.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.entrolabs.apsacs.Bean.TwoBean;
import com.entrolabs.apsacs.R;


import java.util.ArrayList;

public class SelectionAdapter2 extends RecyclerView.Adapter< SelectionAdapter2.ViewHolder> {
    ArrayList<TwoBean> stringArrayList;
    Context context;
    CLickCallBack2 cLickCallBack2;
    String Selection;

    public SelectionAdapter2(ArrayList<TwoBean> sting_arraylist, Context context, String Selection, CLickCallBack2 cLickCallBack2) {
        this.stringArrayList = sting_arraylist;
        this.context = context;
        this.cLickCallBack2 = cLickCallBack2;
        this.Selection = Selection;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_selection2, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.Tvname.setText(stringArrayList.get(position).getName());
        holder.CardSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cLickCallBack2.onClick(stringArrayList.get(position));
                if(Selection.equalsIgnoreCase("counseling_list")){
                    holder.checked.setVisibility(View.VISIBLE);
                    holder.checked.setChecked(true);
                }else if(Selection.equalsIgnoreCase("complication_list")){
                    holder.checked.setChecked(true);
                    holder.checked.setVisibility(View.VISIBLE);
                }else if(Selection.equalsIgnoreCase("highrisk_list")){
                    holder.checked.setChecked(true);
                    holder.checked.setVisibility(View.VISIBLE);
                }else if(Selection.equalsIgnoreCase("vaccines")){
                    holder.checked.setChecked(true);
                    holder.checked.setVisibility(View.VISIBLE);
                }else if(Selection.equalsIgnoreCase("counselling")){
                    holder.checked.setChecked(true);
                    holder.checked.setVisibility(View.VISIBLE);
                }else if(Selection.equalsIgnoreCase("complications_mother")){

                    holder.checked.setChecked(true);
                    holder.checked.setVisibility(View.VISIBLE);
                }else if(Selection.equalsIgnoreCase("complications_child")){
                    holder.checked.setChecked(true);
                    holder.checked.setVisibility(View.VISIBLE);
                }
                else if(Selection.equalsIgnoreCase("attended_people")){
                    holder.checked.setChecked(true);
                    holder.checked.setVisibility(View.VISIBLE);
                }
                else if(Selection.equalsIgnoreCase("discussed_topics")){
                    holder.checked.setChecked(true);
                    holder.checked.setVisibility(View.VISIBLE);
                }else if(Selection.equalsIgnoreCase("child_vaccines")){
                    holder.checked.setChecked(true);
                    holder.checked.setVisibility(View.VISIBLE);
                }else if(Selection.equalsIgnoreCase("prev_complications")){
                    holder.checked.setChecked(true);
                    holder.checked.setVisibility(View.VISIBLE);
                }else if(Selection.equalsIgnoreCase("other_tests")){
                    holder.checked.setChecked(true);
                    holder.checked.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return stringArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView Tvname;
        CardView CardSelection;
        CheckBox checked;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Tvname = itemView.findViewById(R.id.Tvname);
            CardSelection =  itemView.findViewById(R.id.CardSelection);

        }
    }
}
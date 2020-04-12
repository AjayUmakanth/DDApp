package com.example.depressiondetection;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    ArrayList<TestResult> data;

    CustomAdapter(ArrayList<TestResult>  data)
    {
        this.data=data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card, parent,false);
        return  new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TestResult currentResult = data.get(position);
        holder.id.setText("Test Id : "+currentResult.getId());
        holder.date.setText("Date : "+currentResult.getDate());
        holder.time.setText("Time : "+currentResult.getTime());
        holder.qres.setText("Questionnaire Result : "+currentResult.getQres());
        holder.mres.setText("Model Result : "+currentResult.getMres());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView id,date,time,qres,mres;
        public ViewHolder(View itemView) {
            super(itemView);
            id=itemView.findViewById(R.id.id);
            date=itemView.findViewById(R.id.date);
            time=itemView.findViewById(R.id.time);
            qres=itemView.findViewById(R.id.qres);
            mres=itemView.findViewById(R.id.mres);
        }
    }
}

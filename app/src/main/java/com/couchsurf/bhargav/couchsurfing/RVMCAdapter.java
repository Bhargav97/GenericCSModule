package com.couchsurf.bhargav.couchsurfing;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.couchsurf.bhargav.couchsurfing.R;

import java.util.Collections;
import java.util.List;

public class RVMCAdapter extends RecyclerView.Adapter<RVMCAdapter.MyViewHolder> {
    private RecyclerViewClickListener mListener;
    private LayoutInflater inflater;
    List<RVManageCouch> data = Collections.emptyList();

    public RVMCAdapter(Context context, List<RVManageCouch> data, RecyclerViewClickListener listener) {
        inflater = LayoutInflater.from(context);

        this.data = data;
        mListener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.custom_row_mc, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(v, mListener);
        return myViewHolder;
    }



    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        RVManageCouch current = data.get(position);
        holder.couchId.setText(current.id);
        holder.nameText.setText(current.name);
        holder.locText.setText(current.loc);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView nameText, locText, couchId;
        private RecyclerViewClickListener mListener;

        public MyViewHolder(View itemView, RecyclerViewClickListener listener) {
            super(itemView);
            mListener = listener;
            couchId = itemView.findViewById(R.id.couchId);
            nameText = itemView.findViewById(R.id.nameText);
            locText = itemView.findViewById(R.id.locText);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mListener.onClick(view, getAdapterPosition());
        }
    }
}

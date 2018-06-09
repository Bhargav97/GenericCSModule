package com.couchsurf.bhargav.couchsurfing;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;

import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RVBDAdapter extends RecyclerView.Adapter<RVBDAdapter.MyViewHolder> {
    private RecyclerViewClickListener mListener;
    private LayoutInflater inflater;
    List<RVBookingDisplay> data = Collections.emptyList();

    public RVBDAdapter(Context context, List<RVBookingDisplay> data, RecyclerViewClickListener listener) {
        inflater = LayoutInflater.from(context);
        this.data = data;
        mListener = listener;
    }

    @NonNull
    @Override
    public RVBDAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.custom_row_bd, parent, false);
        RVBDAdapter.MyViewHolder myViewHolder = new RVBDAdapter.MyViewHolder(v, mListener);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RVBDAdapter.MyViewHolder holder, int position) {
        RVBookingDisplay current = data.get(position);
        Glide.with(holder.nameText.getContext()).load(current.url).apply(new RequestOptions().placeholder(R.drawable.ic_person).signature(new ObjectKey(System.currentTimeMillis()))).into(holder.hostPic);
        holder.accText.setText("  " + current.accFor);
        holder.GRid.setText("ID# " + current.GRid);
        holder.fromDate.setText(current.fromDate);
        holder.toDate.setText(current.toDate);
        holder.couchName.setText(current.couchName);
        holder.couchLoc.setText(current.couchLoc);
        holder.nameText.setText(current.hostName);


    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView nameText, accText, fromDate, toDate, couchName, GRid, couchLoc, newText;
        CircleImageView hostPic;
        private RecyclerViewClickListener mListener;

        public MyViewHolder(View itemView, RecyclerViewClickListener listener) {
            super(itemView);
            mListener = listener;
            hostPic = itemView.findViewById(R.id.hostPicBD);
            nameText = itemView.findViewById(R.id.hostNameTextBD);
            accText = itemView.findViewById(R.id.accDataBD);
            fromDate = itemView.findViewById(R.id.dateFromTextBD);
            toDate = itemView.findViewById(R.id.dateToTextBD);
            couchLoc = itemView.findViewById(R.id.couchLocTextBD);
            couchName = itemView.findViewById(R.id.couchNameTextBD);
            GRid = itemView.findViewById(R.id.globalRidBD);
            //newText = itemView.findViewById(R.id.newTextCR);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mListener.onClick(view, getAdapterPosition());
        }
    }
}

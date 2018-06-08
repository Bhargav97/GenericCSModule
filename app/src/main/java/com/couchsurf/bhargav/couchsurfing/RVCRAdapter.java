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

public class RVCRAdapter extends RecyclerView.Adapter<RVCRAdapter.MyViewHolder>{

    private RecyclerViewClickListener mListener;
    private LayoutInflater inflater;
    List<RVCouchReq> data = Collections.emptyList();

    public RVCRAdapter(Context context, List<RVCouchReq> data, RecyclerViewClickListener listener) {
        inflater = LayoutInflater.from(context);
        this.data = data;
        mListener = listener;
    }

    @NonNull
    @Override
    public RVCRAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.custom_row_cr, parent, false);
        RVCRAdapter.MyViewHolder myViewHolder = new RVCRAdapter.MyViewHolder(v, mListener);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RVCRAdapter.MyViewHolder holder, int position) {
        RVCouchReq current = data.get(position);
        Glide.with(holder.nameText.getContext()).load(current.url).apply(new RequestOptions().placeholder(R.drawable.ic_person).signature(new ObjectKey(System.currentTimeMillis()))).into(holder.guestPic);
        holder.nameText.setText(current.name);
        holder.accText.setText("  "+current.accFor);
        holder.GRid.setText("ID# "+current.GRid);
        holder.fromDate.setText(current.fromDate);
        holder.toDate.setText(current.toDate);
        holder.couchName.setText(current.couchName);
        holder.couchLoc.setText(current.couchLoc);
        if(current.hostSeen.trim().equals("false"))
            holder.newText.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView nameText, accText, fromDate, toDate, couchName, GRid, couchLoc, newText;
        CircleImageView guestPic;
        private RecyclerViewClickListener mListener;

        public MyViewHolder(View itemView, RecyclerViewClickListener listener) {
            super(itemView);
            mListener = listener;
            guestPic = itemView.findViewById(R.id.guestPic);
            nameText = itemView.findViewById(R.id.nameTextCR);
            accText = itemView.findViewById(R.id.accData);
            fromDate = itemView.findViewById(R.id.dateFromTextCR);
            toDate = itemView.findViewById(R.id.dateToTextCR);
            couchLoc = itemView.findViewById(R.id.couchLocTextCR);
            couchName = itemView.findViewById(R.id.couchNameTextCR);
            GRid = itemView.findViewById(R.id.globalRid);
            newText = itemView.findViewById(R.id.newTextCR);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mListener.onClick(view, getAdapterPosition());
        }
    }
}

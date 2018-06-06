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
import com.couchsurf.bhargav.couchsurfing.R;

import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RVEDAdapter extends RecyclerView.Adapter<RVEDAdapter.MyViewHolder> {
    private RecyclerViewClickListener mListener;
    private LayoutInflater inflater;
    List<RVExploreDisplay> data = Collections.emptyList();

    public RVEDAdapter(Context context, List<RVExploreDisplay> data, RecyclerViewClickListener listener) {
        inflater = LayoutInflater.from(context);
        this.data = data;
        mListener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.custom_row_ed, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(v, mListener);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        RVExploreDisplay current = data.get(position);
        Glide.with(holder.nameText.getContext()).load(current.url).apply(new RequestOptions().signature(new ObjectKey(System.currentTimeMillis()))).into(holder.ownerPic);
        holder.nameText.setText(current.name);
        holder.vacText.setText("Accomodation for: "+ Integer.toString(current.vacancyFor));
        holder.globalCidText.setText("ID# "+current.global_cid);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView nameText, vacText, globalCidText;
        CircleImageView ownerPic;
        private RecyclerViewClickListener mListener;

        public MyViewHolder(View itemView, RecyclerViewClickListener listener) {
            super(itemView);
            mListener = listener;
            ownerPic = itemView.findViewById(R.id.couchOwnerPic);
            nameText = itemView.findViewById(R.id.nameTextExplore);
            vacText = itemView.findViewById(R.id.vacancyData);
            globalCidText = itemView.findViewById(R.id.globalCid);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mListener.onClick(view, getAdapterPosition());
        }
    }
}

package com.zriton.pigeon.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zriton.pigeon.R;
import com.zriton.pigeon.data.model.ModelMessageRow;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by aditya on 21/10/16.
 */

public abstract class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.DataHolder> {

    private ArrayList<ModelMessageRow> mMessageRowArrayList;

    public abstract void messageClicked(String address);

    protected MessageAdapter() {
        mMessageRowArrayList = new ArrayList<>();
    }

    public void addMessages(ArrayList<ModelMessageRow> pMessageRowArrayList) {
        mMessageRowArrayList.clear();
        mMessageRowArrayList.addAll(pMessageRowArrayList);
        notifyDataSetChanged();
    }

    @Override
    public DataHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_message_item, parent, false);
        return new DataHolder(v);
    }

    @Override
    public void onBindViewHolder(DataHolder holder, int position) {
        final ModelMessageRow lModelMessageRow = mMessageRowArrayList.get(position);
        holder.tvName.setText(lModelMessageRow.getAddress() +" ("+String.valueOf(lModelMessageRow.getThreadCount())+")");
        holder.tvConversation.setText(lModelMessageRow.getBody());
        holder.tvDate.setText(lModelMessageRow.getDate());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View pView) {
                messageClicked(lModelMessageRow.getAddress());
            }
        });
    }


    @Override
    public int getItemCount() {
        return mMessageRowArrayList.size();
    }

    static class DataHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvName)
        TextView tvName;
        @BindView(R.id.tvConversation)
        TextView tvConversation;
        @BindView(R.id.tvDate)
        TextView tvDate;
        @BindView(R.id.ivAvatar)
        ImageView ivAvatar;
        @BindView(R.id.rowContainer)
        View mView;

        DataHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


}


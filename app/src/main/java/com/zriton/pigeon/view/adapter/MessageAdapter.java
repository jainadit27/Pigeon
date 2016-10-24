package com.zriton.pigeon.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zriton.pigeon.R;
import com.zriton.pigeon.data.model.ModelMessage;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by aditya on 21/10/16.
 */

public abstract class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.DataHolder> {

    private HashMap<String,ArrayList<ModelMessage>> mMessageHashMap;

    public abstract void messageClicked(String address);

    protected MessageAdapter() {
        mMessageHashMap = new HashMap<>();
    }

    public void addMessages(HashMap<String,ArrayList<ModelMessage>> pMessageHashMap) {
        mMessageHashMap.clear();
        mMessageHashMap = pMessageHashMap;
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
        final ArrayList<ModelMessage> lModelMessageArrayList =(new ArrayList<>(mMessageHashMap.values())).get(position);
        final ModelMessage lModelMessage = lModelMessageArrayList.get(lModelMessageArrayList.size()-1);
        holder.tvName.setText(lModelMessage.getAddress());
        holder.tvConversation.setText(lModelMessage.getBody());
        holder.tvDate.setText(lModelMessage.getDate());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View pView) {
                messageClicked(lModelMessage.getAddress());
            }
        });
    }


    @Override
    public int getItemCount() {
        return mMessageHashMap.size();
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


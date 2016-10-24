package com.zriton.pigeon.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zriton.pigeon.R;
import com.zriton.pigeon.data.model.ModelMessage;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by aditya on 24/10/16.
 */

public class ThreadAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private ArrayList<ModelMessage> mMessageArrayList;

    private final int VIEW_TYPE_LEFT = 0;
    private final int VIEW_TYPE_RIGHT = 1;

    public ThreadAdapter() {
        this.mMessageArrayList = new ArrayList<>();
    }


    public void addData(ArrayList<ModelMessage> pMessageArrayList) {
        mMessageArrayList.clear();
        mMessageArrayList.addAll(pMessageArrayList);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case VIEW_TYPE_LEFT:
                View v1 = inflater.inflate(R.layout.layout_thread_left, parent, false);
                viewHolder = new LeftViewHolder(v1);
                break;
            case VIEW_TYPE_RIGHT:
                View v2 = inflater.inflate(R.layout.layout_thread_right, parent, false);
                viewHolder = new RightViewHolder(v2);
                break;
            default:
                View v = inflater.inflate(R.layout.layout_thread_left, parent, false);
                viewHolder = new LeftViewHolder(v);
                break;
        }
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ModelMessage lModelStartup = mMessageArrayList.get(position);
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_LEFT:
                LeftViewHolder lLeftViewHolder = (LeftViewHolder) holder;
                lLeftViewHolder.tvBody.setText(lModelStartup.getBody());
                lLeftViewHolder.tvDate.setText(lModelStartup.getDate());
                break;
            case VIEW_TYPE_RIGHT:
                RightViewHolder lRightViewHolder = (RightViewHolder) holder;
                lRightViewHolder.tvBody.setText(lModelStartup.getBody());
                lRightViewHolder.tvDate.setText(lModelStartup.getDate());
                break;
        }

    }


    @Override
    public int getItemCount() {
        return mMessageArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(mMessageArrayList.get(position).getType().equals("1"))
            return VIEW_TYPE_LEFT;
        return VIEW_TYPE_RIGHT;
    }

    class RightViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvBody)
        TextView tvBody;

        @BindView(R.id.tvDate)
        TextView tvDate;

        private RightViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class LeftViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvBody)
        TextView tvBody;

        @BindView(R.id.tvDate)
        TextView tvDate;

        private LeftViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}


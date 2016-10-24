package com.zriton.pigeon.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zriton.pigeon.R;
import com.zriton.pigeon.data.model.ModelMessage;
import com.zriton.pigeon.utils.Singleton;
import com.zriton.pigeon.view.activity.MainActivity;

import java.util.ArrayList;

import butterknife.ButterKnife;

/**
 * Created by aditya on 21/10/16.
 */

public class ThreadFragment extends Fragment {

    public static final String TAG = "ThreadFragment";


    public static ThreadFragment newInstance(String address) {
        Bundle arguments = new Bundle();
        arguments.putString("address",address);
        ThreadFragment fragment = new ThreadFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    ArrayList<ModelMessage> mModelMessageArrayList;
    String address;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View lView = inflater.inflate(R.layout.fragment_thread,container,false);
        ButterKnife.bind(this,lView);
        address = getArguments().getString("address");
        mModelMessageArrayList = Singleton.getInstance().getMessageHashMap().get(address);

        return lView;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).getToolbar().setTitle(address);
        ActionBar lActionBar = ((MainActivity)getActivity()).getSupportActionBar();
        lActionBar.setDisplayHomeAsUpEnabled(true);
        lActionBar.setDisplayShowHomeEnabled(true);
    }


}

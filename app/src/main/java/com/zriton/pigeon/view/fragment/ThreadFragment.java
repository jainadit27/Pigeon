package com.zriton.pigeon.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.zriton.pigeon.R;
import com.zriton.pigeon.data.model.ModelMessage;
import com.zriton.pigeon.utils.DateUtils;
import com.zriton.pigeon.utils.Singleton;
import com.zriton.pigeon.view.activity.MainActivity;
import com.zriton.pigeon.view.adapter.ThreadAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by aditya on 21/10/16.
 */

public class ThreadFragment extends Fragment {

    public static ThreadFragment newInstance(String address) {
        Bundle arguments = new Bundle();
        arguments.putString("address", address);
        ThreadFragment fragment = new ThreadFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.etMessage)
    EditText etMessage;
    @BindView(R.id.ivSend)
    ImageView ivSend;

    @OnClick(R.id.ivSend)
    void sendMessage() {
        String message = etMessage.getText().toString().trim();
        if (!message.equals("")) {

            ModelMessage lModelMessage = buildMessage(message);

            /*SmsManager sm = SmsManager.getDefault();
            sm. sendTextMessage(lModelMessage.getAddress(),null,lModelMessage.getBody(),null,null);*/

            mThreadAdapter.updateData(lModelMessage);
            restoreState();

        }

    }

    ThreadAdapter mThreadAdapter;
    ArrayList<ModelMessage> mModelMessageArrayList;
    String address;

    TextWatcher mWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence pCharSequence, int pI, int pI1, int pI2) {

        }

        @Override
        public void onTextChanged(CharSequence pCharSequence, int pI, int pI1, int pI2) {
        }

        @Override
        public void afterTextChanged(Editable pEditable) {
            if (pEditable.toString().equals("")) {
                ivSend.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_send_grey_24dp));
            } else {
                ivSend.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_send_accent_24dp));
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View lView = inflater.inflate(R.layout.fragment_thread, container, false);
        ButterKnife.bind(this, lView);
        address = getArguments().getString("address");
        mModelMessageArrayList = Singleton.getInstance().getMessageHashMap().get(address);
        initRecyclerView();
        etMessage.addTextChangedListener(mWatcher);
        mThreadAdapter.addData(mModelMessageArrayList);
        return lView;
    }


    public void initRecyclerView() {
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mRecyclerView.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mThreadAdapter = new ThreadAdapter();
        mRecyclerView.setAdapter(mThreadAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).getToolbar().setTitle(address);
        ActionBar lActionBar = ((MainActivity) getActivity()).getSupportActionBar();
        lActionBar.setDisplayHomeAsUpEnabled(true);
        lActionBar.setDisplayShowHomeEnabled(true);
    }

    /**
     * Build ModelMessage
     * @param message Body of message
     * @return
     */
    private ModelMessage buildMessage(String message) {
        ModelMessage lModelMessage = new ModelMessage();
        lModelMessage.setType("2");
        lModelMessage.setAddress(address);
        lModelMessage.setBody(message);
        lModelMessage.setTimestamp(DateUtils.getCurrentTimeStamp());
        lModelMessage.setDate(DateUtils.getCurrentDate());
        return lModelMessage;
    }

    /**
     * Chnage conversation to initial view
     */
    private void restoreState() {
        etMessage.setText("");
        mRecyclerView.smoothScrollToPosition(mThreadAdapter.getMessageArrayList().size() - 1);
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


}

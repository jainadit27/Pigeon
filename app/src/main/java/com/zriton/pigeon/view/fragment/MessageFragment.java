package com.zriton.pigeon.view.fragment;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.zriton.pigeon.R;
import com.zriton.pigeon.data.model.ModelMessage;
import com.zriton.pigeon.utils.DateUtils;
import com.zriton.pigeon.utils.Singleton;
import com.zriton.pigeon.view.activity.MainActivity;
import com.zriton.pigeon.view.adapter.MessageAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by aditya on 21/10/16.
 */

public class MessageFragment extends Fragment implements SearchView.OnQueryTextListener {


    public static MessageFragment newInstance() {
        Bundle arguments = new Bundle();
        MessageFragment fragment = new MessageFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    @OnClick(R.id.fabNewMessage)
    void newMessage() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        NewMessageFragment lNewMessageFragment = NewMessageFragment.newInstance();
        fm.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .replace(R.id.container, lNewMessageFragment).addToBackStack(null)
                .commit();
    }

    MessageAdapter mMessageAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View lView = inflater.inflate(R.layout.fragment_message, container, false);
        ButterKnife.bind(this, lView);
        initRecyclerView();
        mMessageAdapter.addMessages(getMessages());
        return lView;
    }

    public void initRecyclerView() {
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mRecyclerView.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mMessageAdapter = new MessageAdapter() {
            @Override
            public void messageClicked(String address) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                ThreadFragment lThreadFragment = ThreadFragment.newInstance(address);
                fm.beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .replace(R.id.container, lThreadFragment).addToBackStack(null)
                        .commit();
            }
        };
        mRecyclerView.setAdapter(mMessageAdapter);
    }

    private HashMap<String, ArrayList<ModelMessage>> getMessages() {
        HashMap<String, ArrayList<ModelMessage>> lMessageHashMap = new HashMap<>();
        Cursor cursor = getActivity().
                getContentResolver().query(Uri.parse("content://sms"),
                null, null, null, null);

        assert cursor != null;


        int indexAddress = cursor.getColumnIndex("address");
        int indexBody = cursor.getColumnIndex("body");
        int indexDate = cursor.getColumnIndex("date");
        int indexType = cursor.getColumnIndex("type");


        if (cursor.moveToFirst()) {
            do {
                ArrayList<ModelMessage> lModelMessageArrayList = new ArrayList<>();

                ModelMessage lModelMessage = new ModelMessage();
                lModelMessage.setAddress(cursor.getString(indexAddress));
                lModelMessage.setBody(cursor.getString(indexBody));
                Long milliseconds = Long.parseLong(cursor.getString(indexDate));
                lModelMessage.setDate(DateUtils.changeFormat(milliseconds, "dd MMM"));
                lModelMessage.setType(cursor.getString(indexType));

                if (lMessageHashMap.containsKey(lModelMessage.getAddress())) {
                    lModelMessageArrayList = lMessageHashMap.get(lModelMessage.getAddress());
                }

                lModelMessageArrayList.add(lModelMessage);
                lMessageHashMap.put(lModelMessage.getAddress(), lModelMessageArrayList);
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            // empty box, no SMS
            cursor.close();
        }
        Singleton.getInstance().setMessageHashMap(lMessageHashMap);
        return lMessageHashMap;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).getToolbar().setTitle("Pigeon");
        ActionBar lActionBar = ((MainActivity) getActivity()).getSupportActionBar();
        lActionBar.setDisplayHomeAsUpEnabled(false);
        lActionBar.setDisplayShowHomeEnabled(false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_message, menu);
        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);

        MenuItemCompat.setOnActionExpandListener(item,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        // mMessageAdapter.setFilter(mCountryModel);
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        return true;
                    }
                });
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    private void filter() {

    }
}

package com.zriton.pigeon.view.fragment;

import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.zriton.pigeon.R;
import com.zriton.pigeon.data.model.ModelMessage;
import com.zriton.pigeon.data.model.ModelMessageRow;
import com.zriton.pigeon.utils.DateUtils;
import com.zriton.pigeon.utils.Singleton;
import com.zriton.pigeon.view.activity.MainActivity;
import com.zriton.pigeon.view.adapter.MessageAdapter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * Created by aditya on 21/10/16.
 */

public class MessageFragment extends Fragment implements SearchView.OnQueryTextListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


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
    ArrayList<ModelMessageRow> mMessageRowArrayList = new ArrayList<>();
    private GoogleApiClient mApiClient;
    private static final int RESOLVE_CONNECTION_REQUEST_CODE = 1;
    private DriveId mDriveId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View lView = inflater.inflate(R.layout.fragment_message, container, false);
        ButterKnife.bind(this, lView);
        initRecyclerView();
        mMessageRowArrayList = getMessages();
        mMessageAdapter.addMessages(mMessageRowArrayList);
        mApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
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

    private ArrayList<ModelMessageRow> getMessages() {
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
                lModelMessage.setTimestamp(milliseconds);

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

        //For adapter
        Iterator it = lMessageHashMap.entrySet().iterator();
        ArrayList<ModelMessageRow> lMessageRowArrayList = new ArrayList<>();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            if (pair.getValue() instanceof ArrayList) {
                ArrayList<ModelMessage> conversations = (ArrayList<ModelMessage>) pair.getValue();
                String searchKey = "";
                for (ModelMessage item : conversations) {
                    searchKey = searchKey + item.getBody();
                }

                ModelMessage lModelMessage = conversations.get(conversations.size() - 1);

                ModelMessageRow lModelMessageRow = new ModelMessageRow();
                lModelMessageRow.setBody(lModelMessage.getBody());
                lModelMessageRow.setAddress(lModelMessage.getAddress());
                lModelMessageRow.setTimestamp(lModelMessage.getTimestamp());
                lModelMessageRow.setDate(lModelMessage.getDate());
                lModelMessageRow.setThreadCount(conversations.size());
                lModelMessageRow.setSearchKey(searchKey);

                lMessageRowArrayList.add(lModelMessageRow);
            }

        }
        Collections.sort(lMessageRowArrayList);
        Singleton.getInstance().setMessageHashMap(lMessageHashMap);
        return lMessageRowArrayList;
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
        MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
        MenuItemCompat.setOnActionExpandListener(item,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        mMessageAdapter.addMessages(mMessageRowArrayList);
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        return true;
                    }
                });
        item = menu.findItem(R.id.action_upload);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem pMenuItem) {
                mApiClient.connect();
                return true;
            }
        });
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mMessageAdapter.addMessages(filter(mMessageRowArrayList, newText));
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    /**
     * Filters the input list
     *
     * @param pMessageRowList The original list to be filtered
     * @param query           Query to be searched
     * @return List of filtered items
     */

    private ArrayList<ModelMessageRow> filter(ArrayList<ModelMessageRow> pMessageRowList, String query) {
        query = query.toLowerCase();

        final ArrayList<ModelMessageRow> filteredModelList = new ArrayList<>();
        for (ModelMessageRow lModelMessageRow : pMessageRowList) {
            final String text = lModelMessageRow.getBody().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(lModelMessageRow);
            }
        }
        return filteredModelList;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Query query = new Query.Builder()
                .addFilter(Filters.and(Filters.eq(
                        SearchableField.TITLE, "PigeonBackup"),
                        Filters.eq(SearchableField.TRASHED, false)))
                .build();

        Drive.DriveApi.query(mApiClient, query)
                .setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
                    @Override
                    public void onResult(DriveApi.MetadataBufferResult result) {
                        if (result.getStatus().isSuccess()) {
                            boolean isFound = false;
                            for (Metadata m : result.getMetadataBuffer()) {
                                if (m.getTitle().equals("SMSMessages")) {
                                    mDriveId = m.getDriveId();
                                    isFound = true;
                                    break;
                                }
                            }
                            if (!isFound) {
                                MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                        .setTitle("PigeonBackup")
                                        .build();
                                Drive.DriveApi.getRootFolder(mApiClient)
                                        .createFolder(mApiClient, changeSet)
                                        .setResultCallback(new ResultCallback<DriveFolder.DriveFolderResult>() {
                                            @Override
                                            public void onResult(DriveFolder.DriveFolderResult result) {
                                                if (!result.getStatus().isSuccess()) {
                                                    Toast.makeText(getContext(),
                                                            "Error while trying to create the folder",
                                                            Toast.LENGTH_LONG).show();
                                                } else {
                                                    Toast.makeText(getContext(),
                                                            "Created Folder!",
                                                            Toast.LENGTH_LONG).show();
                                                    mDriveId = result.getDriveFolder().getDriveId();
                                                }
                                            }
                                        });
                            } else {
                                Toast.makeText(getContext(), "Found Folder",
                                        Toast.LENGTH_SHORT).show();
                            }

                            Drive.DriveApi.newDriveContents(mApiClient)
                                    .setResultCallback(driveContentsCallback);

                        }
                    }
                });


    }


    final private ResultCallback<DriveApi.DriveContentsResult> driveContentsCallback =
            new ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult result) {
                    DriveFolder folder = mDriveId.asDriveFolder();
                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle("SMSBackup")
                            .setMimeType("text/plain")
                            .setStarred(true).build();

                    DriveContents c = result.getDriveContents();

                    folder.createFile(mApiClient, changeSet, c)
                            .setResultCallback(fileCallback);
                }
            };

    final private ResultCallback<DriveFolder.DriveFileResult> fileCallback =
            new ResultCallback<DriveFolder.DriveFileResult>() {
                @Override
                public void onResult(DriveFolder.DriveFileResult result) {

                    Toast.makeText(getContext(), "File Created/Updated", Toast.LENGTH_SHORT)
                            .show();

                    DriveFile file = result.getDriveFile();
                    file.open(mApiClient, DriveFile.MODE_WRITE_ONLY, null)
                            .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
                                @Override
                                public void onResult(DriveApi.DriveContentsResult result) {
                                    if (!result.getStatus().isSuccess()) {
                                        // Handle error
                                        return;
                                    }
                                    DriveContents driveContents = result.getDriveContents();
                                    try {
                                        // Append to the file.
                                        OutputStream outputStream = driveContents.getOutputStream();

                                        for (int i = 0; i < mMessageRowArrayList.size(); i++) {
                                            ModelMessageRow lModelMessageRow = mMessageRowArrayList.get(i);
                                            String sender = lModelMessageRow.getAddress();
                                            String body = lModelMessageRow.getBody();
                                            outputStream.write(sender.getBytes());
                                            outputStream.write("\n".getBytes());
                                            outputStream.write(body.getBytes());
                                            outputStream.write("\n".getBytes());
                                        }

                                    } catch (IOException e) {
                                        Log.d("Main", e.getMessage());
                                    }

                                    driveContents.commit(mApiClient, null).setResultCallback(new ResultCallback<Status>() {
                                        @Override
                                        public void onResult(Status result) {
                                            // Handle the response status
                                            if (result.isSuccess()) {
                                                Toast.makeText(getActivity(), "Wrote to file", Toast.LENGTH_LONG).show();
                                            } else {
                                                Log.d("Main", result.getStatusMessage() + " Message");
                                            }
                                        }
                                    });
                                }
                            });
                }
            };

    @Override
    public void onConnectionSuspended(int pI) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult pConnectionResult) {
        if (pConnectionResult.hasResolution()) {
            try {
                pConnectionResult.startResolutionForResult(getActivity(), RESOLVE_CONNECTION_REQUEST_CODE);
            } catch (IntentSender.SendIntentException e) {
                // Unable to resolve, message user appropriately
            }
        } else {
            GooglePlayServicesUtil.getErrorDialog(pConnectionResult.getErrorCode(), getActivity(), 0).show();
        }
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case RESOLVE_CONNECTION_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    mApiClient.connect();
                }
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mApiClient.disconnect();
    }
}

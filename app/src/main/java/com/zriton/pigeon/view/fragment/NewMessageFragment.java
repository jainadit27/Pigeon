package com.zriton.pigeon.view.fragment;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.zriton.pigeon.R;
import com.zriton.pigeon.data.model.ModelMessage;
import com.zriton.pigeon.utils.DateUtils;
import com.zriton.pigeon.utils.Miscellaneous;
import com.zriton.pigeon.view.activity.MainActivity;
import com.zriton.pigeon.view.adapter.ThreadAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * Created by aditya on 24/10/16.
 */

public class NewMessageFragment extends Fragment {

    public static NewMessageFragment newInstance() {
        Bundle arguments = new Bundle();
        NewMessageFragment fragment = new NewMessageFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.etMessage)
    EditText etMessage;
    @BindView(R.id.ivSend)
    ImageView ivSend;
    @BindView(R.id.actContact)
    AutoCompleteTextView actContact;

    @OnClick(R.id.ivSend)
    void sendMessage() {
        String message = etMessage.getText().toString().trim();
        String number = actContact.getText().toString().trim();
        if(number.equals(""))
        {
            Toast.makeText(getContext(), "Please enter a number", Toast.LENGTH_SHORT).show();
        }
        else if(number.length()!=10)
        {
            Toast.makeText(getContext(), "Please enter a valid mobile number", Toast.LENGTH_SHORT).show();
        }
        else if (!message.equals("")) {
            ModelMessage lModelMessage = buildMessage(message);

            /*SmsManager sm = SmsManager.getDefault();
            sm. sendTextMessage(lModelMessage.getAddress(),null,lModelMessage.getBody(),null,null);*/

            mThreadAdapter.updateData(lModelMessage);
            restoreState();

        }

    }
    @OnClick(R.id.ivContact)
    void pickContact() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, PICK_CONTACT);
        }
    }

    ThreadAdapter mThreadAdapter;
    String address;
    private final int PICK_CONTACT = 1;

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
        View lView = inflater.inflate(R.layout.fragment_new_message, container, false);
        ButterKnife.bind(this, lView);
        address = getArguments().getString("address");
        initRecyclerView();
        etMessage.addTextChangedListener(mWatcher);
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
        ((MainActivity) getActivity()).getToolbar().setTitle("New Message");
        ActionBar lActionBar = ((MainActivity) getActivity()).getSupportActionBar();
        lActionBar.setDisplayHomeAsUpEnabled(true);
        lActionBar.setDisplayShowHomeEnabled(true);
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(reqCode, resultCode, data);

        if (reqCode == PICK_CONTACT) {
            if (resultCode == RESULT_OK) {
                Uri contactData = data.getData();
                Cursor c = getActivity().getContentResolver().query(contactData, null, null, null, null);

                if (c.moveToFirst()) {
                    String name = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                    Toast.makeText(getContext(), "You've picked:" + name, Toast.LENGTH_LONG).show();
                    ContentResolver cr = getActivity().getContentResolver();
                    Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
                            "DISPLAY_NAME = '" + name + "'", null, null);
                    if (cursor.moveToFirst()) {
                        String contactId =
                                cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                        //
                        //  Get all phone numbers.
                        //
                        Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                        while (phones.moveToNext()) {
                            String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            number = number.replaceAll("\\s","");
                            number = number.replaceAll("-","");
                            actContact.setText(Miscellaneous.removeCountryCode(number));
                        }
                        phones.close();
                    }
                    cursor.close();

                }


            }
        }
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

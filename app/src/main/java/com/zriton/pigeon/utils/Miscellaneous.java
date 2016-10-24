package com.zriton.pigeon.utils;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aditya on 04/06/16.
 */
public class Miscellaneous {


    // Always send the number to this function to remove the first n digits (+1,+52, +520, etc)
    public static String removeCountryCode(String number) {
        int digits = 10;
        if (hasCountryCode(number)) {
            // +52 for MEX +526441122345, 13-10 = 3, so we need to remove 3 characters
            int country_digits = number.length() - digits;
            number = number.substring(country_digits);
        }
        return number;
    }

    // Every country code starts with + right?
    public static boolean hasCountryCode(String number) {
        int plus_sign_pos = 0;
        return number.charAt(plus_sign_pos) == '+' || number.charAt(plus_sign_pos) == '0'; // Didn't String had contains() method?...
    }


    public static List<String> getGroupsTitle(Context context,String name) {

        List<String> groupsTitle = new ArrayList<>();

        String contactId = null;

        Cursor cursorContactId = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.CONTACT_ID},
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + "=?",
                new String[]{name},
                null);

        if (cursorContactId.moveToFirst()) {
            contactId = cursorContactId.getString(0);
        }

        cursorContactId.close();

        if (contactId == null)
            return null;

        List<String> groupIdList = new ArrayList<>();

        Cursor cursorGroupId = context.getContentResolver().query(
                ContactsContract.Data.CONTENT_URI,
                new String[]{ContactsContract.Data.DATA1},
                String.format("%s=? AND %s=?", ContactsContract.Data.CONTACT_ID, ContactsContract.Data.MIMETYPE),
                new String[]{contactId, ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE},
                null);

        while (cursorGroupId.moveToNext()) {
            String groupId = cursorGroupId.getString(0);
            groupIdList.add(groupId);
        }
        cursorGroupId.close();

        Cursor cursorGroupTitle = context.getContentResolver().query(
                ContactsContract.Groups.CONTENT_URI, new String[]{ContactsContract.Groups.TITLE},
                ContactsContract.Groups._ID + " IN (" + TextUtils.join(",", groupIdList) + ")",
                null,
                null);

        while (cursorGroupTitle.moveToNext()) {
            String groupName = cursorGroupTitle.getString(0);
            Log.e("groupName", groupName);
            groupsTitle.add(groupName);
        }
        cursorGroupTitle.close();

        return groupsTitle;
    }

}

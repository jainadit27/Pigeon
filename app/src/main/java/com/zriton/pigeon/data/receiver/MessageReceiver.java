package com.zriton.pigeon.data.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.telephony.SmsMessage;
import android.util.Log;

import com.zriton.pigeon.R;

/**
 * Created by aditya on 24/10/16.
 */

public class MessageReceiver extends BroadcastReceiver {

    private static final int NOTIFY_ID = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        final Bundle lBundle = intent.getExtras();
        try {

            if (lBundle != null) {
                final Object[] lObjects = (Object[]) lBundle.get("pdus");
                for (int i = 0; i < lObjects.length; i++) {

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) lObjects[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                    String senderNum = phoneNumber;
                    String message = currentMessage.getDisplayMessageBody();

                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);

                    mBuilder.setContentTitle(senderNum)
                            .setContentText(message)
                            .setSmallIcon(R.drawable.ic_send_accent_24dp)
                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                            .build();

                    NotificationManager manager = (NotificationManager)
                            context.getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.notify(NOTIFY_ID, mBuilder.build());

                }
            }

        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" +e);

        }


    }
}

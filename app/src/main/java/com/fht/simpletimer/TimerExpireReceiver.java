package com.fht.simpletimer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class TimerExpireReceiver extends BroadcastReceiver {
    private static final String TAG = Const.TAG + TimerExpireReceiver.class.getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        int id = intent.getIntExtra(Const.ID, -1);
        String msg = "received action = " + action + ", id = "+ id;
        Log.i(TAG, msg);
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        startAlarmActivity(context, id);
    }

    private void startAlarmActivity(Context context, int id) {
        Intent intent = new Intent(context, AlarmActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Const.ID, id);
        context.startActivity(intent);
    }
}
package com.fht.simpletimer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.fht.simpletimer.db.TimerTable;

public class AlarmActivity extends AppCompatActivity {
    private static final String TAG = Const.TAG + AlarmActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        Intent intent = getIntent();
        int id = intent.getIntExtra(Const.ID, -1);

        TimerTable table = new TimerTable(this);
        TimerItem item = table.query(id);
        item.startTime = item.remainTime = 0;
        table.setRemainTime(item);
        table.setStartTime(item);
    }
}

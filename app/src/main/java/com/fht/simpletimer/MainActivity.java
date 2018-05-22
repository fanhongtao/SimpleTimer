package com.fht.simpletimer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fht.simpletimer.db.DbIniter;
import com.fht.simpletimer.db.TimerTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = Const.TAG + MainActivity.class.getSimpleName();

    private final int ADD_TIMER = 1;
    private final int EDIT_TIMER = 2;

    private final int MENU_EDIT_TIMER = 1;
    private final int MENU_DELETE_TIMER = 2;

    protected TimerListAdapter mAdapter;

    // All the timers.
    private List<TimerItem> mTimers;
    private List<TimerItem> mRunningList;

    private Map<Integer, MyCountDownTimer> map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        map = new HashMap<>();
        mRunningList = new ArrayList<>();
        DbIniter.init();
        TimerTable table = new TimerTable(this);
        mTimers = table.getTimerList();
        Log.i(TAG, "Exist timers:");
        for (TimerItem item: mTimers) {
            Log.i(TAG, "\t" + item.toString());
        }
        long currTime = System.currentTimeMillis();
        for (TimerItem item : mTimers) {
            if (item.startTime != 0) {
                if (currTime < item.startTime + item.remainTime) {
                    item.running = true;
                    Log.i(TAG, "Timer : " + item.id + " is running...");
                    mRunningList.add(item);
                } else {
                    item.running = false;
                    item.startTime = item.remainTime = 0;
                    table.setStartTime(item);
                    table.setRemainTime(item);
                    Log.i(TAG, "Timer : " + item.id + " is expired.");
                }
            }
        }

        ListView listView = findViewById(R.id.timerList);
        mAdapter = new TimerListAdapter();
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TimerItem item = mTimers.get(position);
                item.running = ! item.running;
                if (item.running) {
                    startTimer(item, ((ViewHolder)view.getTag()).remainTime);
                } else {
                    cancelTimer(item);
                }
                mAdapter.notifyDataSetChanged();
            }
        });

        listView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v,
                                            ContextMenu.ContextMenuInfo menuInfo) {
                menu.setHeaderTitle(R.string.menu_title_operation);
                menu.add(0, MENU_EDIT_TIMER, 0, R.string.menu_edit);
                menu.add(0, MENU_DELETE_TIMER, 0, R.string.menu_delete);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (Map.Entry<Integer, MyCountDownTimer> entry : map.entrySet()) {
            entry.getValue().cancel();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_add:
                startAddTimerActivity();
                return true;
            case R.id.action_settings:
                startSettingActivity();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int index = (int)info.id;
        TimerItem timerItem = mTimers.get(index);
        Log.i(TAG, "Click ContextMenu " + item.getItemId() + ". " + timerItem.toString());
        switch (item.getItemId()) {
            case MENU_EDIT_TIMER:
                startEditTimerActivity(timerItem, index);
                return true;
            case MENU_DELETE_TIMER:
                deleteTimer(timerItem, index);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ADD_TIMER:
                if (resultCode == RESULT_OK){
                    addTimer(data);
                } else {
                    Log.i(TAG, "Add canceled.");
                }
                break;
            case EDIT_TIMER:
                if (resultCode == RESULT_OK){
                    updateTimer(data);
                } else {
                    Log.i(TAG, "Edit canceled.");
                }
                break;
        }
    }

    void startSettingActivity() {
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }

    void startAddTimerActivity() {
        Intent intent = new Intent(this, TimerEditActivity.class);
        startActivityForResult(intent, ADD_TIMER);
    }

    void startEditTimerActivity(TimerItem timerItem, int index) {
        Intent intent = new Intent(this, TimerEditActivity.class);
        intent.putExtra(Const.INDEX, index);
        intent.putExtra(Const.TIMER, timerItem);
        startActivityForResult(intent, EDIT_TIMER);
    }

    void addTimer(Intent intent) {
        TimerItem timerItem = (TimerItem)intent.getSerializableExtra(Const.TIMER);
        timerItem.id = (int)new TimerTable(this).addTimer(timerItem);
        if (-1 != timerItem.id) {
            mTimers.add(timerItem);
            mAdapter.notifyDataSetChanged();
            Log.i(TAG, "Create timer. " + timerItem);
        } else {
            String msg = "Can't create timer. " + timerItem;
            Log.e(TAG, msg);
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }
    }

    void deleteTimer(TimerItem timerItem, int index) {
        cancelTimer(timerItem);
        new TimerTable(this).delete(timerItem.id);
        mTimers.remove(index);
        mAdapter.notifyDataSetChanged();
        Log.i(TAG, "Delete timer. " + timerItem);
    }

    void updateTimer(Intent intent) {
        int index = intent.getIntExtra(Const.INDEX, 0);
        TimerItem item = mTimers.get(index);
        cancelTimer(item);

        TimerItem back = (TimerItem)intent.getSerializableExtra(Const.TIMER);
        item.name = back.name;
        item.hour = back.hour;
        item.minute = back.minute;
        item.second = back.second;
        item.startTime = item.remainTime = 0;
        item.running = false;
        new TimerTable(this).updateTimer(item);
        mAdapter.notifyDataSetChanged();
        Log.i(TAG, "Update timer. " + item);
    }

    void createCountTimer(TimerItem item, TextView remainTimeView, long remainTime) {
        Log.i(TAG, "Create CountDownTimer. " + item);
        MyCountDownTimer countDownTimer = new MyCountDownTimer(remainTime, 1000, item, remainTimeView);
        map.put(item.id, countDownTimer);
        countDownTimer.start();
    }

    void startTimer(TimerItem item, TextView remainTimeView) {
        if (item.remainTime <= 0) {
            item.resetRemainTime();
        }
        item.startTime = System.currentTimeMillis();
        TimerTable table = new TimerTable(this);
        table.setStartTime(item);
        table.setRemainTime(item);

        createCountTimer(item, remainTimeView, item.remainTime);

        AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(Const.TIMER_EXPIRE_INTENT);
        intent.putExtra(Const.ID, item.id);
        PendingIntent operation = PendingIntent.getBroadcast(this, item.id, intent, 0);
        long triggerTime = System.currentTimeMillis() + item.remainTime;
        am.set(AlarmManager.RTC_WAKEUP, triggerTime, operation);
    }

    void cancelTimer(TimerItem item) {
        AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(Const.TIMER_EXPIRE_INTENT);
        intent.putExtra(Const.ID, item.id);
        PendingIntent operation = PendingIntent.getBroadcast(this, item.id, intent, 0);
        am.cancel(operation);

        long currTime = System.currentTimeMillis();
        Log.i(TAG, "Curr time: " + currTime);
        Log.i(TAG, "Before cancel: " + item);
        item.calcRemainTime(currTime);
        item.startTime = 0;
        item.running = false;
        TimerTable table = new TimerTable(this);
        table.setRemainTime(item);
        table.setStartTime(item);
        Log.i(TAG, "After cancel: " + item);

        Log.i(TAG, "Cancel timer. ID: " + item.id);
        MyCountDownTimer countDownTimer = map.remove(item.id);
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private void showRemainedTime(TextView textView, long remainedTime) {
        long temp = remainedTime / 1000;
        int hour = (int) temp / 3600;
        int minute = (int)(temp - hour * 3600) / 60;
        int second = (int) temp % 60;

        textView.setText(Utils.formatTime(hour, minute, second));
    }

    class TimerListAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        TimerListAdapter() {
            super();
            mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return mTimers.size();
        }

        @Override
        public Object getItem(int position) {
            return mTimers.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        /*
         * Android will cache view
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get Holder
            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.timer_item, null);

                holder = new ViewHolder();
                holder.timerName = convertView.findViewById(R.id.timerName);
                holder.totalTime = convertView.findViewById(R.id.totalTime);
                holder.remainTime = convertView.findViewById(R.id.remainTime);
                holder.status = convertView.findViewById(R.id.status);

                convertView.setTag(holder);
                convertView.setMinimumHeight(100);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // Set content
            @SuppressWarnings("unchecked")
            TimerItem item = (TimerItem) getItem(position);
            holder.timerName.setText(item.name);
            holder.totalTime.setText(Utils.formatTime(item.hour, item.minute, item.second));
            showRemainedTime(holder.remainTime, item.remainTime);
            if (item.running) {
                holder.status.setImageResource(android.R.drawable.ic_media_pause);
            } else {
                holder.status.setImageResource(android.R.drawable.ic_media_play);
            }

            if (!mRunningList.isEmpty() && item.running) {
                if (mRunningList.contains(item) && !map.containsKey(item.id)) {
                    Log.i(TAG, "Create count timer by TimerListAdapter");
                    long remainTime = item.remainTime - (System.currentTimeMillis() - item.startTime);
                    createCountTimer(item, holder.remainTime, remainTime);
                    mRunningList.remove(item);
                }
            }

            return convertView;
        }
    }

    public static class ViewHolder {
        public TextView timerName;
        public TextView totalTime;
        public TextView remainTime;
        public ImageView status;
    }

    class MyCountDownTimer extends CountDownTimer {
        private TimerItem mTimerItem;
        private TextView mTextView;

        MyCountDownTimer(long millisInFuture, long countDownInterval, TimerItem timerItem, TextView textView) {
            super(millisInFuture, countDownInterval);
            this.mTimerItem = timerItem;
            this.mTextView = textView;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            showRemainedTime(mTextView, millisUntilFinished);
        }

        @Override
        public void onFinish() {
            Log.i(TAG, "CountDownTimer finish. " + mTimerItem.toString());
            mTimerItem.remainTime = 0;
            mTimerItem.running = false;
            mAdapter.notifyDataSetChanged();
        }
    }
}

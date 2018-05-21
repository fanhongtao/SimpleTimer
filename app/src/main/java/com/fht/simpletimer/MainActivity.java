package com.fht.simpletimer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = Const.TAG + MainActivity.class.getSimpleName();

    private final int ADD_TIMER = 1;
    private final int EDIT_TIMER = 2;

    private final int MENU_EDIT_TIMER = 1;
    private final int MENU_DELETE_TIMER = 2;

    protected TimerListAdapter mAdapter;

    // All the timers.
    private List<TimerItem> mTimers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTimers = getTimerList();
        Log.i(TAG, "Exist timers:");
        for (TimerItem item: mTimers) {
            Log.i(TAG, "\t" + item.toString());
        }
        ListView listView = findViewById(R.id.timerList);
        mAdapter = new TimerListAdapter();
        listView.setAdapter(mAdapter);

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
        mTimers.add(timerItem);
        mAdapter.notifyDataSetChanged();
        Log.i(TAG, "Create timer. " + timerItem);
    }

    void deleteTimer(TimerItem timerItem, int index) {
        mTimers.remove(index);
        mAdapter.notifyDataSetChanged();
        Log.i(TAG, "Delete timer. " + timerItem);
    }

    void updateTimer(Intent intent) {
        int index = intent.getIntExtra(Const.INDEX, 0);
        TimerItem item = mTimers.get(index);

        TimerItem back = (TimerItem)intent.getSerializableExtra(Const.TIMER);
        item.name = back.name;
        item.hour = back.hour;
        item.minute = back.minute;
        item.second = back.second;
        mAdapter.notifyDataSetChanged();
        Log.i(TAG, "Update timer. " + item);
    }

    private void showRemainedTime(TextView textView, long remainedTime) {
        long temp = remainedTime / 1000;
        int hour = (int) temp / 3600;
        int minute = (int)(temp - hour * 3600) / 60;
        int second = (int) temp % 60;

        textView.setText(Utils.formatTime(hour, minute, second));
    }

    private List<TimerItem> getTimerList() {
        List<TimerItem> list = new ArrayList<>();
        list.add(new TimerItem("5 seconds", 0, 0, 5));
        list.add(new TimerItem("Siesta (30 minutes)", 0, 30, 0));
        list.add(new TimerItem("Bake bread (60 minutes)", 1, 0, 0));
        return list;
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
            showRemainedTime(holder.remainTime, 0);

            return convertView;
        }
    }

    public static class ViewHolder {
        public TextView timerName;
        public TextView totalTime;
        public TextView remainTime;
        public ImageView status;
    }
}

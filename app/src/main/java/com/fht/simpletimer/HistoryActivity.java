package com.fht.simpletimer;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.fht.simpletimer.db.HistoryTable;

import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    protected HistoryListAdapter mAdapter;
    private List<HistoryItem> mHistories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        HistoryTable table = new HistoryTable(this);
        mHistories = table.getHistoryList();

        ListView listView = findViewById(R.id.historyList);
        mAdapter = new HistoryListAdapter();
        listView.setAdapter(mAdapter);
    }

    class HistoryListAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        HistoryListAdapter() {
            super();
            mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return mHistories.size();
        }

        @Override
        public Object getItem(int position) {
            return mHistories.get(position);
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
                convertView = mInflater.inflate(R.layout.history_item, null);

                holder = new ViewHolder();
                holder.timerName = convertView.findViewById(R.id.timerName);
                holder.startTime = convertView.findViewById(R.id.startTime);
                holder.stopTime = convertView.findViewById(R.id.stopTime);

                convertView.setTag(holder);
                convertView.setMinimumHeight(100);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // Set content
            @SuppressWarnings("unchecked")
            HistoryItem item = (HistoryItem) getItem(position);
            holder.timerName.setText(item.name);
            holder.startTime.setText(Utils.formatDateTime(item.startTime));
            holder.stopTime.setText(Utils.formatDateTime(item.stopTime));

            return convertView;
        }
    }

    public static class ViewHolder {
        public TextView timerName;
        public TextView startTime;
        public TextView stopTime;
    }
}

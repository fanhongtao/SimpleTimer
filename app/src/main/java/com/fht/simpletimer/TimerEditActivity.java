package com.fht.simpletimer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

public class TimerEditActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_edit);

        final TextView nameView = findViewById(R.id.timerName);

        final NumberPicker hourPicker = findViewById(R.id.hourPicker);
        hourPicker.setMaxValue(99);
        hourPicker.setMinValue(0);

        final NumberPicker minutePicker = findViewById(R.id.minutePicker);
        minutePicker.setMaxValue(59);
        minutePicker.setMinValue(0);

        final NumberPicker secondPicker = findViewById(R.id.secondPicker);
        secondPicker.setMaxValue(59);
        secondPicker.setMinValue(0);

        Intent requestIntent = getIntent();
        final int index = requestIntent.getIntExtra(Const.INDEX, -1);
        if (index != -1) {
            // edit mode
            setTitle(R.string.title_edit_timer);
            TimerItem timerItem = (TimerItem)requestIntent.getSerializableExtra(Const.TIMER);
            nameView.setText(timerItem.name);
            hourPicker.setValue(timerItem.hour);
            minutePicker.setValue(timerItem.minute);
            secondPicker.setValue(timerItem.second);
        } else {
            // add mode
            setTitle(R.string.title_add_timer);
        }

        Button okBtn = findViewById(R.id.okButton);
        okBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                TimerItem timerItem = new TimerItem();
                timerItem.name = nameView.getText().toString();
                timerItem.hour = hourPicker.getValue();
                timerItem.minute = minutePicker.getValue();
                timerItem.second = secondPicker.getValue();

                Intent intent = new Intent();
                intent.putExtra(Const.INDEX, index);
                intent.putExtra(Const.TIMER, timerItem);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        Button cancelBtn = findViewById(R.id.cancelButton);
        cancelBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });
    }
}

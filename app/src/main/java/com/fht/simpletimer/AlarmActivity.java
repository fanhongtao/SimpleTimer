package com.fht.simpletimer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import com.fht.simpletimer.db.TimerTable;

import java.util.Timer;
import java.util.TimerTask;

public class AlarmActivity extends AppCompatActivity {
    private static final String TAG = Const.TAG + AlarmActivity.class.getSimpleName();

    private AudioManager mAudioManager;
    private int mOldStreamVolume;
    private MediaPlayer mMediaPlayer = null;
    private Vibrator mVibrator = null;
    private Timer mTimer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_alarm);
        setTitle(R.string.title_alarm);

        Intent intent = getIntent();
        int id = intent.getIntExtra(Const.ID, -1);

        TimerTable table = new TimerTable(this);
        TimerItem item = table.query(id);
        item.startTime = item.remainTime = 0;
        table.setRemainTime(item);
        table.setStartTime(item);

        mAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        mOldStreamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        Log.i(TAG, "old volume: " + mOldStreamVolume);
        TextView nameView = findViewById(R.id.timerName);
        nameView.setText(item.name);
        startAlarm();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTimer.cancel();
        stopAlarm();
        Log.i(TAG, "reset volume to: " + mOldStreamVolume);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                mOldStreamVolume,
                AudioManager.FLAG_PLAY_SOUND);
    }

    private void startAlarm() {
        Log.i(TAG, "start Alarm");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this) ;

        mMediaPlayer = MediaPlayer.create(this, getAlarmRingtoneUri(prefs));
        mMediaPlayer.setLooping(true);
        mMediaPlayer.start();
        int prefVolume = prefs.getInt("pref_ring_volume", 3);
        Log.i(TAG, "set volume to: " + prefVolume);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                prefVolume,
                AudioManager.FLAG_PLAY_SOUND);

        boolean needVibrate = prefs.getBoolean("pref_vibrate", false);
        if (needVibrate) {
            mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            long[] patter = {1000, 1000};
            mVibrator.vibrate(patter, 0);
        }

        int ringTime = prefs.getInt("pref_ring_time", 30);
        final boolean closeActivity = prefs.getBoolean("pref_close_activity", false);
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                timeUp(closeActivity);
            }
        };
        mTimer.schedule(task, ringTime * 1000);
    }

    private synchronized void stopAlarm() {
        if (mVibrator != null) {
            mVibrator.cancel();
            mVibrator = null;
        }
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private Uri getAlarmRingtoneUri(SharedPreferences prefs) {
        String ringTonePref = prefs.getString("pref_ring_tone", null);
        Log.i(TAG, "Ring tone in pref: " + ringTonePref);
        Uri uri;
        if ((ringTonePref == null) || (ringTonePref.length() == 0)) {
            uri = RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_ALARM);
            Log.i(TAG, "Get default Alarm ring tone. uri: " + uri);
        } else {
            uri = Uri.parse(ringTonePref);
            Log.i(TAG, "Get ring tone through pref. uri: " + uri);
        }
        return uri;
    }

    private void timeUp(boolean closeActivity) {
        stopAlarm();
        if (closeActivity) {
            this.finish();
        }
    }
}

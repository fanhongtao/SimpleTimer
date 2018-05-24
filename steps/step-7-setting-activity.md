# 1. Add SeekBarPreference

Native android preference does not support 'SeekBarPreference'.

In preference support library v7, we can find 'SeekBarPreference'. But can NOT find 'RingtonePreference'.

In [Android Support library - preference v7 bugfix](https://github.com/Gericop/Android-Support-Preference-V7-Fix), we can find both 'SeekBarPreference' and 'RingtonePreference'. But the 'RingtonePreference' can only choose ringtone of the system.
For more infomation, see branch 'support-v7-fix'.

In order to choose my favorite song as alarm tone, so I write a simple SeekBarPreference.

# 2. Update SettingActivity

* Use RingtonePreference to choose tone.
* Use SeekBarPreference to set tone time.

# Reference

| Source File (In Android Repo)| Local version |
| ------------- |:-------------|
| [SeekBarPreference.java](https://github.com/aosp-mirror/platform_frameworks_base/blob/master/core/java/android/preference/SeekBarPreference.java) | [SeekBarPreference.java](ref/SeekBarPreference.java) |
| [preference_widget_seekbar.xml](https://github.com/aosp-mirror/platform_frameworks_base/blob/master/core/res/res/layout/preference_widget_seekbar.xml) | [preference_widget_seekbar.xml](ref/preference_widget_seekbar.xml) |

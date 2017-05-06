package com.krevin.crockpod.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.krevin.crockpod.podcast.Podcast;

import org.joda.time.DateTime;

public class Alarm {

    private static final String PODCAST_FEED_KEY = "podcast_feed";
    private static final String PODCAST_NAME_KEY = "podcast_name";
    private static final String PODCAST_AUTHOR_KEY = "podcast_author";
    private static final String PODCAST_LOGO_KEY = "podcast_logo";
    private static final String ALARM_ID_KEY = "alarm_id";
    private static final String ALARM_HOUR_KEY = "alarm_hour";
    private static final String ALARM_MINUTE_KEY = "alarm_minute";

    private Intent mIntent;
    private Podcast mPodcast;
    private Integer mHourOfDay;
    private Integer mMinute;
    private Context mContext;
    private Integer mId;

    public Alarm(Context context, Intent intent) {
        mContext = context;
        mIntent = intent;
    }

    public Alarm(Context context, Podcast podcast, int hourOfDay, int minute) {
        mContext = context;
        mPodcast = podcast;
        mHourOfDay = hourOfDay;
        mMinute = minute;
    }

    public static boolean exists(Intent intent) {
        return intent.hasExtra(PODCAST_FEED_KEY);
    }

    public int getId() {
        int id = mId == null ? mIntent.getIntExtra(ALARM_ID_KEY, -1) : mId;
        if (id < 0) {
            throw new IllegalStateException("ID not set!");
        }
        return id;
    }

    public void setId(int id) {
        mId = id;
    }

    public Context getContext() {
        return mContext;
    }

    public Intent getIntent() {
        return mIntent == null ? buildIntent() : mIntent;
    }

    public Podcast getPodcast() {
        return mPodcast == null ? buildPodcast() : mPodcast;
    }

    public int getHourOfDay() {
        return mHourOfDay == null ? mIntent.getIntExtra(ALARM_HOUR_KEY, 0) : mHourOfDay;
    }

    public int getMinute() {
        return mMinute == null ? mIntent.getIntExtra(ALARM_MINUTE_KEY, 0) : mMinute;
    }

    public DateTime getNextTriggerTime() {
        DateTime now = DateTime.now();
        DateTime target = now
                .withHourOfDay(getHourOfDay())
                .withMinuteOfHour(getMinute())
                .withSecondOfMinute(0);

        return target.getMillis() > now.getMillis() ? target : target.plusMinutes(2); //target.plusDays(1);
    }

    public PendingIntent buildPendingIntent() {
        return PendingIntent.getBroadcast(
                mContext.getApplicationContext(),
                getId(),
                getIntent(),
                PendingIntent.FLAG_UPDATE_CURRENT
        );
    }

    public void set() {
        DateTime nextAlarmTime = getNextTriggerTime();

        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                nextAlarmTime.getMillis(),
                buildPendingIntent()
        );
    }

    private Podcast buildPodcast() {
        return new Podcast(
                mIntent.getStringExtra(PODCAST_NAME_KEY),
                mIntent.getStringExtra(PODCAST_FEED_KEY),
                mIntent.getStringExtra(PODCAST_AUTHOR_KEY),
                mIntent.getStringExtra(PODCAST_LOGO_KEY)
        );
    }

    private Intent buildIntent() {
        Intent intent = AlarmReceiver.getIntent(mContext);
        intent.putExtra(ALARM_ID_KEY, mId);
        intent.putExtra(ALARM_HOUR_KEY, mHourOfDay);
        intent.putExtra(ALARM_MINUTE_KEY, mMinute);
        intent.putExtra(PODCAST_NAME_KEY, mPodcast.getName());
        intent.putExtra(PODCAST_FEED_KEY, mPodcast.getRssFeedUrl());
        intent.putExtra(PODCAST_AUTHOR_KEY, mPodcast.getAuthor());
        intent.putExtra(PODCAST_LOGO_KEY, mPodcast.getLogoUrl());
        return intent;
    }
}

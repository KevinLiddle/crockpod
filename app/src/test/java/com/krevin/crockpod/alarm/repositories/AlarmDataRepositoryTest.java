package com.krevin.crockpod.alarm.repositories;

import android.content.Context;

import com.krevin.crockpod.BuildConfig;
import com.krevin.crockpod.alarm.Alarm;
import com.krevin.crockpod.podcast.Podcast;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class AlarmDataRepositoryTest {

    private Context context;
    private AlarmDataRepository alarmDataRepository;

    @Before
    public void setUp() throws Exception {
        context = RuntimeEnvironment.application;
        alarmDataRepository = new AlarmDataRepository(context);
    }

    @Test
    public void canUpsertAndFindAlarms() {
        Podcast podcast = new Podcast(
                "cool_podcast",
                "cool_podcast_url",
                "cool_author",
                "cool_art",
                "cool_big_art"
        );
        Alarm alarm = new Alarm(context, podcast, 14, 33);
        alarm.setRepeatDays(Arrays.asList(true, false, true, true, false, true, false));

        alarmDataRepository.upsert(alarm);
        Alarm fetchedAlarm = alarmDataRepository.find(alarm.getId().toString());

        assertEquals(alarm, fetchedAlarm);
    }

    @Test
    public void allReturnsAllAddedAlarmsInOrderOfTime() {
        Alarm alarm1 = new Alarm(
                context,
                new Podcast(
                        "cool_podcast",
                        "cool_podcast_url",
                        "cool_author",
                        "cool_art",
                        "cool_big_art"
                ),
                9,
                25
        );
        Alarm alarm2 = new Alarm(
                context,
                new Podcast(
                        "neat_podcast",
                        "neat_podcast_url",
                        "neat_author",
                        "neat_art",
                        "neat_big_art"
                ),
                8,
                37
        );
        Alarm alarm3 = new Alarm(
                context,
                new Podcast(
                        "lame_podcast",
                        "lame_podcast_url",
                        "lame_author",
                        "lame_art",
                        "lame_big_art"
                ),
                20,
                50);

        alarmDataRepository.upsert(alarm1);
        alarmDataRepository.upsert(alarm2);
        alarmDataRepository.upsert(alarm3);

        List<Alarm> alarms = alarmDataRepository.all();

        assertEquals(alarm2, alarms.get(0));
        assertEquals(alarm1, alarms.get(1));
        assertEquals(alarm3, alarms.get(2));
    }

    @Test
    public void removeRemovesAlarmsFromRepoById() {
        Alarm alarm1 = new Alarm(
                context,
                new Podcast(
                        "cool_podcast",
                        "cool_podcast_url",
                        "cool_author",
                        "cool_art",
                        "cool_big_art"
                ),
                4,
                40
        );
        Alarm alarm2 = new Alarm(
                context,
                new Podcast(
                        "neat_podcast",
                        "neat_podcast_url",
                        "neat_author",
                        "neat_art",
                        "neat_big_art"
                ),
                3,
                30
        );
        Alarm alarm3 = new Alarm(
                context,
                new Podcast(
                        "lame_podcast",
                        "lame_podcast_url",
                        "lame_author",
                        "lame_art",
                        "lame_big_art"
                ),
                20,
                20
        );

        alarmDataRepository.upsert(alarm1);
        alarmDataRepository.upsert(alarm2);
        alarmDataRepository.upsert(alarm3);

        List<Alarm> alarms = alarmDataRepository.all();

        assertEquals(alarm2, alarms.get(0));
        assertEquals(alarm1, alarms.get(1));
        assertEquals(alarm3, alarms.get(2));

        alarmDataRepository.remove(alarm2);

        List<Alarm> currentAlarms = alarmDataRepository.all();

        assertEquals(2, currentAlarms.size());
        assertEquals(alarm1, currentAlarms.get(0));
        assertEquals(alarm3, currentAlarms.get(1));
    }

    @Test
    public void addDoesNotAddDuplicateAlarms() {
        Podcast podcast = new Podcast(
                "cool_podcast",
                "cool_podcast_url",
                "cool_author",
                "cool_art",
                "cool_big_art"
        );
        Alarm alarm = new Alarm(
                context,
                podcast,
                14,
                33
        );

        alarmDataRepository.upsert(alarm);
        alarmDataRepository.upsert(alarm);

        assertEquals(1, alarmDataRepository.all().size());
    }
}

package thiovan.submission5.receivers;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import thiovan.submission5.R;

public class AlarmReceiver extends BroadcastReceiver {

    public static final String TYPE_RELEASE_REMINDER = "release_reminder";
    public static final String TYPE_DAILY_REMINDER = "daily_reminder";
    private static final String EXTRA_MESSAGE = "message";
    private static final String EXTRA_TITLE = "title";
    private static final String EXTRA_TYPE = "type";

    private final static int ID_RELEASE_REMINDER = 100;
    private final static int ID_DAILY_REMINDER = 101;

    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra(EXTRA_TITLE);
        String message = intent.getStringExtra(EXTRA_MESSAGE);
        String type = intent.getStringExtra(EXTRA_TYPE);
        int notifId = type.equalsIgnoreCase(TYPE_RELEASE_REMINDER) ? ID_RELEASE_REMINDER : ID_DAILY_REMINDER;

        if (type.equalsIgnoreCase(TYPE_RELEASE_REMINDER)) {
            fetchReleaseToday(context, notifId);
        } else {
            showDailyNotification(context, title, message, notifId);
        }
    }

    public void fetchReleaseToday(final Context context, final int notifId) {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        String formattedDate = df.format(c);

        AndroidNetworking.get("https://api.themoviedb.org/3/discover/movie")
                .addQueryParameter("api_key", "a050df5725f01a6d3fe03f86baecd970")
                .addQueryParameter("primary_release_date.gte", formattedDate)
                .addQueryParameter("primary_release_date.lte", formattedDate)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray results = response.getJSONArray("results");
                            List<String> releasedMovies = new ArrayList<>();
                            for (int i = 0; i < results.length(); i++) {
                                JSONObject currentMovie = results.getJSONObject(i);
                                String title = currentMovie.getString("original_title");
                                releasedMovies.add(title);
                            }

                            showReleaseNotification(context, releasedMovies, notifId);
                        } catch (JSONException e) {
                            //failed parse json
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        //fetch failed
                    }
                });
    }

    private void showReleaseNotification(Context context, List<String> movies, int notifId) {
        String CHANNEL_ID = "Channel_1";
        String CHANNEL_NAME = "Reminder channel";

        NotificationManager notificationManagerCompat = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        for (String movie : movies) {
            inboxStyle.addLine(movie + context.getResources().getString(R.string.released_today));
        }
        inboxStyle.setBigContentTitle(context.getResources().getString(R.string.new_movie_today));
        inboxStyle.setSummaryText((movies.size() - 1) + " " + context.getResources().getString(R.string.movies));
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setStyle(inboxStyle)
                .setSmallIcon(R.drawable.ic_movie_filter_white_24dp)
                .setContentTitle(context.getResources().getString(R.string.new_movie_today))
                .setColor(ContextCompat.getColor(context, android.R.color.transparent))
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setSound(alarmSound);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);

            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{1000, 1000, 1000, 1000, 1000});

            builder.setChannelId(CHANNEL_ID);

            if (notificationManagerCompat != null) {
                notificationManagerCompat.createNotificationChannel(channel);
            }
        }

        Notification notification = builder.build();

        if (notificationManagerCompat != null) {
            notificationManagerCompat.notify(notifId, notification);
        }

    }

    private void showDailyNotification(Context context, String title, String message, int notifId) {
        String CHANNEL_ID = "Channel_1";
        String CHANNEL_NAME = "Reminder channel";

        NotificationManager notificationManagerCompat = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_movie_filter_white_24dp)
                .setContentTitle(title)
                .setContentText(message)
                .setColor(ContextCompat.getColor(context, android.R.color.transparent))
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setSound(alarmSound);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);

            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{1000, 1000, 1000, 1000, 1000});

            builder.setChannelId(CHANNEL_ID);

            if (notificationManagerCompat != null) {
                notificationManagerCompat.createNotificationChannel(channel);
            }
        }

        Notification notification = builder.build();

        if (notificationManagerCompat != null) {
            notificationManagerCompat.notify(notifId, notification);
        }

    }

    public void setRepeatingAlarm(Context context, String title, String time, String message, String type) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(EXTRA_MESSAGE, message);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_TYPE, type);

        String[] timeArray = time.split(":");

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(timeArray[1]));
        calendar.set(Calendar.SECOND, 0);

        int idAlarm = type.equalsIgnoreCase(TYPE_RELEASE_REMINDER) ? ID_RELEASE_REMINDER : ID_DAILY_REMINDER;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, idAlarm, intent, 0);
        if (alarmManager != null) {
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }
    }

    public void cancelAlarm(Context context, String type) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        int requestCode = type.equalsIgnoreCase(TYPE_RELEASE_REMINDER) ? ID_RELEASE_REMINDER : ID_DAILY_REMINDER;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, 0);
        pendingIntent.cancel();

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    public boolean isAlarmSet(Context context, String type) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        int requestCode = type.equalsIgnoreCase(TYPE_RELEASE_REMINDER) ? ID_RELEASE_REMINDER : ID_DAILY_REMINDER;

        return PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_NO_CREATE) != null;
    }
}

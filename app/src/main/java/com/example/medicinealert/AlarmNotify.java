package com.example.medicinealert;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.RemoteViews;

public class AlarmNotify extends Service {

    boolean isRunning;
    int startId;
    RemoteViews remoteViews;
    MediaPlayer mediaPlayer;
    PendingIntent pendingIntent;
    Intent intent_ring;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String status = intent.getExtras().getString("data");
        int id = intent.getExtras().getInt("data1");
        String notifyText = intent.getExtras().getString("data2");

        assert status!= null;

        switch (status){
            case "on":
                startId = 1;
                break;
            case "off":
                startId = 0;
                break;
            default:
                startId = 0;
                break;
        }

        if(!this.isRunning && startId == 1){

            mediaPlayer = MediaPlayer.create(this,R.raw.ring_google);
            mediaPlayer.start();

            this.isRunning = true;
            this.startId = 0;

            NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

            intent_ring = new Intent(this,MainActivity.class);
            remoteViews = new RemoteViews(getPackageName(),R.layout.custom_notify);
            remoteViews.setImageViewResource(R.id.notify_img_id,R.drawable.notify_clock);
            remoteViews.setTextViewText(R.id.notify_text1_id,"Time to take");
            remoteViews.setTextViewText(R.id.notify_text2_id, notifyText);

            pendingIntent = PendingIntent.getActivity(this, id, intent_ring, id);
            Notification notification = new Notification.Builder(this)
                    .setSmallIcon(R.drawable.notify_clock)
                    .setContent(remoteViews)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build();
            notificationManager.notify(id,notification);
        }
        else if(this.isRunning && startId == 0){
            mediaPlayer.stop();
            this.isRunning = false;
            this.startId = 0;
        }
        return START_NOT_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        this.isRunning=false;
    }
}

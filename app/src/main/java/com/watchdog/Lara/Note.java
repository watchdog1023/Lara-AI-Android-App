package com.watchdog.Lara;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.media.MediaPlayer;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

public class Note extends AccessibilityService {
    MediaPlayer mp;
    @Override
    protected void onServiceConnected() {
        Toast.makeText(this,"Service connected", Toast.LENGTH_LONG).show();
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK;;
        info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED ;

        // If you only want this service to work with specific applications, set their
        // package names here.  Otherwise, when the service is activated, it will listen
        // to events from all applications.
        info.packageNames = new String[] {"com.whatsapp"};
        info.notificationTimeout = 100;

        setServiceInfo(info);

    }

    public void test() throws InterruptedException {
        mp = MediaPlayer.create(Note.this, R.raw.note_sound);
        mp.seekTo(0);
        mp.start();
        Thread.sleep(3000);
        mp.stop();
    }
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if(event.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
            Toast.makeText(this,"Notification Caught", Toast.LENGTH_LONG).show();
            try {
                test();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onInterrupt() {

    }
}
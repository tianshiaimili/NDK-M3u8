/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tvb.smartdownload.push;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.SparseArray;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import com.tvb.smartdownload.Constants;
import com.tvb.smartdownload.R;
import com.tvb.smartdownload.utils.LogUtils;
/**
 * IntentService responsible for handling GCM messages.
 */
public class GCMIntentService extends GCMBaseIntentService {

    @SuppressWarnings("hiding")
    private static final String TAG = "GCMIntentService";
    
    @Override
    public void onCreate() {
    	super.onCreate();
    	LogUtils.i("------------onCreate");
    	
    	
    }
    
    public GCMIntentService() {
        super(CommonUtilities.SENDER_ID);
    }

    @Override
    protected void onRegistered(Context context, String registrationId) {
        LogUtils.i("Device registered: regId = " + registrationId);
        CommonUtilities.displayMessage(context, getString(R.string.gcm_registered));
      //  ServerUtilities.register(context, registrationId);
    }

    @Override
    protected void onUnregistered(Context context, String registrationId) {
        LogUtils.i("Device unregistered");
        CommonUtilities.displayMessage(context, getString(R.string.gcm_unregistered));
        if (GCMRegistrar.isRegisteredOnServer(context)) {
         //   ServerUtilities.unregister(context, registrationId);
        	GCMRegistrar.setRegisteredOnServer(context, true);
        } else {
            // This callback results from the call to unregister made on
            // ServerUtilities when the registration to the server failed.
            LogUtils.i("Ignoring unregister callback");
        }
    }

    /**
     * 接收从GCM发送过来的消息
     */
    @Override
    protected void onMessage(Context context, Intent intent) {
        LogUtils.i("Received message");
        String message = "";
        SparseArray<String> array = new SparseArray<String>();
        try {
            String action = intent.getAction();
            if (action.equals("com.google.android.c2dm.intent.RECEIVE")) {
            	message = intent.getStringExtra("msg");
            	LogUtils.i("message --msg == "+message);
            	array.put(0, message);
            	
            	LogUtils.i("message--score == "+message);
            	message = intent.getStringExtra("score");
            	array.put(1, message);
            	
            	message = intent.getStringExtra("downUri");
            	LogUtils.i("message--downUri == "+message);
            	array.put(2, message);
            }
            Intent intent2  = new Intent(Constants.DOWN_LOAD_BROADCAST_ACTION);
//        	LogUtils.i("message--downUri == "+array.get(2));
            intent2.putExtra("downUri", array.get(2));
            sendBroadcast(intent2);
            
        } catch(Exception e){
        	e.printStackTrace();
        }
        
     // String message = getString(R.string.gcm_message);
        CommonUtilities.displayMessage(context, message);
        // notifies user
//        generateNotification(context, message);
    }

    @Override
    protected void onDeletedMessages(Context context, int total) {
        LogUtils.i("Received deleted messages notification");
        String message = getString(R.string.gcm_deleted, total);
        CommonUtilities.displayMessage(context, message);
        // notifies user
//        context.getApplicationInfo().
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName componentName = activityManager.getRunningTasks(1).get(0).topActivity;
//        componentName.
//        generateNotification(context, message,((Activity)componentName));
    }

    @Override
    public void onError(Context context, String errorId) {
        LogUtils.i("Received error: " + errorId);
        CommonUtilities.displayMessage(context, getString(R.string.gcm_error, errorId));
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        // log message
        LogUtils.i("Received recoverable error: " + errorId);
        CommonUtilities.displayMessage(context, getString(R.string.gcm_recoverable_error,
                errorId));
        return super.onRecoverableError(context, errorId);
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    private static void generateNotification(Context context, String message,Activity activity) {
        int icon = R.drawable.ic_launcher;
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(icon, message, when);
        String title = context.getString(R.string.app_name);
        Intent notificationIntent = new Intent(context, activity.getClass());
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent =
                PendingIntent.getActivity(context, 0, notificationIntent, 0);
        notification.setLatestEventInfo(context, title, message, intent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, notification);
    }

}

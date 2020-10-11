package com.financialcalculator.firebase;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.financialcalculator.PrefManager.SharedPrefManager;
import com.financialcalculator.R;
import com.financialcalculator.home.MainActivity;
import com.financialcalculator.utility.Constants;
import com.financialcalculator.utility.GenericImageLoader;
import com.financialcalculator.utility.Logger;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Calendar;
import java.util.Map;


/**
 * <p></p>
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFcmListenerService";
    private NotificationManager mNotificationManager;

    /**
     * Called when message is received.
     *
     * @param remoteMessage remoteMessage to get data from.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Logger.d("Push message received " + remoteMessage.getData());
        if (remoteMessage.getData().size() > 0) {
            String message, dld, title, imageUrl, messageId, deliveryId, channelID;
            Map<String, String> data = remoteMessage.getData();
            message = data.get("msg");
            dld = data.get("dld");
            title = data.get("title");
            imageUrl = data.get("img");

            messageId = (!TextUtils.isEmpty(data.get("_mId")) ? data.get("_mId") : data.get("mId"));

            deliveryId = data.get("_dId");
            channelID = TextUtils.isEmpty(data.get("channelId")) ? Constants.DEFAULT_CHANNEL_ID : data.get("channelId");
//            }

            Logger.d(TAG, "mID " + messageId + " dld " + dld + " Bundle: " + data);
            int msgId = TextUtils.isEmpty(messageId) || !TextUtils.isDigitsOnly(messageId) ? 1 : Integer.parseInt(messageId);
            Bundle data1 = new Bundle();
            for (Map.Entry<String, String> entry : data.entrySet()) {
                data1.putString(entry.getKey(), entry.getValue());
                Logger.e("Key : ", entry.getKey());
                Logger.e("Value : ", entry.getValue());
            }

            try {
//                if (TextUtils.isEmpty(data.getString("productId"))) {
                PushNotification pushNotification = new PushNotification();
                pushNotification.setTime(Calendar.getInstance().getTimeInMillis());
                pushNotification.setTitle(title);
                pushNotification.setMsg(message);
                pushNotification.setDld(dld);
                pushNotification.setWurl(data.get("wurl"));
                pushNotification.setImageUrl(imageUrl);
                //todo: save push notification for showing in list
                //GeneralUtils.savePushNotification(pushNotification, this);
//                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            // not from CleverTap handle yourself or pass to another provider
            sendNotification(msgId, title, message, data1, channelID);

        }
        // [END_EXCLUDE]
    }

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param msgId     GCM msgId.
     * @param message   GCM message received.
     * @param data      GCM data.
     * @param channelID GCM channelID.
     */
    private void sendNotification(final int msgId, String title, final String message, Bundle data, final String channelID) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction(Constants.ActionTags.ACTION_NOTIFY);
        intent.putExtras(data);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, msgId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        String notificationTitle = TextUtils.isEmpty(title) ? getString(R.string.app_name) : title;
        NotificationCompat.Style style = new NotificationCompat.BigTextStyle()
                .bigText(message)
                .setBigContentTitle(notificationTitle);


        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, channelID)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setColor(Color.parseColor("#A9143C"))
                .setStyle(style)
                .setContentTitle(notificationTitle)
                .setContentText(message);

        mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        // Notification Channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final NotificationChannel notificationChannel = new NotificationChannel(channelID, getString(R.string.text_channel_promotional_name), NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription(getString(R.string.text_channel_promotional_description));
            if (mNotificationManager != null)
                mNotificationManager.createNotificationChannel(notificationChannel);
        }

        if (data.containsKey("img") && data.getString("img", "").length() > 0) {
            final String img = data.getString("img");
            if (img != null && img.length() > 0) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    try {
                        GenericImageLoader.loadImageAsBitmap(getApplicationContext(), img, new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, Transition<? super Bitmap> transition) {
                                mBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(resource).setSummaryText(message));
                                /*try {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                                                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                                                .build();
                                        final NotificationChannel notificationChannel = new NotificationChannel(channelID, getString(R.string.text_channel_promotional_name), NotificationManager.IMPORTANCE_HIGH);
                                        notificationChannel.setDescription(getString(R.string.text_channel_promotional_description));
                                        notificationChannel.setSound(alarmSound, audioAttributes);
                                        if (mNotificationManager != null)
                                            mNotificationManager.createNotificationChannel(notificationChannel);
                                    }
                                    if (mNotificationManager != null)*/
                                showNotification(msgId, mBuilder.build());
                                    /*mNotificationManager.notify(msgId, mBuilder.build());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }*/
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {

                            }

                            @Override
                            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                /*try {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        final NotificationChannel notificationChannel = new NotificationChannel(channelID, getString(R.string.text_channel_promotional_name), NotificationManager.IMPORTANCE_HIGH);
                                        if (mNotificationManager != null)
                                            mNotificationManager.createNotificationChannel(notificationChannel);
                                    }
                                    if (mNotificationManager != null)*/
                                showNotification(msgId, mBuilder.build());
                                    /*mNotificationManager.notify(msgId, mBuilder.build());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }*/
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                });
            } else {
                showNotification(msgId, mBuilder.build());
            }
        } else {
            showNotification(msgId, mBuilder.build());
        }
    }

    private void sendNotificationWithImgSlider(final int msgId, String title, final String message, Bundle data, final String channelID) {
        /*try {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setAction(Constants.ActionTags.ACTION_NOTIFY);
            intent.putExtras(data);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, msgId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            ////
            RemoteViews expandedView = new RemoteViews(getPackageName(), R.layout.notification_slider_layout);

            // notification title
            expandedView.setTextViewText(R.id.textViewTitle, title);
            expandedView.setTextViewText(R.id.textViewSubTitle, message);
            if (data.containsKey("sliderImgUrl") && data.getString("sliderImgUrl", "").length() > 0) {

                String[] elements = data.getString("sliderImgUrl", "").split(",");
                if (elements.length > 1) {
                    for (String imgUrl : elements) { //for loop start
                        RemoteViews viewFlipperImage = new RemoteViews(getPackageName(), R.layout.notification_slider_image);
                        if (TextUtils.isEmpty(imgUrl)) { // default image
                            viewFlipperImage.setImageViewResource(R.id.imageViewSliderNotif, R.drawable.ic_credit_card);
                        } else {
                            try {
                                // Below line must not be executed on Mainthread.
                                BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inPreferredConfig = Bitmap.Config.RGB_565;

                                InputStream is = (InputStream) new URL(imgUrl).getContent();
                                Bitmap imgthumBitmap = BitmapFactory.decodeStream(is, null, options);
                                viewFlipperImage.setImageViewBitmap(R.id.imageViewSliderNotif, imgthumBitmap);
                            } catch (Exception e) {
                                // set default image
                                viewFlipperImage.setImageViewResource(R.id.imageViewSliderNotif, R.mipmap.ic_launcher);
                            }
                        }
                        // Adding each image view in the viewflipper.
                        expandedView.addView(R.id.viewFlipper, viewFlipperImage);
                    } // for loop finish
                }
            } else {
                if (!TextUtils.isEmpty(data.getString("wzrk_bp"))) {
                    RemoteViews viewFlipperImage = new RemoteViews(getPackageName(), R.layout.notification_slider_image);
                    try {
                        // Below line must not be executed on Mainthread.
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inPreferredConfig = Bitmap.Config.RGB_565;

                        InputStream is = (InputStream) new URL(data.getString("wzrk_bp")).getContent();
                        Bitmap imgthumBitmap = BitmapFactory.decodeStream(is, null, options);
                        viewFlipperImage.setImageViewBitmap(R.id.imageViewSliderNotif, imgthumBitmap);
                    } catch (Exception e) {
                        // set default image
                        viewFlipperImage.setImageViewResource(R.id.imageViewSliderNotif, R.mipmap.ic_launcher);
                    }
                    expandedView.addView(R.id.viewFlipper, viewFlipperImage);

                }
            }
            String notificationTitle = TextUtils.isEmpty(title) ? getString(R.string.app_name) : title;
            NotificationCompat.Style style = new NotificationCompat.BigTextStyle()
                    .bigText(message)
                    .setBigContentTitle(notificationTitle);

            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + getPackageName() + "/" + R.raw.notification);

            final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, channelID)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                    .setSmallIcon(R.drawable.ic_notify_white)
                    .setColor(Color.parseColor("#A9143C"))
                    .setStyle(style)
                    .setSound(alarmSound)
                    .setContentTitle(notificationTitle)
                    .setCustomBigContentView(expandedView)
                    .setContentText(message);


            mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

            // Notification Channel
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .build();
                final NotificationChannel notificationChannel = new NotificationChannel(channelID, getString(R.string.text_channel_promotional_name), NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setDescription(getString(R.string.text_channel_promotional_description));
                notificationChannel.setSound(alarmSound, audioAttributes);
                if (mNotificationManager != null)
                    mNotificationManager.createNotificationChannel(notificationChannel);
            }


            showNotification(msgId, mBuilder.build());

        } catch (Exception e) {
            sendNotification(msgId, title, message, data, channelID);
        }*/
    }

    private void showNotification(int msgId, Notification notification) {
        if (null != mNotificationManager)
            mNotificationManager.notify(msgId, notification);
    }

    @Override
    public void onNewToken(@NonNull String newToken) {
        super.onNewToken(newToken);
        Logger.d(TAG, "Refreshed token: " + newToken);
        // Saving below refreshedToken to shared Preferences
        storeTokenInSPref(newToken);
        // sending refreshedToken to server
        sendRegistrationToServer(newToken);
        Logger.d(TAG, newToken);
    }

    private void storeTokenInSPref(String token) {
        SharedPrefManager.getInstance(this).putStringValueForKey(Constants.PREF_GCM_TOKEN, token);
    }

    private void sendRegistrationToServer(String token) {
        /*if (!SharedPrefManager.getInstance(this).getBooleanValueForKey(Constants.IS_TOKEN_SENT_TO_SERVER, false) && GeneralUtils.isValidCustomer(HttpService.getInstance().getAppCustomer())) {
            //send token to backend
            Logger.d(TAG, "GCM Registration Token (sending to server): " + token);
            HttpService.getInstance().saveDeviceInfo(token, true).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<BaseResponse>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(BaseResponse baseResponse) {
                            if (baseResponse.isSuccess()) {
                                SharedPrefManager.getInstance(MyFirebaseMessagingService.this).putBooleanValueForKey(Constants.IS_TOKEN_SENT_TO_SERVER, true);
                                Logger.d(TAG, "GCM Registration Token sent and saved successfully");
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                        }

                        @Override
                        public void onComplete() {
                        }
                    });
        } else {
            Logger.d(TAG, "GCM Registration Token (already sent to server or customer not logged in): " + token);
        }
        AppsFlyerLib.getInstance().updateServerUninstallToken(getApplicationContext(), token);*/
    }


}

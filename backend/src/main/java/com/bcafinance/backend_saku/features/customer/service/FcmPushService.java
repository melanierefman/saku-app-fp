package com.bcafinance.backend_saku.features.customer.service;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FcmPushService {

    public void sendPush(String fcmToken, String title, String body, Map<String, String> data) {
        if (fcmToken == null || fcmToken.isBlank()) {
            log.debug("FCM Token is empty, skipping push notification.");
            return;
        }
        if (FirebaseApp.getApps().isEmpty()) {
            log.warn("FirebaseApp not initialized, skipping push notification.");
            return;
        }

        try {
            Notification notification = Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build();

            AndroidNotification androidNotification = AndroidNotification.builder()
                    .setIcon("ic_notification_saku")
                    .setColor("#FF7A00")
                    .setSound("default")
                    .setPriority(AndroidNotification.Priority.HIGH)
                    .setChannelId("saku_loan_channel")
                    .build();

            AndroidConfig androidConfig = AndroidConfig.builder()
                    .setPriority(AndroidConfig.Priority.HIGH)
                    .setNotification(androidNotification)
                    .build();

            Message.Builder messageBuilder = Message.builder()
                    .setToken(fcmToken)
                    .setNotification(notification)
                    .setAndroidConfig(androidConfig);

            if (data != null && !data.isEmpty()) {
                messageBuilder.putAllData(data);
            }

            String response = FirebaseMessaging.getInstance().send(messageBuilder.build());
            log.info("FCM push notification sent successfully: {}", response);
        } catch (Exception e) {
            log.error("Failed to send FCM push notification to token [{}]: {}", fcmToken, e.getMessage());
        }
    }
}

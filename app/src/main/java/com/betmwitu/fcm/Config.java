package com.betmwitu.fcm;

/**
 * Created by root on 8/3/17.
 */

public class Config {

    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    public static final String SHARED_PREF = "ah_firebase";

    public static final String LOGIN_URL = "http://sikumojaventures.com/betmwitu/login.php";
    public static final String TAG_SUCCESS = "success";
    public static final String TAG_MESSAGE = "message";
    public static final String TAG_USER_NAME = "user_name";

    public static final String SIGNUP_URL = "http://sikumojaventures.com/betmwitu/registration.php";

    public static final String FORGORT_PASS_URL = "http://sikumojaventures.com/betmwitu/reset_pass.php";
}
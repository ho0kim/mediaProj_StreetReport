package com.example.streetreport;

import android.app.Activity;
import android.app.Application;

import com.kakao.auth.KakaoSDK;

public class GlobalApplication extends Application {

    private static volatile GlobalApplication instance = null;
    private static volatile Activity currentActivity = null;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        KakaoSDK.init(new KakaoSDKAdapter());
    }

    public static GlobalApplication getGlobalApplicationContext(){
        return instance;
    }

    public static void setCurrentActivity(Activity currentActivity){
        GlobalApplication.currentActivity = currentActivity;
    }

    public static Activity getCurrentActivity(){
        return currentActivity;
    }
}



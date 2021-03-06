package io.agora.rtm;

import android.app.Application;

import io.agora.rtm.rtmtutorial.ChatManager;

public class AGApplication extends Application {
    private static AGApplication sInstance;
    private ChatManager mChatManager;


    public static AGApplication the() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        mChatManager = new ChatManager(this);
        mChatManager.init();
    }

    public ChatManager getChatManager() {
        return mChatManager;
    }
}


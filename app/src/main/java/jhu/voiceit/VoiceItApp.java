package jhu.voiceit;

import com.firebase.client.Firebase;

/**
 * Created by GradyXiao on 4/22/16.
 */
public class VoiceItApp extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}

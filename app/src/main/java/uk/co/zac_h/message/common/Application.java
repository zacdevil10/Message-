package uk.co.zac_h.message.common;

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(new LifecycleHandler());
    }
}

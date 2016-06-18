package cn.edu.hit.project.ec;

import android.app.Application;
import android.content.Intent;

import cn.edu.hit.project.ec.models.user.User;
import cn.edu.hit.project.ec.services.NotificationService;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class App extends Application {
    public final static String API_SERVER = "123.206.59.206:8081";
    public final static String STREAM_SERVER = "115.28.134.90";
    public final static long SESSION_ID = System.currentTimeMillis();

    private int sensorId;
    private String apiToken;

    @Override
    public void onCreate() {
        super.onCreate();
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(this)
                .name(String.format("database:%s.realm", sensorId))
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfig);

        Intent intent = new Intent(this, NotificationService.class);
        startService(intent);
    }

    public void setUser(User user) {
        if (user == null) {
            sensorId = -1;
            apiToken = null;
        } else {
            sensorId = user.id;
            apiToken = user.apiToken;
        }
    }

    public int getSensorId() {
        return sensorId;
    }

    public String getApiToken() {
        return apiToken;
    }
}

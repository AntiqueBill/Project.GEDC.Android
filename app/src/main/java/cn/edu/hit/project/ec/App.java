package cn.edu.hit.project.ec;

import android.app.Application;
import android.content.Intent;

import cn.edu.hit.project.ec.services.NotificationService;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class App extends Application {
    public final static int SENSOR_ID = 1;
    public final static long SESSION_ID = System.currentTimeMillis();

    @Override
    public void onCreate() {
        super.onCreate();
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(this)
                .name(String.format("database:%s.realm", SENSOR_ID))
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfig);

        Intent intent = new Intent(this, NotificationService.class);
        startService(intent);
    }
}

package cn.edu.hit.project.ec;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import java.util.regex.Pattern;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class DebugApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        RealmConfiguration realmConfig = new RealmConfiguration.Builder(this)
                .name(String.format("database:%s.realm", App.SENSOR_ID)) // database:{sensorId}
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfig);

        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                        .build());
    }
}

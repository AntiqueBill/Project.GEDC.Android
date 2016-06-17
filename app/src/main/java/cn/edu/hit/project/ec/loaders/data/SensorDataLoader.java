package cn.edu.hit.project.ec.loaders.data;

import android.content.Context;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.edu.hit.project.ec.App;
import cn.edu.hit.project.ec.models.data.SensorData;
import cn.edu.hit.project.ec.network.DataService;
import cn.edu.hit.project.ec.network.ServiceFactory;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Response;

public class SensorDataLoader extends BaseDataLoader<List<SensorData>> {
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

    public SensorDataLoader(Context context, Date from, Date to, OnLoadFailedListener listener) {
        super(context, from, to, listener);
    }

    @Override
    protected void onStartLoading() {
        forceLoad(); // 在找到合适的本地数据过期判断之前，强制从网络获取数据
//        RealmQuery<SensorData> query = Realm.getDefaultInstance().where(SensorData.class);
//        RealmResults<SensorData> dataList = query.between("timestamp", from, to).findAllSorted("timestamp");
//        RealmResults<SensorData> outOfDateData = dataList.where().notEqualTo("sessionId", App.SESSION_ID).findAll();
//        if (dataList.size() == 0 || outOfDateData.size() > 0) {
//            forceLoad();
//        } else {
//            deliverResult(dataList);
//        }
    }

    @Override
    @Nullable
    public List<SensorData> loadInBackground() {
        List<SensorData> dataList = new ArrayList<>();
        DataService service = ServiceFactory.getService(DataService.class);
        Call<List<SensorData>> call = service.listSensorData(app.getSensorId(), format.format(from), format.format(to), app.getApiToken());
        try {
            Response<List<SensorData>> response = call.execute();
            if (response.code() == 401) {
                listener.onLoadFailed();
                return null;
            }
            dataList = response.body();
            Realm realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            for (SensorData data : dataList) {
                data.setSessionId(App.SESSION_ID);
                realm.copyToRealmOrUpdate(data);
            }
            realm.commitTransaction();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataList;
    }
}

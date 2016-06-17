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
import cn.edu.hit.project.ec.models.data.HourlyData;
import cn.edu.hit.project.ec.network.DataService;
import cn.edu.hit.project.ec.network.ServiceFactory;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Response;

public class HourlyDataLoader extends BaseDataLoader<List<HourlyData>> {
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:00:00", Locale.CHINA);

    public HourlyDataLoader(Context context, Date from, Date to, OnLoadFailedListener listener) {
        super(context, from, to, listener);
    }

    @Override
    protected void onStartLoading() {
        forceLoad(); // 在找到合适的本地数据过期判断之前，强制从网络获取数据
//        RealmQuery<HourlyData> query = Realm.getDefaultInstance().where(HourlyData.class);
//        RealmResults<HourlyData> dataList = query.between("timestamp", from, to).findAllSorted("timestamp");
//        RealmResults<HourlyData> outOfDateData = dataList.where().notEqualTo("sessionId", App.SESSION_ID).findAll();
//        if (dataList.size() == 0 || outOfDateData.size() > 0) {
//            forceLoad();
//        } else {
//            deliverResult(dataList);
//        }
    }

    @Override
    @Nullable
    public List<HourlyData> loadInBackground() {
        List<HourlyData> dataList = new ArrayList<>();
        DataService service = ServiceFactory.getService(DataService.class);
        Call<List<HourlyData>> call = service.listHourlyData(app.getSensorId(), format.format(from), format.format(to), app.getApiToken());
        try {
            Response<List<HourlyData>> response = call.execute();
            if (response.code() == 401) {
                listener.onLoadFailed();
                return null;
            }
            dataList = response.body();
            Realm realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            for (HourlyData data : dataList) {
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

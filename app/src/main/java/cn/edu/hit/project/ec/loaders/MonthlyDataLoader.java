package cn.edu.hit.project.ec.loaders;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.edu.hit.project.ec.App;
import cn.edu.hit.project.ec.models.data.MonthlyData;
import cn.edu.hit.project.ec.network.DataService;
import cn.edu.hit.project.ec.network.ServiceFactory;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import retrofit2.Call;

public class MonthlyDataLoader extends AsyncTaskLoader<List<MonthlyData>> {
    private Date to;
    private Date from;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-01", Locale.CHINA);

    public MonthlyDataLoader(Context context, Date from, Date to) {
        super(context);
        this.to = to;
        this.from = from;
    }

    @Override
    protected void onStartLoading() {
        RealmQuery<MonthlyData> query = Realm.getDefaultInstance().where(MonthlyData.class);
        RealmResults<MonthlyData> dataList = query.between("timestamp", from, to).findAllSorted("timestamp");
        RealmResults<MonthlyData> outOfDateData = dataList.where().notEqualTo("sessionId", App.SESSION_ID).findAll();
        if (dataList.size() == 0 || outOfDateData.size() > 0) {
            forceLoad();
        } else {
            deliverResult(dataList);
        }
    }

    @Override
    @Nullable
    public List<MonthlyData> loadInBackground() {
        List<MonthlyData> dataList = new ArrayList<>();
        DataService service = ServiceFactory.getService(DataService.class);
        Call<List<MonthlyData>> call = service.listMonthlyData(App.SENSOR_ID, format.format(from), format.format(to));
        try {
            dataList = call.execute().body();
            Realm realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            for (MonthlyData data : dataList) {
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

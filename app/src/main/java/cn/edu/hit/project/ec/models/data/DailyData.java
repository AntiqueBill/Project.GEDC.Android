package cn.edu.hit.project.ec.models.data;

import android.support.annotation.NonNull;

import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.edu.hit.project.ec.models.base.Coordinate;
import cn.edu.hit.project.ec.utils.DateUtils;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

import static com.google.common.base.Preconditions.checkNotNull;

public class DailyData extends RealmObject implements BaseData {
    @PrimaryKey
    private int id;
    private int count;
    private double ibi;
    private double bpm;
    private double tem;
    private Date timestamp;
    private long sessionId;

    public DailyData() {}

    @Override
    public int getId() {
        return id;
    }

    @Override
    public double getBpm() {
        return bpm;
    }

    @Override
    public double getIbi() {
        return ibi;
    }

    @Override
    public double getTem() {
        return tem;
    }

    @Override
    public Date getTimestamp() {
        return timestamp;
    }

    @Override
    public long getSessionId() {
        return sessionId;
    }
    @Override
    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public static Coordinate<Date, DailyData>[] getCoordinates(@NonNull List<DailyData> dataList, Date start, Date end) {
        checkNotNull(dataList);
        // Assume results are sorted by timestamp
        int cursor = 0;
        int dataSize = dataList.size();
        int days = DateUtils.diff(Calendar.DATE, start, end) + 1;
        Coordinate<Date, DailyData>[] coordinates = new Coordinate[days];
        for (int i = 0; i < days; i++) {
            Date date = DateUtils.add(start, Calendar.DATE, i);
            DailyData value = null;
            for (int j = cursor; j < dataSize; j++) {
                if (DateUtils.equals(Calendar.DATE, date, dataList.get(j).timestamp)) {
                    value = dataList.get(j);
                    cursor = j + 1;
                    break;
                }
            }
            coordinates[i] = new Coordinate<>(date, value);
        }
        return coordinates;
    }
}

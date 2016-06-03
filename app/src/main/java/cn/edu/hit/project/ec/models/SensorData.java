package cn.edu.hit.project.ec.models;


import android.support.annotation.NonNull;

import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.edu.hit.project.ec.models.base.Coordinate;
import cn.edu.hit.project.ec.utils.DateUtils;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

import static com.google.common.base.Preconditions.checkNotNull;

@RealmClass
public class SensorData extends RealmObject implements BaseData {
    @PrimaryKey
    private int id;
    private int count;
    public double acv;
    public double acx;
    public double acy;
    public double acz;
    private double ibi;
    private double bpm;
    private double tem;
    private Date timestamp;
    private long sessionId;

    public SensorData() {}

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

    public static Coordinate<Date, SensorData>[] getCoordinates(@NonNull List<SensorData> dataList, Date start, Date end) {
        checkNotNull(dataList);
        // Assume results are sorted by timestamp
        int cursor = 0;
        int dataSize = dataList.size();
        int minutes = DateUtils.diff(Calendar.MINUTE, start, end) + 1;
        Coordinate<Date, SensorData>[] coordinates = new Coordinate[minutes];
        for (int i = 0; i < minutes; i++) {
            Date minute = DateUtils.add(start, Calendar.MINUTE, i);
            SensorData value = null;
            // Get first point of every minute
            for (int j = cursor; j < dataSize; j++) {
                if (DateUtils.equals(Calendar.MINUTE, minute, dataList.get(j).timestamp)) {
                    value = dataList.get(j);
                    cursor = j + 1;
                    break;
                }
            }
            coordinates[i] = new Coordinate<>(minute, value);
        }
        return coordinates;
    }
}

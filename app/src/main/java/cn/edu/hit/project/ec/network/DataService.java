package cn.edu.hit.project.ec.network;

import java.util.List;

import cn.edu.hit.project.ec.models.DailyData;
import cn.edu.hit.project.ec.models.HourlyData;
import cn.edu.hit.project.ec.models.MonthlyData;
import cn.edu.hit.project.ec.models.SensorData;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DataService {
    @GET("{sensor_id}")
    Call<List<SensorData>> listSensorData(@Path("sensor_id") int sensorId, @Query("from") String from, @Query("to") String to);

    @GET("{sensor_id}/hour")
    Call<List<HourlyData>> listHourlyData(@Path("sensor_id") int sensorId, @Query("from") String from, @Query("to") String to);

    @GET("{sensor_id}/day")
    Call<List<DailyData>> listDailyData(@Path("sensor_id") int sensorId, @Query("from") String from, @Query("to") String to);

    @GET("{sensor_id}/month")
    Call<List<MonthlyData>> listMonthlyData(@Path("sensor_id") int sensorId, @Query("from") String from, @Query("to") String to);
}

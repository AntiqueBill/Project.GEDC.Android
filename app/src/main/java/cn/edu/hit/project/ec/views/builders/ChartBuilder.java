package cn.edu.hit.project.ec.views.builders;

import android.support.annotation.NonNull;

import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.edu.hit.project.ec.models.data.BaseData;
import cn.edu.hit.project.ec.models.data.DailyData;
import cn.edu.hit.project.ec.models.data.HourlyData;
import cn.edu.hit.project.ec.models.data.MonthlyData;
import cn.edu.hit.project.ec.models.data.SensorData;
import cn.edu.hit.project.ec.models.base.Coordinate;
import cn.edu.hit.project.ec.models.base.DataScale;

import static com.google.common.base.Preconditions.checkNotNull;

public class ChartBuilder {
    public final static DataTransformer HEART_RATE_TRANSFORMER;
    public final static DataTransformer TEMPERATURE_TRANSFORMER;

    private Date to;
    private Date from;
    private int color;
    private DataScale scale;
    private ValueLineChart view;
    private DateFormat dateFormat;
    private List<BaseData> dataList;
    private DataTransformer transformer;

    public interface DataTransformer {
        float getValue(BaseData data);
    }

    static {
        HEART_RATE_TRANSFORMER = new DataTransformer() {
            @Override
            public float getValue(BaseData data) {
                return (float) data.getBpm();
            }
        };
        TEMPERATURE_TRANSFORMER = new DataTransformer() {
            @Override
            public float getValue(BaseData data) {
                return (float) data.getTem();
            }
        };
    }

    public ChartBuilder(@NonNull ValueLineChart view) {
        this.view = view;
    }

    public ChartBuilder withColor(int color) {
        this.color = color;
        return this;
    }

    public ChartBuilder withScale(@NonNull DataScale scale) {
        this.scale = scale;
        switch (scale) {
            case MONTHLY:   dateFormat = new SimpleDateFormat("MMM", Locale.CHINESE); break;
            case DAILY:     dateFormat = new SimpleDateFormat("dd", Locale.CHINESE); break;
            case HOURLY:    dateFormat = new SimpleDateFormat("HH", Locale.CHINESE); break;
            case SENSOR:    dateFormat = new SimpleDateFormat("mm", Locale.CHINESE); break;
        }
        return this;
    }

    public ChartBuilder withData(@NonNull List<BaseData> dataList) {
        this.dataList = dataList;
        return this;
    }

    public ChartBuilder withDateRange(@NonNull Date from, @NonNull Date to) {
        this.from = from;
        this.to = to;
        return this;
    }

    public ChartBuilder withTransformer(@NonNull DataTransformer transformer) {
        this.transformer = transformer;
        return this;
    }

    public Coordinate[] getCoordinates() {
        switch (scale) {
            case MONTHLY:   return MonthlyData.getCoordinates((List<MonthlyData>)(List<?>) dataList, from, to);
            case DAILY:     return DailyData.getCoordinates((List<DailyData>)(List<?>) dataList, from, to);
            case HOURLY:    return HourlyData.getCoordinates((List<HourlyData>)(List<?>) dataList, from, to);
            case SENSOR:    return SensorData.getCoordinates((List<SensorData>)(List<?>)dataList, from, to);
        }
        return null;
    }

    private String[] getLabels(Coordinate[] coordinates) {
        String[] labels = new String[coordinates.length];
        for (int i = 0; i < coordinates.length; i++) {
            labels[i] = dateFormat.format((Date) coordinates[i].x);
        }
        return labels;
    }

    private float[] getValues(Coordinate[] coordinates) {
        float[] values = new float[coordinates.length];
        for (int i = 0; i < coordinates.length; i++) {
            if (coordinates[i].y == null) {
                values[i] = 0;
            } else {
                values[i] = transformer.getValue((BaseData) coordinates[i].y);
            }
        }
        return values;
    }

    private ValueLineSeries createSeries(String[] labels, float[] values) {
        ValueLineSeries series = new ValueLineSeries();
        for (int i = 0, len = Math.min(labels.length, values.length); i < len; i++) {
            series.addPoint(new ValueLinePoint(labels[i], values[i]));
        }
        series.setColor(color);
        return series;
    }

    public void build() {
        checkNotNull(to);
        checkNotNull(from);
        checkNotNull(view);
        checkNotNull(scale);
        checkNotNull(dataList);
        checkNotNull(transformer);

        Coordinate<String, BaseData>[] coordinates = getCoordinates();
        if (coordinates == null) {
            return;
        }

        ValueLineSeries series = createSeries(getLabels(coordinates), getValues(coordinates));
        view.addSeries(series);
        view.startAnimation();
    }
}

package cn.edu.hit.project.ec;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.eazegraph.lib.charts.ValueLineChart;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.edu.hit.project.ec.loaders.data.BaseDataLoader;
import cn.edu.hit.project.ec.loaders.data.DailyDataLoader;
import cn.edu.hit.project.ec.loaders.data.HourlyDataLoader;
import cn.edu.hit.project.ec.loaders.data.MonthlyDataLoader;
import cn.edu.hit.project.ec.loaders.data.SensorDataLoader;
import cn.edu.hit.project.ec.models.base.Coordinate;
import cn.edu.hit.project.ec.models.base.DataScale;
import cn.edu.hit.project.ec.models.base.DataType;
import cn.edu.hit.project.ec.models.data.BaseData;
import cn.edu.hit.project.ec.models.user.User;
import cn.edu.hit.project.ec.utils.DateUtils;
import cn.edu.hit.project.ec.utils.ViewUtils;
import cn.edu.hit.project.ec.views.adapters.ListAdapter;
import cn.edu.hit.project.ec.views.builders.ChartBuilder;

public class DataActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks,
                AdapterView.OnItemClickListener,
                BaseDataLoader.OnLoadFailedListener {
    private String mUnit;
    private Date mDateTo;
    private Date mDateFrom;
    private DataType mType;
    private DataScale mScale;
    private DateFormat mDateFormat;
    private List<BaseData> mDataList;

    private ListView mList;
    private ValueLineChart mChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mChart = (ValueLineChart) findViewById(R.id.chart);
        mList = (ListView) findViewById(R.id.list);
        mChart.setVisibility(View.GONE);
        mList.setVisibility(View.GONE);
        mList.setOnItemClickListener(this);

        mDataList = new ArrayList<>();

        Intent intent = getIntent();
        mDateTo = new Date(intent.getLongExtra("to", 0));
        mDateFrom = new Date(intent.getLongExtra("from", 0));
        mType = DataType.fromInteger(intent.getIntExtra("type", 0));
        mScale = DataScale.fromInteger(intent.getIntExtra("scale", 0));

        switch (mScale) {
            case MONTHLY:   mDateFormat = new SimpleDateFormat("y年M月", Locale.CHINESE); break;
            case DAILY:     mDateFormat = new SimpleDateFormat("y年M月d日", Locale.CHINESE); break;
            case HOURLY:    mDateFormat = new SimpleDateFormat("y年M月d日 H时", Locale.CHINESE); break;
            case SENSOR:    mDateFormat = new SimpleDateFormat("y年M月d日 H时m分", Locale.CHINESE); break;
        }

        switch (mType) {
            case BPM: mUnit = "次/分钟"; break;
            case TEM: mUnit = "℃"; break;
        }
        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        switch (mScale) {
            case MONTHLY:
                return new MonthlyDataLoader(this, mDateFrom, mDateTo, this);
            case DAILY:
                return new DailyDataLoader(this, mDateFrom, mDateTo, this);
            case HOURLY:
                return new HourlyDataLoader(this, mDateFrom, mDateTo, this);
            case SENSOR:
                return new SensorDataLoader(this, mDateFrom, mDateTo, this);
            default:
                return null;
        }
    }

    private void showEmptyMessage() {
        ViewGroup rootView = (ViewGroup) ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        Snackbar.make(rootView, getString(R.string.error_data_not_found), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        if (data != null) {
            ChartBuilder builder = new ChartBuilder(mChart).withScale(mScale).withData((List<BaseData>) data);
            switch (mType) {
                case BPM: builder = builder.withColor(getResources().getColor(R.color.card_red)).withTransformer(ChartBuilder.HEART_RATE_TRANSFORMER); break;
                case TEM: builder = builder.withColor(getResources().getColor(R.color.card_blue)).withTransformer(ChartBuilder.TEMPERATURE_TRANSFORMER); break;
                default: return;
            }
            builder = builder.withDateRange(mDateFrom, mDateTo);
            builder.build();

            Coordinate<Date, BaseData>[] coordinates = builder.getCoordinates();
            if (coordinates != null) {
                mDataList.clear();
                List<String> labels = new ArrayList<>();
                List<Float> values = new ArrayList<>();
                for (Coordinate<Date, BaseData> coordinate : coordinates) {
                    Date t = coordinate.x;
                    BaseData d = coordinate.y;
                    if (d == null) {
                        continue;
                    }
                    mDataList.add(d);
                    labels.add(mDateFormat.format(t));
                    switch (mType) {
                        case BPM: values.add((float) d.getBpm()); break;
                        case TEM: values.add((float) d.getTem()); break;
                    }
                }
                mList.setAdapter(new ListAdapter(this, labels, values, mUnit));
                ViewUtils.resetListViewHeight(mList);
                if (mDataList.size() > 0) {
                    mChart.setVisibility(View.VISIBLE);
                    mList.setVisibility(View.VISIBLE);
                    return;
                }
            }
        }
        showEmptyMessage();
    }

    @Override
    public void onLoaderReset(Loader loader) {}

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mDataList != null && mDataList.size() > position) {
            DataScale nextScale;
            Date nextFrom, nextTo;
            Date date = mDataList.get(position).getTimestamp();
            Intent intent = new Intent(DataActivity.this, DataActivity.class);
            intent.putExtra("type", mType.ordinal());
            switch (mScale) {
                case MONTHLY:
                    nextScale = DataScale.DAILY;
                    nextFrom = DateUtils.startOf(date, Calendar.MONTH);
                    nextTo = DateUtils.endOf(date, Calendar.MONTH);
                    break;
                case DAILY:
                    nextScale = DataScale.HOURLY;
                    nextFrom = DateUtils.startOf(date, Calendar.DATE);
                    nextTo = DateUtils.endOf(date, Calendar.DATE);
                    break;
                case HOURLY:
                    nextScale = DataScale.SENSOR;
                    nextFrom = DateUtils.startOf(date, Calendar.HOUR);
                    nextTo = DateUtils.endOf(date, Calendar.HOUR);
                    break;
                default: return;
            }
            intent.putExtra("to", nextTo.getTime());
            intent.putExtra("scale", nextScale.ordinal());
            intent.putExtra("from", nextFrom.getTime());
            startActivity(intent);
        }
    }

    @Override
    public void onLoadFailed() {
        User.logout(PreferenceManager.getDefaultSharedPreferences(this));
        Intent intent = new Intent(this, OverviewActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("EXIT", true);
        startActivity(intent);
        finish();
    }
}

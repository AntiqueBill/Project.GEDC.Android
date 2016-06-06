package cn.edu.hit.project.ec.views.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.eazegraph.lib.charts.ValueLineChart;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.edu.hit.project.ec.DataActivity;
import cn.edu.hit.project.ec.OverviewActivity;
import cn.edu.hit.project.ec.R;
import cn.edu.hit.project.ec.loaders.data.DailyDataLoader;
import cn.edu.hit.project.ec.loaders.data.HourlyDataLoader;
import cn.edu.hit.project.ec.loaders.data.MonthlyDataLoader;
import cn.edu.hit.project.ec.models.base.DataScale;
import cn.edu.hit.project.ec.models.base.DataType;
import cn.edu.hit.project.ec.models.data.BaseData;
import cn.edu.hit.project.ec.utils.DateUtils;
import cn.edu.hit.project.ec.views.builders.ChartBuilder;

public class OverviewFragment extends Fragment
        implements LoaderManager.LoaderCallbacks {
    private static Map<DataScale, OverviewFragment> fragmentStore = new HashMap<>();

    private DataScale mScale;
    private Date mDateTo;
    private Date mDateFrom;

    private View mRootView;

    public OverviewFragment() {}

    public static OverviewFragment getInstance(@NonNull DataScale type) {
        OverviewFragment fragment = fragmentStore.get(type);
        if (fragment == null) {
            fragment = new OverviewFragment();
            Bundle args = new Bundle();
            args.putInt("type", type.ordinal());
            fragment.setArguments(args);
            fragmentStore.put(type, fragment);
        }
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScale = DataScale.fromInteger(getArguments().getInt("type"));
        switch (mScale) {
            case HOURLY: mDateFrom = DateUtils.add(Calendar.DATE, -1); break;
            case DAILY: mDateFrom = DateUtils.add(Calendar.MONTH, -1); break;
            case MONTHLY: mDateFrom = DateUtils.add(Calendar.YEAR, -1); break;
        }
        mDateTo = new Date();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_overview, container, false);
        mRootView.findViewById(R.id.cardHeartRate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DataActivity.class);
                intent.putExtra("to", mDateTo.getTime());
                intent.putExtra("scale", mScale.ordinal());
                intent.putExtra("from", mDateFrom.getTime());
                intent.putExtra("type", DataType.BPM.ordinal());
                startActivity(intent);
            }
        });
        mRootView.findViewById(R.id.cardTemperature).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DataActivity.class);
                intent.putExtra("to", mDateTo.getTime());
                intent.putExtra("scale", mScale.ordinal());
                intent.putExtra("from", mDateFrom.getTime());
                intent.putExtra("type", DataType.TEM.ordinal());
                startActivity(intent);
            }
        });
        getActivity().getSupportLoaderManager().initLoader(mScale.ordinal(), null, this);
        return mRootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fragmentStore.remove(mScale);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        switch (mScale) {
            case HOURLY:
                return new HourlyDataLoader(getContext(), mDateFrom, mDateTo, (OverviewActivity) getActivity());
            case DAILY:
                return new DailyDataLoader(getContext(), mDateFrom, mDateTo, (OverviewActivity) getActivity());
            case MONTHLY:
                return new MonthlyDataLoader(getContext(), mDateFrom, mDateTo, (OverviewActivity) getActivity());
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        if (data != null) {
            ChartBuilder hrBuilder = new ChartBuilder((ValueLineChart) mRootView.findViewById(R.id.heartRateChart))
                    .withScale(mScale)
                    .withData((List<BaseData>) data)
                    .withDateRange(mDateFrom, mDateTo)
                    .withColor(getResources().getColor(R.color.card_red))
                    .withTransformer(ChartBuilder.HEART_RATE_TRANSFORMER);
            ChartBuilder temBuilder = new ChartBuilder((ValueLineChart) mRootView.findViewById(R.id.temperatureChart))
                    .withScale(mScale)
                    .withData((List<BaseData>) data)
                    .withDateRange(mDateFrom, mDateTo)
                    .withColor(getResources().getColor(R.color.card_blue))
                    .withTransformer(ChartBuilder.TEMPERATURE_TRANSFORMER);
            hrBuilder.build();
            temBuilder.build();
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
}
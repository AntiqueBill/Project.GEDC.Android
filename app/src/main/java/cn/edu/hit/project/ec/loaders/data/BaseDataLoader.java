package cn.edu.hit.project.ec.loaders.data;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.Date;

import cn.edu.hit.project.ec.App;

public abstract class BaseDataLoader<T> extends AsyncTaskLoader<T> {
    protected App app;
    protected Date to;
    protected Date from;
    protected OnLoadFailedListener listener;

    public interface OnLoadFailedListener {
        void onLoadFailed();
    }

    public BaseDataLoader(Context context, Date from, Date to, OnLoadFailedListener listener) {
        super(context);
        this.to = to;
        this.from = from;
        this.listener = listener;
        app = (App) context.getApplicationContext();
    }
}

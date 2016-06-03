package cn.edu.hit.project.ec.utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

public class ViewUtils {

    /**
     * 修复listView在ScrollView中的高度问题
     * @param listView
     */
    public static void resetListViewHeight(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight+ listView.getDividerHeight() * (listAdapter.getCount() - 1);
        listView.setLayoutParams(params);
    }
}

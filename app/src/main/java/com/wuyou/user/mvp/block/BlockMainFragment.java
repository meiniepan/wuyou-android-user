package com.wuyou.user.mvp.block;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.gs.buluo.common.utils.ToastUtils;
import com.wuyou.user.CarefreeApplication;
import com.wuyou.user.Constant;
import com.wuyou.user.R;
import com.wuyou.user.util.RxUtil;
import com.wuyou.user.view.fragment.BaseFragment;
import com.wuyou.user.view.widget.lineChart.Axis;
import com.wuyou.user.view.widget.lineChart.AxisValue;
import com.wuyou.user.view.widget.lineChart.Line;
import com.wuyou.user.view.widget.lineChart.LineChartData;
import com.wuyou.user.view.widget.lineChart.LineChartOnValueSelectListener;
import com.wuyou.user.view.widget.lineChart.LineChartView;
import com.wuyou.user.view.widget.lineChart.PointValue;
import com.wuyou.user.view.widget.lineChart.Viewport;
import com.wuyou.user.view.widget.lineChart.ZoomType;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by DELL on 2018/9/26.
 */

public class BlockMainFragment extends BaseFragment {
    @BindView(R.id.block_main_search)
    EditText blockSearch;
    LineChartView chartBottom;
    public final static String[] axisDadaX = new String[]{"1", "2", "3", "4", "5",};
    private static LineChartData lineData;
    int numValues = 5;
    float maxY = 20;//Y坐标最大值
    static private int range = 100;
    private final float baseMaxY = 20;//Y坐标的最小范围
    private Disposable subscribe;

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_main_block;
    }

    @Override
    protected void bindView(Bundle savedInstanceState) {
        chartBottom = getActivity().findViewById(R.id.chart_bottom);
        blockSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (blockSearch.length() == 0) return false;
                String searchText = blockSearch.getText().toString().trim();
                doSearch(searchText);
                return true;
            }
            return false;
        });
        initChart();
    }

    private void initChart() {
        generateInitialLineData();
        subscribe = Observable.interval(2, TimeUnit.SECONDS).compose(RxUtil.switchSchedulers()).subscribe(aLong -> generateLineData(0xFF3285FF, range));
    }

    private void doSearch(String searchText) {
        if (searchText.length() == 0) return;
        Intent intent = new Intent(mCtx, BlockDetailActivity.class);
        intent.putExtra(Constant.SEARCH_TEXT, searchText);
        startActivity(intent);
    }

    private void generateInitialLineData() {
        List<AxisValue> axisValues = new ArrayList<>();
        List<PointValue> values = new ArrayList<>();
        for (int i = 0; i < numValues; ++i) {
            float y = (float) (Math.random() * range);
            if (y > maxY) maxY = (float) (y * 1.2);
            values.add(new PointValue(i, y));
            axisValues.add(new AxisValue(i).setLabel(axisDadaX[i]));
        }

        Line line = new Line(values);
        line.setColor(0xFF3285FF).setCubic(true);
        List<Line> lines = new ArrayList<>();
        lines.add(line);
        lineData = new LineChartData(lines);
        lineData.setAxisXBottom(new Axis(axisValues).setHasLines(true));
        lineData.setAxisYLeft(new Axis().setHasLines(true).setMaxLabelChars(3));
        chartBottom.setLineChartData(lineData);
        // For build-up animation you have to disable viewport recalculation.
        chartBottom.setViewportCalculationEnabled(false);
        // And set initial max viewport and current viewport- remember to set viewports after data.
        Viewport v = new Viewport(0, maxY, 4, 0);
        chartBottom.setMaximumViewport(v);
        chartBottom.setCurrentViewport(v);
        chartBottom.setZoomEnabled(false);
        chartBottom.setOnValueTouchListener(new LineChartOnValueSelectListener() {
            @Override
            public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
                ToastUtils.ToastMessage(getContext(), "x:" + value.getX() + " y:" + value.getY());
            }

            @Override
            public void onValueDeselected() {

            }
        });
    }

    private void generateLineData(int color, float range) {
        // Cancel last animation if not finished.
        float curMaxY;//本轮Y坐标的最大值
        chartBottom.cancelDataAnimation();
        List<AxisValue> axisValues = new ArrayList<>();
        List<PointValue> values;
        // Modify data targets
        Line line = lineData.getLines().get(0);// For this example there is always only one line.
        line.setColor(color);
        values = line.getValues();
        //设置X轴刻度
        for (int i = 0; i < numValues; ++i) {
            axisValues.add(new AxisValue(i).setLabel(i + ""));
        }
        lineData.setAxisXBottom(new Axis(axisValues).setHasLines(true));
        float y = (float) (Math.random() * range);//给新加入的点赋值
        curMaxY = y;
        for (int i = 0; i < values.size(); i++) {
            if (i < values.size() - 1) {
                float yy = values.get(i + 1).getY();
                if (yy > curMaxY) curMaxY = yy;//为本轮maxY赋值
                values.set(i, new PointValue(values.get(i).getX() + 1, yy));//把后一个点的值赋给当前点
            } else {
                values.set(i, new PointValue(values.get(i).getX() + 1, y));//为新加入的点赋值
            }
            values.get(i).setTarget(values.get(i).getX(), values.get(i).getY());
        }
        resetMaxY(curMaxY, y);
        // Start new data animation with 500ms duration;
        Viewport v = new Viewport(0, maxY, 4, 0);
        chartBottom.setMaximumViewport(v);
        chartBottom.setCurrentViewport(v);
        chartBottom.startDataAnimation(500);
    }

    private void resetMaxY(float curMaxY, float y) {
        //如果出现新的最大值，则将maxY调整为新的最大值的1.2倍。如果本轮最大值比上次最大值的0.6倍小，则缩小最大值0.8倍。
        if (y > maxY) maxY = (float) (y * 1.2);
        if (curMaxY < maxY * 0.6) maxY = (float) (maxY * 0.8);
        if (maxY < baseMaxY) maxY = baseMaxY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        subscribe.dispose();
    }

}
package vn.binhnc.manager.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import vn.binhnc.manager.R;
import vn.binhnc.manager.retrofit.ApiBanHang;
import vn.binhnc.manager.retrofit.RetrofitClient;
import vn.binhnc.manager.utils.NetWorkChangeListener;
import vn.binhnc.manager.utils.Utils;

public class ThongKeActivity extends AppCompatActivity {
    // Kiểm tra kết nối Internet
    NetWorkChangeListener netWorkChangeListener = new NetWorkChangeListener();
    private Toolbar toolbar;
    private PieChart pieChart;
    private BarChart barChart;
    private CompositeDisposable compositeDisposable;
    private ApiBanHang apiBanHang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thong_ke);
        //=>
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        compositeDisposable = new CompositeDisposable();
        //=>
        initView();
        ActionToolBar();
        getDataChart();
        settingBarChart();
    }


    private void settingBarChart() {
        barChart.getDescription().setEnabled(false);
        barChart.setDrawValueAboveBar(false);
        // Trục x
        XAxis xAxis = barChart.getXAxis();
        xAxis.setAxisMinimum(1);
        xAxis.setAxisMaximum(12);
        // Trục y
        YAxis yAxisRight = barChart.getAxisRight();
        yAxisRight.setAxisMinimum(0);
        YAxis yAxisLeft = barChart.getAxisLeft();
        yAxisLeft.setAxisMinimum(0);

    }

    // Menu thống kê doanh thu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_thongke, menu);
        return true;
    }

    // Bắt sự kiện chọn vào menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.tk_thang) {
            getThongKeThang();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getThongKeThang() {
        barChart.setVisibility(View.VISIBLE);
        pieChart.setVisibility(View.GONE);
        compositeDisposable.add(apiBanHang.getThongKeThang()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        thongKeModel -> {
                            if (thongKeModel.isSuccess()) {
                                List<BarEntry> listdata = new ArrayList<>();
                                for (int i = 0; i < thongKeModel.getResult().size(); i++) {
                                    String tongtien = thongKeModel.getResult().get(i).getTongtienthang();
                                    String thang = thongKeModel.getResult().get(i).getThang();
                                    listdata.add(new BarEntry(Integer.parseInt(thang), Float.parseFloat(tongtien)));
                                }
                                BarDataSet barDataSet = new BarDataSet(listdata, "Thống kê");
                                barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                                barDataSet.setValueTextSize(14f);
                                barDataSet.setValueTextColor(Color.RED);

                                BarData data = new BarData(barDataSet);
                                barChart.animateXY(2000, 2000);
                                barChart.setData(data);
                                barChart.invalidate();
                            }
                        }, throwable -> Log.d("loggg", Objects.requireNonNull(throwable.getMessage()))
                ));
    }

    private void getDataChart() {
        List<PieEntry> listdata = new ArrayList<>();
        compositeDisposable.add(apiBanHang.getThongKe()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        thongKeModel -> {
                            if (thongKeModel.isSuccess()) {
                                for (int i = 0; i < thongKeModel.getResult().size(); i++) {
                                    String tensp = thongKeModel.getResult().get(i).getTensp();
                                    int tong = thongKeModel.getResult().get(i).getTong();
                                    listdata.add(new PieEntry(tong, tensp));
                                }
                                PieDataSet pieDataSet = new PieDataSet(listdata, "Thống kê");
                                PieData data = new PieData();
                                data.setDataSet(pieDataSet);
                                data.setValueTextSize(12f);
                                data.setValueFormatter(new PercentFormatter(pieChart));
                                pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                                // -->
                                pieChart.setData(data);
                                pieChart.animateXY(2000, 2000);
                                pieChart.setUsePercentValues(true);
                                pieChart.getDescription().setEnabled(false);
                                pieChart.invalidate();

                            }
                        },
                        throwable -> Log.d("logg", Objects.requireNonNull(throwable.getMessage()))
                ));
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar_statistical);
        pieChart = findViewById(R.id.pie_chart);
        barChart = findViewById(R.id.bar_chart);

    }

    private void ActionToolBar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setTitle("Thống kê");
        toolbar.setNavigationOnClickListener(view -> finish());
    }

    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netWorkChangeListener, filter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(netWorkChangeListener);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}
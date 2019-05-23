package com.saratms.campaigntask.Activities;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.saratms.campaigntask.R;
import com.saratms.campaigntask.Utilities.Networking;
import com.saratms.campaigntask.ViewModels.ChartDisplayViewModel;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.spinner_1)
    Spinner firstSpinner;
    @BindView(R.id.spinner_2)
    Spinner secondSpinner;
    @BindView(R.id.barChart)
    BarChart chart;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    float barWidth = 0.35f;
    float barSpace = 0.01f;
    float groupSpace = 0.28f;

    ChartDisplayViewModel mChartDisplayViewModel;
    private String[] mKeys;
    private String[] mDownstreamKeys;
    private ArrayList yVals1;
    private ArrayList yVals2;
    private ArrayAdapter mFirstArrayAdapter;
    private ArrayAdapter mSecondArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        mChartDisplayViewModel = ViewModelProviders.of(this).get(ChartDisplayViewModel.class);
        //Each grouped bar consists of first bar and second bar, we setup observers to observe any change in their values
        MutableLiveData<ArrayList> firstBarValues = mChartDisplayViewModel.getFirstBarValues();
        MutableLiveData<ArrayList> secondBarValues = mChartDisplayViewModel.getSecondBarValues();
        setupObservers(firstBarValues, secondBarValues);

        //Initial setup of spinners that contain dimensions used for grouping campaigns data
        setupSpinners();

        //This will prevent choice from first spinner to show in the second one to avoid overlapping
        mediateSpinnersChoices();

        setupChartProperties();
    }

    private void setupObservers(MutableLiveData<ArrayList> firstBarValues, MutableLiveData<ArrayList> secondBarValues) {
        firstBarValues.observe(this, new Observer<ArrayList>() {
            @Override
            public void onChanged(@Nullable ArrayList arrayList) {
                if(arrayList == null){
                    hideProgressBar();
                    Toast.makeText(MainActivity.this, R.string.no_enough_data_toast, Toast.LENGTH_SHORT).show();
                    return;
                }
                yVals1 = arrayList;
                //This will make sure that whatever first observed a change(whether it's yVals1 or yVals2), will wait for the other
                //so that drawChart method won't be called unless they are both got their corresponding data
                if (yVals2 != null) {
                    drawChart();
                }
            }
        });

        secondBarValues.observe(this, new Observer<ArrayList>() {
            @Override
            public void onChanged(@Nullable ArrayList arrayList) {
                yVals2 = arrayList;
                if (yVals1 != null) {
                    drawChart();
                }
            }
        });
    }

    private void setupSpinners() {
        //Create this adapter from array resources because we won't change its content later
        mFirstArrayAdapter = ArrayAdapter.createFromResource(this, R.array.grouping_criteria, R.layout.spinner_item);
        mFirstArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Because this adapter contents change dynamically based on choice from first spinner
        //we should populate its contents from List, this is why we convert array to list
        String[] groupingCriteriaArray = getResources().getStringArray(R.array.grouping_criteria_2);
        ArrayList<String> groupingCriteriaList = new ArrayList<>(Arrays.asList(groupingCriteriaArray));
        mSecondArrayAdapter = new ArrayAdapter(this, R.layout.spinner_item, groupingCriteriaList);
        mSecondArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        firstSpinner.setAdapter(mFirstArrayAdapter);
        secondSpinner.setAdapter(mSecondArrayAdapter);
    }

    private void mediateSpinnersChoices() {
        firstSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] groupingCriteriaArray = getResources().getStringArray(R.array.grouping_criteria);
                ArrayList<String> groupingList = new ArrayList<>(Arrays.asList(groupingCriteriaArray));
                groupingList.remove(position);
                mSecondArrayAdapter.clear();
                mSecondArrayAdapter.addAll(groupingList);
                mSecondArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setupChartProperties() {
        chart.setDescription(null);
        chart.setPinchZoom(false);
        chart.setScaleEnabled(false);
        chart.setDrawBarShadow(false);
        chart.setDrawGridBackground(false);
        chart.fitScreen();
    }

    private void drawChart() {
        updateChartKeys();
        drawXAxis();
        drawYAxis();
        hideProgressBar();
        drawGraph();
        drawLegend();
    }

    private void updateChartKeys() {
        mKeys = mChartDisplayViewModel.getKeys();
        mDownstreamKeys = mChartDisplayViewModel.getDownstreamKeys();
    }

    private void drawYAxis() {
        chart.getAxisRight().setEnabled(false);
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return String.valueOf(value);
            }
        });
        leftAxis.setGranularity(1f);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setDrawGridLines(true);
        leftAxis.setSpaceTop(35f);
        leftAxis.setAxisMinimum(0f);
    }

    private void drawXAxis() {
        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setCenterAxisLabels(true);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisMaximum(6);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(mKeys));
    }

    private void drawGraph() {
        BarDataSet set1, set2;
        set1 = new BarDataSet(yVals1, mDownstreamKeys[0]);
        set1.setColor(Color.parseColor("#4387f5"));
        set2 = new BarDataSet(yVals2, mDownstreamKeys[1]);
        set2.setColor(Color.parseColor("#dc4538"));
        BarData data = new BarData(set1, set2);
        data.setDrawValues(false);
        data.setHighlightEnabled(false);
        chart.setData(data);
        chart.getBarData().setBarWidth(barWidth);
        chart.getXAxis().setAxisMinimum(0);
        chart.getXAxis().setAxisMaximum(0 + chart.getBarData().getGroupWidth(groupSpace, barSpace) * mKeys.length);
        chart.groupBars(0, groupSpace, barSpace);
        chart.invalidate();
    }

    private void drawLegend() {
        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(true);
        l.setYOffset(20f);
        l.setXOffset(0f);
        l.setYEntrySpace(0f);
        l.setTextSize(8f);
        l.setTextColor(Color.GRAY);
    }

    private void showProgressBar(){
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar(){
        progressBar.setVisibility(View.GONE);
    }

    @OnClick(R.id.draw_chart_button)
    public void getSpinnerSelection() {
        if(Networking.isConnected(this)){
        showProgressBar();
        String spinnerOneSelectedItem = (String) firstSpinner.getSelectedItem();
        String spinnerTwoSelectedItem = (String) secondSpinner.getSelectedItem();
        mChartDisplayViewModel.handleSelections(spinnerOneSelectedItem, spinnerTwoSelectedItem);}
        else {
            Toast.makeText(this, R.string.no_connection, Toast.LENGTH_SHORT).show();
        }
    }

}

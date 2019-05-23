package com.saratms.campaigntask.ViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.github.mikephil.charting.data.BarEntry;
import com.saratms.campaigntask.Data.Models.Campaign;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Sarah Al-Shamy on 19/05/2019.
 */

public class ChartDisplayViewModel extends AndroidViewModel implements ChartDisplayRepository.CampaignDataListener {

    private MutableLiveData<ArrayList> firstBarValues;
    private MutableLiveData<ArrayList> secondBarValues;
    //Act as X Values in the chart
    private String[] keys;
    //Act as Y Value labels in the chart
    private String[] downstreamKeys;
    private Function<Campaign, String> mFirstGroupingFunction;
    private Function<Campaign, String> mSecondGroupingFunction;
    private ChartDisplayRepository mChartDisplayRepository;

    public ChartDisplayViewModel(@NonNull Application application) {
        super(application);
        firstBarValues = new MutableLiveData<>();
        secondBarValues = new MutableLiveData<>();
    }

    public MutableLiveData<ArrayList> getFirstBarValues() {
        return firstBarValues;
    }

    public MutableLiveData<ArrayList> getSecondBarValues() {
        return secondBarValues;
    }

    public String[] getKeys() {
        return keys;
    }

    public String[] getDownstreamKeys() {
        return downstreamKeys;
    }

    public void handleSelections(String firstSpinnerSelection, String secondSpinnerSelection) {
        //According to the dimensions selected, we get the corresponding functions
        //Dimensions are the criteria for grouping campaigns data
        mFirstGroupingFunction = getGroupingFunction(firstSpinnerSelection);
        mSecondGroupingFunction = getGroupingFunction(secondSpinnerSelection);

        getCampaignsData();
    }

    private void getCampaignsData() {
        mChartDisplayRepository = new ChartDisplayRepository();
        mChartDisplayRepository.getCampaignsData(this);
    }

    private Map<String, Map<String, Long>> groupingData(List<Campaign> campaigns, Function<Campaign, String> firstGroupingFunction, Function<Campaign, String> secondGroupingFunction) {
        Map<String, Map<String, Long>> groupedData = campaigns.stream()
                .collect(Collectors.groupingBy(firstGroupingFunction, Collectors.groupingBy(secondGroupingFunction, Collectors.counting())));
        return groupedData;
    }

    private void updateChartAttributes(Map<String, Map<String, Long>> groupedData) {
        updateXValues(groupedData);

        if (keys.length != 0) {
            downstreamKeys = getDownstreamKeys(keys, groupedData);
            if (downstreamKeys != null) {
                updateYValues(groupedData);
            } else {
                firstBarValues.setValue(null);
            }
        } else {
            firstBarValues.setValue(null);
        }
    }

    private void updateXValues(Map<String, Map<String, Long>> groupedData) {
        Set<String> keysSet = groupedData.keySet();
        keys = keysSet.toArray(new String[keysSet.size()]);
    }

    private String[] getDownstreamKeys(String[] keys, Map<String, Map<String, Long>> groupedData) {
        //Since every key may contain several values, and we only want two values
        //We extract the first two downstream keys of the first key, to intersect them with the rest of the data
        String[] firstDownstreamKeys = extractDownstreamKeysFromKey(keys[0], groupedData);

        //Make sure the downstream keys list contains two or more values, else extract the downstreamkeys from the second key
        //but make sure first that the keys list contains more than one key
        if (firstDownstreamKeys.length > 1) {
            return new String[]{firstDownstreamKeys[0], firstDownstreamKeys[1]};
        } else if (keys.length > 1) {
            String[] secondDownstreamKeys = extractDownstreamKeysFromKey(keys[1], groupedData);
            if (secondDownstreamKeys.length > 1) {
                return new String[]{secondDownstreamKeys[0], secondDownstreamKeys[1]};
            } else {
                return null;
            }
        }else{
            return null;
        }
    }

    private String[] extractDownstreamKeysFromKey(String key, Map<String, Map<String, Long>> groupedData) {
        Map<String, Long> firstMapValue = groupedData.get(key);
        Set<String> downstreamKeysSet = firstMapValue.keySet();
        return downstreamKeysSet.toArray(new String[downstreamKeysSet.size()]);
    }

    private void updateYValues(Map<String, Map<String, Long>> groupedData) {

        ArrayList yValues1 = new ArrayList();
        ArrayList yValues2 = new ArrayList();

        //Loop through each key to get the corresponding map value
        //For each map value, we extract the value of the first two keys
        for (int n = 0; n < keys.length; n++) {
            String key = keys[n];
            Map<String, Long> mapValue = groupedData.get(key);
            yValues1 = addDataToBarValuesArray(downstreamKeys[0], mapValue, yValues1, n);
            yValues2 = addDataToBarValuesArray(downstreamKeys[1], mapValue, yValues2, n);
        }
        firstBarValues.setValue(yValues1);
        secondBarValues.setValue(yValues2);
    }

    private ArrayList addDataToBarValuesArray(String downstreamKey, Map<String, Long> mapValue, ArrayList barValues, int n) {
        if (mapValue.containsKey(downstreamKey)) {
            barValues.add(new BarEntry(n + 1, mapValue.get(downstreamKey)));
        } else {
            barValues.add(new BarEntry(n + 1, 0));
        }
        return barValues;
    }

    private Function<Campaign, String> getGroupingFunction(String selection) {
        switch (selection) {
            case "Country": {
                return Campaign::getCountry;
            }
            case "Category": {
                return Campaign::getCategory;
            }

            case "Goal": {
                return Campaign::getGoal;
            }
        }

        return null;
    }

    @Override
    public void onDataReturn(List<Campaign> campaignList) {
        if (campaignList != null) {
            //Using those grouping functions as parameters, we get the corresponding composite Map
            //parent Map will contain keys for the first dimension and values for the downstream Map
            //The downstream Map will contain keys for the second dimension and values for their count in campaigns data
            Map<String, Map<String, Long>> groupedData = groupingData(campaignList, mFirstGroupingFunction, mSecondGroupingFunction);
            updateChartAttributes(groupedData);
        } else {
            firstBarValues.setValue(null);
        }
    }
}

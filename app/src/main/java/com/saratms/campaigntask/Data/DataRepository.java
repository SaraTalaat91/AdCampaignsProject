package com.saratms.campaigntask.Data;

import com.saratms.campaigntask.Data.Models.Campaign;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sarah Al-Shamy on 18/05/2019.
 */

public class DataRepository {

    private static DataRepository sInstance;
    private List<Campaign> mCampaignList;

    private DataRepository(){
        mCampaignList = new ArrayList<>();
        initCampaignData();
    }

    private void initCampaignData() {
        mCampaignList.add(new Campaign(null,"n1", "USA", 149, "Awareness", "Technology"));
        mCampaignList.add(new Campaign(null,"n2", "USA", 149, "Awareness", "Sports"));
        mCampaignList.add(new Campaign(null, "n3", "EGY", 149, "Awareness", "Technology"));
        mCampaignList.add(new Campaign(null, "n4", "EGY", 149, "Awareness","Sports" ));
        mCampaignList.add(new Campaign(null, "n5", "USA", 149, "Conversion", "Sports"));
    }

    public static DataRepository getInstance(){
        if(sInstance == null){
           sInstance = new DataRepository();
        }
        return sInstance;
    }

    public List<Campaign> getCampaignsData(){
     return mCampaignList;
    }
}

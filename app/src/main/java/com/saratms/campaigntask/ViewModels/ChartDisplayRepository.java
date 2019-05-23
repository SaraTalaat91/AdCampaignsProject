package com.saratms.campaigntask.ViewModels;

import android.text.TextUtils;
import android.util.Log;

import com.saratms.campaigntask.Data.ApiClient;
import com.saratms.campaigntask.Data.ApiInterface;
import com.saratms.campaigntask.Data.DataRepository;
import com.saratms.campaigntask.Data.Models.Campaign;
import com.saratms.campaigntask.Data.Models.CategoryModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

/**
 * Created by Sarah Al-Shamy on 23/05/2019.
 */

public class ChartDisplayRepository {

    List<Campaign> mCampaignList;
    int i = 0;
    private ApiInterface mApiInterface;
    private CampaignDataListener mCampaignDataListener;

    public ChartDisplayRepository() {
        //We get the initial campaigns data from the DataRepository
        mCampaignList = DataRepository.getInstance().getCampaignsData();
        mApiInterface = ApiClient.getClient().create(ApiInterface.class);
    }

    public void getCampaignsData(CampaignDataListener campaignDataListener) {
        mCampaignDataListener = campaignDataListener;
        //This will loop through each campaign in the list to make sure they all have category set
        //If not, it passes their url to the analyze service to get their category
        if (mCampaignList != null && mCampaignList.size() != 0) {
            checkCampaignForCategory();
        }else{
            mCampaignDataListener.onDataReturn(null);
        }
    }

    private void checkCampaignForCategory() {
        if (i < mCampaignList.size()) {
            Campaign campaign = mCampaignList.get(i);
            if (TextUtils.isEmpty(campaign.getCategory())) {
                String url = campaign.getUrl();
                updateCampaignWithCategory(campaign, url);
            } else {
                i++;
                checkCampaignForCategory();
            }
        } else {
            mCampaignDataListener.onDataReturn(mCampaignList);
        }
    }

    private void updateCampaignWithCategory(Campaign campaign, String url) {

        Call<CategoryModel> categoryModelCall = mApiInterface.getCategoryFromUrl(url);
        categoryModelCall.enqueue(new Callback<CategoryModel>() {
            @Override
            public void onResponse(Call<CategoryModel> call, Response<CategoryModel> response) {
                if (response.isSuccessful()) {
                    String categoryName = response.body().getCategory().getName();
                    campaign.setCategory(categoryName);
                    i++;
                    checkCampaignForCategory();
                    Log.d(TAG, "Successful!" + categoryName);
                } else {
                    mCampaignDataListener.onDataReturn(null);
                    Log.d(TAG, "Not successful!");
                }
            }

            @Override
            public void onFailure(Call<CategoryModel> call, Throwable t) {
                mCampaignDataListener.onDataReturn(null);
                Log.d(TAG, "Failed");
            }
        });
    }

    public interface CampaignDataListener {
        void onDataReturn(List<Campaign> campaignList);
    }
}

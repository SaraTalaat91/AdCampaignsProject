package com.saratms.campaigntask.Data;

import com.saratms.campaigntask.Data.Models.CategoryModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Sarah Al-Shamy on 23/05/2019.
 */

public interface ApiInterface {
    @GET("api/")
    Call<CategoryModel> getCategoryFromUrl(@Query("url") String url);
}

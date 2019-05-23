package com.saratms.campaigntask.Data.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Sarah Al-Shamy on 23/05/2019.
 */

public class CategoryModel {
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("category")
    @Expose
    private Category category;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}

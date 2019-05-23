package com.saratms.campaigntask.Data.Models;

/**
 * Created by Sarah Al-Shamy on 19/05/2019.
 */

public class Campaign {

    private String mName;
    private String mCountry;
    private int mBudget;
    private String mGoal;
    private String mCategory;
    private String mUrl;

    public Campaign(String url, String name, String country, int budget, String goal, String category) {
        mName = name;
        mCountry = country;
        mBudget = budget;
        mGoal = goal;
        mUrl = url;
        mCategory = category;
    }

    public Campaign(String name, String country, int budget, String goal, String category) {
        mName = name;
        mCountry = country;
        mBudget = budget;
        mGoal = goal;
        mCategory = category;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getCountry() {
        return mCountry;
    }

    public void setCountry(String country) {
        mCountry = country;
    }

    public int getBudget() {
        return mBudget;
    }

    public void setBudget(int budget) {
        mBudget = budget;
    }

    public String getGoal() {
        return mGoal;
    }

    public void setGoal(String goal) {
        mGoal = goal;
    }

    public String getCategory() {
        return mCategory;
    }

    public void setCategory(String category) {
        mCategory = category;
    }

    public String getUrl() {return mUrl;}

    public void setUrl(String url) {mUrl = url;}
}

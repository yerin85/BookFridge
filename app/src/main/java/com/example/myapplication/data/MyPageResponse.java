package com.example.myapplication.data;

import com.google.gson.annotations.SerializedName;

public class MyPageResponse {
    @SerializedName("userId")
    private String userId;

    @SerializedName("fantasy")
    private int fantasy;

    @SerializedName("mystery")
    private int mystery;

    @SerializedName("horror")
    private int horror;

    @SerializedName("classical")
    private int classical;

    @SerializedName("action")
    private int action;

    @SerializedName("sf")
    private int sf;

    @SerializedName("theatrical")
    private int theatrical;

    @SerializedName("martialArt")
    private int martialArt;

    @SerializedName("poem")
    private int poem;

    @SerializedName("essay")
    private int essay;

    @SerializedName("novel")
    private int novel;

    @SerializedName("comics")
    private int comics;

    @SerializedName("others")
    private int others;

    @SerializedName("total")
    private int total;

    @SerializedName("goal")
    private int goal;

    @SerializedName("achievement")
    private int achievement;

    public String getUserId() {
        return userId;
    }

    public int getFantasy() {
        return fantasy;
    }

    public int getMystery() {
        return mystery;
    }

    public int getHorror() {
        return horror;
    }

    public int getClassical() {
        return classical;
    }

    public int getAction() {
        return action;
    }

    public int getSf() {
        return sf;
    }

    public int getTheatrical() {
        return theatrical;
    }

    public int getMartialArt() {
        return martialArt;
    }

    public int getPoem() {
        return poem;
    }

    public int getEssay() {
        return essay;
    }

    public int getNovel() {
        return novel;
    }

    public int getComics() {
        return comics;
    }

    public int getOthers() {
        return others;
    }

    public int getTotal() {
        return total;
    }

    public int getGoal() {
        return goal;
    }

    public int getAchievement() {
        return achievement;
    }
}

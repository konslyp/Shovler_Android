package com.app.shovelerapp.model;

import com.google.gson.Gson;

/**
 * Created by supriya.n on 27-08-2016.
 */
public class FAQModel {

    /**
     * Que : Que A?
     * Ans : Ans A
     */

    private String Que;
    private String Ans;

    public static FAQModel objectFromData(String str) {

        return new Gson().fromJson(str, FAQModel.class);
    }

    public String getQue() {
        return Que;
    }

    public void setQue(String Que) {
        this.Que = Que;
    }

    public String getAns() {
        return Ans;
    }

    public void setAns(String Ans) {
        this.Ans = Ans;
    }
}

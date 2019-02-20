package com.app.shovelerapp.model;

import com.google.gson.Gson;

/**
 * Created by supriya.n on 27-09-2016.
 */
public class Promocode {

    /**
     * pcode : adminpc123
     * fromdt : 2016-09-01
     * todate : 2016-09-30
     * discount : 10
     */

    private String pcode;
    private String fromdt;
    private String todate;
    private String discount;

    public static Promocode objectFromData(String str) {

        return new Gson().fromJson(str, Promocode.class);
    }

    public String getPcode() {
        return pcode;
    }

    public void setPcode(String pcode) {
        this.pcode = pcode;
    }

    public String getFromdt() {
        return fromdt;
    }

    public void setFromdt(String fromdt) {
        this.fromdt = fromdt;
    }

    public String getTodate() {
        return todate;
    }

    public void setTodate(String todate) {
        this.todate = todate;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }
}

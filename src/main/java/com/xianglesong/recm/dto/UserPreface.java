/**
 * @CopyRight all rights reserved
 */

package com.xianglesong.recm.dto;


public class UserPreface {

    private int id;
    private int pref;
    private float rating;

    public UserPreface() {
    }

    public UserPreface(int id, int pref, float rating) {
        this.id = id;
        this.pref = pref;
        this.rating = rating;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPref() {
        return pref;
    }

    public void setPref(int pref) {
        this.pref = pref;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        // 0::2::3
        return id + "::" + pref + "::" + rating;
    }

    public static UserPreface parseRating(String str) {
        String[] fields = str.split("::");
        int id = Integer.parseInt(fields[0]);
        int pref = Integer.parseInt(fields[1]);
        float rating = Float.parseFloat(fields[2]);
        return new UserPreface(id, pref, rating);
    }

}

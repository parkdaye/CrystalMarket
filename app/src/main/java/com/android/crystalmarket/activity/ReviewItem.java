package com.android.crystalmarket.activity;

/**
 * Created by user on 2018-06-20.
 */
public class ReviewItem {
    private String userimg;
    private String usernic;
    private float rating;
    private String review;
    private String id;

    public String getUserimg(){  return userimg; }
    public String getUsernic(){ return usernic; }
    public float getRating() { return rating; }
    public String getReview() { return review; }
    public String getId() {return id;}

    public ReviewItem(String a, String b, float c, String d, String e){
        this.userimg = a;
        this.usernic = b;
        this.rating = c;
        this.review = d;
        this.id = e;
    }
}

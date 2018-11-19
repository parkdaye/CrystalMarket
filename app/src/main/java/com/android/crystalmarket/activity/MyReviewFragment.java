package com.android.crystalmarket.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.crystalmarket.app.AppConfig;
import com.android.crystalmarket.app.AppController;
import com.android.crystalmarket.helper.SessionManager;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import info.androidhive.loginandregistration.R;

import static android.app.Activity.RESULT_OK;

/**
 * Created by user on 2018-06-20.
 */

public class MyReviewFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = MyReviewFragment.class.getSimpleName();

    private ReviewAdapter ra = null;
    private ArrayList<ReviewItem> rlistdata = new ArrayList<>();
    private int count_people;
    private ListView reviewlist;
    private RatingBar ratingBar1
            ;
    private float rating_aver;//평점
    private float sum; //평점 평균

    TextView nickname;
    private String id;

    public MyReviewFragment() {
        // Required empty public constructor
    }

    public static MyReviewFragment newInstance(String param1, String param2) {
        MyReviewFragment fragment = new MyReviewFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_review, container, false);
        reviewlist = (ListView) view.findViewById(R.id.review_listview);


        fetchStoreItems(id);


        //사용자 정보 가지고 오기
        SessionManager session = new SessionManager(getActivity());
        HashMap<String, String> user = session.getUserDetails();
        id = user.get(SessionManager.IS_ID);

        /*
        //액션바(21이하 버전이라 getActionBar하면 crash현상)
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        //중간정렬을 위한 부분
        actionBar.setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(getLayoutInflater().inflate(R.layout.ab_mypage, null),
                new android.support.v7.app.ActionBar.LayoutParams(
                        android.support.v7.app.ActionBar.LayoutParams.WRAP_CONTENT,
                        android.support.v7.app.ActionBar.LayoutParams.WRAP_CONTENT,
                        Gravity.CENTER
                )
        );
        //홈버튼은 안보이고 뒤로가기 보이게
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
*/
        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
        }
    }



    private void fetchStoreItems(final String productNum) {
        // Tag used to cancel the request
        String tag_string_req = "req_cmt";

        StringRequest strReq = new StringRequest(Request.Method.POST,AppConfig.URL_reviewlist, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "detail Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    //result에 해당하는 정보를 가지고 온다
                    JSONArray ja = jObj.getJSONArray("reviewlist");
                    boolean error = jObj.getBoolean("error");


                    // Check for error node in json
                    if (!error) {
                        for (int i = 0; i < ja.length(); i++) {
                            JSONObject jo = ja.getJSONObject(i);
                            String a = jo.getString("profile"); //사용자 이미지
                            String b = jo.getString("nick");    //사용자 닉네임
                            String c = jo.getString("rating");
                            String d = jo.getString("contents");
                            String id = jo.getString("reviewing_id");

                            count_people = ja.length(); //도는 수만큼 사람수
                            rating_aver += Float.parseFloat(c); //평점 다 더하고
                            float rating = Float.parseFloat(c);

                            ReviewItem item = new ReviewItem(a, b, rating, d, id);
                            rlistdata.add(item);
                        }

                        ra = new ReviewAdapter(getActivity(), rlistdata);
                        reviewlist.setAdapter(ra);

                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getContext(),
                                errorMsg, Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Detail Error: " + error.getMessage());
                Toast.makeText(getContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", id);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }



    public static Bitmap StringToBitMap(String image){
        try{
            byte [] encodeByte= Base64.decode(image, Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            Log.e("StringToBitMap","good");
            return bitmap;
        }catch(Exception e){
            e.getMessage();
            return null;
        }
    }



    private class ViewHolder{
        public ImageView userImage;
        public TextView userNic;
        public TextView userReview;
        public RatingBar ratingBar;
    }

    public class ReviewAdapter extends BaseAdapter {
        private Context mContext = null;
        private LayoutInflater inflater;
        private ArrayList<ReviewItem> reviews = new ArrayList<>();

        public ReviewAdapter(Context mContext, ArrayList<ReviewItem> arr){
            super();
            this.mContext = mContext;
            this.inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.reviews = arr;
        }

        @Override
        public int getCount() { return reviews.size(); }
        @Override
        public Object getItem(int position) { return reviews.get(position); }
        @Override
        public long getItemId(int position) { return position; }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView == null){
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.product_review_item, null);
                holder.userImage = (ImageView) convertView.findViewById(R.id.profile);
                holder.userNic = (TextView) convertView.findViewById(R.id.userNic);
                holder.userReview = (TextView) convertView.findViewById(R.id.review);
                holder.ratingBar = (RatingBar) convertView.findViewById(R.id.ratingBar);
                convertView.setTag(holder);
            } else{
                holder = (ViewHolder) convertView.getTag(); }

            ReviewItem reviewItem = (ReviewItem) getItem(position);

            if(!reviewItem.getUserimg().equals("")) {
                Bitmap cmt_img = StringToBitMap(reviewItem.getUserimg());
                holder.userImage.setImageBitmap(cmt_img);
            }
            else
                holder.userImage.setImageDrawable(getResources().getDrawable(R.drawable.profile));

            holder.userNic.setText(reviewItem.getUsernic());
            holder.userReview.setText(reviewItem.getReview());
            holder.ratingBar.setRating(reviewItem.getRating());

            return convertView;
        }

    }

    //뒤로가기 구현

}




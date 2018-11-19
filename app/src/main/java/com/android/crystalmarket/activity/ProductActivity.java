//최종 상세페이지
package com.android.crystalmarket.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.crystalmarket.helper.SessionManager;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.androidhive.loginandregistration.R;
import com.android.crystalmarket.app.AppConfig;
import com.android.crystalmarket.app.AppController;

public class ProductActivity extends AppCompatActivity {

    // LogCat tag
    private static final String TAG = Product.class.getSimpleName();
    private ProgressDialog pDialog;

    private String productNum;
    private ImageView productImg, vendorPic;
    private TextView productInfo, productName, productPrice;
    private TextView vendorNick;
    private EditText inputCmt;
    private Button scrap, chat, cmt;
    private ViewGroup layout;

    //private ArrayList<PRItem> listdata = new ArrayList<>();
    //private PRItem pitem;
    private Bitmap bitmap;

    private ListView commentlist;
    private CommentAdapter ca = null;
    private ArrayList<CommentItem> clistdata = new ArrayList<>();
    View header;
    SessionManager session;

    private String userid;
    private String vendorid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        session = new SessionManager(getApplicationContext());
        session.getUserDetails();
        HashMap<String, String> user = session.getUserDetails();
        userid = user.get(SessionManager.IS_ID);

        Intent i = getIntent();
        productNum = i.getStringExtra("productnum");

        commentlist = (ListView) findViewById(R.id.commentList);
        inputCmt = (EditText)findViewById(R.id.insertComment);
        cmt = (Button) findViewById(R.id.buttoncmt);

        header = getLayoutInflater().inflate(R.layout.product_header,null,false);
        commentlist.addHeaderView(header);
        productImg = (ImageView) header.findViewById(R.id.pImage);
        productName = (TextView) header.findViewById(R.id.productName);
        productPrice = (TextView) header.findViewById(R.id.productPrice);
        productInfo = (TextView) header.findViewById(R.id.productInfo);
        vendorNick = (TextView) header.findViewById(R.id.vendor_nickname);
        vendorPic = (ImageView) header.findViewById(R.id.vendor_profile);

        layout = (ViewGroup) header.findViewById(R.id.vendorclick);
        scrap = (Button) header.findViewById(R.id.scrap);

        downProduct(productNum);
        downComment(productNum);


        //찜 추가하기
        scrap.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userid.equals(vendorid))
                    Toast.makeText(ProductActivity.this, "자신의 상품은 찜할 수 없습니다.", Toast.LENGTH_SHORT).show();
                else {
                    addScrap(productNum);
                }

            }
        });

        //댓글 추가
        cmt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String cmtContext = inputCmt.getText().toString().trim();
                if (!cmtContext.isEmpty()) {
                    addCmt(productNum, cmtContext);

                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                }
                else
                    Toast.makeText(ProductActivity.this, "댓글내용을 입력해주세요.", Toast.LENGTH_SHORT).show();

            }
        });

        layout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userid.equals(vendorid)) {
                    Intent intent = new Intent(ProductActivity.this, MyPageActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    Intent intent = new Intent(ProductActivity.this, VendorPageActivity.class);
                    intent.putExtra("vendorid", vendorid);
                    startActivityForResult(intent, 2000);
                    finish();
                }

            }
        });


        //actionBar();

    }


    private void downProduct(final String productNum) {
        // Tag used to cancel the request
        String tag_string_req = "req_detail";

        pDialog.setMessage("로딩중...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,AppConfig.URL_product, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "detail Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    JSONObject product = jObj.getJSONObject("product");//user 에 해당하는 JSONObject정보를 가져옴
                    JSONObject vendor = jObj.getJSONObject("vendor");
                    boolean error = jObj.getBoolean("error");


                    // Check for error node in json
                    if (!error) {
                        String name = product.getString("name");
                        String price = product.getString("price");
                        String info = product.getString("info");
                        String pic = product.getString("pic");
                        String category = product.getString("category");

                        String nickname = vendor.getString("nickname");
                        vendorid = vendor.getString("vid");
                        String vendor_pic = vendor.getString("vendor_pic");

                        if(!vendor_pic.equals("")) {
                            Bitmap bitmap2 = StringToBitMap(vendor_pic);
                            vendorPic.setImageBitmap(bitmap2);
                        }
                        else
                            vendorPic.setImageDrawable(getResources().getDrawable(R.drawable.admin));
                        bitmap = StringToBitMap(pic);

                        productName.setText(name);
                        productPrice.setText(price + " 원");
                        productInfo.setText(info);
                        productImg.setImageBitmap(bitmap);
                        vendorNick.setText(nickname);

                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
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
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("products_num", productNum);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }



    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    //-------------------------------------------------------------------------------------------------------------댓글 보여주기
    private void downComment(final String productNum) {
        // Tag used to cancel the request
        String tag_string_req = "req_cmt";

        pDialog.setMessage("로딩중...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,AppConfig.URL_cmtlist, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "detail Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    //result에 해당하는 정보를 가지고 온다
                    JSONArray ja = jObj.getJSONArray("cmtlist");
                    boolean error = jObj.getBoolean("error");


                    // Check for error node in json
                    if (!error) {
                        for (int i = 0; i < ja.length(); i++) {
                            JSONObject jo = ja.getJSONObject(i);
                            String a = jo.getString("profile"); //사용자 이미지
                            String b = jo.getString("nick");    //사용자 닉네임
                            String d = jo.getString("contents");
                            String id = jo.getString("id");

                            CommentItem item = new CommentItem(a, b, d, id);
                            clistdata.add(item);
                        }

                        ca = new CommentAdapter(ProductActivity.this, clistdata);
                        commentlist.setAdapter(ca);

                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
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
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("products_num", productNum);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    //-------------------------------------------------------------------------------------------------------------DB서버에 위시리스트 추가
    private void addScrap(final String productNum) {
        String tag_string_req = "req_scrap";
        pDialog.setMessage("등록 중...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_scrap, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Scrap Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if(!error)
                        Toast.makeText(getApplicationContext(), "상품을 찜했습니다!", Toast.LENGTH_LONG).show();
                    else {
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Wish Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", userid);
                params.put("products_num", productNum);

                return params;
            }

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    //------------------------------------------------------------------------------------------------------------댓글 추가
    private void addCmt(final String productNum, final String cmtContext) {
        String tag_string_req = "req_cmt";
        pDialog.setMessage("등록 중...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_cmt, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Cmt Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if(error) {
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Comment Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", userid);
                params.put("products_num", productNum);
                params.put("cmt_context", cmtContext);

                return params;
            }

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }



    //액션바 뒤로가기 구현
    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // NavUtils.navigateUpFromSameTask(this);//누르게되면 아예 첫화면으로 가게되버림
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    };

    /*
    private void actionBar(){
        //액션바(21이하 버전이라 getActionBar하면 crash현상)
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        //중간정렬을 위한 부분
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(getLayoutInflater().inflate(R.layout.ab_detail, null),
                new ActionBar.LayoutParams(
                        ActionBar.LayoutParams.WRAP_CONTENT,
                        ActionBar.LayoutParams.WRAP_CONTENT,
                        Gravity.CENTER
                )
        );
        //홈버튼은 안보이고 뒤로가기 안보이게
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (android.os.Build.VERSION.SDK_INT > 9) {

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()

                    .permitAll().build();

            StrictMode.setThreadPolicy(policy);

        }
    }


*/

    private class ViewHolder{
        public ImageView userImage;
        public TextView userNic;
        public TextView userReview;
    }

    public class CommentAdapter extends BaseAdapter {
        private Context mContext = null;
        private LayoutInflater inflater;
        private ArrayList<CommentItem> reviews = new ArrayList<>();

        public CommentAdapter(Context mContext, ArrayList<CommentItem> arr){
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
                convertView = inflater.inflate(R.layout.product_comment_item, null);
                holder.userImage = (ImageView) convertView.findViewById(R.id.profile);
                holder.userNic = (TextView) convertView.findViewById(R.id.userName);
                holder.userReview = (TextView) convertView.findViewById(R.id.review);
                convertView.setTag(holder);
            } else{
                holder = (ViewHolder) convertView.getTag(); }

            CommentItem reviewItem = (CommentItem) getItem(position);

            if(!reviewItem.getUserimg().equals("")) {
                Bitmap cmt_img = StringToBitMap(reviewItem.getUserimg());
                holder.userImage.setImageBitmap(cmt_img);
            }
            else
                holder.userImage.setImageDrawable(getResources().getDrawable(R.drawable.admin));

            if(reviewItem.getId().equals(vendorid))
                holder.userNic.setText("판매자");
            else
                holder.userNic.setText(reviewItem.getUsernic());

            holder.userReview.setText(reviewItem.getReview());

            return convertView;
        }

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

}
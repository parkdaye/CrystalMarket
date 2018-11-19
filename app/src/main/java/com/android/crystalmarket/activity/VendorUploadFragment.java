package com.android.crystalmarket.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.crystalmarket.app.AppConfig;
import com.android.crystalmarket.app.AppController;
import com.android.crystalmarket.helper.SessionManager;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.androidhive.loginandregistration.R;

/**
 * Created by user on 2018-06-21.
 */

public class VendorUploadFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = VendorUploadFragment.class.getSimpleName();

    private RecyclerView recyclerView;
    private List<Product> productList;
    private VendorUploadFragment.StoreAdapter mAdapter;

    TextView nickname;
    private String id;
    private String vendorid;



    public VendorUploadFragment() {
        // Required empty public constructor
    }

    public static VendorUploadFragment newInstance(String param1, String param2) {
        VendorUploadFragment fragment = new VendorUploadFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_upload, container, false);

        recyclerView = view.findViewById(R.id.mu_view);
        productList = new ArrayList<>();
        mAdapter = new VendorUploadFragment.StoreAdapter(getActivity(), productList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 3);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new VendorUploadFragment.GridSpacingItemDecoration(2, dpToPx(5), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.setNestedScrollingEnabled(false);

        vendorid = getArguments().getString("vendorid");

        fetchStoreItems();

        nickname = (TextView)view.findViewById(R.id.nickname);

        // sqllite에서 사용자 정보 가지고 오기
        SessionManager session = new SessionManager(getActivity());
        HashMap<String, String> user = session.getUserDetails();
        id = user.get(SessionManager.IS_ID);

        // Displaying the user details on the screen
        //nickname.setText(id);//닉네임으로 바꿀것


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

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }




    class StoreAdapter extends RecyclerView.Adapter<VendorUploadFragment.StoreAdapter.MyViewHolder> {
        private Context context;
        private List<Product> productList;
        Bitmap image;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView name, price;
            public ImageView thumbnail;
            CardView cardview;

            public MyViewHolder(View view) {
                super(view);
                name = view.findViewById(R.id.name);
                price = view.findViewById(R.id.price);
                thumbnail = view.findViewById(R.id.thumbnail);
                cardview=(CardView)itemView.findViewById(R.id.card_view);
            }
        }


        public StoreAdapter(Context context, List<Product> productList) {
            this.context = context;
            this.productList = productList;
        }

        @Override
        public VendorUploadFragment.StoreAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);

            return new VendorUploadFragment.StoreAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(VendorUploadFragment.StoreAdapter.MyViewHolder holder, final int position) {
            final Product product = productList.get(position);
            //Log.d("image", activity_product.getImage());
            image = StringToBitMap(product.getImage());

            Log.d("name", product.getName());

            holder.name.setText(product.getName());
            holder.price.setText(product.getPrice() + " 원");
            holder.thumbnail.setImageBitmap(image);

            holder.cardview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), ProductActivity.class);
                    intent.putExtra("productnum", product.getNum());
                    startActivity(intent);

                    //VendorUploadFragment.StoreAdapter.notifyDataSetChanged();

                }
            });

        }

        @Override
        public int getItemCount() {
            return productList.size();
        }
    }

    private void fetchStoreItems() {
        String tag_string_req = "req_vendorupload";
        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_myupload, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                if (response == null) {
                    Toast.makeText(getActivity(), "Couldn't fetch the store items! Pleas try again.", Toast.LENGTH_LONG).show();
                    return;
                }
                Log.e("vendorupload response", response.toString());
                List<Product> items = new Gson().fromJson(response, new TypeToken<List<Product>>() {}.getType());

                productList.clear();
                productList.addAll(items);

                // refreshing recycler view
                mAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error in getting json
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", vendorid);
                return params;
            }
        };
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


    //뒤로가기 구현

}

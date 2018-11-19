package com.android.crystalmarket.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.android.crystalmarket.app.AppConfig;
import com.android.crystalmarket.app.AppController;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import info.androidhive.loginandregistration.R;

import static android.app.Activity.RESULT_OK;

public class CategoryFragment extends Fragment {
    private static final String TAG = CategoryFragment.class.getSimpleName();

    private ArrayList<String> mGroupList = null;
    private com.android.crystalmarket.activity.Adapter mAdapter;
    private ExpandableListView mListView;
    public String result;

    public CategoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_category, container, false);
        mListView = (ExpandableListView) view.findViewById(R.id.list);

        mGroupList = new ArrayList<>();

        mGroupList.add("의류");
        mGroupList.add("잡화");
        mGroupList.add("뷰티/미용");
        mGroupList.add("도서");
        mGroupList.add("티켓");
        mGroupList.add("문구");
        mGroupList.add("식품");
        mGroupList.add("가구");
        mGroupList.add("스포츠");
        mGroupList.add("애완");
        mGroupList.add("기타");

        mAdapter = new com.android.crystalmarket.activity.Adapter(getActivity(), mGroupList);
        mListView.setAdapter(mAdapter);

        //그룹 Indicator 삭제(그룹 왼쪽에 기본제공되는 화살표 아이콘 삭제
        mListView.setGroupIndicator(null);

        //처음 시작시 그룹 모두 열기(expandGroup)
//        int groupCount = (int) mAdapter.getGroupCount();
//        for (int i = 0; i<groupCount; i++)  {
//            mListView.expandGroup(i);
//        }

        //그룹 클릭했을 경우 이벤트
        mListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                //Listener에서 Adapter 사용법(getExpandableListAdapter 사용해야함)
                //BaseExpandableAdapter에 오버라이드 된 함수들을 사용할 수 있다.
                //int groupCount = (int) parent.getExpandableListAdapter().getGroupCount();
                result = (String) parent.getAdapter().getItem(groupPosition);

                Intent intent = new Intent(getActivity(),CategorypdActivity.class);
                intent.putExtra("category", result);
                getActivity().startActivity(intent);
                return false;
            }
        });

        return view;

    }



    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CategoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CategoryFragment newInstance(String param1, String param2) {
        CategoryFragment fragment = new CategoryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;

    }


}
package com.android.crystalmarket.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Adapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.util.ArrayList;

import info.androidhive.loginandregistration.R;

public class CategoryActivity extends AppCompatActivity {
    private ArrayList<String> mGroupList = null;
    private com.android.crystalmarket.activity.Adapter mAdapter;
    private ExpandableListView mListView;
    public String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_category);

        setLayout();

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

        mAdapter = new com.android.crystalmarket.activity.Adapter(this, mGroupList);
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
                Toast.makeText(getApplicationContext(), result + "를 선택하셨습니다!",
                        Toast.LENGTH_SHORT).show();

                Intent resultIntent = new Intent();
                resultIntent.putExtra("result",result);
                setResult(RESULT_OK,resultIntent);
                finish();


                return false;
            }
        });

        //그룹이 닫힐 경우 이벤트
//        mListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
//            @Override
//            public void onGroupCollapse(int groupPosition) {
//                Toast.makeText(getApplicationContext(), "g Collapse = " + groupPosition,
//                        Toast.LENGTH_SHORT).show();
//            }
//        });

        //그룹이 열릴 경우 이벤트
//        mListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
//            @Override
//            public void onGroupExpand(int groupPosition) {
//                Toast.makeText(getApplicationContext(), "g Expand = " + groupPosition,
//                        Toast.LENGTH_SHORT).show();
//
//                int groupCount = mAdapter.getGroupCount();
//
//                //한 그룹을 클릭하면 나머지 그룹들은 닫힌다.
//                for(int i = 0; i<groupCount;i++)    {
//                    if(!(i == groupPosition))
//                        mListView.collapseGroup(i);
//                }
//            }
//        });
    }

    private void setLayout() {
        mListView = (ExpandableListView) findViewById(R.id.list);
    }
}

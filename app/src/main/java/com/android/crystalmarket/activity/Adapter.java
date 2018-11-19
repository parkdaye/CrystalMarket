package com.android.crystalmarket.activity;

import android.app.Fragment;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import info.androidhive.loginandregistration.R;

public class Adapter extends BaseExpandableListAdapter{
    private ArrayList<String> groupList = null;
    private LayoutInflater inflater = null;
    private ViewHolder viewHolder = null;

    public Adapter(Context c, ArrayList<String> groupList){
        super();
        this.inflater = LayoutInflater.from(c);
        this.groupList = groupList;
    }


    //그룹 사이즈를 반환한다.
    @Override
    public int getGroupCount() {
        return groupList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 0;
    }

    //그룹 포지션을 반환한다.
    @Override
    public String getGroup(int groupPosition) {
        return groupList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    //그룹 ID를 반환한다.
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    //그룹뷰 각각의 ROW
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View v = convertView;
        if(v==null){
            viewHolder = new ViewHolder();
            v = inflater.inflate(R.layout.category_item, parent, false);
            viewHolder.groupName = (TextView) v.findViewById(R.id.group);
            viewHolder.image = (ImageView) v.findViewById(R.id.image);
            v.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)v.getTag();
        }
        viewHolder.groupName.setText(getGroup(groupPosition));
        return v;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        return null;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private class ViewHolder {
        public ImageView image;
        public TextView groupName;
    }
}

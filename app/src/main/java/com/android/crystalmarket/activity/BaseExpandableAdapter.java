package com.android.crystalmarket.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import info.androidhive.loginandregistration.R;

public class BaseExpandableAdapter extends BaseExpandableListAdapter {

    private ArrayList<String> groupList = null;
    private ArrayList<ArrayList<String>> childList = null;
    private LayoutInflater inflater = null;
    private ViewHolder viewHolder = null;

    public BaseExpandableAdapter(Context c, ArrayList<String> groupList,
                                 ArrayList<ArrayList<String>> childList) {
        super();
        this.inflater = LayoutInflater.from(c);
        this.groupList = groupList;
        this.childList = childList;
    }

    @Override
    public String getGroup(int groupPosition) {
        return groupList.get(groupPosition);
    }


    @Override
    public int getGroupCount() {
        return groupList.size();
    }


    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.activity_notification_list_row, parent, false);
            viewHolder.tv_groupName = (TextView) convertView.findViewById(R.id.tv_group);
            viewHolder.iv_image = (ImageView) convertView.findViewById(R.id.iv_image);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        if (isExpanded) {
            //viewHolder.iv_image.setBackgroundResource(R.drawable.expandable_list_view_down_btn);
        } else {
           // viewHolder.iv_image.setBackgroundResource(R.drawable.expandable_list_view_right_btn);
        }

        viewHolder.tv_groupName.setText(getGroup(groupPosition));

        return convertView;
    }


    @Override
    public String getChild(int groupPosition, int childPosition) {
        return childList.get(groupPosition).get(childPosition);
    }


    @Override
    public int getChildrenCount(int groupPosition) {
        return childList.get(groupPosition).size();
    }


    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }


    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            viewHolder = new ViewHolder();
            v = inflater.inflate(R.layout.activity_notification_list_row, null);
            viewHolder.tv_childName = (TextView) v.findViewById(R.id.tv_child);
            v.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) v.getTag();
        }

        viewHolder.tv_childName.setText(getChild(groupPosition, childPosition));

        return v;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    class ViewHolder {
        public ImageView iv_image;
        public TextView tv_groupName;
        public TextView tv_childName;
    }

}

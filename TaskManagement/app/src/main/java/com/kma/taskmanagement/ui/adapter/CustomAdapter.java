package com.kma.taskmanagement.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kma.taskmanagement.R;
import com.kma.taskmanagement.data.model.Category;

import java.util.List;

public class CustomAdapter extends BaseAdapter {
    Context context;
    List<Category> categories;
    LayoutInflater inflter;

    public CustomAdapter(Context applicationContext, List<Category> categories) {
        this.context = applicationContext;
        this.categories = categories;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        if(categories == null) {
            return 0;
        }
        return categories.size();
    }

    @Override
    public Category getItem(int i) {
        return categories.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    public void setList(List<Category> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.item_cate_layout, null);
        TextView names = (TextView) view.findViewById(R.id.tvNameCate);
        names.setText(categories.get(i).getName().toString());

        return view;
    }
}
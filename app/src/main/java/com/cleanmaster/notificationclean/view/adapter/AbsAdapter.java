package com.cleanmaster.notificationclean.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Created by i on 2016/8/25.
 * <p/>
 * <p/>
 * /**
 * Created by dengshengjin on 16/6/30.
 */
public abstract class AbsAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;

    public AbsAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    protected LayoutInflater getInflater() {
        return mInflater;
    }

    protected Context getContext() {
        return mContext;
    }

    protected String getString(int resId) {
        return getContext().getString(resId);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final AbsViewHolder holder;
        if (convertView == null) {
            holder = onCreateViewHolder(parent, getItemViewType(position));
            convertView = holder.getConvertView();
            convertView.setTag(holder);
        } else {
            holder = (AbsViewHolder) convertView.getTag();
        }
        onBindViewHolder(holder, position);
        return convertView;
    }

    public abstract AbsViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

    public abstract void onBindViewHolder(AbsViewHolder holder, int position);
}

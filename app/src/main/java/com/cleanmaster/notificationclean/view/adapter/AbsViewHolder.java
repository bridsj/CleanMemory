package com.cleanmaster.notificationclean.view.adapter;

import android.view.View;

/**
 * Created by i on 2016/8/25.
 */
public abstract class AbsViewHolder {
    private View mConvertView;

    public AbsViewHolder(View itemView) {
        mConvertView = itemView;
        bindView(itemView);
    }

    protected abstract void bindView(View itemView);

    public View getConvertView() {
        return mConvertView;
    }
}


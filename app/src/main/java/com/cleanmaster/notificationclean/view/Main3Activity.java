package com.cleanmaster.notificationclean.view;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cleanmaster.notificationclean.view.adapter.AbsAdapter;
import com.cleanmaster.notificationclean.view.adapter.AbsViewHolder;
import com.cleanmaster.notificationclean.view.swipe.SwipeListView;
import com.cleanmaster.notificationclean.view.swipe.SwipeListViewListenerAdapter;
import com.really.cleanmemory.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by i on 2016/8/25.
 */
public class Main3Activity extends FragmentActivity {
    private SwipeListView mSwipeListView;
    private NotificationCleanerAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        mSwipeListView = (SwipeListView) findViewById(R.id.example_lv_list);
        mAdapter = new NotificationCleanerAdapter(getApplicationContext());
        mSwipeListView.setAdapter(mAdapter);
        mSwipeListView.setSwipeListViewListener(new SwipeListViewListenerAdapter() {
            @Override
            public void onDismiss(boolean isManual, int[] reverseSortedPositions) {
                super.onDismiss(isManual, reverseSortedPositions);
                if (reverseSortedPositions == null) {
                    return;
                }
                if (isFinishing()) {
                    return;
                }
                for (int position : reverseSortedPositions) {
                    mAdapter.removeItem(position);
                }
                if (!isManual) {
                    mSwipeListView.post(new Runnable() {
                        @Override
                        public void run() {
                            mSwipeListView.performSwipeItem();
                        }
                    });
                }
            }
        });

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeListView.startShakeAnimators();
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeListView.clearShakeAnimations();
                        mSwipeListView.performSwipeItem();
                    }
                }, 3000);
            }
        }, 2000);
    }

    private static class NotificationCleanerAdapter extends AbsAdapter {
        private List<String> mContentList;

        public NotificationCleanerAdapter(Context context) {
            super(context);
            mContentList = new ArrayList<>();
            for (int position = 0; position < 100; position++) {
                if (position % 3 == 0) {
                    mContentList.add("short text " + position);
                } else if (position % 3 == 1) {
                    mContentList.add("middle middle middle middle  text " + position);
                } else if (position % 3 == 2) {
                    mContentList.add("long long long long long long long long long long long long long long long long long long text " + position);
                }
            }
        }

        @Override
        public AbsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getInflater().inflate(R.layout.notification_cleaner_content_item, parent, false);
            return new NotificationHolder(view);
        }

        @Override
        public void onBindViewHolder(AbsViewHolder holder, int position) {
            NotificationHolder notificationHolder = (NotificationHolder) holder;
            notificationHolder.mTextView.setText(getItem(position));
        }

        public void removeItem(int position) {
            if (mContentList == null || position >= getCount()) {
                return;
            }
            mContentList.remove(position);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mContentList.size();
        }

        @Override
        public String getItem(int position) {
            return mContentList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        private static class NotificationHolder extends AbsViewHolder {
            private TextView mTextView;

            public NotificationHolder(View itemView) {
                super(itemView);
            }

            @Override
            protected void bindView(View itemView) {
                mTextView = (TextView) itemView.findViewById(R.id.text);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSwipeListView != null) {
            mSwipeListView.clearShakeAnimations();
        }
    }
}

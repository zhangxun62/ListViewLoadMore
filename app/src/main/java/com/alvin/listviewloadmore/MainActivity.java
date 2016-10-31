package com.alvin.listviewloadmore;

import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.alvin.listviewloadmore.R.id.listView;
import static com.alvin.listviewloadmore.R.id.swipeRefreshLayout;

/**
 * ListView 加载更多
 * <p>
 * 参照 郭霖微信推送文章 所写
 */
public class MainActivity extends AppCompatActivity {
    ListViewLoadMore listViewLoadMore;
    ArrayList<String> mArrayList = new ArrayList<>();
    ArrayAdapter<String> adapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (0 == msg.what) {
                ArrayList<String> arrayList = new ArrayList<>();
                for (int i = 15; i < 20; i++) {
                    arrayList.add("Add" + i);
                }
//            mArrayList.addAll(arrayList);

                listViewLoadMore.onFinishLoadMore(false, arrayList);
            } else if (1 == msg.what) {
                mArrayList.clear();
                ArrayList<String> arrayList = new ArrayList<>();
                for (int i = 0; i < 14; i++) {
                    arrayList.add("item" + i);
                }
                mArrayList.addAll(arrayList);
                mSwipeRefreshLayout.setRefreshing(false);
                adapter.notifyDataSetChanged();
//                listViewLoadMore.onFinishLoadMore(false, mArrayList);
            }

            Log.i("Size------->>", "" + mArrayList.size());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        ArrayList<String> arrayList = new ArrayList<>();
        for (int i = 0; i < 14; i++) {
            arrayList.add("item" + i);
        }
        listViewLoadMore = (ListViewLoadMore) findViewById(listView);
        mArrayList.addAll(arrayList);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mArrayList);
        listViewLoadMore.setAdapter(adapter);
        listViewLoadMore.setOnLoadMoreListener(new ListViewLoadMore.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            Thread.sleep(5000);
                            mHandler.sendEmptyMessage(0);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();

            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            Thread.sleep(5000);
                            mHandler.sendEmptyMessage(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });

    }
}

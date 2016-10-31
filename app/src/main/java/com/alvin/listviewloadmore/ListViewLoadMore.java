package com.alvin.listviewloadmore;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

/**
 * @Title ListViewLoadMore
 * @Description:
 * @Author: alvin
 * @Date: 2016/10/31.09:47
 * @E-mail: 49467306@qq.com
 */
public class ListViewLoadMore extends ListView implements AbsListView.OnScrollListener {
    private static final String TAG = ListViewLoadMore.class.getSimpleName();
    private int lastVisibleItem;//当前ListView中最后一个Item的索引
    private boolean mIsLoading = false;
    private boolean mIsPageFinished = false;
    private OnScrollListener mOnScrollListener;
    private View mFooterView;
    private int mMaxItemCount = 0;

    public ListViewLoadMore(Context context) {
        this(context, null);
    }

    public ListViewLoadMore(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ListViewLoadMore(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();


    }

    private void init() {
        mFooterView = getFooterView();
        super.setOnScrollListener(this);

    }

    private View getFooterView() {
        LinearLayout mFooterView = new LinearLayout(getContext());
        mFooterView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mFooterView.setOrientation(LinearLayout.VERTICAL);
        mFooterView.setPadding(0, 20, 0, 0);
        mFooterView.setGravity(Gravity.CENTER);
        mFooterView.setTag("footerView");

        ProgressBar progressBar = new ProgressBar(getContext());
        mFooterView.addView(progressBar);

        TextView textView = new TextView(getContext());
        textView.setText("正在加载...");
        textView.setGravity(Gravity.CENTER);
        mFooterView.addView(textView);
        return mFooterView;
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {
        if (null != mOnScrollListener) {
            mOnScrollListener.onScrollStateChanged(absListView, i);
        }
    }

    /**
     * @param absListView
     * @param firstVisibleItem
     * @param visibleItemCount
     * @param totalItemCount
     */
    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (null != mOnScrollListener) {
            mOnScrollListener.onScroll(absListView, firstVisibleItem, visibleItemCount, totalItemCount);
        }
        int lastVisibleItem = firstVisibleItem + visibleItemCount;
        // 这里增加一个 当前可显示最大条目判断,如不加此判断,即使服务没有更多数据时 它也会进行加载更多数据操作
        if (0 == mMaxItemCount) {
            View child = absListView.getChildAt(0);
            if (null != child) {
                int height = child.getHeight() + getDividerHeight();
                mMaxItemCount = getHeight() / height;
                Log.i("mMaxItemCount----->", "" + mMaxItemCount);
            }
        }
        if (!mIsLoading && !mIsPageFinished && lastVisibleItem == totalItemCount && visibleItemCount >= mMaxItemCount) {
            if (null != mOnLoadMoreListener) {
                mIsLoading = true;
                if (null == findViewWithTag("footerView"))
                    addFooterView(mFooterView);
                mOnLoadMoreListener.onLoadMore();
            }
        }
    }

    @Override
    public void setOnScrollListener(OnScrollListener l) {
        mOnScrollListener = l;
    }

    /**
     * @param isPageFinished 当真时,意思是所有分页数据已经加载完毕
     * @param newItems       新数据
     */
    public void onFinishLoadMore(boolean isPageFinished, List<?> newItems) {
        mIsLoading = false;
        setPageFinished(isPageFinished);
        if (null != newItems && newItems.size() > 0 && null != getAdapter()) {
            HeaderViewListAdapter headerViewListAdapter = (HeaderViewListAdapter) getAdapter();
            ListAdapter listAdapter = headerViewListAdapter.getWrappedAdapter();
            if (null != listAdapter && listAdapter instanceof ArrayAdapter) {
                ArrayAdapter arrayAdapter = (ArrayAdapter) listAdapter;
                arrayAdapter.addAll(newItems);
                arrayAdapter.notifyDataSetChanged();
            }
        } else {
            setPageFinished(true);
        }
    }

    private void setPageFinished(boolean pageFinished) {
        mIsPageFinished = pageFinished;
        removeFooterView(mFooterView);


    }


    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    private OnLoadMoreListener mOnLoadMoreListener;

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        mOnLoadMoreListener = onLoadMoreListener;
    }
}

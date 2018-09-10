package com.cheersmind.cheersgenie.features.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.holder.BaseHolder;
import com.cheersmind.cheersgenie.features.interfaces.RecyclerLoadMoreStatusHandler;
import com.cheersmind.cheersgenie.main.util.OnMultiClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * recycler的通用适配器
 */
public class BaseAdapter<T> extends RecyclerView.Adapter<BaseHolder> implements RecyclerLoadMoreStatusHandler {

    //普通布局的type
    static final int TYPE_ITEM = 0;
    //脚布局
    static final int TYPE_FOOTER = 1;

    //数据集合
    private List<T> mList = new ArrayList<>();
    //子项布局ID
    private int layoutId;
    //脚布局当前的状态,默认为隐藏状态
    int footer_state = FootViewHolder.LOAD_MORE_GONE;

    //加载失败视图，用于点击重载
    private View llFailed;


    public BaseAdapter(int layoutId, List<T> list){
        this.layoutId=layoutId;
        this. mList=list;
    }
    //onCreateViewHolder用来给rv创建缓存
    @Override
    public BaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            //普通子项布局
            View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
            BaseHolder holder = new BaseHolder(view);
            return holder;
        } else if (viewType == TYPE_FOOTER) {
            //footer布局
//            View view = View.inflate(context, R.layout.recycler_footer, null);//这种方式，提示文本等视图不会居中
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_footer, parent, false);
            //加载失败
            llFailed = view.findViewById(R.id.ll_failed);
            llFailed.setOnClickListener(new OnMultiClickListener() {
                @Override
                public void onMultiClick(View view) {
                    //本次加载结束（为了显示正在加载的提示视图，有待优化）
                    loadMoreComplete();
                    //通知加载更多
                    notifyLoadMoreToLoading();
                }
            });
            FootViewHolder footViewHolder = new FootViewHolder(view);
            return footViewHolder;
        }

        return null;
    }
    //onBindViewHolder给缓存控件设置数据
    @Override
    public void onBindViewHolder(BaseHolder holder, int position) {
        if (holder instanceof BaseAdapter.FootViewHolder) {
            //footer的holder
            FootViewHolder footViewHolder  = (FootViewHolder) holder;

            //如果第一个就是脚布局，那就让他隐藏
            if (position == 0) {
//                footViewHolder.ll_failed.setVisibility(View.GONE);
//                footViewHolder.ll_complete.setVisibility(View.GONE);
//                footViewHolder.ll_loading.setVisibility(View.GONE);
                footer_state = FootViewHolder.LOAD_MORE_GONE;
            }

            //根据状态来让脚布局发生改变
            switch (footer_state) {
                //加载隐藏
                case FootViewHolder.LOAD_MORE_GONE: {
                    footViewHolder.ll_failed.setVisibility(View.GONE);
                    footViewHolder.ll_complete.setVisibility(View.GONE);
                    footViewHolder.ll_loading.setVisibility(View.GONE);
                    break;
                }
                //本次加载完成
                case FootViewHolder.LOAD_MORE_COMPLETE: {
                    footViewHolder.ll_failed.setVisibility(View.GONE);
                    footViewHolder.ll_complete.setVisibility(View.GONE);
                    footViewHolder.ll_loading.setVisibility(View.VISIBLE);
                    break;
                }
                //所有数据加载结束
                case FootViewHolder.LOAD_MORE_END: {
                    footViewHolder.ll_failed.setVisibility(View.GONE);
                    footViewHolder.ll_complete.setVisibility(View.VISIBLE);
                    footViewHolder.ll_loading.setVisibility(View.GONE);
                    break;
                }
                //加载失败
                case FootViewHolder.LOAD_MORE_FAILED: {
                    footViewHolder.ll_failed.setVisibility(View.VISIBLE);
                    footViewHolder.ll_complete.setVisibility(View.GONE);
                    footViewHolder.ll_loading.setVisibility(View.GONE);
                    break;
                }
            }

        } else {
            //普通的holder
            T item = mList.get(position);
            convert(holder, item);
        }
    }

    protected void convert(BaseHolder holder, T item) {
        //什么都没有做
    }

    //获取记录数据
    @Override
    public int getItemCount() {
        return mList != null ? mList.size() + 1 : 0;
    }

    @Override
    public int getItemViewType(int position) {
        //如果position加1正好等于所有item的总和,说明是最后一个item,将它设置为脚布局
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public void loadMoreEnd() {
        footer_state = FootViewHolder.LOAD_MORE_END;
        mLoading = false;//标记未正在加载中
        mAllHasLoaded = true;//标记全部数据结束
        notifyDataSetChanged();
    }

    @Override
    public void loadMoreComplete() {
        footer_state = FootViewHolder.LOAD_MORE_COMPLETE;
        mLoading = false;//标记未正在加载中
        mAllHasLoaded = false;//标记全部数据未结束
        notifyDataSetChanged();
    }

    @Override
    public void loadMoreFail() {
        footer_state = FootViewHolder.LOAD_MORE_FAILED;
        mLoading = false;//标记未正在加载中
        mAllHasLoaded = false;//标记全部数据未结束
        notifyDataSetChanged();
    }

    /**
     * 隐藏footer中的所有提示view
     */
    private void hideAllFooterView() {
    }

    /**
     * 脚布局的ViewHolder
     */
    public class FootViewHolder extends BaseHolder {
        //加载布局隐藏
        public static final int LOAD_MORE_GONE = 0;
        //本次加载完成
        public static final int LOAD_MORE_COMPLETE  = 1;
        //全部数据已经加载结束
        public static final int LOAD_MORE_END = 2;
        //加载失败
        public static final int LOAD_MORE_FAILED = 3;

        //正在加载
        private LinearLayout ll_loading;
        //加载完成
        private LinearLayout ll_complete;
        //网络错误，点击加载
        private LinearLayout ll_failed;


        public FootViewHolder(View itemView) {
            super(itemView);
            ll_loading = itemView.findViewById(R.id.ll_loading);
            ll_complete = itemView.findViewById(R.id.ll_complete);
            ll_failed = itemView.findViewById(R.id.ll_failed);
        }
    }

    private RecyclerView mRecyclerView;

    protected RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    private void setRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
    }

    /**
     * 加载更多的监听
     */
    public interface RequestLoadMoreListener {

        void onLoadMoreRequested();

    }

    //加载更多的监听
    private RequestLoadMoreListener mRequestLoadMoreListener;
    //正在请求数据
    private boolean mLoading = false;
    //全部数据加载结束（到底了）
    private boolean mAllHasLoaded = false;

    private void openLoadMore(RequestLoadMoreListener requestLoadMoreListener) {
        this.mRequestLoadMoreListener = requestLoadMoreListener;
//        mNextLoadEnable = true;
//        mLoadMoreEnable = true;
        mLoading = false;
    }

    /**
     * 设置加载更多的监听器
     * @param requestLoadMoreListener
     * @param recyclerView
     */
    public void setOnLoadMoreListener(RequestLoadMoreListener requestLoadMoreListener, RecyclerView recyclerView) {
        openLoadMore(requestLoadMoreListener);
        if (getRecyclerView() == null) {
            setRecyclerView(recyclerView);

            if (recyclerView != null) {
                //为recyclerview添加滑动监听
                doRecyclerViewSettingScrollListener(this, recyclerView);
            }
        }
    }

    //最后一个可见项索引
    private int lastVisibleItem;

    /**
     * 为recyclerView添加滑动监听
     * @param recyclerView
     */
    private void doRecyclerViewSettingScrollListener(final BaseAdapter baseAdapter, RecyclerView recyclerView) {
        if (recyclerView == null) return;

        //给recyclerView添加滑动监听
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                /*
                到达底部了,如果不加!isLoading的话到达底部如果还一滑动的话就会一直进入这个方法
                就一直去做请求网络的操作,这样的用户体验肯定不好.添加一个判断,每次滑倒底只进行一次网络请求去请求数据
                当请求完成后,在把isLoading赋值为false,下次滑倒底又能进入这个方法了
                 */
                if (newState == RecyclerView.SCROLL_STATE_IDLE //闲置状态
                        && lastVisibleItem + 1 == baseAdapter.getItemCount() //最后可见项为实际内容子项的最后一项
                        && !mLoading//当前没有在加载
                        && !mAllHasLoaded) {//加载未结束全部数据
                    //加载回调
                    onLoadMoreRequested();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //拿到最后一个出现的item的位置
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (layoutManager instanceof LinearLayoutManager) {
                    lastVisibleItem = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                }
            }
        });
    }

    /**
     * 通知加载更多
     */
    public void notifyLoadMoreToLoading() {
        if (mLoading) {
            return;
        }

        if (!mLoading//当前没有在加载
                && !mAllHasLoaded) {//加载未结束全部数据
            //加载回调
            onLoadMoreRequested();
        }
    }

    /**
     * 加载更多回调请求
     */
    private void onLoadMoreRequested() {
        //加载回调
        if (mRequestLoadMoreListener != null) {
            mLoading = true;//标记正在加载
            mRequestLoadMoreListener.onLoadMoreRequested();
        }
    }

}

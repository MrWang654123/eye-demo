package com.cheersmind.cheersgenie.features.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cheersmind.cheersgenie.features.holder.BaseHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * recycler的通用适配器
 */
public class BaseAdapter_Bak<T> extends RecyclerView.Adapter<BaseHolder> {

    //数据集合
    private List<T> mList = new ArrayList<>();
    //子项布局ID
    private int layoutId;

    public BaseAdapter_Bak(int layoutId, List<T> list){
        this.layoutId=layoutId;
        this. mList=list;
    }
    //onCreateViewHolder用来给rv创建缓存
    @Override
    public BaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //参数3 判断条件 true  1.打气 2.添加到paraent
        // false 1.打气 （参考parent的宽度）
        View view =   LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        BaseHolder holder = new BaseHolder(view);
        return holder;
    }
    //onBindViewHolder给缓存控件设置数据
    @Override
    public void onBindViewHolder(BaseHolder holder, int position) {
        T item = mList.get(position);
        convert(holder,item);
    }

    protected void convert(BaseHolder holder, T item) {
        //什么都没有做
    }

    //获取记录数据
    @Override
    public int getItemCount() {
        return mList.size();
    }

}

package com.cheersmind.cheersgenie.main.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.main.entity.TopicInfoEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by goodm on 2017/4/22.
 */
public class TopicSelectPopWindow extends PopupWindow {

    private Context mContext;
    private View view;
    private TextView tvPopClose;
    private GridView gvPopTopic;
    private AdapterView.OnItemClickListener itemClick;
    private List<TopicInfoEntity> baseTopicList = new ArrayList<>();

    public TopicSelectPopWindow(Context context,List<TopicInfoEntity> baseTopicList ,AdapterView.OnItemClickListener itemClick) {
        this.mContext = context;
        this.baseTopicList = baseTopicList;
        this.itemClick = itemClick;
        this.view = LayoutInflater.from(mContext).inflate(R.layout.pop_proj, null);
        tvPopClose = (TextView)view.findViewById(R.id.tv_pop_close);
        tvPopClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        gvPopTopic = (GridView)view.findViewById(R.id.gv_pop_topic);
        gvPopTopic.setAdapter(adapter);
        gvPopTopic.setOnItemClickListener(itemClick);

        // 设置外部可点击
        this.setOutsideTouchable(true);


    /* 设置弹出窗口特征 */
        // 设置视图
        this.setContentView(this.view);
        // 设置弹出窗体的宽和高
        //this.setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
        Display display = ((Activity)mContext).getWindowManager().getDefaultDisplay();

        //this.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);
//        this.setWidth(display.getWidth()*4/5);
//        this.setHeight(display.getHeight()*3/4);
        this.setWidth(display.getWidth());
        this.setHeight(display.getHeight());
        //this.setHeight(600);

        // 设置弹出窗体可点击
        this.setFocusable(true);

        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        // 设置弹出窗体的背景
        this.setBackgroundDrawable(dw);

        // 设置弹出窗体显示时的动画，从底部向上弹出
        this.setAnimationStyle(R.style.proj_popwindow);
    }

    private BaseAdapter adapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return baseTopicList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if(convertView == null){
                convertView = View.inflate(mContext,R.layout.item_pop_topic,null);
                viewHolder = new ViewHolder();
                viewHolder.initView(convertView);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }
            TopicInfoEntity entity = baseTopicList.get(position);

            viewHolder.tvTitle.setText(entity.getTopicName());
            return convertView;
        }
    };

    class ViewHolder{
        ImageView ivIcon;
        TextView tvTitle;
        void initView(View view){
            ivIcon = (ImageView)view.findViewById(R.id.iv_icon);
            tvTitle = (TextView)view.findViewById(R.id.tv_title);
        }
    }

}

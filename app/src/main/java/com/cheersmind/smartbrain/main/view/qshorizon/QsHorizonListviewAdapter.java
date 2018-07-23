package com.cheersmind.smartbrain.main.view.qshorizon;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cheersmind.smartbrain.R;
import com.cheersmind.smartbrain.main.entity.DimensionInfoChildEntity;
import com.cheersmind.smartbrain.main.entity.DimensionInfoEntity;
import com.cheersmind.smartbrain.main.util.imagetool.ImageCacheTool;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/5/30 0030.
 */

public class QsHorizonListviewAdapter extends BaseAdapter {

    private Context context;
    private List<DimensionInfoEntity> entities = new ArrayList<>();
    private int index;

    public QsHorizonListviewAdapter(Context context, List<DimensionInfoEntity> entities,int index) {
        this.context = context;
        this.entities = entities;
        this.index = index;
}

    @Override
    public int getCount() {
        return entities.size();
    }

    @Override
    public Object getItem(int position) {
        return entities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder ;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.qs_item_my_evaluate_topic, null);
            holder.initView(convertView);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        DimensionInfoEntity entity = entities.get(position);
        DimensionInfoChildEntity childEntity = entity.getChildDimension();
        if(index%2 == 0){
            if(childEntity!=null && childEntity.getCompleteFactorCount() == entity.getFactorCount()){
                holder.rtItem.setBackgroundResource(R.mipmap.qs_evaluate_item_bg_pig_select);
            }else{
                holder.rtItem.setBackgroundResource(R.mipmap.qs_evaluate_item_bg_pig_nor);
            }
        } else {
            if(childEntity!=null && childEntity.getCompleteFactorCount() == entity.getFactorCount()){
                holder.rtItem.setBackgroundResource(R.mipmap.qs_evaluate_item_bg_blue_select);
            }else{
                holder.rtItem.setBackgroundResource(R.mipmap.qs_evaluate_item_bg_blue_nor);
            }
        }

        holder.tvItemTitle.setText(entity.getDimensionName());
        if (entity.getChildDimension() != null) {

            String iconUrl = entity.getIcon();
            ImageCacheTool imageCacheTool = ImageCacheTool.getInstance();
            if (!TextUtils.isEmpty(iconUrl)) {
                try {
                    imageCacheTool.asyncLoadImage(new URL(iconUrl), holder.ivIcon);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    holder.ivIcon.setImageResource(R.mipmap.dimension_icon_default);
                }
            }

        } else {
            holder.ivIcon.setImageResource(R.mipmap.dimension_icon_default);
        }

        if(entity.getIsLocked() == 1){
            holder.ivLock.setVisibility(View.VISIBLE);
        }else{
            holder.ivLock.setVisibility(View.GONE);
        }

        return convertView;
    }

    class ViewHolder {
        RelativeLayout rtItem;
        ImageView ivIcon;
        ImageView ivLock;
        TextView tvItemTitle;

        void initView(View view){
            rtItem = (RelativeLayout)view.findViewById(R.id.rt_item);
            ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
            ivLock = (ImageView) view.findViewById(R.id.iv_lock);
            tvItemTitle = (TextView)view.findViewById(R.id.tv_item_title);
        }
    }

}
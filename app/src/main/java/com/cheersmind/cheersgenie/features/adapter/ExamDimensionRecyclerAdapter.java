package com.cheersmind.cheersgenie.features.adapter;

import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.entity.RecyclerCommonSection;
import com.cheersmind.cheersgenie.main.entity.DimensionInfoChildEntity;
import com.cheersmind.cheersgenie.main.entity.DimensionInfoEntity;
import com.cheersmind.cheersgenie.main.entity.TopicInfoChildEntity;
import com.cheersmind.cheersgenie.main.entity.TopicInfoEntity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 测评页面的维度的Recycler适配器
 */
public class ExamDimensionRecyclerAdapter extends BaseSectionQuickAdapter<RecyclerCommonSection<DimensionInfoEntity>, BaseViewHolder> {

    private Fragment fragment;

    /**
     * 构造方法
     * @param layoutResId 普通项的布局ID
     * @param sectionHeadResId 头部项的布局ID
     * @param data 数据集合
     */
    public ExamDimensionRecyclerAdapter(int layoutResId, int sectionHeadResId, List<RecyclerCommonSection<DimensionInfoEntity>> data) {
        super(layoutResId, sectionHeadResId, data);
    }

    /**
     * 构造方法
     * @param layoutResId 普通项的布局ID
     * @param sectionHeadResId 头部项的布局ID
     * @param data 数据集合
     */
    public ExamDimensionRecyclerAdapter(Fragment fragment, int layoutResId, int sectionHeadResId, List<RecyclerCommonSection<DimensionInfoEntity>> data) {
        super(layoutResId, sectionHeadResId, data);
        this.fragment = fragment;
    }

    @Override
    protected void convertHead(BaseViewHolder helper, RecyclerCommonSection item) {
//        DimensionInfoEntity dimensionInfo = (DimensionInfoEntity) item.t;
        TopicInfoEntity topicInfo = (TopicInfoEntity) item.getInfo();
        DimensionInfoEntity dimensionInfo = topicInfo.getDimensions().get(0);
        //标题
        helper.setText(R.id.tv_title, item.header);

        //适合人群
        String suitableUser = "";
        //学生
        if (dimensionInfo.getSuitableUser() == Dictionary.Exam_Suitable_User_Student) {
            suitableUser = fragment.getResources().getString(R.string.exam_topic_suitable_user, fragment.getResources().getString(R.string.student));

        } else if (dimensionInfo.getSuitableUser() == Dictionary.Exam_Suitable_User_Parent) {
            //家长
            suitableUser = fragment.getResources().getString(R.string.exam_topic_suitable_user, fragment.getResources().getString(R.string.parent));
        }
        helper.setText(R.id.tv_suitable_user, suitableUser);

        //有效期
        String dateStr = topicInfo.getEndTime();//ISO8601 时间字符串
        SimpleDateFormat formatIso8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        try {
            Date date = formatIso8601.parse(dateStr);
            SimpleDateFormat formatNormal = new SimpleDateFormat("yyyy-MM-dd");
            String normalDateStr = formatNormal.format(date);
            helper.setText(R.id.tv_end_date, fragment.getResources().getString(R.string.exam_topic_endtime,normalDateStr));

        } catch (ParseException e) {
            e.printStackTrace();
        }

        //查看报告按钮
        TopicInfoChildEntity childTopic = topicInfo.getChildTopic();
        //整个topic已完成
        if (childTopic != null && childTopic.getStatus() == Dictionary.TOPIC_STATUS_COMPLETE) {
            helper.getView(R.id.tv_nav_to_report).setVisibility(View.VISIBLE);
        } else {
            helper.getView(R.id.tv_nav_to_report).setVisibility(View.GONE);
        }

        //场景介绍按钮点击监听
        helper.addOnClickListener(R.id.iv_desc);
        //查看报告按钮点击监听
        helper.addOnClickListener(R.id.tv_nav_to_report);
    }

    @Override
    protected void convert(BaseViewHolder helper, RecyclerCommonSection item) {
        DimensionInfoEntity dimensionInfo = (DimensionInfoEntity) item.t;
        //标题
        helper.setText(R.id.tv_title2, dimensionInfo.getDimensionName());
        //使用人数
        String useCountFormatStr = fragment.getResources().getString(R.string.exam_dimension_use_count);
        String useCount = String.format(useCountFormatStr, dimensionInfo.getUseCount() + "");
        helper.setText(R.id.tv_used_count, useCount);
        //状态：是否完成
        DimensionInfoChildEntity childDimension = dimensionInfo.getChildDimension();
        if (childDimension == null || childDimension.getStatus() == Dictionary.DIMENSION_STATUS_INCOMPLETE) {
            //未完成状态
            helper.getView(R.id.tv_status).setVisibility(View.GONE);

        } else if (childDimension.getStatus() == Dictionary.DIMENSION_STATUS_COMPLETE) {
            //个人报告（已完成）
            helper.getView(R.id.tv_status).setVisibility(View.VISIBLE);
        }

//        Uri uri = Uri.parse(dimensionInfo.getIcon());
//        //初始化控件
//        SimpleDraweeView draweeView = helper.getView(R.id.iv_icon);
//        //加载图片
//        draweeView.setImageURI(uri);

        String url = dimensionInfo.getIcon();
        ImageView imageView = helper.getView(R.id.iv_icon);

        if(!TextUtils.isEmpty(url) && !url.equals(imageView.getTag(R.id.iv_icon))){
            Glide.with(fragment)
                    .load(url)
                    .skipMemoryCache(false)
                    .thumbnail(0.5f)
//                    .fitCenter()
                    .placeholder(R.mipmap.dimension_icon_default)
                    .error(R.mipmap.dimension_icon_default)
                    .dontAnimate()//Glide默认是渐变动画，设置dontAnimate()不要动画
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);
            imageView.setTag(R.id.iv_icon,url);
        }

    }

}

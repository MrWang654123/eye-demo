package com.cheersmind.smartbrain.main.fragment;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cheersmind.smartbrain.R;
import com.cheersmind.smartbrain.main.Exception.QSCustomException;
import com.cheersmind.smartbrain.main.activity.MainActivity;
import com.cheersmind.smartbrain.main.activity.QsDimensionDetailsActivity;
import com.cheersmind.smartbrain.main.dao.ChildInfoDao;
import com.cheersmind.smartbrain.main.entity.DimensionInfoChildEntity;
import com.cheersmind.smartbrain.main.entity.DimensionInfoEntity;
import com.cheersmind.smartbrain.main.entity.ReportItemEntity;
import com.cheersmind.smartbrain.main.entity.TopicInfoEntity;
import com.cheersmind.smartbrain.main.entity.TopicRootEntity;
import com.cheersmind.smartbrain.main.entity.UpdateNotificationEntity;
import com.cheersmind.smartbrain.main.service.BaseService;
import com.cheersmind.smartbrain.main.service.DataRequestService;
import com.cheersmind.smartbrain.main.util.DateTimeUtils;
import com.cheersmind.smartbrain.main.util.InjectionWrapperUtil;
import com.cheersmind.smartbrain.main.util.JsonUtil;
import com.cheersmind.smartbrain.main.util.OnMultiClickListener;
import com.cheersmind.smartbrain.main.util.ToastUtil;
import com.cheersmind.smartbrain.main.view.CustomScrollBar;
import com.cheersmind.smartbrain.main.view.EmptyLayout;
import com.cheersmind.smartbrain.main.view.LoadingView;
import com.cheersmind.smartbrain.main.view.horizon.MyListview;
import com.cheersmind.smartbrain.main.view.qshorizon.QsHorizonListviewAdapter;
import com.cheersmind.smartbrain.main.view.qshorizon.QsHorizontalListView;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class QsEvaluateFragment extends Fragment implements View.OnClickListener{

    private View contentView;
    private View headView;
    private EmptyLayout emptyLayout;

//    private MarqueeText tvNotification;
//    private CustomScrollBar tvNotification;
    private RelativeLayout rlNotification;
//    private ImageView ivNotification;

    private LinearLayout llRoot;
    private Button btnLateStart;
    private TextView tvDimensionName;
    private LinearLayout llStage;
    private TextView tvDimensionTime;
    private SimpleDraweeView ivDimension;
    private TextView tvUserCount;
    private TextView tvPraise;

    private MyListview lvEvaluate;
    private EvaluateAdapter evaluateAdapter;

    List<TopicInfoEntity> topicList = new ArrayList<>();
    DimensionInfoEntity headDimension;

    public QsEvaluateFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(contentView == null){
            contentView = inflater.inflate(R.layout.qs_fragment_evaluate, container,
                    false);
        }

        initView();
        getUpdateNotification();
        return contentView;
    }

    @Override
    public void onClick(View view) {
        if(view == btnLateStart){
            startCurrrentDimension(headDimension,new TopicInfoEntity());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadChildTopicList();
    }

    private void initView(){
        emptyLayout = (EmptyLayout) contentView.findViewById(R.id.emptyLayout);
        emptyLayout.setOnLayoutClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View view) {
                loadChildTopicList();
            }
        });

        rlNotification = (RelativeLayout) contentView.findViewById(R.id.rl_notification);

        headView = View.inflate(getActivity(),R.layout.qs_fragment_evaluate_head,null);

        llRoot = (LinearLayout)headView.findViewById(R.id.ll_root);
        tvDimensionName = (TextView)headView.findViewById(R.id.tv_dimension_name);
        tvDimensionTime = (TextView)headView.findViewById(R.id.tv_dimension_time);
        tvUserCount = (TextView)headView.findViewById(R.id.tv_user_count);
        tvPraise = (TextView)headView.findViewById(R.id.tv_praise);
        llStage = (LinearLayout)headView.findViewById(R.id.ll_stage);
        ivDimension = (SimpleDraweeView)headView.findViewById(R.id.iv_dimension);
        btnLateStart = (Button)headView.findViewById(R.id.btn_late_start);
        btnLateStart.setOnClickListener(this);

        lvEvaluate = (MyListview)contentView.findViewById(R.id.lv_my_evaluate);
        lvEvaluate.addHeaderView(headView);
        evaluateAdapter = new EvaluateAdapter(getActivity());
        lvEvaluate.setAdapter(evaluateAdapter);
        evaluateAdapter.notifyDataSetChanged();

    }

    private void updateHeadData(){
        if(headDimension!=null){
            if(!TextUtils.isEmpty(headDimension.getDimensionName())){
                tvDimensionName.setText(headDimension.getDimensionName());
            }
//            if(!TextUtils.isEmpty(headDimension.getIcon())){
//                try {
//                    ImageCacheTool.getInstance().asyncLoadImage(new URL(headDimension.getIcon()),ivDimension,R.mipmap.dimension_icon_default);
//                } catch (MalformedURLException e) {
//                    e.printStackTrace();
//                }
//            }
            if(TextUtils.isEmpty(headDimension.getIcon())){
                ivDimension.setImageResource(R.mipmap.dimension_icon_default);
            }else {
                ivDimension.setImageURI(Uri.parse(headDimension.getIcon()));
            }

//            String content = getResources().getString(R.string.qs_factor_complete_hint,"8儿童焦虑");
//            SpannableString spanString = new SpannableString(content);
//            spanString.setSpan(new RadiusBackgroundSpan("#ffa200", 10), 4, 5, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//            ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#12b2f4"));
//            spanString.setSpan(colorSpan, 4, 5, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

            if(!TextUtils.isEmpty(headDimension.getUpdateTime())){
                tvDimensionTime.setText(DateTimeUtils.getTimeFormatText(DateTimeUtils.strToDateLong(headDimension.getUpdateTime())));
            }else{
                tvDimensionTime.setText("");
            }

            tvUserCount.setText(getActivity().getResources().getString(R.string.qs_dimension_user_count,String.valueOf(headDimension.getUseCount())));
            tvPraise.setText(String.valueOf(headDimension.getUseCount()));

            DimensionInfoChildEntity childEntity = headDimension.getChildDimension();
            llStage.removeAllViews();
            if(childEntity == null){
                btnLateStart.setText("开始");
                for(int i=0;i<headDimension.getFactorCount();i++){
                    View itemView = View.inflate(getActivity(),R.layout.qs_factor_stage_item,null);
                    TextView tv = (TextView) itemView.findViewById(R.id.tv_bg);
                    tv.setText(String.valueOf(i+1));
                    tv.setBackgroundResource(R.mipmap.qs_number_bg_nor);
                    llStage.addView(itemView);
                }
            }else{
                if(childEntity.getStatus() == 1){
                    btnLateStart.setText("查看报告");
                }else{
                    btnLateStart.setText("继续该阶段测评");
                }
                for(int i=0;i<headDimension.getFactorCount();i++){
                    View itemView = View.inflate(getActivity(),R.layout.qs_factor_stage_item,null);
                    TextView tv = (TextView) itemView.findViewById(R.id.tv_bg);
                    tv.setText(String.valueOf(i+1));
                    if(i<childEntity.getCompleteFactorCount()){
                        tv.setBackgroundResource(R.mipmap.qs_number_bg_select);
                    }else{
                        tv.setBackgroundResource(R.mipmap.qs_number_bg_nor);
                    }
                    llStage.addView(itemView);
                }
            }


        }
    }

    private class EvaluateAdapter extends BaseAdapter {

        private LayoutInflater mInflater;


        public EvaluateAdapter(Context context){
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return topicList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            convertView = View.inflate(getActivity(),R.layout.qs_item_my_evaluate,null);
            TextView tvProjName = (TextView)convertView.findViewById(R.id.tv_topic_name);
            tvProjName.setText(topicList.get(position).getTopicName());

            TextView tvProjsSatus = (TextView)convertView.findViewById(R.id.tv_topic_status);
            if(topicList.get(position).getChildTopic() == null){
                tvProjsSatus.setText(getActivity().getResources().getString(R.string.qs_topic_status));
            }else{
                if(topicList.get(position).getChildTopic().getStatus() == 1){
                    tvProjsSatus.setText(getActivity().getResources().getString(R.string.qs_topic_status1));
                }else{
                    tvProjsSatus.setText(getActivity().getResources().getString(R.string.qs_topic_status0));
                }
            }

            final List<DimensionInfoEntity> dimensions = topicList.get(position).getDimensions();
            QsHorizontalListView horizontalListView = (QsHorizontalListView) convertView.findViewById(R.id.hsv);
            QsHorizonListviewAdapter adapter = new QsHorizonListviewAdapter(getActivity(),
                    dimensions,position);
            horizontalListView.setAdapter(adapter);

            horizontalListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int index, long id) {
                    TopicInfoEntity topicInfoEntity = topicList.get(position);
                    startCurrrentDimension(dimensions.get(index),topicInfoEntity);
                }
            });
            return convertView;
        }
    }

    //获取孩子关注主题列表
    public void loadChildTopicList(){
        LoadingView.getInstance().show(getActivity());
        DataRequestService.getInstance().loadChildTopicList(ChildInfoDao.getDefaultChildId(), 0, 100, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                LoadingView.getInstance().dismiss();
                emptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
            }

            @Override
            public void onResponse(Object obj) {
                LoadingView.getInstance().dismiss();
                emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                if(obj!=null){
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    TopicRootEntity topicData = InjectionWrapperUtil.injectMap(dataMap, TopicRootEntity.class);
                    if(topicData != null && topicData.getItems()!=null && topicData.getItems().size()>0){
                        topicList = topicData.getItems();
                        evaluateAdapter.notifyDataSetChanged();
                    }else{
                        emptyLayout.setErrorType(EmptyLayout.NODATA);
                    }
                    getLatestDimension();
                }else{
                    emptyLayout.setErrorType(EmptyLayout.NODATA);
                }
            }
        });
    }

    private void getLatestDimension(){
        LoadingView.getInstance().show(getActivity());
        DataRequestService.getInstance().getLatestDimension(ChildInfoDao.getDefaultChildId(),new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                e.printStackTrace();
                LoadingView.getInstance().dismiss();
                if(headView!=null){
                    llRoot.setVisibility(View.GONE);
                }
            }

            @Override
            public void onResponse(Object obj) {
                LoadingView.getInstance().dismiss();
                if(obj!=null){
                    Map dataMap = JsonUtil.fromJson(obj.toString(),Map.class);
                    headDimension = InjectionWrapperUtil.injectMap(dataMap,DimensionInfoEntity.class);
                    if(headDimension!=null && !TextUtils.isEmpty(headDimension.getDimensionId())){
                        if(headView!=null){
                            llRoot.setVisibility(View.VISIBLE);
                        }
                        updateHeadData();
                    }else{
                        if(headView!=null){
                            llRoot.setVisibility(View.GONE);
                        }
                    }
                }else{
                    if(headView!=null){
                        llRoot.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    private void startCurrrentDimension(final DimensionInfoEntity dimensionInfoEntity,final TopicInfoEntity topicInfoEntity) {
        if( dimensionInfoEntity==null ){
            return;
        }

        if(dimensionInfoEntity.getIsLocked() == 1){
            if(!TextUtils.isEmpty(dimensionInfoEntity.getPreDimensions())){
                String [] dimensionIds = dimensionInfoEntity.getPreDimensions().split(",");
                List<DimensionInfoEntity> dimensions = topicInfoEntity.getDimensions();
                if(dimensionIds.length>0 && dimensions.size()>0){
                    StringBuffer stringBuffer = new StringBuffer("");
                    for(int i=0;i<dimensions.size();i++){
                        for(int j=0;j<dimensionIds.length;j++){
                            if(dimensionIds[j].equals(dimensions.get(i).getDimensionId())){
                                stringBuffer.append(dimensions.get(i).getDimensionName());
                                if(j!=dimensionIds.length-1){
                                    stringBuffer.append("、");
                                }
                            }
                        }
                    }
                    if(!TextUtils.isEmpty(stringBuffer.toString())){
                        String str = getActivity().getResources().getString(R.string.qs_dimension_pre_complete,stringBuffer.toString());
                        ToastUtil.showLong(getActivity(),str);
                    }
                }
            }
            return;
        }
        DimensionInfoChildEntity entity = dimensionInfoEntity.getChildDimension();
        if (entity == null) {
            //开始这个量表
            LoadingView.getInstance().show(getActivity());
            DataRequestService.getInstance().startChidlDimension(ChildInfoDao.getDefaultChildId(), dimensionInfoEntity.getTopicId(),dimensionInfoEntity.getDimensionId(),topicInfoEntity.getExamId(),new BaseService.ServiceCallback() {
                @Override
                public void onFailure(QSCustomException e) {
                    LoadingView.getInstance().dismiss();
                    ToastUtil.showShort(getActivity(),"开始量表失败");
                }

                @Override
                public void onResponse(Object obj) {
                    LoadingView.getInstance().dismiss();
                    if(obj!=null) {
                        Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                        DimensionInfoChildEntity startEntity = InjectionWrapperUtil.injectMap(dataMap, DimensionInfoChildEntity.class);
                        if (startEntity != null) {
                            dimensionInfoEntity.setChildDimension(startEntity);
                            QsDimensionDetailsActivity.startQsDimensionDetailsActivity(getActivity(),topicInfoEntity,dimensionInfoEntity);
                        }
                    }
                }
            });
        } else {
            if(entity.getStatus() == 0){
                //继续
                QsDimensionDetailsActivity.startQsDimensionDetailsActivity(getActivity(),topicInfoEntity,dimensionInfoEntity);
            }else{
                //量表报告
                showReportPanel(null,dimensionInfoEntity);
//                LoadingView.getInstance().show(getActivity());
//                DataRequestService.getInstance().getTopicReportByRelation(ChildInfoDao.getDefaultChildId(),
//                        dimensionInfoEntity.getChildDimension().getExamId(),
//                        dimensionInfoEntity.getTopicDimensionId(),
//                        ChartViewHelper.REPORT_RELATION_TOPIC_DIMENSION,
//                        "0", new BaseService.ServiceCallback() {
//                            @Override
//                            public void onFailure(QSCustomException e) {
//                                LoadingView.getInstance().dismiss();
//                                ToastUtil.showShort(getActivity(),"获取量表报告失败");
//                            }
//
//                            @Override
//                            public void onResponse(Object obj) {
//                                LoadingView.getInstance().dismiss();
//                                if(obj!=null){
//                                    Map dataMap = JsonUtil.fromJson(obj.toString(),Map.class);
//                                    ReportRootEntity data = InjectionWrapperUtil.injectMap(dataMap,ReportRootEntity.class);
//                                    if(data!=null && data.getChartDatas()!=null){
//
//                                        if(data.getChartDatas().size()==0 && data.getReportResults().size()==0){
//                                            ToastUtil.showShort(getActivity(),"感谢您的信息搜集");
//                                            return;
//                                        }
//                                        List<ReportResultEntity> reportResultEntities = data.getReportResults();
//                                        List<ReportItemEntity>  dimensionReports = data.getChartDatas();
//                                        if(dimensionReports!=null && dimensionReports.size()>0) {
//                                            for (int i = 0; i < dimensionReports.size(); i++) {
//                                                if (reportResultEntities != null && reportResultEntities.size() > 0) {
//                                                    if (dimensionReports.get(i).getReportResult() == null) {
//                                                        if(dimensionReports.get(i).getReportResult()==null){
//                                                            dimensionReports.get(i).setReportResult(reportResultEntities.get(0));
//                                                        }
//                                                    }
//                                                }
//                                            }
//                                        }
//
//                                        showReportPanel(dimensionReports,dimensionInfoEntity);
//                                    }
//
//                                }
//                            }
//                        });

            }
        }
    }

    private void showReportPanel(List<ReportItemEntity> dimensionReports, DimensionInfoEntity entity){
        if(getActivity() instanceof MainActivity){
            MainActivity mainActivity = (MainActivity)getActivity();
            mainActivity.showReportPanel(dimensionReports,entity);
        }
    }

    private void getUpdateNotification(){
        DataRequestService.getInstance().getUpdateNotification(new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {

            }

            @Override
            public void onResponse(Object obj) {
                if(obj!=null){
                    Map map = JsonUtil.fromJson(obj.toString(),Map.class);
                    UpdateNotificationEntity notificationEntity = InjectionWrapperUtil.injectMap(map,UpdateNotificationEntity.class);
                    if(notificationEntity!=null){
                        if(notificationEntity.isNotice() && !TextUtils.isEmpty(notificationEntity.getMessage())){
                            View view = View.inflate(getActivity(),R.layout.layout_notification,null);
                            rlNotification.setVisibility(View.VISIBLE);
                            rlNotification.removeAllViews();
                            rlNotification.addView(view);
                            CustomScrollBar tvNotification = (CustomScrollBar) view.findViewById(R.id.tv_notification);
                            tvNotification.setText(notificationEntity.getMessage());
                            ImageView ivNotification = (ImageView)view.findViewById(R.id.iv_notification);
                            ivNotification.setOnClickListener(new OnMultiClickListener() {
                                @Override
                                public void onMultiClick(View view) {
                                    rlNotification.removeAllViews();
                                    rlNotification.setVisibility(View.GONE);
                                }
                            });
                        }
                    }
                }
            }
        });
    }

}

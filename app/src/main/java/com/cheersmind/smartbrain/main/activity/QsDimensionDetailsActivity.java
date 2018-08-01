package com.cheersmind.smartbrain.main.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cheersmind.smartbrain.R;
import com.cheersmind.smartbrain.main.Exception.QSCustomException;
import com.cheersmind.smartbrain.main.dao.ChildInfoDao;
import com.cheersmind.smartbrain.main.entity.DimensionInfoChildEntity;
import com.cheersmind.smartbrain.main.entity.DimensionInfoEntity;
import com.cheersmind.smartbrain.main.entity.FactorInfoChildEntity;
import com.cheersmind.smartbrain.main.entity.FactorInfoEntity;
import com.cheersmind.smartbrain.main.entity.FactorRootEntity;
import com.cheersmind.smartbrain.main.entity.TopicInfoEntity;
import com.cheersmind.smartbrain.main.event.CommonEvent;
import com.cheersmind.smartbrain.main.event.ContinueFactorEvent;
import com.cheersmind.smartbrain.main.service.BaseService;
import com.cheersmind.smartbrain.main.service.DataRequestService;
import com.cheersmind.smartbrain.main.util.InjectionWrapperUtil;
import com.cheersmind.smartbrain.main.util.JsonUtil;
import com.cheersmind.smartbrain.main.util.OnMultiClickListener;
import com.cheersmind.smartbrain.main.util.RepetitionClickUtil;
import com.cheersmind.smartbrain.main.util.ToastUtil;
import com.cheersmind.smartbrain.main.util.imagetool.ImageCacheTool;
import com.cheersmind.smartbrain.main.view.EmptyLayout;
import com.cheersmind.smartbrain.main.view.LoadingView;
import com.cheersmind.smartbrain.main.view.qsdialog.DimensionDetailsDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/5/30 0030.
 */

public class QsDimensionDetailsActivity extends BaseActivity implements View.OnClickListener{

    public static final String TOPIC_INFO = "topic_info";
    public static final String DIMENSION_INFO = "dimension_info";

    private EmptyLayout emptyLayout;
    private TextView ivBack;
    private RelativeLayout rtDimension;
    private ImageView ivDimension;
    private TextView tvDimensionName;
    private TextView tvDimensionDesc;
    private LinearLayout llStage;
    private TextView tvTestCount;
    private TextView tvSupportCount;
    private ListView lvFactor;

    private TopicInfoEntity topicInfoEntity;
    private DimensionInfoEntity dimensionInfoEntity;
    List<FactorInfoEntity> factorBaseList = new ArrayList<>();
    List<FactorInfoEntity> factorBaseListSort = new ArrayList<>();
    private int curCanDoStage = 1;//当前因子列表可做的第一个阶段

    public static void startQsDimensionDetailsActivity(Context context,TopicInfoEntity topicInfoEntity, DimensionInfoEntity dimensionInfoEntity){
        Intent intent = new Intent(context, QsDimensionDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(QsDimensionDetailsActivity.TOPIC_INFO, topicInfoEntity);
        bundle.putSerializable(QsDimensionDetailsActivity.DIMENSION_INFO, dimensionInfoEntity);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qs_activity_demension_detail);
        getSupportActionBar().hide();
        initView();
        initData();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadChildFactorList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initView(){
        emptyLayout = (EmptyLayout)findViewById(R.id.emptyLayout);
        emptyLayout.setOnLayoutClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View view) {
                loadChildFactorList();
            }
        });
        View headView = View.inflate(QsDimensionDetailsActivity.this,R.layout.qs_acticity_dimension_factor_head,null);
        ivBack = (TextView)headView.findViewById(R.id.iv_back);
        ivBack.setOnClickListener(this);
        rtDimension = (RelativeLayout)headView.findViewById(R.id.rt_dimension);
        rtDimension.setOnClickListener(this);
        ivDimension = (ImageView)headView.findViewById(R.id.iv_dimension);
        tvDimensionName = (TextView)headView.findViewById(R.id.tv_dimension_name);
        tvDimensionDesc = (TextView)headView.findViewById(R.id.tv_dimension_desc);
        llStage = (LinearLayout)headView.findViewById(R.id.ll_stage);
        tvTestCount = (TextView)headView.findViewById(R.id.tv_test_count);
        tvSupportCount = (TextView)headView.findViewById(R.id.tv_support_count);

        lvFactor = (ListView)findViewById(R.id.lv_factor);
        lvFactor.addHeaderView(headView);
        lvFactor.setAdapter(adapter);
    }

    private void initData(){
        if(getIntent()==null || getIntent().getExtras()==null){
            ToastUtil.showShort(QsDimensionDetailsActivity.this,"数据传递有误");
            return;
        }
        topicInfoEntity = (TopicInfoEntity)getIntent().getExtras().getSerializable(TOPIC_INFO);
        dimensionInfoEntity = (DimensionInfoEntity) getIntent().getExtras().getSerializable(DIMENSION_INFO);

        updateDimensionDetails(dimensionInfoEntity);
    }

    private void updateDimensionDetails(DimensionInfoEntity entity){
        if(entity==null){
            return;
        }
        if(TextUtils.isEmpty(entity.getIcon())){
            ivDimension.setImageResource(R.mipmap.dimension_icon_default);
        }else{
            ImageCacheTool imageCacheTool = ImageCacheTool.getInstance();
            try {
                imageCacheTool.asyncLoadImage(new URL(entity.getIcon()),ivDimension);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        if(TextUtils.isEmpty(entity.getDimensionName())){
            tvDimensionName.setText("");
        }else{
            tvDimensionName.setText(entity.getDimensionName());
        }

        if(TextUtils.isEmpty(entity.getDefinition())){
            if(!TextUtils.isEmpty(entity.getDescription())){
                tvDimensionDesc.setText(entity.getDescription());
            }else{
                tvDimensionDesc.setText("");
            }
        }else{
            tvDimensionDesc.setText(entity.getDefinition());
        }

        tvTestCount.setText(getResources().getString(R.string.qs_dimension_user_count,String.valueOf(entity.getUseCount())));
    }

    private void updateStageLayout(LinearLayout llStage){
        llStage.removeAllViews();
        for(int i=0;i<factorBaseList.size();i++){
            FactorInfoChildEntity entity = factorBaseList.get(i).getChildFactor();
            View itemView = View.inflate(QsDimensionDetailsActivity.this,R.layout.qs_factor_stage_item,null);
            TextView tv = (TextView) itemView.findViewById(R.id.tv_bg);
            tv.setText(String.valueOf(i+1));
            if(entity!=null && entity.getStatus()==2){
                tv.setBackgroundResource(R.mipmap.qs_number_bg_select);
            }else{
                tv.setBackgroundResource(R.mipmap.qs_number_bg_nor);
            }
            llStage.addView(itemView);
        }
    }

    @Override
    public void onClick(View view) {
        if(!RepetitionClickUtil.isFastClick()){
            return;
        }
        if(view == rtDimension){
            DimensionDetailsDialog dimensionDetailsDialog = new DimensionDetailsDialog(QsDimensionDetailsActivity.this,dimensionInfoEntity);
            dimensionDetailsDialog.show();
        }else if(view == ivBack){
            finish();
        }
    }

    BaseAdapter adapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return factorBaseListSort == null ? 0 : factorBaseListSort.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if(view == null){
                view = View.inflate(QsDimensionDetailsActivity.this,R.layout.qs_item_dimension_factor,null);
                holder = new ViewHolder();
                holder.initView(view);
                view.setTag(holder);
            }else {
                holder = (ViewHolder)view.getTag();
            }

            final FactorInfoEntity factorInfoEntity = factorBaseListSort.get(i);

            holder.tvIndex.setText(String.valueOf(factorInfoEntity.getStage()));

            if(TextUtils.isEmpty(factorInfoEntity.getFactorName())){
                holder.tvFactorName.setText("");
            }else{
                holder.tvFactorName.setText(factorInfoEntity.getFactorName());
            }

            if(TextUtils.isEmpty(factorInfoEntity.getDeccription())){
                holder.tvFactorDesc.setText("");
            }else{
                holder.tvFactorDesc.setText(Html.fromHtml(factorInfoEntity.getDeccription()));
            }

            if(TextUtils.isEmpty(factorInfoEntity.getInstruction())){
                holder.tvFactorHint.setText("");
            }else{
                String hint ="答题提醒：<br/>" + factorInfoEntity.getInstruction();
                holder.tvFactorHint.setText(Html.fromHtml(hint));
            }

            final FactorInfoChildEntity childEntity = factorInfoEntity.getChildFactor();

            //答题数、获得鲜花、反应时间
            if(childEntity == null){
                String zero = "0";
                holder.tvQuesCount.setText(getResources().getString(R.string.qs_factor_detail_ques_count,
                        zero,
                        String.valueOf(factorInfoEntity.getQuestionCount())));
                holder.tvFlowerCount.setText(getResources().getString(R.string.qs_factor_detail_flower_count,
                        zero,
                        String.valueOf(factorInfoEntity.getFlowers()*factorInfoEntity.getQuestionCount())));
                holder.tvTimeCount.setText(getResources().getString(R.string.qs_factor_detail_react_count,zero));
            }else{
                holder.tvQuesCount.setText(getResources().getString(R.string.qs_factor_detail_ques_count,
                        String.valueOf(childEntity.getCompleteCount()),
                        String.valueOf(factorInfoEntity.getQuestionCount())));
                holder.tvFlowerCount.setText(getResources().getString(R.string.qs_factor_detail_flower_count,
                        String.valueOf(childEntity.getFlowers()),
                        String.valueOf(factorInfoEntity.getFlowers()*factorInfoEntity.getQuestionCount())));
                String per = "0";
                if(childEntity.getCompleteCount() != 0){
                    float avageTime = childEntity.getCostTime() / childEntity.getCompleteCount();
                    //构造方法的字符格式这里如果小数不足2位,会以0补足.
                    DecimalFormat decimalFormat=new DecimalFormat(".00");
                    per = decimalFormat.format(avageTime);
                    if(per.equals(".00")){
                        per = "0.00";
                    }
                }

                if(per.equals("0.00")){
                    holder.tvTimeCount.setText(getResources().getString(R.string.qs_factor_detail_react_count,"--"));

                }else{
                    holder.tvTimeCount.setText(getResources().getString(R.string.qs_factor_detail_react_count,String.valueOf(per)));

                }
            }
            //按钮状态
            if(curCanDoStage == factorInfoEntity.getStage()){
                if(childEntity == null){
                    holder.btnStart.setBackgroundResource(R.drawable.btn_login_selector);
                    holder.btnStart.setText("开始该阶段测评");
                    holder.btnStart.setTextColor(getResources().getColor(R.color.color_text_white));
                }else{
                    holder.btnStart.setBackgroundResource(R.drawable.btn_login_selector);
                    holder.btnStart.setText("继续该阶段测评");
                    holder.btnStart.setTextColor(getResources().getColor(R.color.color_text_white));
                }
                holder.tvIndex.setBackgroundResource(R.mipmap.qs_number_circle_nor);
                holder.tvIndex.setTextColor(getResources().getColor(R.color.color_2e2e2e));
            }else{
                if(childEntity == null){
                    holder.btnStart.setBackgroundResource(R.mipmap.qs_factor_complete_bg);
                    holder.btnStart.setText("未开始");
                    holder.btnStart.setTextColor(getResources().getColor(R.color.color_6e6e6e));

                    holder.tvIndex.setBackgroundResource(R.mipmap.qs_number_circle_nor);
                    holder.tvIndex.setTextColor(getResources().getColor(R.color.color_2e2e2e));
                }else{
                    if(childEntity.getStatus()==2){
                        holder.btnStart.setBackgroundResource(R.mipmap.qs_factor_complete_bg);
                        holder.btnStart.setText("已完成");
                        holder.btnStart.setTextColor(getResources().getColor(R.color.color_6e6e6e));

                        holder.tvIndex.setBackgroundResource(R.mipmap.qs_factor_stage_bg_select);
                        holder.tvIndex.setTextColor(getResources().getColor(R.color.color_text_white));
                    }else{
                        holder.btnStart.setBackgroundResource(R.drawable.btn_login_selector);
                        holder.btnStart.setText("继续该阶段测评");
                        holder.btnStart.setTextColor(getResources().getColor(R.color.color_text_white));

                        holder.tvIndex.setBackgroundResource(R.mipmap.qs_number_circle_nor);
                        holder.tvIndex.setTextColor(getResources().getColor(R.color.color_2e2e2e));
                    }
                }
            }

            holder.btnStart.setOnClickListener(new OnMultiClickListener() {
                @Override
                public void onMultiClick(View view) {
                    if(childEntity!=null && childEntity.getStatus()==2){
                        return;
                    }
                    startCurrentFactor(factorInfoEntity);
                }
            });

            return view;
        }
    };

    class ViewHolder{
        TextView tvIndex;
        TextView tvFactorName;
        TextView tvFactorDesc;
        TextView tvFactorHint;
        TextView tvQuesCount;
        TextView tvFlowerCount;
        TextView tvTimeCount;
        Button btnStart;
        void initView(View view){
            tvIndex = (TextView)view.findViewById(R.id.tv_index);
            tvFactorName = (TextView)view.findViewById(R.id.tv_factor_name);
            tvFactorDesc = (TextView)view.findViewById(R.id.tv_factor_desc);
            tvFactorHint = (TextView)view.findViewById(R.id.tv_factor_hint);
            tvQuesCount = (TextView)view.findViewById(R.id.tv_ques_count);
            tvFlowerCount = (TextView)view.findViewById(R.id.tv_flower_count);
            tvTimeCount = (TextView)view.findViewById(R.id.tv_time_count);
            btnStart = (Button) view.findViewById(R.id.btn_start);
        }
    }

    private void startCurrentFactor(final FactorInfoEntity factorInfoEntity){
        FactorInfoChildEntity entity = factorInfoEntity.getChildFactor();

        String examId = "";
        DimensionInfoChildEntity dimensionInfoChildEntity = dimensionInfoEntity.getChildDimension();
        if(dimensionInfoChildEntity!=null){
            examId = dimensionInfoChildEntity.getExamId();
        }
        if(entity==null){
            DataRequestService.getInstance().startChildFactor(ChildInfoDao.getDefaultChildId(),
                    dimensionInfoEntity.getTopicId(),
                    dimensionInfoEntity.getDimensionId(),
                    factorInfoEntity.getFactorId(),
                    examId,
                    new BaseService.ServiceCallback() {
                @Override
                public void onFailure(QSCustomException e) {
                    e.printStackTrace();
                    ToastUtil.showShort(QsDimensionDetailsActivity.this,"开始因子失败！");
                }

                @Override
                public void onResponse(Object obj) {
                    if(obj!=null){
                        Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                        FactorInfoChildEntity childFactor = InjectionWrapperUtil.injectMap(dataMap, FactorInfoChildEntity.class);
                        childFactor.setFactorName(factorInfoEntity.getFactorName());
                        factorInfoEntity.setChildFactor(childFactor);
                        QsEvaluateQuestionActivity.startEvaluateQuestionActivity(QsDimensionDetailsActivity.this,
                                factorInfoEntity,dimensionInfoEntity,factorBaseList);
                    }

                }
            });
        }else{
            if(entity.getStatus()!=2){
                entity.setFactorName(factorInfoEntity.getFactorName());
                QsEvaluateQuestionActivity.startEvaluateQuestionActivity(QsDimensionDetailsActivity.this,
                        factorInfoEntity,dimensionInfoEntity,factorBaseList);
            }else{
                ToastUtil.showShort(QsDimensionDetailsActivity.this,"已完成且已提交因子，不能重复答题！");
            }

        }
    }

    private boolean isLastFactor(){
        int count = 0;//完成数量
        for(int i=0;i<factorBaseList.size();i++) {
            FactorInfoChildEntity entity = factorBaseList.get(i).getChildFactor();
            if (entity != null && entity.getStatus() == 2) {
                count++;
            }
        }

        if(count == factorBaseList.size()-1){
            return true;
        }
        return false;
    }

    /**
     * 设置因子的阶段数
     */
    private void setFactorBaseListStage(){
        if(factorBaseList!=null && factorBaseList.size()>0){
            for(int i=0;i<factorBaseList.size();i++){
                factorBaseList.get(i).setStage(i+1);
            }
        }
    }

    private void loadChildFactorList(){
        if(dimensionInfoEntity==null){
            return;
        }
        LoadingView.getInstance().show(QsDimensionDetailsActivity.this);
        DataRequestService.getInstance().getFactorList(ChildInfoDao.getDefaultChildId(),dimensionInfoEntity.getDimensionId(),
                dimensionInfoEntity.getChildDimension().getChildDimensionId(), 0, 100, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                emptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                LoadingView.getInstance().dismiss();
                String message = e.getMessage();
                if(TextUtils.isEmpty(message)){
                    message = "获取因子列表失败";
                }
                ToastUtil.showShort(QsDimensionDetailsActivity.this,message);
            }

            @Override
            public void onResponse(Object obj) {
                emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                LoadingView.getInstance().dismiss();
                if(obj!=null){
                    Map dataMap = JsonUtil.fromJson(obj.toString(),Map.class);
                    FactorRootEntity entity = InjectionWrapperUtil.injectMap(dataMap,FactorRootEntity.class);
                    if(entity!=null && entity.getItems()!=null){
                        factorBaseList = entity.getItems();
                        setFactorBaseListStage();
                        factorBaseListSort.clear();
                        //列表排序依次：当前作答量表、未作答从小到大、已完成从小到大
                        if(factorBaseList!=null && factorBaseList.size()>0){
                            FactorInfoEntity candoFactor = factorBaseList.get(0);
                            for(FactorInfoEntity factorInfoEntity:factorBaseList){
                                FactorInfoChildEntity factorInfoChildEntity = factorInfoEntity.getChildFactor();
                                if(factorInfoChildEntity!=null){
                                    if(factorInfoChildEntity.getStatus() !=2){
                                        candoFactor = factorInfoEntity;
                                        break;
                                    }
                                }else{
                                    candoFactor = factorInfoEntity;
                                    break;
                                }
                            }
                            curCanDoStage = candoFactor.getStage();
                            factorBaseListSort.add(candoFactor);
                            List<FactorInfoEntity> nodoList = new ArrayList<>();
                            List<FactorInfoEntity> completeList = new ArrayList<>();
                            for(int i=0; i<factorBaseList.size(); i++){
                                FactorInfoEntity factorInfoEntity = factorBaseList.get(i);
                                if(candoFactor.getStage() != factorBaseList.get(i).getStage()){
                                    if(factorInfoEntity.getChildFactor()!=null && factorInfoEntity.getChildFactor().getStatus()==2){
                                        completeList.add(factorInfoEntity);
                                    }else{
                                        nodoList.add(factorInfoEntity);
                                    }
                                }
                            }
                            factorBaseListSort.addAll(nodoList);
                            factorBaseListSort.addAll(completeList);
                            adapter.notifyDataSetChanged();
                            updateStageLayout(llStage);
                        }else{
                            emptyLayout.setErrorType(EmptyLayout.NODATA);
                        }
                    }else{
                        emptyLayout.setErrorType(EmptyLayout.NODATA);
                    }
                }
            }
        });
    }

    /**
     * 处理继续下一阶段因子消息时间
     * @param continueFactorEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void continueFactorEventBus(ContinueFactorEvent continueFactorEvent){
        if(continueFactorEvent!=null){
            FactorInfoEntity factorInfoEntity = continueFactorEvent.getFactorInfoEntity();
            if(factorInfoEntity!=null){
                startCurrentFactor(factorInfoEntity);
            }else{
                EventBus.getDefault().post(new CommonEvent(CommonEvent.EVENT_REFRESH_REPORT));
                finish();
            }
        }
    }

}

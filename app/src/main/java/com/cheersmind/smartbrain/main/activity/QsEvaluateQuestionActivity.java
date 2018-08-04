package com.cheersmind.smartbrain.main.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cheersmind.smartbrain.R;
import com.cheersmind.smartbrain.main.Exception.QSCustomException;
import com.cheersmind.smartbrain.main.dao.ChildInfoDao;
import com.cheersmind.smartbrain.main.entity.DimensionInfoEntity;
import com.cheersmind.smartbrain.main.entity.FactorInfoChildEntity;
import com.cheersmind.smartbrain.main.entity.FactorInfoEntity;
import com.cheersmind.smartbrain.main.entity.QuestionInfoChildEntity;
import com.cheersmind.smartbrain.main.entity.QuestionInfoEntity;
import com.cheersmind.smartbrain.main.entity.QuestionRootEntity;
import com.cheersmind.smartbrain.main.entity.ReportItemEntity;
import com.cheersmind.smartbrain.main.entity.ReportResultEntity;
import com.cheersmind.smartbrain.main.entity.ReportRootEntity;
import com.cheersmind.smartbrain.main.event.ContinueFactorEvent;
import com.cheersmind.smartbrain.main.fragment.questype.QsAccordJudgeFragment;
import com.cheersmind.smartbrain.main.fragment.questype.QsBalanceSelectFragment;
import com.cheersmind.smartbrain.main.fragment.questype.QsDefaultQuestionFragment;
import com.cheersmind.smartbrain.main.fragment.questype.QsFrequencySelectFragment;
import com.cheersmind.smartbrain.main.fragment.questype.QsTickedCorrectFragment;
import com.cheersmind.smartbrain.main.helper.ChartViewHelper;
import com.cheersmind.smartbrain.main.service.BaseService;
import com.cheersmind.smartbrain.main.service.DataRequestService;
import com.cheersmind.smartbrain.main.util.InjectionWrapperUtil;
import com.cheersmind.smartbrain.main.util.JsonUtil;
import com.cheersmind.smartbrain.main.util.RepetitionClickUtil;
import com.cheersmind.smartbrain.main.util.SoundPlayUtils;
import com.cheersmind.smartbrain.main.util.ToastUtil;
import com.cheersmind.smartbrain.main.view.LoadingView;
import com.cheersmind.smartbrain.main.view.qsdialog.DimensionReportDialog;
import com.cheersmind.smartbrain.main.view.qsdialog.FactorReportDialog;
import com.cheersmind.smartbrain.main.view.qsdialog.QuestionCompleteDialog;
import com.cheersmind.smartbrain.main.view.qsdialog.QuestionQuitDialog;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 答题
 * Created by gwb on 2018/6/4.
 */

public class QsEvaluateQuestionActivity extends BaseActivity implements View.OnClickListener{

    public static final String INTENT_BASE_FACTOR_LIST = "base_factor_list";
    public static final String INTENT_BASE_FACTOR = "base_factor";
    public static final String INTENT_BASE_DIMENSION = "base_dimension";
//    public static final String INTENT_LAST_FACTOR= "last_factor";

    private ImageView ivStop;
    private TextView tvTime;
    private TextView tvFlower;
    private ViewPager vpQuestion;
    private TextView tvPrevious;
    private TextView tvNext;

    private TextView tvQuestionCount;
    private TextView tvQuestionCur;
    private TextView tvQuestionTitle;

//    private QuestionCompleteDialog questionCompleteDialog;
//    private QuestionQuitDialog questionQuitDialog;

    List<QuestionInfoEntity> questionList = new ArrayList<>();
    private int timeCount = 120;
    List<Fragment> fragments = new ArrayList<>();
    FragmentPagerAdapter adapter;
    private int curPageIndex = 0;
    private int curFlowers;//当前鲜花数
    Timer timer;
    private int hasAnswer;
    boolean needRefrash = true;

    //GO相关view
    private View layoutCountDown;
    private TextView tvTimeGo;
    private RelativeLayout rtPanelRoot;
    private RelativeLayout rtPanelShow;
    private TextView tvPanelOk;
    private TextView tvPanelContent;
    private Timer timerGo;
    private int timeCout = 1;
    private boolean isFirst = true;

    List<FactorInfoEntity> factorBaseList = new ArrayList<>();
    private FactorInfoEntity factorInfoEntity;//当前答题因子
    private DimensionInfoEntity dimensionInfoEntity;
    private boolean isLastFactor;

    public static void startEvaluateQuestionActivity(Context context,
                                                     FactorInfoEntity factorInfoEntity,
                                                     DimensionInfoEntity dimensionInfoEntity,
                                                     List<FactorInfoEntity> factorBaseList){
        Intent intent = new Intent(context,QsEvaluateQuestionActivity.class);
//        intent.putExtra(INTENT_LAST_FACTOR,isLastFactor);
        Bundle bundle = new Bundle();
        bundle.putSerializable(INTENT_BASE_FACTOR_LIST, (Serializable) factorBaseList);
        bundle.putSerializable(INTENT_BASE_FACTOR,factorInfoEntity);
        bundle.putSerializable(INTENT_BASE_DIMENSION,dimensionInfoEntity);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qs_activity_evaluate_question);
        getSupportActionBar().hide();
        initView();
        initData();
//        initDialog();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isFirst){
            isFirst = false;
            return;
        }
        if(timer!=null && timeCount>0){
            startTimeTask();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(timer!=null){
            timer.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(timer!=null){
            timer.cancel();
        }
//        commitChildFactor(getConstTimeCount(),true);
    }

    private void initView(){
        ivStop = (ImageView) findViewById(R.id.iv_stop);
        tvTime = (TextView) findViewById(R.id.tv_time);
        tvFlower = (TextView) findViewById(R.id.tv_flower);
        ivStop.setOnClickListener(this);
        tvFlower.setOnClickListener(this);

        vpQuestion = (ViewPager)findViewById(R.id.vp_question);
        tvPrevious = (TextView)findViewById(R.id.tv_previous);
        tvPrevious.setOnClickListener(this);
        tvNext = (TextView)findViewById(R.id.tv_next);
        tvNext.setOnClickListener(this);

        tvQuestionCount = (TextView)findViewById(R.id.tv_question_count);
        tvQuestionCur = (TextView)findViewById(R.id.tv_question_cur);
        tvQuestionTitle = (TextView)findViewById(R.id.tv_question_title);

        initViewGo();
    }

    private void initData(){
//
//        if(getIntent()!=null){
//            isLastFactor = getIntent().getBooleanExtra(INTENT_LAST_FACTOR,false);
//        }

        if(getIntent()!=null && getIntent().getExtras()!=null){
            factorInfoEntity = (FactorInfoEntity) getIntent().getExtras().getSerializable(INTENT_BASE_FACTOR);
            dimensionInfoEntity = (DimensionInfoEntity) getIntent().getExtras().get(INTENT_BASE_DIMENSION);
            factorBaseList = (List<FactorInfoEntity>) getIntent().getExtras().getSerializable(INTENT_BASE_FACTOR_LIST);
        }

        if(factorInfoEntity == null || dimensionInfoEntity == null || factorBaseList == null){
            //显示异常界面
        }

        if(factorInfoEntity.getStage()==factorBaseList.size()){
            isLastFactor = true;
        }else{
            isLastFactor = false;
        }
        FactorInfoChildEntity childFactor = factorInfoEntity.getChildFactor();
        if(childFactor!=null){
            curFlowers = childFactor.getFlowers();
        }

    }

    private void initViewGo(){
        layoutCountDown = (View)findViewById(R.id.layout_count_down);
        tvTimeGo = (TextView)layoutCountDown.findViewById(R.id.tv_time_go);
        startGo();
    };

    private void startGo(){
        layoutCountDown.setVisibility(View.VISIBLE);
        tvTimeGo.setVisibility(View.GONE);
        timeCout = 1;

        timerGo = new Timer();
        timerGo.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(tvTimeGo.getVisibility() == View.GONE){
                            tvTimeGo.setVisibility(View.VISIBLE);
                        }
                        if(timeCout==1){
                            tvTimeGo.setText("GO");
                            SoundPlayUtils.play(2);
                            showTimeAnim();
                            timeCout --;
                        }else{
                            timerGo.cancel();
                        }
                    }
                });
            }
        },1000,1000);
    }

    private void showTimeAnim(){
        Animation showAnim = AnimationUtils.loadAnimation(
                QsEvaluateQuestionActivity.this, R.anim.anim_bottom_in);
        showAnim.setDuration(200);
        tvTimeGo.startAnimation(showAnim);
        showAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        layoutCountDown.setVisibility(View.GONE);
                        loadChildQuestions();
                    }
                },1000);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if(!RepetitionClickUtil.isFastClick()){
            Log.e("重复点击————","重复点击");
            return;
        }
        switch (v.getId()){
            case R.id.iv_stop:
                showQuestionQuitDialog(QuestionQuitDialog.QUIT_TYPE_STOP);
                break;
            case R.id.tv_previous:
                if(curPageIndex <= 0){
//                    Toast.makeText(this,"当前是第一题",Toast.LENGTH_SHORT).show();
                    return;
                }
                curPageIndex--;
                vpQuestion.setCurrentItem(curPageIndex,true);
                updateLastNextText();
                break;
            case R.id.tv_next:

                if(!questionList.get(curPageIndex).isHasAnswer()){
//                    Toast.makeText(this,"当前题目未回答",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(curPageIndex >= questionList.size()-1){
                    if(questionList.get(curPageIndex).isHasAnswer()){
                        showQuestionCompleteDialog();
                    }
                    return;
                }

                curPageIndex++;
                vpQuestion.setCurrentItem(curPageIndex,true);
                updateLastNextText();
                break;
            case R.id.rt_time_panel:
                break;
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if(hasAnswer==questionList.size()){
                showQuestionQuitDialog(QuestionQuitDialog.QUIT_TYPE_STOP);
                return false;
            }

        }
        return super.onKeyDown(keyCode, event);

    }

    private void initQuestionFragment() {
        //设置viewpage缓存界面数
        vpQuestion.setOffscreenPageLimit(questionList.size());
        for (int i = 0; i < questionList.size(); i++) {
            QuestionInfoEntity entity = questionList.get(i);

            Bundle bundle = new Bundle();
            bundle.putSerializable("question_content", entity);
            bundle.putString("child_factor_id", factorInfoEntity.getChildFactor().getChildFactorId());

            if (entity.getShowType() == 11) {
                QsAccordJudgeFragment fg = new QsAccordJudgeFragment();
                fg.setArguments(bundle);
                fragments.add(fg);
            } else if (entity.getShowType() == 12 || entity.getShowType() == 14 || entity.getShowType() == 16) {
                QsTickedCorrectFragment fg = new QsTickedCorrectFragment();
                fg.setArguments(bundle);
                fragments.add(fg);
            } else if (entity.getShowType() == 15 || entity.getShowType() == 17 || entity.getShowType() == 18) {
                QsBalanceSelectFragment fg = new QsBalanceSelectFragment();
                fg.setArguments(bundle);
                fragments.add(fg);
            } else if (entity.getShowType() == 13) {
                QsFrequencySelectFragment fg = new QsFrequencySelectFragment();
                fg.setArguments(bundle);
                fragments.add(fg);
            } else {
                QsDefaultQuestionFragment fg = new QsDefaultQuestionFragment();
                fg.setArguments(bundle);
                fragments.add(fg);
            }
        }

        adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        };

        vpQuestion.setAdapter(adapter);
        vpQuestion.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
//                curPageIndex = position;
//                vpQuestion.setCurrentItem(curPageIndex);
                if(position>hasAnswer){
                    needRefrash = false;
                    vpQuestion.setCurrentItem(curPageIndex);
                    return;
                }else{
                    needRefrash = true;
                }

                if(needRefrash){
                    curPageIndex = position;
//                    if(curPageIndex>0){
//                        int index = curPageIndex -1;
//                        QuestionInfoEntity entity = questionList.get(index);
//                        if(entity.getType() == 2){
//                            EvaluateQuestionFragment fragment = (EvaluateQuestionFragment) fragments.get(index);
//                            fragment.commitQestion(entity.getOptions().get(0));
//                        }
//                    }
                }
                updateLastNextText();

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //        curPageIndex = 0;
        vpQuestion.setCurrentItem(curPageIndex);
        updateLastNextText();

        startTimeTask();
    }

    private void startTimeTask(){
        if(questionList!=null && questionList.size() == 0){
            return;
        }
        if(timeCount <= 0){
            return;
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        timeCount--;
                        if(timeCount>=0){
                            tvTime.setText(String.valueOf(timeCount));
                        }else{
                            //时间到
                            timer.cancel();
                            showQuestionQuitDialog(QuestionQuitDialog.QUIT_TYPE_TIMEOUT);
                            //时间到自动提交因子
//                            commitChildFactor(getConstTimeCount(),false);
                        }
                    }
                });

            }
        },1000,1000);
    }

    public void updateProgress(){
        questionList.get(curPageIndex).setHasAnswer(true);
        updateLastNextText();
        if(hasAnswer < curPageIndex+1){
            hasAnswer = curPageIndex+1;
        }
//        pbQuestion.setProgress(hasAnswer);
        if(hasAnswer == questionList.size()){
//            tvPbEnd.setBackgroundResource(R.mipmap.qs_dian_select);
        }
    }

    private void initHasAnswer(List<QuestionInfoEntity> questionfilterList){
        tvQuestionCount.setText(String.valueOf(questionList.size()));
        tvQuestionCur.setText(String.valueOf(curPageIndex+1));
        hasAnswer = questionfilterList.size();
    }

    private void updateLastNextText(){
        if(curPageIndex==questionList.size()-1){
            tvNext.setText("提交");
        }else{
            tvNext.setText("确定");
        }
        if(questionList.get(curPageIndex).isHasAnswer()){
            tvNext.setBackgroundColor(getResources().getColor(R.color.color_12b2f4));
        }else{
            tvNext.setBackgroundColor(getResources().getColor(R.color.color_80d0f1));
        }
        if(curPageIndex==0){
            tvPrevious.setBackgroundColor(getResources().getColor(R.color.color_80d0f1));
        }else{
            tvPrevious.setBackgroundColor(getResources().getColor(R.color.color_12b2f4));
        }

        tvQuestionCur.setText(String.valueOf(curPageIndex+1));
        tvQuestionTitle.setText(questionList.get(curPageIndex).getStem());
    }

    public List<Fragment> getFragments() {
        return fragments;
    }

    public ViewPager getVpQuestion() {
        return vpQuestion;
    }

    public int getCurPageIndex() {
        return curPageIndex;
    }

    public void setCurPageIndex(int curPageIndex) {
        this.curPageIndex = curPageIndex;
    }

    public int getConstTimeCount() {
        int constTime = factorInfoEntity.getCostTime() - timeCount;
        if(constTime<=0){
            constTime = 0;
        }
        return constTime;
    }

    public int getCurFlowers() {
        return curFlowers;
    }

    public void setCurFlowers(int curFlowers) {
        this.curFlowers = curFlowers;
        if(tvFlower!=null){
            tvFlower.setText(String.valueOf(curFlowers));
        }
    }

    public int getTimeCount() {
        return timeCount;
    }

    public void setTimeCount(int timeCount) {
        this.timeCount = timeCount;
    }

    public void showQuestionCompleteDialog(){
        if(timer!=null){
            timer.cancel();
        }
        final QuestionCompleteDialog questionCompleteDialog = new QuestionCompleteDialog(QsEvaluateQuestionActivity.this,
                factorInfoEntity, new QuestionCompleteDialog.FactorCompleteDialogCallback() {
            @Override
            public void onBackModify() {
                //返回修改
                startTimeTask();
            }

            @Override
            public void onSureCommit() {
                //确定提交
                commitChildFactor(getConstTimeCount(),true);
            }
        });
        questionCompleteDialog.show();

    }

    public void showQuestionQuitDialog(int type){
        if(timer!=null){
            timer.cancel();
        }
        QuestionQuitDialog questionQuitDialog = new QuestionQuitDialog(QsEvaluateQuestionActivity.this,
                type, new QuestionQuitDialog.QuestionQuitDialogCallback() {
            @Override
            public void onQuesExit() {
                //退出
                finish();
            }

            @Override
            public void onQuesContinue() {
                //继续答题
                startTimeTask();
            }
        });
        questionQuitDialog.show();
    }

    private void loadChildQuestions(){
        LoadingView.getInstance().show(this);
        FactorInfoChildEntity childFactor = factorInfoEntity.getChildFactor();
        DataRequestService.getInstance().getChildQuestions(childFactor.getChildId(),childFactor.getFactorId(),childFactor.getChildFactorId(), 0, 1000, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                e.printStackTrace();
                LoadingView.getInstance().dismiss();
            }

            @Override
            public void onResponse(Object obj) {
                LoadingView.getInstance().dismiss();
                if(obj!=null){
                    Map dataMap = JsonUtil.fromJson(obj.toString(),Map.class);
                    QuestionRootEntity rootData = InjectionWrapperUtil.injectMap(dataMap,QuestionRootEntity.class);
                    if(rootData!=null && rootData.getItems()!=null){
                        questionList = rootData.getItems();
                        //过滤已经做过的题目
                        List<QuestionInfoEntity> questionfilterList = new ArrayList<>();
                        for(int i=0;i<questionList.size();i++){
                            QuestionInfoChildEntity childEntity = questionList.get(i).getChildQuestion();
                            if(childEntity!=null){
                                questionList.get(i).setHasAnswer(true);
                                questionfilterList.add(questionList.get(i));
                            }
                        }
//                        questionList.removeAll(questionfilterList);
                        if(questionfilterList.size()>0){
                            if(questionfilterList.size() == questionList.size()){
                                //全部题目都回答过，但是因子没有提交
                                curPageIndex = questionfilterList.size()-1;
                            }else{
                                curPageIndex = questionfilterList.size();
                            }
                        }

                        if(questionList.size()==0){
                            showQuestionCompleteDialog();
                        }else{
                            if(factorInfoEntity.getQuestionCount() != 0){
                                timeCount = (factorInfoEntity.getCostTime()/factorInfoEntity.getQuestionCount())*questionList.size();
                            }else{
                                timeCount = 120;
                            }
                            initHasAnswer(questionfilterList);
                            initQuestionFragment();

                        }

                    }
                }
            }
        });

    }

    /**
     * 提交因子
     * @param costedTime
     * @param isShowHint
     */
    public void commitChildFactor(int costedTime,final boolean isShowHint){
        LoadingView.getInstance().show(QsEvaluateQuestionActivity.this);
        DataRequestService.getInstance().commitChildFactor(ChildInfoDao.getDefaultChildId(),
                factorInfoEntity.getChildFactor().getChildFactorId(), costedTime, "abc", new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                LoadingView.getInstance().dismiss();
                String factorName = factorInfoEntity.getChildFactor().getFactorName();
                if(TextUtils.isEmpty(factorName)){
                    factorName = "";
                }
                if(isShowHint){
                    String resultString = getResources().getString(R.string.factor_commit_fail,String.valueOf(factorInfoEntity.getStage()),factorName);
                    Toast.makeText(QsEvaluateQuestionActivity.this,resultString,Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onResponse(Object obj) {
                LoadingView.getInstance().dismiss();
                if(obj!=null){
                    Map dataMap = JsonUtil.fromJson(obj.toString(),Map.class);
                    FactorInfoChildEntity childEntity = InjectionWrapperUtil.injectMap(dataMap,FactorInfoChildEntity.class);
                    factorInfoEntity.setChildFactor(childEntity);
                    //展示因子报表
                    FactorReportDialog factorReportDialog = new FactorReportDialog(QsEvaluateQuestionActivity.this,
                            factorInfoEntity,isLastFactor,factorBaseList.size(),new FactorReportDialog.FactorReportDialogCallback() {
                        @Override
                        public void onClose() {
                            if(isLastFactor){
                                //最后一个因子点击关闭，关闭量表详情页面
                                EventBus.getDefault().post(new ContinueFactorEvent(null));
                            }
                            finish();
                        }

                        @Override
                        public void onNextFactor() {
                            if(isLastFactor){

                                DataRequestService.getInstance().getTopicReportByRelation(ChildInfoDao.getDefaultChildId(),
                                        dimensionInfoEntity.getChildDimension().getExamId(),
                                        dimensionInfoEntity.getTopicDimensionId(),
                                        ChartViewHelper.REPORT_RELATION_TOPIC_DIMENSION,
                                        "0", new BaseService.ServiceCallback() {
                                            @Override
                                            public void onFailure(QSCustomException e) {
                                                ToastUtil.showShort(QsEvaluateQuestionActivity.this, "获取量表报告失败");
                                            }

                                            @Override
                                            public void onResponse(Object obj) {
                                                if (obj != null) {

                                                    Map dataMap = JsonUtil.fromJson(obj.toString(),Map.class);
                                                    ReportRootEntity data = InjectionWrapperUtil.injectMap(dataMap,ReportRootEntity.class);
                                                    if(data!=null && data.getChartDatas()!=null && data.getChartDatas().size()>0){
                                                        List<ReportResultEntity> reportResultEntities = data.getReportResults();
                                                        List<ReportItemEntity>  dimensionReports = data.getChartDatas();
                                                        if(dimensionReports!=null && dimensionReports.size()>0) {
                                                            for (int i = 0; i < dimensionReports.size(); i++) {
                                                                if (reportResultEntities != null && reportResultEntities.size() > 0) {
                                                                    if (dimensionReports.get(i).getReportResult() == null) {
                                                                        dimensionReports.get(i).setReportResult(reportResultEntities.get(0));
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        DimensionReportDialog dimensionReportDialog = new DimensionReportDialog(QsEvaluateQuestionActivity.this,dimensionReports, dimensionInfoEntity, new DimensionReportDialog.DimensionReportCallback() {
                                                            @Override
                                                            public void onClose() {
                                                                //最后一个因子点击关闭，关闭量表详情页面
                                                                EventBus.getDefault().post(new ContinueFactorEvent(null));
                                                                finish();
                                                            }
                                                        });
                                                        dimensionReportDialog.show();
                                                    }

                                                }
                                            }
                                        });


                            }else{
                                sendNextFactorData();
                                finish();
                            }
                        }
                    });
                    factorReportDialog.show();

                }
            }
        });
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

    //获取下一阶段因子
    private void sendNextFactorData(){
        int stage = factorInfoEntity.getStage();
        if(stage<factorBaseList.size()){
            factorInfoEntity = factorBaseList.get(stage);
//            startCurrentFactor(factorInfoEntity);
            EventBus.getDefault().post(new ContinueFactorEvent(factorInfoEntity));
        }

    }

}

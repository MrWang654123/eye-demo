package com.cheersmind.cheersgenie.main.view.qsdialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.dao.ChildInfoDao;
import com.cheersmind.cheersgenie.main.entity.FactorInfoChildEntity;
import com.cheersmind.cheersgenie.main.entity.FactorInfoEntity;
import com.cheersmind.cheersgenie.main.entity.FactorRankReportEntity;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.RepetitionClickUtil;
import com.cheersmind.cheersgenie.main.util.ToastUtil;
import com.cheersmind.cheersgenie.main.view.LoadingView;
import com.cheersmind.cheersgenie.main.view.NormalityView;

import java.text.DecimalFormat;
import java.util.Map;

/**
 * Created by Administrator on 2018/6/6.
 */

public class FactorReportDialog extends Dialog implements View.OnClickListener{

    private Context context;
    private FactorInfoEntity entity;
    private boolean isLastFactor;
    private int factorCount;
    private FactorReportDialogCallback callback;

    private TextView tvTitle;
    private TextView tvResult;
    private TextView tvCompleteTime;
    private TextView tvGetFlowers;
    private TextView tvCompleteFactor;
    private LinearLayout llStage;
    private ImageView ivClose;
    private Button btnNext;
    private NormalityView factorChar;
    private TextView tvFlowers;

    public FactorReportDialog(@NonNull Context context, FactorInfoEntity entity,boolean isLastFactor,int factorCount, FactorReportDialogCallback callback) {
        super(context);
        this.context = context;
        this.entity = entity;
        this.isLastFactor = isLastFactor;
        this.factorCount = factorCount;
        this.callback = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.qs_dialog_factor_report);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        initView();

    }

    private void initView(){
        tvTitle = (TextView)findViewById(R.id.tv_title);
        tvResult = (TextView)findViewById(R.id.tv_result);
        tvCompleteTime = (TextView)findViewById(R.id.tv_complete_time);
        tvGetFlowers = (TextView)findViewById(R.id.tv_get_flowers);
        tvCompleteFactor = (TextView)findViewById(R.id.tv_complete_factor);
        llStage = (LinearLayout)findViewById(R.id.ll_stage);
        ivClose = (ImageView)findViewById(R.id.iv_close);
        ivClose.setOnClickListener(this);
        btnNext = (Button)findViewById(R.id.btn_next);
        btnNext.setOnClickListener(this);

        tvFlowers = (TextView)findViewById(R.id.tv_flowers);
        factorChar = (NormalityView)findViewById(R.id.factor_char);

        updateData();
    }

    public void onDismiss(){
        this.dismiss();
    }

    @Override
    public void onClick(View v) {
        if(!RepetitionClickUtil.isFastClick()){
            return;
        }
        if(v == ivClose){
            if(callback!=null){
                callback.onClose();
            }
            onDismiss();
        }else if(v == btnNext){
            if(callback!=null){
                callback.onNextFactor();
            }
            onDismiss();
        }
    }

    private void updateData(){
        if(entity == null){
            return;
        }
        tvTitle.setText(entity.getFactorName());

        if(isLastFactor){
            btnNext.setText(context.getResources().getString(R.string.qs_factor_report_btn_report));
        }else{
            btnNext.setText(context.getResources().getString(R.string.qs_factor_report_btn_next));
        }

        if(entity.getChildFactor() == null){
            return;
        }
        getChildRankReport(entity.getChildFactor().getChildFactorId());
        updateStageLayout(llStage);
        updateFactorCompleteDetail();

    }

    private void updateStageLayout(LinearLayout llStage){
        llStage.removeAllViews();
        int complete = entity.getStage();
        for(int i=0;i<factorCount;i++){
            View itemView = View.inflate(context,R.layout.qs_factor_stage_item,null);
            TextView tv = (TextView) itemView.findViewById(R.id.tv_bg);
            tv.setText(String.valueOf(i+1));
            if(i < complete){
                tv.setBackgroundResource(R.mipmap.qs_number_bg_select);
            }else{
                tv.setBackgroundResource(R.mipmap.qs_number_bg_nor);
            }
            llStage.addView(itemView);
        }
    }

    private void updateFactorCompleteDetail(){
        FactorInfoChildEntity childEntity = entity.getChildFactor();

        String per = "0";
        if(childEntity!=null){
            if(entity.getQuestionCount()>0){
                if(entity.getQuestionCount() != 0){
                    float avageTime = childEntity.getCostTime() / entity.getQuestionCount();
                    DecimalFormat decimalFormat=new DecimalFormat(".00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
                    per = decimalFormat.format(avageTime);
                    if(per.equals(".00")){
                        per = "0.00";
                    }
                }

            }
        }
        if(per.equals("0.00")){
            tvCompleteTime.setText("--");
        }else{
            tvCompleteTime.setText(per+"s");
        }

        String flower = context.getResources().getString(R.string.factor_datails_common,
                String.valueOf((entity.getChildFactor()==null?0:entity.getChildFactor().getFlowers())),
                String.valueOf(entity.getFlowers()*entity.getQuestionCount()));
        tvGetFlowers.setText(Html.fromHtml(flower));
        tvFlowers.setText("+" + String.valueOf((entity.getChildFactor()==null?0:entity.getChildFactor().getFlowers())));

        String complete ="";
        if(childEntity == null){
            complete = context.getResources().getString(R.string.factor_datails_common,
                    "0",String.valueOf(entity.getQuestionCount()));
        }else{
            complete = context.getResources().getString(R.string.factor_datails_common,
                    String.valueOf(childEntity.getCompleteCount()),String.valueOf(entity.getQuestionCount()));
        }

        tvCompleteFactor.setText(Html.fromHtml(complete));
    }

    private void getChildRankReport(String childFactorId){
        LoadingView.getInstance().show(context);
        DataRequestService.getInstance().getChildFactorRankReport(ChildInfoDao.getDefaultChildId(), childFactorId, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                LoadingView.getInstance().dismiss();
                ToastUtil.showShort(context,"获取因子排名失败");
            }

            @Override
            public void onResponse(Object obj) {
                LoadingView.getInstance().dismiss();
                if(obj!=null){
                    Map dataMap = JsonUtil.fromJson(obj.toString(),Map.class);
                    FactorRankReportEntity entity = InjectionWrapperUtil.injectMap(dataMap,FactorRankReportEntity.class);
                    if(entity!=null){

                        int rank = 0;
                        if(entity.getTotal()!=0){
                            rank = entity.getRank()*100/entity.getTotal();
                        }
                        //当只有一个人答题的时候，值取50%
                        if(rank == 0){
                            rank = 50;
                        }
                        factorChar.setProgress(rank);
                        factorChar.invalidate();
                        tvResult.setText(Html.fromHtml(context.getResources().getString(R.string.qs_factor_report_btn_result,String.valueOf(rank) + "%")));
                    }
                }
            }
        });
    }

    public interface FactorReportDialogCallback{
        void onClose();
        void onNextFactor();
    }
}

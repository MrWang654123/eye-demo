package com.cheersmind.cheersgenie.main.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.dao.ChildInfoDao;
import com.cheersmind.cheersgenie.main.entity.ChildInfoEntity;
import com.cheersmind.cheersgenie.main.entity.UserDetailsEntity;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.imagetool.ImageCacheTool;
import com.cheersmind.cheersgenie.module.mine.CommentAppActivity;
import com.cheersmind.cheersgenie.module.mine.MineInfoActivity;
import com.cheersmind.cheersgenie.module.mine.MyFlowerActivity;
import com.cheersmind.cheersgenie.module.mine.SettingActivity;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class QsMineFragment extends Fragment implements View.OnClickListener{

    private View contentView;

    private ImageView ivHead;
    private TextView tvFlower;
    private TextView tvChildName;
    private TextView tvChildSchool;
    private LinearLayout llTest;

    private LinearLayout llMedal;
    private LinearLayout llAppraise;
    private LinearLayout llFeedback;
    private LinearLayout llSetting;
    private TextView tvFlowerCount;

    ChildInfoEntity defaultChild;

    public QsMineFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(contentView==null){
            contentView = View.inflate(getActivity(), R.layout.qs_fragment_mine,null);
        }
        initView();
        initData();

        return contentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getUserDetails();
    }

    private void initView(){
        ivHead = (ImageView)contentView.findViewById(R.id.iv_head);
        tvChildName = (TextView)contentView.findViewById(R.id.tv_child_name);
        tvChildSchool = (TextView)contentView.findViewById(R.id.tv_child_school);
        tvFlower = (TextView)contentView.findViewById(R.id.tv_flower);

        llTest = (LinearLayout) contentView.findViewById(R.id.ll_test);
        llMedal = (LinearLayout)contentView.findViewById(R.id.ll_medal);
        llAppraise = (LinearLayout)contentView.findViewById(R.id.ll_appraise);
        llFeedback = (LinearLayout)contentView.findViewById(R.id.ll_feedback);
        llSetting = (LinearLayout)contentView.findViewById(R.id.ll_setting);
        tvFlowerCount = (TextView)contentView.findViewById(R.id.tv_flower_count);
        ivHead.setOnClickListener(this);
        llTest.setOnClickListener(this);
        llMedal.setOnClickListener(this);
        llAppraise.setOnClickListener(this);
        llFeedback.setOnClickListener(this);
        llSetting.setOnClickListener(this);

        FeedbackAPI.setBackIcon(R.mipmap.qs_blue_back);
    }

    private void initData(){
        defaultChild = ChildInfoDao.getDefaultChild();
        if(defaultChild!=null){
            if(TextUtils.isEmpty(defaultChild.getChildName())){
                tvChildName.setText("未知");
            }else{
                tvChildName.setText(defaultChild.getChildName());
            }

            StringBuffer buffer = new StringBuffer();
            if(!TextUtils.isEmpty(defaultChild.getSchoolName())){
                buffer.append(defaultChild.getSchoolName());
            }
            if(!TextUtils.isEmpty(defaultChild.getGrade())){
                buffer.append(" ");
                buffer.append(defaultChild.getGrade());
            }
            if(!TextUtils.isEmpty(buffer.toString())){
                tvChildSchool.setText(buffer.toString());
            }

            if(TextUtils.isEmpty(defaultChild.getAvatar())){
                if(defaultChild.getSex()==2){
                    ivHead.setImageResource(R.mipmap.head_girl);
                }else{
                    ivHead.setImageResource(R.mipmap.head_boy);
                }

            }else{
                ImageCacheTool imageCacheTool = ImageCacheTool.getInstance();
                try {
                    imageCacheTool.asyncLoadImage(new URL(defaultChild.getAvatar()),ivHead);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        if(v == ivHead){
            startActivity(new Intent(getActivity(),MineInfoActivity.class));
        }else if(v == llTest){
            startActivity(new Intent(getActivity(),MineInfoActivity.class));
        }else if(v == llMedal){
            startActivity(new Intent(getActivity(),MyFlowerActivity.class));
        }else if(v == llAppraise){
            startActivity(new Intent(getActivity(),CommentAppActivity.class));
        }else if(v == llFeedback){
            FeedbackAPI.openFeedbackActivity();
        }else if(v == llSetting){
            startActivity(new Intent(getActivity(),SettingActivity.class));
        }
    }

    private void getUserDetails(){
        DataRequestService.getInstance().getUserDetails(new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {

            }

            @Override
            public void onResponse(Object obj) {
                if(obj!=null){
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    UserDetailsEntity userDetails = InjectionWrapperUtil.injectMap(dataMap, UserDetailsEntity.class);
                    if(userDetails!=null){
                        tvFlowerCount.setText(String.valueOf(userDetails.getFlowers()) + "朵");
                        defaultChild.setFlowerCount(userDetails.getFlowers());
                    }
                }
            }
        });
    }
}

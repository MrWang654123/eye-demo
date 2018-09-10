package com.cheersmind.cheersgenie.main.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.adapter.ReportRootAdapter;
import com.cheersmind.cheersgenie.main.dao.ChildInfoDao;
import com.cheersmind.cheersgenie.main.entity.TopicInfoEntity;
import com.cheersmind.cheersgenie.main.entity.TopicRootEntity;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.LogUtils;
import com.cheersmind.cheersgenie.main.util.OnMultiClickListener;
import com.cheersmind.cheersgenie.main.view.EmptyLayout;
import com.cheersmind.cheersgenie.main.view.LoadingView;
import com.cheersmind.cheersgenie.main.view.ViewPagerIndicate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/6/2.
 */

public class QsReportFragment extends Fragment {

    private View contentView;
    private EmptyLayout emptyLayout;
    private LinearLayout llRoot;

    private ViewPager mViewPager;
    private ViewPagerIndicate mIndicate;

    private int[] mTextColors = {0xFF828282, 0xffffffff};
    private int mUnderlineColor = 0xFF168EFF;
    private List<Fragment> topicReportFragments = new ArrayList<>();

    List<TopicInfoEntity> childTopicList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(contentView == null){
            contentView = inflater.inflate(R.layout.qs_fragment_report, container,
                    false);
        }

        initView();
        initData();
        return contentView;
    }

    private void initView(){
        emptyLayout = (EmptyLayout) contentView.findViewById(R.id.emptyLayout);
        emptyLayout.setOnLayoutClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View view) {
                loadChildTopicList(true);
            }
        });
        llRoot = (LinearLayout)contentView.findViewById(R.id.ll_root);

    }

    private void initData(){
        loadChildTopicList(false);
    }

    private void initViewPager() {
        llRoot.removeAllViews();
        View pageView = View.inflate(getActivity(),R.layout.qs_fragment_report_viewpage,null);
        llRoot.addView(pageView);
        mViewPager = (ViewPager) pageView.findViewById(R.id.report_pager);
        topicReportFragments.clear();
        mViewPager.removeAllViewsInLayout();
        for (int i = 0; i < childTopicList.size(); i++) {
//            TopicReportFragment topicReportFragment = new TopicReportFragment();
//            Bundle bundle = new Bundle();
//            bundle.putSerializable(TopicReportFragment.TOPIC_REPORT,childTopicList.get(i));
//            topicReportFragment.setArguments(bundle);
//            topicReportFragments.add(topicReportFragment);
            TopicDimensionReportFragmet topicDimensionReportFragmet = new TopicDimensionReportFragmet();
            Bundle bundle = new Bundle();
            bundle.putSerializable(TopicDimensionReportFragmet.TOPIC_DIM_REPORT, childTopicList.get(i));
            topicDimensionReportFragmet.setArguments(bundle);
            topicReportFragments.add(topicDimensionReportFragmet);
        }

        mViewPager.setOffscreenPageLimit(childTopicList.size()-1);
        mViewPager.setAdapter(new ReportRootAdapter(getActivity(),getChildFragmentManager(), (ArrayList<Fragment>) topicReportFragments));

        initViewPagerIndicate(pageView);
    }

    private void initViewPagerIndicate(View view) {
        mIndicate = (ViewPagerIndicate) view.findViewById(R.id.view_indicate);
        //设置标签样式、文本和颜色
        mIndicate.resetText(R.layout.qs_viewpage_indicate_view, getTitleData(), mTextColors);
        //设置下划线粗细和颜色
        mIndicate.resetUnderline(4, mUnderlineColor);
        //设置ViewPager
        mIndicate.resetViewPager(mViewPager);
        //设置初始化完成
        mIndicate.setOk();
    }

    private String[] getTitleData(){
        if(childTopicList==null || childTopicList.size()==0){
            return new String[1];
        }
        String mTitles [] = new String[childTopicList.size()];
        for(int i=0;i<childTopicList.size();i++){
            mTitles[i] = childTopicList.get(i).getTopicName();
        }
        return  mTitles;
    }

    public void loadChildTopicList(final boolean isLoading){
        if(isLoading){
            LoadingView.getInstance().show(getActivity());
        }
        DataRequestService.getInstance().loadChildTopicList(ChildInfoDao.getDefaultChildId(), 0, 100, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                if(isLoading){
                    LoadingView.getInstance().dismiss();
                }
                emptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
            }

            @Override
            public void onResponse(Object obj) {
                if(isLoading){
                    LoadingView.getInstance().dismiss();
                }
                emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                if(obj!=null){
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    TopicRootEntity topicData = InjectionWrapperUtil.injectMap(dataMap, TopicRootEntity.class);
                    if(topicData != null && topicData.getItems()!=null) {
                        childTopicList = topicData.getItems();
                        if(childTopicList!=null && childTopicList.size()>0){
                            initViewPager();
                        }else{
                            emptyLayout.setErrorType(EmptyLayout.NODATA);
                        }
                    }else{
                        emptyLayout.setErrorType(EmptyLayout.NODATA);
                    }
                }else{
                    emptyLayout.setErrorType(EmptyLayout.NODATA);
                }
            }
        });
    }

    public void onRefreshRoport(){
        LogUtils.w("QsReportFragment","刷新报告数据");
        loadChildTopicList(true);
    }

}

package com.cheersmind.smartbrain.main.activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cheersmind.smartbrain.R;
import com.cheersmind.smartbrain.main.entity.DimensionInfoEntity;
import com.cheersmind.smartbrain.main.entity.ReportItemEntity;
import com.cheersmind.smartbrain.main.event.CommonEvent;
import com.cheersmind.smartbrain.main.fragment.QsEvaluateFragment;
import com.cheersmind.smartbrain.main.fragment.QsMineFragment;
import com.cheersmind.smartbrain.main.fragment.QsReportFragment;
import com.cheersmind.smartbrain.main.util.OnMultiClickListener;
import com.cheersmind.smartbrain.main.util.SoundPlayUtils;
import com.cheersmind.smartbrain.main.util.VersionUpdateUtil;
import com.cheersmind.smartbrain.main.view.CustomViewPager;
import com.cheersmind.smartbrain.main.view.qshorizon.ReportViewLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity{

    public static final String SLIDING_ITEM_SHARE_KEY = "sliding_item_shart_key";

    private CustomViewPager vpMainFragments;//首页包含的fragment页面ViewPager
    private FragmentPagerAdapter mAdapter;
    private TabbarAdapter mTabbarAdapter;
    private List<Fragment> mFragments = new ArrayList<Fragment>();//包含的fragment页面
    private List<String> mFragmentTitles = new ArrayList<String>();//包含的fragment页面标题
    private List<String> mFragmentTopTitles = new ArrayList<String>();//顶部标题
    private List<Integer> mFragmentIcon = new ArrayList<Integer>();
    private List<Integer> mFragmentIconSelected = new ArrayList<Integer>();
    private GridView gvTabbar;//底部tabbar
    private long lastTimeMillis;

    //报告底部弹窗
    private RelativeLayout rtReportPopRoot;
    private RelativeLayout rtReportPopContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qs_activity_main);

        getSupportActionBar().hide();
        vpMainFragments = (CustomViewPager) findViewById(R.id.vp_main_fragments);
        vpMainFragments.setOffscreenPageLimit(3);
        gvTabbar = (GridView) findViewById(R.id.gv_tabbar);

        rtReportPopContent = (RelativeLayout)findViewById(R.id.rt_report_pop_content);
        rtReportPopRoot = (RelativeLayout)findViewById(R.id.rt_report_pop_root);
        rtReportPopRoot.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View view) {
                hiddenReportPanel();
            }
        });

        initFraments();
        setupTabbar();
        setTitle("推荐");
        SoundPlayUtils.init(this);
        EventBus.getDefault().register(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (!VersionUpdateUtil.isCurrVersionUpdateDialogShow) {
            VersionUpdateUtil.checkUpdate(this,false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initFraments() {

        QsEvaluateFragment evaluateFragment = new QsEvaluateFragment();
        mFragments.add(evaluateFragment);

        QsReportFragment reportFragment = new QsReportFragment();
        mFragments.add(reportFragment);

        QsMineFragment mineFragment = new QsMineFragment();
        mFragments.add(mineFragment);

//        mFragmentTitles.add("推荐");
        mFragmentTitles.add("智评");
        mFragmentTitles.add("报告");
        mFragmentTitles.add("我的");

//        mFragmentTopTitles.add("推荐");
        mFragmentTopTitles.add("智评");
        mFragmentTopTitles.add("报告");
        mFragmentTopTitles.add("我的");

//        mFragmentIcon.add(R.mipmap.qs_bottombar_home_nor);
//        mFragmentIconSelected.add(R.mipmap.qs_bottombar_home_select);
        mFragmentIcon.add(R.mipmap.qs_bottombar_evaluate_nor);
        mFragmentIconSelected.add(R.mipmap.qs_bottombar_evaluate_select);
        mFragmentIcon.add(R.mipmap.qs_bottombar_report_nor);
        mFragmentIconSelected.add(R.mipmap.qs_bottombar_report_select);
        mFragmentIcon.add(R.mipmap.qs_bottombar_mine_nor);
        mFragmentIconSelected.add(R.mipmap.qs_bottombar_mine_select);

        mAdapter = new MainFragmentPagerAdapter(getSupportFragmentManager());
        vpMainFragments.addOnPageChangeListener(new MainFragmentOnPageChangeListener());
        vpMainFragments.setAdapter(mAdapter);

    }


    private void updateAllPageData(){
        for(int i=0;i<mFragments.size();i++){
            Fragment fragment = mFragments.get(i);
            if(fragment instanceof QsEvaluateFragment){
                QsEvaluateFragment qsEvaluateFragment = (QsEvaluateFragment)fragment;
                qsEvaluateFragment.loadChildTopicList();
            }else if(fragment instanceof QsReportFragment){
                QsReportFragment qsReportFragment = (QsReportFragment)fragment;
                qsReportFragment.loadChildTopicList(true);
            }else if(fragment instanceof QsMineFragment){

            }
        }
    }


    private void setupTabbar() {
        gvTabbar.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gvTabbar.setNumColumns(mFragments.size());
        gvTabbar.setOnItemSelectedListener(new TabbarGridViewOnItemSelectedListener());
        gvTabbar.setOnItemClickListener(new TabbarGridViewOnItemClickListener());
        mTabbarAdapter = new TabbarAdapter();
        gvTabbar.setAdapter(mTabbarAdapter);

        gvTabbar.requestFocusFromTouch();
        gvTabbar.setSelection(0);
    }


    private void setSelected(int position) {
        setTitle(mFragmentTopTitles.get(position));
        mTabbarAdapter.notifyDataSetChanged();
    }

    /**
     * Fragment切换监听
     */
    class MainFragmentOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            //System.out.println("onPageScrollStateChanged : onPageScrolled :position "+position+" | "+positionOffsetPixels);
            if (positionOffset == 0) {
                setSelected(position);
            }
        }

        @Override
        public void onPageSelected(int position) {
            //System.out.println("onPageScrollStateChanged : onPageSelected : "+position);
            setSelected(position);
            Fragment fragment = mFragments.get(position);
//            if (fragment instanceof QsEvaluateFragment) {
//                ((QsEvaluateFragment)fragment).setActive(true);
//            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            //System.out.println("onPageScrollStateChanged : state : "+state);
        }
    }
    /**
     * fragment适配器
     */
    class  MainFragmentPagerAdapter extends FragmentPagerAdapter {

        public MainFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }

    /**
     * tabbar item选中监听
     */
    private class TabbarGridViewOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            //System.out.println("TabbarGridViewOnItemSelectedListener onItemSelected : position : "+position);
            vpMainFragments.setCurrentItem(position,true);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    /**
     * tabbar item点击监听
     */
    private class TabbarGridViewOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
            gvTabbar.requestFocusFromTouch();
            gvTabbar.setSelection(position);
        }
    }

    /**
     * tabbar适配器
     */
    private class TabbarAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public Object getItem(int position) {
            //return this.datalist.get(position);
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(MainActivity.this,R.layout.tabbar_item, null);
                holder=new ViewHolder();
                holder.ivTabbarItem = (ImageView) convertView.findViewById(R.id.image);
                holder.tvTabbarItem = (TextView) convertView.findViewById(R.id.tv_tabbar_item);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }

            @DrawableRes int resId = vpMainFragments.getCurrentItem()==position?mFragmentIconSelected.get(position):mFragmentIcon.get(position);
            holder.ivTabbarItem.setImageResource(resId);

            holder.tvTabbarItem.setText(mFragmentTitles.get(position));
            @ColorInt int colorResId = vpMainFragments.getCurrentItem()==position?getResources().getColor(R.color.color_bar_blue):getResources().getColor(R.color.color_bar_gray);
            holder.tvTabbarItem.setTextColor(colorResId);
            return convertView;
        }
    }

    private final class ViewHolder{
        public ImageView ivTabbarItem;
        public TextView tvTabbarItem;
    }

    //按返回键时， 不退出程序而是返回上一浏览页面
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            long currTimeMillis = System.currentTimeMillis();
            if (currTimeMillis - lastTimeMillis <= 1000) {
                finish();
//                System.exit(0);//正常退出App
            } else {
                Toast.makeText(this,"再按一次退出",Toast.LENGTH_SHORT).show();
            }

            lastTimeMillis = currTimeMillis;
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }


    public void showReportPanel(List<ReportItemEntity> dimensionReports, DimensionInfoEntity entity){
        if(rtReportPopRoot.getVisibility() == View.GONE){
            ReportViewLayout reportViewLayout = new ReportViewLayout(MainActivity.this, dimensionReports, entity, new ReportViewLayout.DimensionReportCallback() {
                @Override
                public void onClose() {
                    hiddenReportPanel();
                }
            });
            rtReportPopContent.removeAllViews();
            rtReportPopContent.addView(reportViewLayout);
            rtReportPopRoot.setVisibility(View.VISIBLE);
            Animation showAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.anim_bottom_center);
            showAnim.setDuration(300);
            rtReportPopContent.startAnimation(showAnim);
        }
    }

    private void hiddenReportPanel(){
        if(rtReportPopRoot.getVisibility() == View.VISIBLE){
            Animation hideAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.anim_center_bottom);
            hideAnim.setDuration(300);
            hideAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    rtReportPopRoot.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            rtReportPopContent.startAnimation(hideAnim);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNeedRefreshRoport(CommonEvent commonEvent){
        if(CommonEvent.EVENT_REFRESH_REPORT.equals(commonEvent.getEventType())){
            Log.i("QsReportFragment","需要刷新报告数据");
            EventBus.getDefault().post(new CommonEvent(CommonEvent.EVENT_REFRESH_REPORT_PAGE));
            Fragment fragment = mFragments.get(1);
            if(fragment instanceof QsReportFragment){
                QsReportFragment qsReportFragment = (QsReportFragment)fragment;
                qsReportFragment.onRefreshRoport();
            }
        }
    }

}

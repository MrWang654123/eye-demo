package com.cheersmind.cheersgenie.features.modules.base.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.modules.login.activity.XLoginActivity;
import com.cheersmind.cheersgenie.main.constant.Constant;
import com.cheersmind.cheersgenie.main.util.DensityUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.relex.circleindicator.CircleIndicator;


/**
 * 引导页面
 */
public class GuideActivity extends BaseActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    //内容viewpager
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    //viewpager的指示器
    @BindView(R.id.indicator)
    CircleIndicator indicator;

    private List<View> views;
    private GuideViewPagerAdapter vpAdapter;
    private Button btnGo;

    @Override
    protected int setContentView() {
        return R.layout.activity_guide;
    }

    @Override
    protected String settingTitle() {
        return null;
    }

    @Override
    protected void onInitView() {

        views = new ArrayList<View>();

        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        for(int i=0; i<3; i++) {
            RelativeLayout rlChildView = new RelativeLayout(this);
            rlChildView.setLayoutParams(mParams);
            views.add(rlChildView);

            ImageView iv = new ImageView(this);
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            iv.setLayoutParams(mParams);
            //iv.setImageResource(pics[i]);
            rlChildView.addView(iv);
            switch (i) {
                case 0:
                    iv.setImageResource(R.mipmap.new_feature_0);
                    break;
                case 1:
                    iv.setImageResource(R.mipmap.new_feature_1);
                    break;
                case 2:
                    iv.setImageResource(R.mipmap.new_feature_2);

                    btnGo = new Button(this);
                    btnGo.setTextSize(14);

                    btnGo.setOnClickListener(this);
                    rlChildView.addView(btnGo);
//                    btnGo.setBackgroundResource(R.drawable.shape_corner_13b2f4);
                    btnGo.setBackgroundResource(R.mipmap.btn_start_app);
                    //btnGo.setTextColor(getResources().getColor(R.color.color_43c8f6));
                    btnGo.setTextColor(Color.WHITE);
                    WindowManager wm = this.getWindowManager();
                    int width = wm.getDefaultDisplay().getWidth();
                    int height = wm.getDefaultDisplay().getHeight();

                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) btnGo.getLayoutParams();
                    params.width = width*4/5;
                    params.height = DensityUtil.dip2px(this,50);

                    params.setMargins((width-params.width)/2, height-params.height- DensityUtil.dip2px(this,16)-indicator.getLayoutParams().height, 0, 0);// 通过自定义坐标来放置你的控件
                    btnGo.setLayoutParams(params);
                    break;
            }

        }


        viewPager = (ViewPager) findViewById(R.id.viewpager);
        //初始化Adapter
        vpAdapter = new GuideViewPagerAdapter(views);
        viewPager.setAdapter(vpAdapter);
        //绑定回调
        viewPager.setOnPageChangeListener(this);

        viewPager.setAdapter(vpAdapter);
        indicator.setViewPager(viewPager);
        vpAdapter.registerDataSetObserver(indicator.getDataSetObserver());

        viewPager.setCurrentItem(0);

        //保存启动页的状态
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = pref.edit();
        //editor.putBoolean("is_first_start",false);
        editor.putInt("feature_version", Constant.VERSION_FEATURE);
        editor.commit();
    }

    @Override
    protected void onInitData() {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View view) {
        if (view == btnGo) {
            Intent intent = new Intent(this, XLoginActivity.class); // 从启动动画ui跳转到主ui
            startActivity(intent);
            finish();
        }
    }

    private class GuideViewPagerAdapter extends PagerAdapter {

        //界面列表
        private List<View> views;

        public GuideViewPagerAdapter(List<View> views){
            this.views = views;
        }

//    @Override
//    public int getCount() {
//        if (views != null) {
//            return views.size();
//        }
//        return 0;
//    }
//
//    @Override
//    public boolean isViewFromObject(View view, Object object) {
//        //return false;
//        return view == object;
//    }

        //销毁arg1位置的界面
        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView(views.get(arg1));
        }

        @Override
        public void finishUpdate(View arg0) {
            // TODO Auto-generated method stub

        }

        //获得当前界面数
        @Override
        public int getCount() {
            if (views != null)
            {
                return views.size();
            }

            return 0;
        }


        //初始化arg1位置的界面
        @Override
        public Object instantiateItem(View arg0, int arg1) {

            ((ViewPager) arg0).addView(views.get(arg1), 0);

            return views.get(arg1);
        }

        //判断是否由对象生成界面
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return (arg0 == arg1);
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
            // TODO Auto-generated method stub

        }

        @Override
        public Parcelable saveState() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void startUpdate(View arg0) {
            // TODO Auto-generated method stub
        }
    }

}

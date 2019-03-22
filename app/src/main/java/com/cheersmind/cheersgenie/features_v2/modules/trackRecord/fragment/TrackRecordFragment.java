package com.cheersmind.cheersgenie.features_v2.modules.trackRecord.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.adapter.TabFragmentPagerAdapter;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.entity.UserInfo;
import com.cheersmind.cheersgenie.features.event.StopFlingEvent;
import com.cheersmind.cheersgenie.features.modules.base.fragment.LazyLoadFragment;
import com.cheersmind.cheersgenie.features.utils.FileUtil;
import com.cheersmind.cheersgenie.features.utils.ImageUtil;
import com.cheersmind.cheersgenie.features_v2.modules.exam.fragment.ExamTaskCommentFragment;
import com.cheersmind.cheersgenie.features_v2.modules.exam.fragment.ExamTaskItemFragment;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.entity.ChildInfoEntity;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.view.LoadingView;
import com.cheersmind.cheersgenie.module.login.UCManager;
import com.facebook.drawee.view.SimpleDraweeView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * 成长档案
 */
public class TrackRecordFragment extends LazyLoadFragment {

    Unbinder unbinder;

    @BindView(R.id.iv_main)
    SimpleDraweeView ivMain;
    //用户名
    @BindView(R.id.tv_user_name)
    TextView tvUserName;
    //性别
    @BindView(R.id.tv_gender)
    TextView tvGender;
    //用户信息（目前是学校信息）
    @BindView(R.id.tv_user_info)
    TextView tvUserInfo;
    //头像
    @BindView(R.id.iv_profile)
    ImageView ivProfile;
    //修改头像的提示图
    @BindView(R.id.tv_modify_profile_tip)
    ImageView tvModifyProfileTip;

    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.appbar_layout)
    AppBarLayout appBarLayout;

    //默认Glide配置
    RequestOptions options;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        options = new RequestOptions()
                .circleCrop()//圆形
                .skipMemoryCache(true)//忽略内存
                .placeholder(R.drawable.ico_head)//占位图
                .dontAnimate()//Glide默认是渐变动画，设置dontAnimate()不要动画
                .diskCacheStrategy(DiskCacheStrategy.NONE);//磁盘缓存策略：不缓存
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_track_record;
    }

    @Override
    protected void onInitView(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);

        //主图
//        ivMain.setImageURI(examTask != null ? examTask.getArticleImg() : "");

        List<Pair<String, Fragment>> items = new ArrayList<>();
        items.add(new Pair<String, Fragment>("生涯发展档案", new CareerPlanReportFragment()));
        items.add(new Pair<String, Fragment>("能力发展档案", new TrackRecordDetailFragment()));
        viewPager.setAdapter(new TabFragmentPagerAdapter(getChildFragmentManager(), items));
        //标签绑定viewpager
        tabs.setupWithViewPager(viewPager);

        //监听 AppBarLayout Offset 变化，动态设置 SwipeRefreshLayout 是否可用
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (verticalOffset >= 0) {
                    //发送停止Fling的事件
                    EventBus.getDefault().post(new StopFlingEvent());
                }

            }
        });

        //初始隐藏个人信息
        tvUserName.setVisibility(View.GONE);

        //获取用户个人资料
        if (UCManager.getInstance().getUserInfo() != null) {
            //刷新个人资料视图
            refreshPersonalInfo(UCManager.getInstance().getUserInfo());
        } else {
            //请求个人资料
            getUserInfo();
        }
    }

    @Override
    protected void lazyLoad() {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * 获取用户信息
     */
    private void getUserInfo () {
//        LoadingView.getInstance().show(getActivity());

        DataRequestService.getInstance().getUserInfoV2(new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                onFailureDefault(e);
            }

            @Override
            public void onResponse(Object obj) {
                LoadingView.getInstance().dismiss();
                try {
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    UserInfo userInfo = InjectionWrapperUtil.injectMap(dataMap, UserInfo.class);
                    //设置用户信息
                    UCManager.getInstance().setUserInfo(userInfo);

                    //刷新个人资料视图
                    refreshPersonalInfo(userInfo);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, httpTag, getActivity());
    }

    /**
     * 刷新个人资料视图
     * @param userInfo 用户信息
     */
    private void refreshPersonalInfo(UserInfo userInfo) {

        //刷新用户名和昵称的视图
        refreshUserNameAndNickname(userInfo.getUserName(), userInfo.getNickName());

        //性别
        tvGender.setText(userInfo.getSex() == 1 ? "男": "女");

        ChildInfoEntity defaultChild = UCManager.getInstance().getDefaultChild();
        if (defaultChild != null) {
            tvUserInfo.setVisibility(View.VISIBLE);
            //学校信息
            tvUserInfo.setText(defaultChild.getSchoolName());
        }

        //刷新头像
        refreshProfile(userInfo.getAvatar());
    }

    /**
     * 刷新用户名和昵称的视图
     * @param userName 用户名
     * @param nickname 昵称
     */
    private void refreshUserNameAndNickname(String userName, String nickname) {
        tvUserName.setVisibility(View.VISIBLE);
//        tvGender.setVisibility(View.VISIBLE);

        if (!TextUtils.isEmpty(nickname)) {
            if (!TextUtils.isEmpty(userName)) {
                //姓名 + 昵称
                tvUserName.setText(String.valueOf(userName + "（" + nickname + "）"));

            } else {
                //昵称
                tvUserName.setText(nickname);
            }
        } else {
            //姓名
            tvUserName.setText(userName);
        }
    }

    /**
     * 刷新头像
     * @param profileUrl 头像url
     */
    private void refreshProfile(final String profileUrl) {
        if (TextUtils.isEmpty(profileUrl)) {
            return;
        }

        //非空
        GlideUrl glideUrl = new GlideUrl(profileUrl, new LazyHeaders.Builder()
                .addHeader(Dictionary.PROFILE_HEADER_KEY, Dictionary.PROFILE_HEADER_VALUE)
                .build());

        //解析出图片名称
        final String imageName = ImageUtil.parseImageNameFromUrl(profileUrl);
        //本地文件的完整名称
        final String fileFullName = FileUtil.getFileFullNameFromExtraDirs(getContext(), imageName);
        Bitmap bitmap = null;
        if (!TextUtils.isEmpty(fileFullName)) {
            try {
                bitmap = BitmapFactory.decodeFile(fileFullName);
            } catch (Exception e) {
                bitmap = null;
            }
        }
        //预加载Bitmap
        final Bitmap preloadBitmap = bitmap;
        //加载网络图片
        ImageViewTarget target = new ImageViewTarget<BitmapDrawable>(ivProfile){

            @Override
            public void onLoadStarted(@Nullable Drawable placeholder) {
                super.onLoadStarted(placeholder);
                if (preloadBitmap != null) {
                    view.setImageBitmap(preloadBitmap);
                }
            }

            @Override
            public void onResourceReady(final @NonNull BitmapDrawable resource, @Nullable Transition<? super BitmapDrawable> transition) {
                super.onResourceReady(resource, transition);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //保存bitmap到外部文件系统
                        FileUtil.saveBitmapToExtraDirs(getContext(), imageName, resource.getBitmap());
                    }
                }).start();
            }

            @Override
            protected void setResource(@Nullable BitmapDrawable resource) {
                if (resource != null) {
                    Bitmap bitmap = resource.getBitmap();
                    view.setImageBitmap(bitmap);
                } else {
                    view.setImageBitmap(null);
                }
            }
        };

        Glide.with(this)
                .load(glideUrl)
                .apply(options)
                .into(target);

//        ImageViewTarget target = new ImageViewTarget<BitmapDrawable>(ivProfile){
//
//            @Override
//            public void onResourceReady(@NonNull BitmapDrawable resource, @Nullable Transition<? super BitmapDrawable> transition) {
//                super.onResourceReady(resource, transition);
//                //解析出图片名称
//                String imageName = ImageUtil.parseImageNameFromUrl(profileUrl);
//                //保存bitmap到外部文件系统
//                FileUtil.saveBitmapToExtraDirs(getContext(), imageName, resource.getBitmap());
//            }
//
//            @Override
//            protected void setResource(@Nullable BitmapDrawable resource) {
//                if (resource != null) {
//                    Bitmap bitmap = resource.getBitmap();
//                    view.setImageBitmap(bitmap);
//                } else {
//                    view.setImageBitmap(null);
//                }
//            }
//        };
//
//
//        ImageViewTarget into = Glide.with(this)
//                .load(glideUrl)
//                .apply(options)
//                .into(target);

//        //解析出图片名称
//        final String imageName = ImageUtil.parseImageNameFromUrl(profileUrl);
//        File fileImage = FileUtil.getFileFromExtraDirs(getContext(), imageName);
//        if (fileImage != null && fileImage.exists()) {
//            //加载本地图片
//            Glide.with(this)
//                    .load(fileImage)
////                .thumbnail(0.5f)
//                    .apply(options)
//                    .into(ivProfile);
//        } else {
//            //加载网络图片
//            Glide.with(this)
//                    .load(profileUrl)
////                .thumbnail(0.5f)
//                    .apply(options)
//                    .into(ivProfile);
//        }

        //隐藏修改头像提示图
//        tvModifyProfileTip.setVisibility(View.GONE);
    }


}


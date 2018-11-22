package com.cheersmind.cheersgenie.features.modules.base.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.alibaba.sdk.android.feedback.impl.IActivityCallback;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.entity.UserInfo;
import com.cheersmind.cheersgenie.features.event.MessageReadEvent;
import com.cheersmind.cheersgenie.features.event.ModifyNicknameEvent;
import com.cheersmind.cheersgenie.features.event.ModifyProfileEvent;
import com.cheersmind.cheersgenie.features.event.RefreshIntegralEvent;
import com.cheersmind.cheersgenie.features.modules.mine.activity.MineExamActivity;
import com.cheersmind.cheersgenie.features.modules.mine.activity.MineFavoriteActivity;
import com.cheersmind.cheersgenie.features.modules.mine.activity.MineIntegralActivity;
import com.cheersmind.cheersgenie.features.modules.mine.activity.MineMessageActivity;
import com.cheersmind.cheersgenie.features.modules.mine.activity.UserInfoActivity;
import com.cheersmind.cheersgenie.features.modules.mine.activity.XSettingActivity;
import com.cheersmind.cheersgenie.features.utils.FileUtil;
import com.cheersmind.cheersgenie.features.utils.ImageUtil;
import com.cheersmind.cheersgenie.features.utils.IntegralUtil;
import com.cheersmind.cheersgenie.features.view.dialog.IntegralTipDialog;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.entity.ChildInfoEntity;
import com.cheersmind.cheersgenie.main.entity.TotalIntegralEntity;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.DensityUtil;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.view.LoadingView;
import com.cheersmind.cheersgenie.module.login.UCManager;

import org.devio.takephoto.model.TResult;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * “我的”页面（个人资料等）
 */
public class MineFragment extends TakePhotoFragment {

    Unbinder unbinder;

    //签到
    @BindView(R.id.btn_sign_in)
    Button btnSignIn;

    //新消息数量
    @BindView(R.id.tv_new_message_count)
    TextView tvNewMessageCount;

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

    //可用积分
    @BindView(R.id.tv_usable_integral_val)
    TextView tvUsableIntegralVal;
    //可用积分的值
    int useableIntegralVal = -1;

    //默认Glide配置
    RequestOptions options;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //注册事件
        EventBus.getDefault().register(this);

        options = new RequestOptions()
                .circleCrop()//圆形
                .skipMemoryCache(true)//忽略内存
                .placeholder(R.drawable.ico_head)//占位图
                .dontAnimate()//Glide默认是渐变动画，设置dontAnimate()不要动画
                .diskCacheStrategy(DiskCacheStrategy.NONE);//磁盘缓存策略：不缓存
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_mine_x;
    }

    @Override
    protected void onInitView(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);

        //初始隐藏个人信息
        tvUserName.setVisibility(View.GONE);
        tvGender.setVisibility(View.GONE);
        tvUserInfo.setVisibility(View.GONE);
        //初始隐藏最新消息条数
        tvNewMessageCount.setVisibility(View.GONE);

        //查询当前签到状态
        queryDailySignInStatus();

        //获取最新消息条数
        queryNewMessageCount();

        //获取用户个人资料
        if (UCManager.getInstance().getUserInfo() != null) {
            //刷新个人资料视图
            refreshPersonalInfo(UCManager.getInstance().getUserInfo());
        } else {
            //请求个人资料
            getUserInfo();
        }

        //加载总积分
        loadIntegralTotalScore();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //注销事件
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void lazyLoad() {
        //查询当前签到状态
//        queryDailySignInStatus();

        //如果最新消息数量为隐藏，则再获取一次
        if (tvNewMessageCount.getVisibility() == View.VISIBLE) {
            //获取最新消息条数
            queryNewMessageCount();
        }

        //获取用户个人资料
        if (UCManager.getInstance().getUserInfo() == null) {
            //请求个人资料
            getUserInfo();
        }

        //如果积分值还是为-1，则再获取一次
        if (useableIntegralVal == -1) {
            //加载总积分
            loadIntegralTotalScore();
        }
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            //加载总积分
            loadIntegralTotalScore();
        }
    }

    /**
     * 消息被置为已读的通知
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
//    @Subscribe
    public void onMessageReadNotice(MessageReadEvent event) {
        if (event == null) {
            return;
        }

        //最新消息数量
        int newMessageCount = Integer.parseInt(tvNewMessageCount.getText().toString());
        if (newMessageCount > 0) {
            newMessageCount = newMessageCount - 1;
        }
        tvNewMessageCount.setText(String.valueOf(newMessageCount));
        //数量为0就隐藏
        if (newMessageCount == 0) {
            tvNewMessageCount.setVisibility(View.GONE);
        }

    }


    /**
     *
     * 修改头像的通知
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
//    @Subscribe
    public void onModifyProfileNotice(ModifyProfileEvent event) {
        if (event == null) {
            return;
        }

        //非空
        String profileUrl = event.getProfileUrl();
        if (!TextUtils.isEmpty(profileUrl)) {
            //刷新头像
            refreshProfile(profileUrl);
        }

    }

    /**
     *
     * 修改昵称的通知
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
//    @Subscribe
    public void onModifyNicknameNotice(ModifyNicknameEvent event) {
        if (event == null) {
            return;
        }

        //刷新昵称视图
        UserInfo userInfo = event.getUserInfo();
        if (userInfo != null) {
            //刷新用户名和昵称的视图
            refreshUserNameAndNickname(userInfo.getUserName(), userInfo.getNickName());
        }

    }


    /**
     * 刷新积分的消息
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
//    @Subscribe
    public void onRefreshIntegralNotice(RefreshIntegralEvent event) {
        //加载总积分
        loadIntegralTotalScore();
    }


    /**
     * 获取最新消息条数
     */
    private void queryNewMessageCount() {
        DataRequestService.getInstance().getNewMessageCount(new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Object obj) {
                try {
                    int messageCount = ((JSONObject) obj).getInt("total");
                    if (messageCount > 0) {
                        tvNewMessageCount.setVisibility(View.VISIBLE);
                        tvNewMessageCount.setText(String.valueOf(messageCount));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 查询签到状态
     */
    private void queryDailySignInStatus() {
//        LoadingView.getInstance().show(getActivity());

        DataRequestService.getInstance().getDailySignInStatus(new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                onFailureDefault(e);
            }

            @Override
            public void onResponse(Object obj) {
                LoadingView.getInstance().dismiss();

                try {
                    boolean isSignIn = ((JSONObject) obj).getBoolean("is_sign_in");
                    //设置签到状态
                    settingSignInStatus(isSignIn);

                } catch (Exception e) {
                    e.printStackTrace();
                    //设置签到状态：未签
                    settingSignInStatus(false);
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @OnClick({R.id.ll_mine_integral, R.id.ll_mine_message, R.id.ll_mine_collect,
            R.id.ll_mine_report, R.id.ll_feedback, R.id.ll_setting, R.id.btn_sign_in, R.id.iv_profile,
            R.id.ll_user_info})
    public void onViewClicked(View view) {
        switch (view.getId()) {
                //用户信息布局
            case R.id.ll_user_info: {
                Intent intent = new Intent(getContext(), UserInfoActivity.class);
                startActivity(intent);

                break;
            }
            //头像
            case R.id.iv_profile: {
                //弹出选择修改头像方式的对话框
                popupModifyProfileWindows(getContext());
                break;
            }
            //签到
            case R.id.btn_sign_in: {
                //签到
                doSignIn();
                break;
            }
            //我的积分
            case R.id.ll_mine_integral: {
                Intent intent = new Intent(getContext(), MineIntegralActivity.class);
                startActivity(intent);

                break;
            }
            //我的消息
            case R.id.ll_mine_message: {
                Intent intent = new Intent(getContext(), MineMessageActivity.class);
                startActivity(intent);
                break;
            }
            //我的收藏
            case R.id.ll_mine_collect: {
                Intent intent = new Intent(getContext(), MineFavoriteActivity.class);
                startActivity(intent);
                break;
            }
            //我的智评（目前就是报告页面）
            case R.id.ll_mine_report: {
                //切换到报告页面
//                ((MasterTabActivity) getActivity()).switchToReportPage();
                Intent intent = new Intent(getContext(), MineExamActivity.class);
                startActivity(intent);
                break;
            }
            //反馈
            case R.id.ll_feedback: {
                //设置标题栏高度，单位为像素
                FeedbackAPI.setTitleBarHeight(DensityUtil.dip2px(MineFragment.this.getContext(), 48));
                FeedbackAPI.openFeedbackActivity();
                //设置回退按钮图标
                FeedbackAPI.setBackIcon(R.drawable.ali_feedback_common_back_btn_bg);
                /**
                 * 在Activity的onCreate中执行的代码
                 * 可以设置状态栏背景颜色和图标颜色，这里使用com.githang:status-bar-compat来实现
                 */
                FeedbackAPI.setActivityCallback(new IActivityCallback() {
                    @Override
                    public void onCreate(Activity activity) {
//                        StatusBarCompat.setStatusBarBackgroundColor(activity,getResources().getColor(R.color.aliwx_setting_bg_nor),true);

                        //设置状态栏颜色
                        setStatusBarColor(activity, Color.parseColor("#FFF000"));

                        //设置状态栏文字颜色及图标为深色
                        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                        //设置状态栏文字颜色及图标为浅色
//                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

                    }
                });
                break;
            }
            //我的设置
            case R.id.ll_setting: {
                startActivity(new Intent(getActivity(), XSettingActivity.class));
                break;
            }
        }
    }

    /**
     * 签到
     */
    private void doSignIn() {
        LoadingView.getInstance().show(getActivity());

        DataRequestService.getInstance().postDailySignIn(new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                onFailureDefault(e);
            }

            @Override
            public void onResponse(Object obj) {
                LoadingView.getInstance().dismiss();
                //设置签到状态：已签
                settingSignInStatus(true);

                //加载总积分
                loadIntegralTotalScore();

                //积分提示
                if (getContext() != null) {
                    IntegralUtil.showIntegralTipDialog(getContext(), obj, null);
                }
            }
        });
    }

    /**
     * 设置签到状态
     * @param has
     */
    private void settingSignInStatus(boolean has) {
        if (has) {
            btnSignIn.setText("已签到");
            btnSignIn.setEnabled(false);
            btnSignIn.setTextColor(Color.parseColor("#ffffff"));
        } else {
            btnSignIn.setText("签到");
            btnSignIn.setEnabled(true);
        }
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
        });
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


    @Override
    public void takeCancel() {
        super.takeCancel();
    }

    @Override
    public void takeFail(TResult result, String msg) {
        super.takeFail(result, msg);
    }

    @Override
    public void takeSuccess(TResult result) {
        super.takeSuccess(result);

        File file = new File(result.getImages().get(0).getCompressPath());
//        Glide.with(this).load(file).into(ivProfile);
        //上传头像
        doPostModifyProfile(file);
    }


    /**
     * 上传头像
     */
    private void doPostModifyProfile(final File file) {
        //通信等待提示
        LoadingView.getInstance().show(getContext());
        DataRequestService.getInstance().postModifyProfile(file, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                onFailureDefault(e);
            }

            @Override
            public void onResponse(Object obj) {
                //关闭通信等待提示
                LoadingView.getInstance().dismiss();

                try {
                    //解析数据
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    //头像url
                    final String profileUrl = (String) dataMap.get("file_path");

                    //解析出图片名称
                    final String imageName = ImageUtil.parseImageNameFromUrl(profileUrl);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //保存文件
                            FileUtil.saveFileToExtraDirs(getContext(), imageName, file);
                        }
                    }).start();

                    //重置本地缓存中的头像url
                    if (UCManager.getInstance().getUserInfo() != null) {
                        UCManager.getInstance().getUserInfo().setAvatar(profileUrl);
                    }

                    //直接加载临时图片文件
                    Glide.with(MineFragment.this)
                            .load(file)
//                .thumbnail(0.5f)
                            .apply(options)
                            .into(ivProfile);

                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(new QSCustomException(getResources().getString(R.string.operate_fail)));
                }
            }
        });
    }


    /**
     * 设置状态栏颜色
     * （目前只支持5.0以上，4.4到5.0之间由于各厂商存在兼容问题，故暂不考虑）
     * （用全屏的方式来强行模拟，感觉没必要）
     * @param activity
     * @param colorStatus
     */
    protected void setStatusBarColor(Activity activity, int colorStatus) {
        //5.0及以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            //需要设置这个 flag 才能调用 setStatusBarBackgroundColor 来设置状态栏颜色
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //设置状态栏颜色
            window.setStatusBarColor(colorStatus);
            //底部导航栏
            //window.setNavigationBarColor(activity.getResources().getColor(colorResId));

            ViewGroup mContentView = (ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT);
            View mChildView = mContentView.getChildAt(0);
            if (mChildView != null) {
                //注意不是设置 ContentView 的 FitsSystemWindows, 而是设置 ContentView 的第一个子 View . 预留出系统 View 的空间.
                mChildView.setFitsSystemWindows(true);
            }

            //4.4到5.0
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            /*Window window = activity.getWindow();
            ViewGroup decorViewGroup = (ViewGroup) window.getDecorView();
            View statusBarView = new View(window.getContext());
            int statusBarHeight = getStatusBarHeight(window.getContext());
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, statusBarHeight);
            params.gravity = Gravity.TOP;
            statusBarView.setLayoutParams(params);
            statusBarView.setBackgroundColor(colorStatus);
            decorViewGroup.addView(statusBarView);

            ViewGroup mContentView = (ViewGroup) window.findViewById(Window.ID_ANDROID_CONTENT);
            View mChildView = mContentView.getChildAt(0);
            if (mChildView != null) {
                //注意不是设置 ContentView 的 FitsSystemWindows, 而是设置 ContentView 的第一个子 View . 预留出系统 View 的空间.
                mChildView.setFitsSystemWindows(true);
            }*/
        }
    }

    /**
     * 用于子页面设置状态栏颜色（目前只支持5.0以后）
     * @return
     */
    protected int getStatusBarColor() {
        return getResources().getColor(R.color.colorPrimary);
    }


    /**
     * 加载总积分
     */
    private void loadIntegralTotalScore() {
        DataRequestService.getInstance().getIntegralTotalScore(new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                tvUsableIntegralVal.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onResponse(Object obj) {
                try {
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    TotalIntegralEntity totalIntegralEntity = InjectionWrapperUtil.injectMap(dataMap, TotalIntegralEntity.class);
                    //刷新总积分视图
                    tvUsableIntegralVal.setVisibility(View.VISIBLE);
                    useableIntegralVal = totalIntegralEntity.getConsumable();
                    tvUsableIntegralVal.setText(String.valueOf(useableIntegralVal));

                } catch (Exception e) {
                    e.printStackTrace();
                    tvUsableIntegralVal.setVisibility(View.INVISIBLE);
                }
            }
        });
    }


}



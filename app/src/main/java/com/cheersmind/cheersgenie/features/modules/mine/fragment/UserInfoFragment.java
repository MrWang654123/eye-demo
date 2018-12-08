package com.cheersmind.cheersgenie.features.modules.mine.fragment;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
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
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.entity.UserInfo;
import com.cheersmind.cheersgenie.features.event.ModifyNicknameEvent;
import com.cheersmind.cheersgenie.features.event.ModifyProfileEvent;
import com.cheersmind.cheersgenie.features.modules.base.fragment.TakePhotoFragment;
import com.cheersmind.cheersgenie.features.utils.DataCheckUtil;
import com.cheersmind.cheersgenie.features.utils.FileUtil;
import com.cheersmind.cheersgenie.features.utils.ImageUtil;
import com.cheersmind.cheersgenie.features.utils.SoftInputUtil;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.features.view.dialog.ModifyNicknameDialog;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.entity.ChildInfoEntity;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.OnMultiClickListener;
import com.cheersmind.cheersgenie.main.util.ToastUtil;
import com.cheersmind.cheersgenie.main.view.LoadingView;
import com.cheersmind.cheersgenie.module.login.UCManager;

import org.devio.takephoto.model.TResult;
import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 个人资料
 */
public class UserInfoFragment extends TakePhotoFragment {

    Unbinder unbinder;

    //孩子信息提示文本
    @BindView(R.id.tv_childInfo_tip)
    TextView tvChildInfoTip;

    //昵称
    @BindView(R.id.tv_nickname)
    TextView tvNickname;
    //姓名
    @BindView(R.id.tv_user_name)
    TextView tvUserName;
    //性别
    @BindView(R.id.tv_gender)
    TextView tvGender;
    //出生日期
    @BindView(R.id.tv_birthday)
    TextView tvBirthday;
    //学校
    @BindView(R.id.tv_school)
    TextView tvSchool;
    //年段
    @BindView(R.id.tv_period)
    TextView tvPeriod;
    //年级
    @BindView(R.id.tv_grade)
    TextView tvGrade;
    //班级
    @BindView(R.id.tv_class)
    TextView tvClass;
    //头像
    @BindView(R.id.iv_profile)
    ImageView ivProfile;
    //修改头像的提示图
    @BindView(R.id.tv_modify_profile_tip)
    ImageView tvModifyProfileTip;

    @BindView(R.id.emptyLayout)
    XEmptyLayout emptyLayout;

    //默认Glide配置
    RequestOptions options;

    //头像图片文件
    private File file;


    @Override
    protected int setContentView() {
        return R.layout.fragment_user_info;
    }

    @Override
    protected void onInitView(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);

        options = new RequestOptions()
                .circleCrop()//圆形
                .skipMemoryCache(true)//忽略内存
                .placeholder(R.drawable.ico_head)//占位图
                .dontAnimate()//Glide默认是渐变动画，设置dontAnimate()不要动画
                .diskCacheStrategy(DiskCacheStrategy.NONE);//磁盘缓存策略：不缓存

        //用户信息
        if (UCManager.getInstance().getUserInfo() == null) {
            //获取用户信息
            getUserInfo();

        } else {
            ChildInfoEntity defaultChild = UCManager.getInstance().getDefaultChild();
            UserInfo userInfo = UCManager.getInstance().getUserInfo();
            //刷新用户信息视图
            refreshUserInfoView(userInfo, defaultChild);
        }

        emptyLayout.setOnReloadListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View view) {
                getUserInfo();
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @Override
    protected void lazyLoad() {

    }

    @OnClick({R.id.iv_profile, R.id.btn_modify_profile, R.id.rl_nickname})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //头像
            case R.id.iv_profile: {
                //弹出选择修改头像方式的对话框
                popupModifyProfileWindows(getContext());
                break;
            }

            //提交头像
            case R.id.btn_modify_profile: {
                if (file != null) {
                    doPostModifyProfile(file);
                }
                break;
            }
            //跳转更改昵称
            case R.id.rl_nickname: {
//                ToastUtil.showShort(getContext(), "跳转更改昵称");
                showModifyNicknameDialog(getActivity());
                break;
            }
        }
    }


    /**
     * 上传头像
     */
    private void doPostModifyProfile(final File file) {
        //通信等待提示
        LoadingView.getInstance().show(getContext(), httpTag);
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
                            //发送修改头像的通知
                            EventBus.getDefault().post(new ModifyProfileEvent(profileUrl));
                        }
                    }).start();

                    //重置本地缓存中的头像url
                    if (UCManager.getInstance().getUserInfo() != null) {
                        UCManager.getInstance().getUserInfo().setAvatar(profileUrl);
                    }

                    //直接加载临时图片文件
                    Glide.with(UserInfoFragment.this)
                            .load(file)
//                .thumbnail(0.5f)
                            .apply(options)
                            .into(ivProfile);

                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(new QSCustomException(getResources().getString(R.string.operate_fail)));
                }
            }
        }, httpTag);
    }

    /**
     * 获取用户信息
     */
    private void getUserInfo () {
        //空布局提示：正在加载
        emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);

        DataRequestService.getInstance().getUserInfoV2(new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                //空布局提示：网络错误
                emptyLayout.setErrorType(XEmptyLayout.NETWORK_ERROR);
            }

            @Override
            public void onResponse(Object obj) {
                //空布局提示：隐藏
                emptyLayout.setErrorType(XEmptyLayout.HIDE_LAYOUT);
                try {
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    UserInfo userInfo = InjectionWrapperUtil.injectMap(dataMap, UserInfo.class);

                    //设置用户信息
                    UCManager.getInstance().setUserInfo(userInfo);
                    //孩子信息
                    ChildInfoEntity defaultChild = UCManager.getInstance().getDefaultChild();
                    //刷新用户信息视图
                    refreshUserInfoView(userInfo, defaultChild);

                } catch (Exception e) {
                    e.printStackTrace();
                    //空布局提示：无数据，可重试
                    emptyLayout.setErrorType(XEmptyLayout.NO_DATA_ENABLE_CLICK);
                }
            }
        }, httpTag);
    }

    /**
     * 刷新用户信息视图
     * @param userInfo 用户信息
     * @param childInfo 孩子信息
     */
    private void refreshUserInfoView(UserInfo userInfo, ChildInfoEntity childInfo) {

        if (userInfo != null) {
            //刷新头像
            refreshProfile(userInfo.getAvatar());
        }

        String userName = "";//用户名
        int gender = 1;//1：男，2：女

        //角色
        int parentRole = childInfo.getParentRole();
        //非自己
        if (parentRole != Dictionary.PARENT_ROLE_MYSELF) {
            //提示是孩子信息
            tvChildInfoTip.setVisibility(View.VISIBLE);
            userName = childInfo.getChildName();
            gender = childInfo.getSex();

        } else {//是自己
            //隐藏提示是孩子信息
            tvChildInfoTip.setVisibility(View.GONE);
            if (userInfo != null) {
                userName = userInfo.getUserName();
                gender = userInfo.getSex();
            }
        }

        if (userInfo != null) {
            //昵称
            tvNickname.setText(userInfo.getNickName());
        }
        //姓名
        tvUserName.setText(userName);
        //性别
        tvGender.setText(gender == 1 ? "男": "女");

        //出生年月
        String dateStr = childInfo.getBirthDay();//ISO8601 时间字符串
        if (!TextUtils.isEmpty(dateStr)) {
            SimpleDateFormat formatIso8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            try {
                Date date = formatIso8601.parse(dateStr);
                SimpleDateFormat formatNormal = new SimpleDateFormat("yyyy-MM-dd");
                String normalDateStr = formatNormal.format(date);
                tvBirthday.setText(normalDateStr);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            tvBirthday.setText("--");
        }

        //学校
        tvSchool.setText(childInfo.getSchoolName());
        //学段
        tvPeriod.setText(childInfo.getPeriod());
        //年级
        tvGrade.setText(childInfo.getGrade());
        //班级
        tvClass.setText(childInfo.getClass_name());

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

//        //非空
//        GlideUrl glideUrl = new GlideUrl(profileUrl, new LazyHeaders.Builder()
//                .addHeader(Dictionary.PROFILE_HEADER_KEY, Dictionary.PROFILE_HEADER_VALUE)
//                .build());
//        //加载网络图片
//        Glide.with(this)
//                .load(glideUrl)
////                .thumbnail(0.5f)
//                .apply(options)
//                .into(ivProfile);

        //解析出图片名称
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
        tvModifyProfileTip.setVisibility(View.GONE);
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

        file = new File(result.getImages().get(0).getCompressPath());
//        Glide.with(this).load(file).into(ivProfile);
        //上传头像
        doPostModifyProfile(file);
    }


    /**
     * 显示修改昵称对话框
     */
    public void showModifyNicknameDialog(final Context context){
        String nickname = tvNickname.getText().toString();
        Dialog dialog = new ModifyNicknameDialog(context, nickname, new ModifyNicknameDialog.OnOperationListener() {

            @Override
            public void onModifySuccess(String nickname) {
                //重置内存中用户信息的昵称
                UserInfo userInfo = UCManager.getInstance().getUserInfo();
                if (userInfo != null) {
                    userInfo.setNickName(nickname);
                }

                //刷新昵称视图
                tvNickname.setText(nickname);

                //发送修改昵称的通知
                EventBus.getDefault().post(new ModifyNicknameEvent(userInfo));

                //提示
                ToastUtil.showShort(context, "修改成功");

            }
        });

        //有弹出软键盘，不用动画
//        if (dialog.getWindow() != null) {
//            dialog.getWindow().setWindowAnimations(R.style.WUI_Animation_Dialog);
//        }
        dialog.show();
    }


}

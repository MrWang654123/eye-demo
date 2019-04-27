package com.cheersmind.cheersgenie.features.modules.mine.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.dto.ThirdPlatBindDto;
import com.cheersmind.cheersgenie.features.entity.ArticleRootEntity;
import com.cheersmind.cheersgenie.features.entity.SimpleArticleEntity;
import com.cheersmind.cheersgenie.features.modules.base.activity.BaseActivity;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features.utils.NetworkUtil;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.features_v2.entity.ThirdPlatformAccount;
import com.cheersmind.cheersgenie.features_v2.entity.ThirdPlatformAccountRoot;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.constant.Constant;
import com.cheersmind.cheersgenie.features.entity.QQTokenEntity;
import com.cheersmind.cheersgenie.main.entity.WXTokenEntity;
import com.cheersmind.cheersgenie.main.event.WXLoginEvent;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.ToastUtil;
import com.cheersmind.cheersgenie.main.view.LoadingView;
import com.cheersmind.cheersgenie.module.login.UCManager;
import com.tencent.connect.common.Constants;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

import static com.cheersmind.cheersgenie.main.constant.Constant.mTencent;

/**
 * 账号绑定页面
 */
public class AccountBindActivity extends BaseActivity {

    @BindView(R.id.tv_phonenum)
    TextView tvPhonenum;
    @BindView(R.id.tv_weixin_bind_tip)
    TextView tvWeixinBindTip;
    @BindView(R.id.tv_qq_bind_tip)
    TextView tvQqBindTip;

    @BindView(R.id.iv_qq_right)
    ImageView iv_qq_right;
    @BindView(R.id.iv_we_chat_right)
    ImageView iv_we_chat_right;

    //QQ绑定
    private boolean isQQBinded;
    private String nicknameQQ;
    //微信绑定
    private boolean isWeixinBinded;
    private String nicknameWeChat;

    @Override
    protected int setContentView() {
        return R.layout.activity_account_bind;
    }

    @Override
    protected String settingTitle() {
        return "账号绑定设置";
    }

    @Override
    protected void onInitView() {
//注册事件
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //注销事件
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onInitData() {
        //查询绑定平台
        queryBindPlatform(true);

        //手机号
        String phoneNum = UCManager.getInstance().getPhoneNum();
        if (TextUtils.isEmpty(phoneNum)) {
            //获取用户手机号
            getUserPhoneNum();
        } else {
            tvPhonenum.setText(Dictionary.Area_Code_86 + " " + phoneNum);
        }
    }

    /**
     * 查询绑定平台
     */
    private void queryBindPlatform(final boolean showLoading) {
        if (showLoading) {
            LoadingView.getInstance().show(this, httpTag);
        }
        DataRequestService.getInstance().getThirdBindPlatform(new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                if (showLoading) {
                    onFailureDefault(e);
                }
            }

            @Override
            public void onResponse(Object obj) {
                if (showLoading) {
                    LoadingView.getInstance().dismiss();
                }
                try {
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    ThirdPlatformAccountRoot root = InjectionWrapperUtil.injectMap(dataMap, ThirdPlatformAccountRoot.class);

                    List<ThirdPlatformAccount> dataList = root.getItems();

                    //空数据处理
                    if (ArrayListUtil.isEmpty(dataList)) {
                        throw new QSCustomException("无绑定项");
                    }

                    //遍历已绑定平台
                    for (ThirdPlatformAccount account : dataList) {
                        String platStr = account.getPlat_source();

                        if ("qq".equals(platStr)) {
                            isQQBinded = true;
                            nicknameQQ = account.getNickname();

                        } else if ("weixin".equals(platStr)) {
                            isWeixinBinded = true;
                            nicknameWeChat = account.getNickname();
                        }
                    }

                    //刷新绑定信息视图
                    refreshBindInfoView();

                } catch (Exception e) {
                    e.printStackTrace();
                    isQQBinded = false;
                    isWeixinBinded = false;
                    nicknameQQ = "";
                    nicknameWeChat = "";
                    //刷新绑定信息视图
                    refreshBindInfoView();
                }
            }
        }, httpTag, AccountBindActivity.this);

    }

    /**
     * 刷新绑定信息视图
     */
    private void refreshBindInfoView() {
        //QQ绑定信息
        if (isQQBinded) {
            tvQqBindTip.setText(nicknameQQ);
            tvQqBindTip.setTextColor(this.getResources().getColor(android.R.color.holo_red_dark));
            iv_qq_right.setVisibility(View.INVISIBLE);
        } else {
            tvQqBindTip.setText("尚未绑定");
            tvQqBindTip.setTextColor(this.getResources().getColor(R.color.color_898989));
        }

        //微信绑定信息
        if (isWeixinBinded) {
            tvWeixinBindTip.setText(nicknameWeChat);
            tvWeixinBindTip.setTextColor(this.getResources().getColor(android.R.color.holo_red_dark));
            iv_we_chat_right.setVisibility(View.INVISIBLE);
        } else {
            tvWeixinBindTip.setText("尚未绑定");
            tvWeixinBindTip.setTextColor(this.getResources().getColor(R.color.color_898989));
        }
    }


    @OnClick({R.id.rl_modify_password, R.id.ll_weixin_bind, R.id.ll_qq_bind})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //修改密码
            case R.id.rl_modify_password: {
                Intent intent = new Intent(AccountBindActivity.this, ModifyPasswordActivity.class);
                startActivity(intent);
                break;
            }
            //微信账号绑定
            case R.id.ll_weixin_bind: {
                if (isWeixinBinded) {
                   //解绑
//                    doUnbindWeixin();
                } else {
                    //绑定
                    doBindWeixin();
                }
                break;
            }
            //QQ账号绑定
            case R.id.ll_qq_bind: {
                if (isQQBinded) {
                    //解绑
//                    doUnbindQQ();
                } else {
                    //绑定
                    doBindQQ();
                }
                break;
            }
        }
    }

    /**
     * 解绑定QQ号
     */
    private void doUnbindQQ() {
        //QQ登录
        doQQLogin();
    }

    /**
     * 解绑定微信号
     */
    private void doUnbindWeixin() {
        //微信登录
        doWxLogin();
    }

    /**
     * 绑定QQ号
     */
    private void doBindQQ() {
        //QQ登录
        doQQLogin();
    }

    /**
     * 绑定微信号
     */
    private void doBindWeixin() {
        //微信登录
        doWxLogin();
    }


    /**
     * 请求第三方平台账号解绑
     * @param bindDto
     */
    private void doThirdPlatUnbind(final ThirdPlatBindDto bindDto) {
        LoadingView.getInstance().show(AccountBindActivity.this, httpTag);
        //解绑第三方平台账号
        DataRequestService.getInstance().postThirdPlatUnbind(bindDto, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                onFailureDefault(e);
            }

            @Override
            public void onResponse(Object obj) {
                ToastUtil.showShort(getApplication(), "解绑成功");

                //刷新绑定信息视图
                if (bindDto.getPlatSource().equals("qq")) {
                    isQQBinded = false;
                } else if (bindDto.getPlatSource().equals("weixin")) {
                    isWeixinBinded = false;
                }
                refreshBindInfoView();
            }
        }, httpTag, AccountBindActivity.this);
    }

    /**
     * 请求第三方平台账号绑定
     * @param bindDto
     */
    private void doThirdPlatBind(final ThirdPlatBindDto bindDto) {
        LoadingView.getInstance().show(AccountBindActivity.this, httpTag);
        //绑定第三方平台账号
        DataRequestService.getInstance().postThirdPlatBind(bindDto, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                onFailureDefault(e);
            }

            @Override
            public void onResponse(Object obj) {
                LoadingView.getInstance().dismiss();
                ToastUtil.showShort(getApplication(), "绑定成功");

                //刷新绑定信息视图
                if (bindDto.getPlatSource().equals("qq")) {
                    isQQBinded = true;
                } else if (bindDto.getPlatSource().equals("weixin")) {
                    isWeixinBinded = true;
                }
                refreshBindInfoView();
                //重新查询绑定平台，用于更新昵称
                queryBindPlatform(false);
            }
        }, httpTag, AccountBindActivity.this);
    }


    /**
     * QQ登录
     */
    private void doQQLogin() {
        if (mTencent == null) {
            mTencent = Tencent.createInstance(Constant.QQ_APP_ID,getApplication());
        }

        //检查网络
        if(!NetworkUtil.isConnectivity(AccountBindActivity.this)){
            ToastUtil.showShort(getApplication(),getResources().getString(R.string.network_no));
            return;
        }


        //开启通信等待提示
        LoadingView.getInstance().show(AccountBindActivity.this, httpTag);
        //请求登录（get_simple_userinfo、all）
        mTencent.login(AccountBindActivity.this, "get_simple_userinfo", loginListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //QQ在某些低端机上调用登录后，由于内存紧张导致APP被系统回收，登录成功后无法成功回传数据。//解决如下
        if (requestCode == Constants.REQUEST_LOGIN) {
            Tencent.onActivityResultData(requestCode,resultCode,data,loginListener);
        }
    }

    /**
     * 获取QQ token成功之后的处理
     * @param entity 微信token对象
     */
    private void doGetQqTokenComplete(QQTokenEntity entity){
        //第三方登入返回
        if(entity!=null){
            //请求数据
            ThirdPlatBindDto bindDto = new ThirdPlatBindDto();
            bindDto.setOpenId(entity.getOpenid());
            bindDto.setPlatSource(Dictionary.Plat_Source_QQ);
            bindDto.setThirdAccessToken(entity.getAccessToken());
            bindDto.setAppId(Constant.QQ_APP_ID);
            bindDto.setTenant(Dictionary.Tenant_CheersMind);//租户名
            if (isQQBinded) {
                //请求第三方账号解绑
//                doThirdPlatUnbind(bindDto);

            } else {
                //请求第三方账号绑定
                doThirdPlatBind(bindDto);
            }
        }
    }

    /**
     * QQ登录监听
     */
    IUiListener loginListener = new BaseUiListener() {
        @Override
        protected void doComplete(JSONObject values) {
            initOpenidAndToken(values);
//            updateUserInfo();
//            updateLoginButton();
            //解析QQ token对象
            Map dataMap = JsonUtil.fromJson(values.toString(), Map.class);
            QQTokenEntity entity = InjectionWrapperUtil.injectMap(dataMap, QQTokenEntity.class);
            //获取QQ token成功之后的处理
            doGetQqTokenComplete(entity);
        }
    };

    /**
     * 初始化QQ的token等信息
     * @param jsonObject
     */
    public static void initOpenidAndToken(JSONObject jsonObject) {
        try {
            String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
            String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
            String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires)
                    && !TextUtils.isEmpty(openId)) {
                mTencent.setAccessToken(token, expires);
                mTencent.setOpenId(openId);
            }
        } catch(Exception e) {
        }
    }

    /**
     * QQ登录监听器
     */
    private class BaseUiListener implements IUiListener {

        @Override
        public void onComplete(Object response) {
            //关闭通信等待提示
//            LoadingView.getInstance().dismiss();

            if (null == response) {
                ToastUtil.showShort(getApplication(), "登录失败");
                return;
            }
            JSONObject jsonResponse = (JSONObject) response;
            if (null != jsonResponse && jsonResponse.length() == 0) {
                ToastUtil.showShort(getApplication(), "未返回数据，登录失败");
                return;
            }

//            ToastUtil.showShort(getApplication(), "登录成功");
            doComplete(jsonResponse);
        }

        protected void doComplete(JSONObject values) {

        }

        @Override
        public void onError(UiError e) {
            //关闭通信等待提示
            LoadingView.getInstance().dismiss();
            ToastUtil.showShort(getApplication(), "QQ登录失败");
        }

        @Override
        public void onCancel() {
            //关闭通信等待提示
            LoadingView.getInstance().dismiss();
//            ToastUtil.showShort(getApplication(), "取消登录");
        }
    }


    /**
     * 微信登录
     */
    private void doWxLogin() {
        //创建微信api并注册到微信
        if (Constant.wx_api == null) {
            Constant.wx_api = WXAPIFactory.createWXAPI(AccountBindActivity.this, Constant.WX_APP_ID, true);
            Constant.wx_api.registerApp(Constant.WX_APP_ID);
        }

        //微信必须已经安装
        if(Constant.wx_api.isWXAppInstalled()){
            startWxLogin();
        }else{
            ToastUtil.showShort(getApplication(), "您还未安装微信客户端");
        }
    }

    /**
     * 发起微信登录请求
     */
    private void startWxLogin(){
        //检查网络
        if(!NetworkUtil.isConnectivity(AccountBindActivity.this)){
            ToastUtil.showShort(getApplication(),getResources().getString(R.string.network_no));
            return;
        }

        //微信必须已经安装
        if (!Constant.wx_api.isWXAppInstalled()) {
            ToastUtil.showShort(getApplication(), "您还未安装微信客户端");
            return;
        }
        //开启通信等待提示
        LoadingView.getInstance().show(AccountBindActivity.this, httpTag);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final SendAuth.Req req = new SendAuth.Req();
                req.scope = "snsapi_userinfo";
                req.state = "wechat_cheersmind_cheersgenie";
                Constant.wx_api.sendReq(req);
            }
        }).start();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWXTokenEntity(WXLoginEvent event) {
        if("error".equals(event.getCode())){
            //关闭通信等待提示
            LoadingView.getInstance().dismiss();
            ToastUtil.showShort(getApplication(),"微信授权失败！");
            return;
        }
        //成功
        if(event!=null && !TextUtils.isEmpty(event.getCode())){
            //获取微信token根据返回的code
            getWxToken(event.getCode());
        }
    }

    /**
     * 获取微信token根据code
     * @param code
     */
    private void getWxToken(String code){

        DataRequestService.getInstance().getWeChartToken(Constant.WX_APP_ID, Constant.WX_APP_SECTET, code, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        LoadingView.getInstance().dismiss();
                        ToastUtil.showShort(getApplication(), "微信授权失败..");
                    }
                });
            }

            @Override
            public void onResponse(Object obj) {
                try {
                    final String responseStr = obj.toString();

                    Map dataMap = JsonUtil.fromJson(responseStr, Map.class);
                    final WXTokenEntity entity = InjectionWrapperUtil.injectMap(dataMap, WXTokenEntity.class);
                    if (entity != null && !TextUtils.isEmpty(entity.getAccessToken())) {
                        //获取微信token成功之后的处理
                        doGetWxTokenComplete(entity);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(new QSCustomException("微信授权失败..."));
                }
            }
        }, httpTag, AccountBindActivity.this);
    }

    /**
     * 获取微信token成功之后的处理
     * @param entity 微信token对象
     */
    private void doGetWxTokenComplete(WXTokenEntity entity){
        //第三方登入返回
        if(entity!=null){
            //请求数据
            ThirdPlatBindDto bindDto = new ThirdPlatBindDto();
            bindDto.setOpenId(entity.getOpenid());//openId
            bindDto.setPlatSource(Dictionary.Plat_Source_Weixin);//平台名
            bindDto.setThirdAccessToken(entity.getAccessToken());//访问token
            bindDto.setTenant(Dictionary.Tenant_CheersMind);//租户名
            if (isWeixinBinded) {
                //请求第三方账号解绑
//                doThirdPlatUnbind(bindDto);

            } else {
                //请求第三方账号绑定
                doThirdPlatBind(bindDto);
            }
        }
    }


    /**
     * 获取用户的手机号
     */
    private void getUserPhoneNum () {
        LoadingView.getInstance().show(this, httpTag);

        DataRequestService.getInstance().getUserPhoneNum(new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                onFailureDefault(e);
            }

            @Override
            public void onResponse(Object obj) {
                LoadingView.getInstance().dismiss();
                try {
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    String phoneNum = (String) dataMap.get("mobile");
                    if (!TextUtils.isEmpty(phoneNum)) {
                        tvPhonenum.setText(Dictionary.Area_Code_86 + " " + phoneNum);
                        //设置手机号
                        UCManager.getInstance().setPhoneNum(phoneNum);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, httpTag, AccountBindActivity.this);
    }

}

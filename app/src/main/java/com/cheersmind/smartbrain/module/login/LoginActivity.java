package com.cheersmind.smartbrain.module.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.cheersmind.smartbrain.R;
import com.cheersmind.smartbrain.main.Exception.QSCustomException;
import com.cheersmind.smartbrain.main.activity.BaseActivity;
import com.cheersmind.smartbrain.main.activity.MainActivity;
import com.cheersmind.smartbrain.main.constant.Constant;
import com.cheersmind.smartbrain.main.constant.HttpConfig;
import com.cheersmind.smartbrain.main.dao.ChildInfoDao;
import com.cheersmind.smartbrain.main.entity.ChildInfoEntity;
import com.cheersmind.smartbrain.main.entity.ChildInfoRootEntity;
import com.cheersmind.smartbrain.main.entity.TopicInfoChildEntity;
import com.cheersmind.smartbrain.main.entity.TopicInfoEntity;
import com.cheersmind.smartbrain.main.entity.TopicRootEntity;
import com.cheersmind.smartbrain.main.entity.UserDetailsEntity;
import com.cheersmind.smartbrain.main.entity.WXTokenEntity;
import com.cheersmind.smartbrain.main.entity.WXUserInfoEntity;
import com.cheersmind.smartbrain.main.event.WXLoginEvent;
import com.cheersmind.smartbrain.main.service.BaseService;
import com.cheersmind.smartbrain.main.service.DataRequestService;
import com.cheersmind.smartbrain.main.util.InjectionWrapperUtil;
import com.cheersmind.smartbrain.main.util.JsonUtil;
import com.cheersmind.smartbrain.main.util.LogUtils;
import com.cheersmind.smartbrain.main.util.ToastUtil;
import com.cheersmind.smartbrain.main.view.LoadingView;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    public static final int USER_REQUEST_CODE = 1111;

    private RelativeLayout rtAuto;
    private ScrollView scvLogin;

    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private TextView tvInvitation;
    private TextView tvLicense;
    private TextView tvLicenseWx;

    private ImageView ivWxLogin;
    private ImageView ivUserLogin;

    private LinearLayout llWx;
    private LinearLayout llUser;

//    public static WXTokenEntity tokenData ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        BaseActivity.setTransparentStatusBar(LoginActivity.this,getResources().getColor(R.color.color_text_white));

        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //创建微信api并注册到微信
        Constant.wx_api = WXAPIFactory.createWXAPI(LoginActivity.this, Constant.WX_APP_ID, true);
        Constant.wx_api.registerApp(Constant.WX_APP_ID);

        EventBus.getDefault().register(this);

        initView();

        //邀请码验证注册返回
        if(getIntent().getBooleanExtra("from_invate",false)){
            login();
            return;
        }

        canAutoLogin();

    }

    private void initView(){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

        rtAuto = (RelativeLayout)findViewById(R.id.rt_auto);
        scvLogin = (ScrollView)findViewById(R.id.scv_login);
        etUsername = (EditText) findViewById(R.id.et_username);
        etPassword = (EditText) findViewById(R.id.et_password);
        String userNameLocal = pref.getString("user_name","");
        String passwordLocal = pref.getString("user_password","");
        etUsername.setText(userNameLocal);
        if (!userNameLocal.isEmpty()) {
            etUsername.setSelection(userNameLocal.length());//将光标移至文字末尾
        }
        etPassword.setText(passwordLocal);

        btnLogin = (Button) findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(this);
        if(!TextUtils.isEmpty(userNameLocal) && !TextUtils.isEmpty(passwordLocal)){
            btnLogin.setBackgroundResource(R.mipmap.login_new_btn_select);
        }

        etUsername.addTextChangedListener(new MyTextWatcher());
        etPassword.addTextChangedListener(new MyTextWatcher());

        tvInvitation = (TextView)findViewById(R.id.tv_invitation);
        tvLicense = (TextView)findViewById(R.id.tv_license);
        tvInvitation.setOnClickListener(this);
        tvLicense.setOnClickListener(this);

        tvLicenseWx = (TextView)findViewById(R.id.tv_license_wx);
        tvLicenseWx.setOnClickListener(this);

        ivWxLogin = (ImageView)findViewById(R.id.iv_wx_login);
        ivWxLogin.setOnClickListener(this);

        ivUserLogin = (ImageView)findViewById(R.id.iv_user_login);
        ivUserLogin.setOnClickListener(this);

        llWx = (LinearLayout)findViewById(R.id.ll_wx);
        llUser = (LinearLayout)findViewById(R.id.ll_user);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWXTokenEntity(WXLoginEvent event) {
        LogUtils.w("WXTest","onEvent微信登入成功回调返回login");
        if(event!=null && !TextUtils.isEmpty(event.getCode())){
            getwxToken(event.getCode());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        if (v == btnLogin) {
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();
            if (username.length()==0) {
                Toast.makeText(this,"用户名不能为空",Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.length()==0) {
                Toast.makeText(this,"密码不能为空",Toast.LENGTH_SHORT).show();
                return;
            }

            login();
        }else if(v == tvInvitation){
            startActivity(new Intent(LoginActivity.this,InvitationCodeActivity.class));
//            startActivity(new Intent(LoginActivity.this,UserRegisterActivity.class));
        }else if(v == tvLicense || v == tvLicenseWx){
            startActivity(new Intent(LoginActivity.this,UserLicenseActivity.class));
        }else if(v == ivWxLogin){
            if(Constant.wx_api.isWXAppInstalled()){
                LogUtils.w("wxtest","已经安装微信客户端");
                startWxLogin();
            }else{
                Toast.makeText(LoginActivity.this, "您还未安装微信客户端",
                        Toast.LENGTH_SHORT).show();
            }
//            testToken();
        }else if(v == ivUserLogin){
            updateLoginView(false);
        }
    }

    private void updateLoginView(boolean isShowWx){
        if(isShowWx){
            llWx.setVisibility(View.VISIBLE);
            llUser.setVisibility(View.GONE);
        }else{
            llUser.setVisibility(View.VISIBLE);
            llWx.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if(llUser.getVisibility() == View.VISIBLE){
                updateLoginView(true);
                return true;
            }
        }
        return super.onKeyDown(keyCode,event);
    }

    private void hiddenSoft(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void login () {
        hiddenSoft();
        LoadingView.getInstance().show(this);
        String loginName = etUsername.getText().toString();
        String pwd = etPassword.getText().toString();
        UserService.logon(loginName, pwd, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {

                LoadingView.getInstance().dismiss();
                if(!TextUtils.isEmpty(e.getMessage()) && e.getMessage().contains("Failed to connect")){
                    Toast.makeText(LoginActivity.this, "网络连接有误！", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(LoginActivity.this, "账号或密码输入有误！", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onResponse(Object obj) {
                getUserInfo();
                saveUserAccount(etUsername.getText().toString(),etPassword.getText().toString());
                long userId = UCManager.getInstance().getUserId();
                Log.i("LoginActivity","userId:"+String.valueOf(userId));
                //当用户使用自有账号登录时，可以这样统计：
                MobclickAgent.onProfileSignIn(String.valueOf(userId));
                //当用户使用第三方账号（如新浪微博）登录时，可以这样统计：
//                MobclickAgent.onProfileSignIn("WB", "userID");
            }
        });
    }

    private void getUserInfo () {
        UserService.getUserInfo(new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                LoadingView.getInstance().dismiss();
            }

            @Override
            public void onResponse(Object obj) {
//                Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
//                UserInfoEntity childData = InjectionWrapperUtil.injectMap(dataMap, UserInfoEntity.class);

                JSONObject objData = (JSONObject)obj;
                try {
                    JSONObject obje = objData.getJSONObject("org_exinfo");
                    String realName = obje.getString("real_name");
                    UCManager.getInstance().setRealName(realName);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                WXUserInfoEntity wxUserInfoEntity = new WXUserInfoEntity();
                wxUserInfoEntity.setAccessToken(UCManager.getInstance().getAcccessToken());
                wxUserInfoEntity.setUserId(UCManager.getInstance().getUserId());
                wxUserInfoEntity.setMacKey(UCManager.getInstance().getMacKey());
                wxUserInfoEntity.setRefreshToken(UCManager.getInstance().getRefreshToken());
                wxUserInfoEntity.setSysTimeMill(System.currentTimeMillis());
                DataSupport.deleteAll(WXUserInfoEntity.class);
                wxUserInfoEntity.save();
                getChildList();
//                ChildInfoEntity entity = ChildInfoDao.getDefaultChild();
//                if (entity==null) {
//                    getChildList();
//                } else {
//                    goMainPage();
//                }
            }
        });
    }

    private void getChildList() {
        UserService.getChildList(new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                LoadingView.getInstance().dismiss();
                Toast.makeText(LoginActivity.this,"获取孩子列表失败",Toast.LENGTH_SHORT).show();
                rtAuto.setVisibility(View.GONE);
                scvLogin.setVisibility(View.VISIBLE);
            }

            @Override
            public void onResponse(Object obj) {
                LoadingView.getInstance().dismiss();
                ChildInfoDao.deleteAllChild();
                if(obj != null){
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    ChildInfoRootEntity childData = InjectionWrapperUtil.injectMap(dataMap, ChildInfoRootEntity.class);
                    if(childData != null && childData.getItems()!=null){
                        List<ChildInfoEntity> childList = childData.getItems();
                        if(childList.size()==0){
                            Toast.makeText(LoginActivity.this,"孩子列表为空",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        for(int i=0;i<childList.size();i++){
                            ChildInfoEntity entity = childList.get(i);
                            if(i==0){
                                entity.setDefaultChild(true);
                            }else{
                                entity.setDefaultChild(false);
                            }
                            UCManager.getInstance().setDefaultChild(entity);
                            entity.save();
                        }
                        goMainPage();
                    }else{
                        Toast.makeText(LoginActivity.this,"获取孩子列表失败",Toast.LENGTH_SHORT).show();
                        return;
                    }

                }


            }
        });
    }

    private void goMainPage () {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("userName_cache", etUsername.getText().toString());
        editor.putString("password_cache", etPassword.getText().toString());
        editor.commit();

        //缓存基础主题列表数据
        LoadingView.getInstance().show(LoginActivity.this);
        DataRequestService.getInstance().loadChildTopicList(ChildInfoDao.getDefaultChildId(),0, 100, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                rtAuto.setVisibility(View.GONE);
                scvLogin.setVisibility(View.VISIBLE);
                LoadingView.getInstance().dismiss();
                ToastUtil.showShort(LoginActivity.this,"获取主题列表数据失败");
            }

            @Override
            public void onResponse(Object obj) {
                LoadingView.getInstance().dismiss();
                DataSupport.deleteAll(TopicInfoEntity.class);
                if(obj!=null){
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    TopicRootEntity topicData = InjectionWrapperUtil.injectMap(dataMap, TopicRootEntity.class);
                    if(topicData != null && topicData.getItems()!=null){
                        DataSupport.saveAll(topicData.getItems());
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class); // 从启动动画ui跳转到主ui
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(LoginActivity.this,"获取主题列表数据失败",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(LoginActivity.this,"获取主题列表数据失败",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    //是否已经关注过
    private boolean isFollowed(List<TopicInfoEntity> list){
        for(int i=0;i<list.size();i++){
            TopicInfoChildEntity entity= list.get(i).getChildTopic();
            if(entity!=null && entity.isFollowed()){
                return true;
            }

        }
        return false;
    }

    class MyTextWatcher implements TextWatcher{

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String userName = etUsername.getText().toString();
            String password = etPassword.getText().toString();
            if(TextUtils.isEmpty(userName) || TextUtils.isEmpty(password)){
                btnLogin.setBackgroundResource(R.mipmap.login_new_btn_nor);
            }else{
                btnLogin.setBackgroundResource(R.mipmap.login_new_btn_select);
            }
        }
    }

    private void saveUserAccount(String userName,String password){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("user_name", userName);
        editor.putString("user_password", password);
        editor.commit();
    }

    private void startWxLogin(){
        if (!Constant.wx_api.isWXAppInstalled()) {
            Toast.makeText(LoginActivity.this,"您还未安装微信客户端",Toast.LENGTH_SHORT).show();
            return;
        }
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_cheersmind_smartbrain";
        Constant.wx_api.sendReq(req);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == USER_REQUEST_CODE){
            //注册界面返回
            LogUtils.w("WXTest", "注册界面返回，获取孩子列表，跳转主页");
            finish();
//            getChildList();
        }
    }

    private void testToken(){
        WXTokenEntity dd = new WXTokenEntity();
        dd.setOpenid("ouyg81Yl8s3kCXyCl0Eb_yxsxpbM");
        dd.setAccessToken("9_IzWISC-kNGb4xMDO1yj26MnncbIOyHXw009we6R9RCvWksfi8CtrvYvwhimAOXECk-rPdCgeN_ZNQqSxwgxa_XWQTPKNBd8xgf5ndCmLTq4");
        wxLoginBack(dd);
    }

    private void wxLoginBack(WXTokenEntity data){
        //第三方登入返回
        LogUtils.w("WXTest", "第三方登入返回");
        if(data != null){
            final WXTokenEntity entity = data;

            if(entity!=null){
                String url = HttpConfig.URL_UC_THIRD_LOGIN;
                LogUtils.w("WXTest", "uc第三方登入url:"+url);
                Map<String,Object> map = new HashMap<>();
                map.put("open_id",entity.getOpenid());
                map.put("plat_source","weixin");
                map.put("third_access_token",entity.getAccessToken());
                map.put("tenant","CHEERSMIND");

                LoadingView.getInstance().show(LoginActivity.this);
                BaseService.post(url, map, false, new BaseService.ServiceCallback() {
                    @Override
                    public void onFailure(QSCustomException e) {
                        LoadingView.getInstance().dismiss();
                        LogUtils.w("WXTest", "获取业务端token失败");
                        //没有在业务端注册
                        Intent intent = new Intent(LoginActivity.this,UserRegisterActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("wx_token_data",entity);
                        intent.putExtras(bundle);
                        startActivityForResult(intent,USER_REQUEST_CODE);
                        LogUtils.w("WXTest", "还没有在业务端注册，去注册2~~");
                    }

                    @Override
                    public void onResponse(Object obj) {
                        LoadingView.getInstance().dismiss();
                        LogUtils.w("WXTest", "获取业务端token成功");
                        if(obj != null){
                            Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                            final WXUserInfoEntity wxUserInfoEntity = InjectionWrapperUtil.injectMap(dataMap, WXUserInfoEntity.class);
                            UCManager.getInstance().setAcccessToken(wxUserInfoEntity.getAccessToken());
                            UCManager.getInstance().setMacKey(wxUserInfoEntity.getMacKey());
                            UCManager.getInstance().setRefreshToken(wxUserInfoEntity.getRefreshToken());
                            UCManager.getInstance().setUserId(wxUserInfoEntity.getUserId());
//                            DataSupport.deleteAll(WXUserInfoEntity.class);
//                            wxUserInfoEntity.save();
                            if(entity!=null){
                                DataRequestService.getInstance().getUserDetails(new BaseService.ServiceCallback() {
                                    @Override
                                    public void onFailure(QSCustomException e) {
                                        LogUtils.w("WXTest", "获取用户信息失败");

                                        //没有在业务端注册
                                        Intent intent = new Intent(LoginActivity.this,UserRegisterActivity.class);
                                        Bundle bundle = new Bundle();
//                                                bundle.putSerializable("token_data",userData);
                                        bundle.putSerializable("wx_token_data",entity);
                                        intent.putExtras(bundle);
                                        startActivityForResult(intent,USER_REQUEST_CODE);
                                        LogUtils.w("WXTest", "还没有在业务端注册，去注册2~~");
                                    }

                                    @Override
                                    public void onResponse(Object obj) {
                                        LogUtils.w("WXTest", "获取用户信息成功");
                                        if(obj != null){
                                            Map dataUserMap = JsonUtil.fromJson(obj.toString(),Map.class);
                                            final UserDetailsEntity userData = InjectionWrapperUtil.injectMap(dataUserMap,UserDetailsEntity.class);
                                            if(userData!=null && userData.getUserId()!=0){
                                                //已经在业务端注册
                                                UCManager.getInstance().setUserId(userData.getUserId());
                                                wxUserInfoEntity.setUserId(userData.getUserId());
                                                wxUserInfoEntity.setSysTimeMill(System.currentTimeMillis());
                                                wxUserInfoEntity.save();
                                                getChildList();
                                                LogUtils.w("WXTest", "已经在业务端注册，跳转主页");
                                            }else{
                                                //没有在业务端注册
                                                Intent intent = new Intent(LoginActivity.this,UserRegisterActivity.class);
                                                Bundle bundle = new Bundle();
//                                                bundle.putSerializable("token_data",userData);
                                                bundle.putSerializable("wx_token_data",entity);
                                                intent.putExtras(bundle);
                                                startActivityForResult(intent,USER_REQUEST_CODE);
                                                LogUtils.w("WXTest", "还没有在业务端注册，去注册~~");
                                            }
                                        }
                                    }
                                });
                            }

                        }
                    }
                });
            }
        }
    }

    private void canAutoLogin(){
        WXUserInfoEntity wxUserInfoEntity  = DataSupport.findFirst(WXUserInfoEntity.class);
        if(wxUserInfoEntity!=null){

            long curTime = System.currentTimeMillis() - wxUserInfoEntity.getSysTimeMill();
            long t = 1000 * 60 * 60 * 24 * 7;//6天
            if(curTime < t){
                //缓存token有效,自动登录
                UCManager.getInstance().setAcccessToken(wxUserInfoEntity.getAccessToken());
                UCManager.getInstance().setMacKey(wxUserInfoEntity.getMacKey());
                UCManager.getInstance().setRefreshToken(wxUserInfoEntity.getRefreshToken());
                UCManager.getInstance().setUserId(wxUserInfoEntity.getUserId());
                getChildList();
            }else{
                //重新请求token
                DataSupport.deleteAll(WXUserInfoEntity.class);
                LogUtils.w("wxtest","重新请求token1");
                rtAuto.setVisibility(View.GONE);
                scvLogin.setVisibility(View.VISIBLE);
            }
        }else{
            //重新请求token
            DataSupport.deleteAll(WXUserInfoEntity.class);
            LogUtils.w("wxtest","重新请求token2");
            rtAuto.setVisibility(View.GONE);
            scvLogin.setVisibility(View.VISIBLE);
        }
    }

    private void getwxToken(String code){
        String url = HttpConfig.URL_WX_GET_TOKEN
                .replace("{appid}", Constant.WX_APP_ID)
                .replace("{secret}", Constant.WX_APP_SECTET)
                .replace("{code}", code);

        LoadingView.getInstance().show(LoginActivity.this);
        OkHttpClient client = new OkHttpClient();
        //构造Request对象
        //采用建造者模式，链式调用指明进行Get请求,传入Get的请求地址
        Request request = new Request.Builder().get().url(url).build();
        Call call = client.newCall(request);
        //异步调用并设置回调函数
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtils.w("WXTest", "获取微信token失败");
                LoadingView.getInstance().dismiss();
                finish();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                LoadingView.getInstance().dismiss();
                final String responseStr = response.body().string();
                LogUtils.w("WXTest", "获取微信token成功");

                Map dataMap = JsonUtil.fromJson(responseStr, Map.class);
                final WXTokenEntity entity = InjectionWrapperUtil.injectMap(dataMap, WXTokenEntity.class);
                if (entity != null && !TextUtils.isEmpty(entity.getAccessToken())) {
                    LogUtils.w("WXTest", "token:" + entity.getAccessToken());
                    wxLoginBack(entity);
                }
            }
        });
    }

}

package com.cheersmind.smartbrain.module.login;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cheersmind.smartbrain.R;
import com.cheersmind.smartbrain.main.Exception.QSCustomException;
import com.cheersmind.smartbrain.main.activity.BaseActivity;
import com.cheersmind.smartbrain.main.constant.HttpConfig;
import com.cheersmind.smartbrain.main.entity.ErrorCodeEntity;
import com.cheersmind.smartbrain.main.entity.WXTokenEntity;
import com.cheersmind.smartbrain.main.entity.WXUserInfoEntity;
import com.cheersmind.smartbrain.main.helper.AutoLoginHelper;
import com.cheersmind.smartbrain.main.service.BaseService;
import com.cheersmind.smartbrain.main.util.InjectionWrapperUtil;
import com.cheersmind.smartbrain.main.util.JsonUtil;
import com.cheersmind.smartbrain.main.util.ToastUtil;
import com.cheersmind.smartbrain.main.view.LoadingView;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static android.app.AlertDialog.THEME_HOLO_LIGHT;

/**
 * Created by Administrator on 2018/4/14.
 */

public class UserRegisterActivity extends BaseActivity implements View.OnClickListener{

    private EditText etClassNum;
    private EditText etUserName;
    private EditText etBirthday;
    private RadioGroup rgSex;
    private RadioButton rbMan;
    private RadioButton rbWoman;

    private TextView tvClassGroup;

    private Button btnRegister;
    private ImageView ivTimeSelect;

//    private UserDetailsEntity userDetailsEntity;
    private WXTokenEntity wxTokenEntity;

    private int sexData = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_register);
        getSupportActionBar().show();
        setTitle("用户信息注册");

        initView();
        initData();
    }

    private void initView(){
        etClassNum = (EditText)findViewById(R.id.et_class_num);
        etUserName = (EditText)findViewById(R.id.et_user_name);
        etBirthday = (EditText)findViewById(R.id.et_birthday);

        rgSex = (RadioGroup)findViewById(R.id.rg_sex);
        rbMan = (RadioButton)findViewById(R.id.rb_man);
        rbWoman = (RadioButton)findViewById(R.id.rb_woman);

        rgSex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.rb_man){
                    sexData = 1;
                }else{
                    sexData = 2;
                }
            }
        });

        btnRegister = (Button)findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(this);

        ivTimeSelect = (ImageView)findViewById(R.id.iv_time_select);
        ivTimeSelect.setOnClickListener(this);

        tvClassGroup = (TextView)findViewById(R.id.tv_class_group);
        tvClassGroup.setOnClickListener(this);
    }

    private void initData(){
        if(getIntent()!=null & getIntent().getExtras()!=null){
//            userDetailsEntity = (UserDetailsEntity)getIntent().getExtras().getSerializable("token_data");
            wxTokenEntity = (WXTokenEntity) getIntent().getExtras().getSerializable("wx_token_data");

        }
    }

    @Override
    public void onClick(View v) {
        if(v == tvClassGroup){
            startActivity(new Intent(UserRegisterActivity.this,ClassGroupActivity.class));
        } else if(v == ivTimeSelect){
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            new DatePickerDialog(this,THEME_HOLO_LIGHT,new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    String str = year + "-" + (monthOfYear+1) + "-" + dayOfMonth;
                    etBirthday.setText(str);
                }
            },year,month,day).show();

        }else if(v == btnRegister){
            if(checkRegisterData()){
                String url = HttpConfig.URL_UC_REGISTER;
                Map<String,Object> map = new HashMap<>();
                map.put("open_id",wxTokenEntity.getOpenid());
                map.put("plat_source","weixin");
                map.put("third_access_token",wxTokenEntity.getAccessToken());
                map.put("real_name",etUserName.getText());
                map.put("birthday",etBirthday.getText());
                map.put("group_no",etClassNum.getText());
                map.put("sex",sexData);

                Log.i("WXTest", "注册url:" + url);

                Log.i("WXTest", "注册url参数:"+ wxTokenEntity.getOpenid() + "/"
                        + wxTokenEntity.getAccessToken() + "/"
                        + "/" + etBirthday.getText()
                        + "/" + etUserName.getText()
                        + "/" + etClassNum.getText());

                LoadingView.getInstance().show(UserRegisterActivity.this);
                BaseService.post(url, map, false, new BaseService.ServiceCallback() {
                    @Override
                    public void onFailure(QSCustomException e) {
                        if(!TextUtils.isEmpty(e.getMessage())){
                            try{
                                String bodyStr = e.getMessage();
                                Map map = JsonUtil.fromJson(bodyStr,Map.class);
                                ErrorCodeEntity errorCodeEntity = InjectionWrapperUtil.injectMap(map,ErrorCodeEntity.class);
                                if(errorCodeEntity!=null){
                                    String message = errorCodeEntity.getMessage();
                                    if(TextUtils.isEmpty(message)){
                                        ToastUtil.showShort(UserRegisterActivity.this,getResources().getString(R.string.error_code_common_text));
                                    }else{
                                        ToastUtil.showShort(UserRegisterActivity.this,message);
                                    }
                                }else{
                                    ToastUtil.showShort(UserRegisterActivity.this,getResources().getString(R.string.error_code_common_text));
                                }
                            }catch (Exception err){
                                if(TextUtils.isEmpty(e.getMessage())){
                                    ToastUtil.showShort(UserRegisterActivity.this,getResources().getString(R.string.error_code_common_text));
                                }else{
                                    ToastUtil.showShort(UserRegisterActivity.this,e.getMessage());
                                }
                            }
                        }else{
                            ToastUtil.showShort(UserRegisterActivity.this,getResources().getString(R.string.error_code_common_text));
                        }

                    }

                    @Override
                    public void onResponse(Object obj) {
                        LoadingView.getInstance().dismiss();
                        Log.i("WXTest", "注册成功！");
                        Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                        WXUserInfoEntity wxUserInfoEntity = InjectionWrapperUtil.injectMap(dataMap, WXUserInfoEntity.class);

                        if(wxUserInfoEntity != null){
                            UCManager.getInstance().setAcccessToken(wxUserInfoEntity.getAccessToken());
                            UCManager.getInstance().setMacKey(wxUserInfoEntity.getMacKey());
                            UCManager.getInstance().setRefreshToken(wxUserInfoEntity.getRefreshToken());
                            UCManager.getInstance().setUserId(wxUserInfoEntity.getUserId());

                            wxUserInfoEntity.setSysTimeMill(System.currentTimeMillis());
                            wxUserInfoEntity.save();
//                            Log.i("WXTest", "注册成功token:"+ wxUserInfoEntity.getAccessToken());
//                            Log.i("WXTest", "注册成功token:"+ wxUserInfoEntity.getMacKey());
//                            Log.i("WXTest", "注册成功token:"+ wxUserInfoEntity.getRefreshToken());
//                            Log.i("WXTest", "注册成功token:"+ wxUserInfoEntity.getUserId());
//                            finish();
                            AutoLoginHelper.getInstance(UserRegisterActivity.this).getChildList();
                        }else{
                            Toast.makeText(UserRegisterActivity.this,"token为空",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

    private boolean checkRegisterData(){
        if(TextUtils.isEmpty(etClassNum.getText())){
            Toast.makeText(UserRegisterActivity.this,"班级群号不能为空！",Toast.LENGTH_SHORT).show();
            return false;
        }

        if(TextUtils.isEmpty(etUserName.getText())){
            Toast.makeText(UserRegisterActivity.this,"姓名不能为空！",Toast.LENGTH_SHORT).show();
            return false;
        }

        if(TextUtils.isEmpty(etBirthday.getText())){
            Toast.makeText(UserRegisterActivity.this,"生日不能为空！",Toast.LENGTH_SHORT).show();
            return false;
        }

        if(wxTokenEntity == null){
            Toast.makeText(UserRegisterActivity.this,"微信token有误！",Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;

    }

}

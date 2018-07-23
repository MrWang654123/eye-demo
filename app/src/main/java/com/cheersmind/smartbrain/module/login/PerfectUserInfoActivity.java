package com.cheersmind.smartbrain.module.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cheersmind.smartbrain.R;
import com.cheersmind.smartbrain.main.Exception.QSCustomException;
import com.cheersmind.smartbrain.main.activity.BaseActivity;
import com.cheersmind.smartbrain.main.constant.HttpConfig;
import com.cheersmind.smartbrain.main.service.BaseService;
import com.cheersmind.smartbrain.main.view.LoadingView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/9/25.
 */

public class PerfectUserInfoActivity extends BaseActivity implements View.OnClickListener{

    private EditText etUser;
    private EditText etPass;
    private EditText etPassSecond;

    private Button btnLogin;

    private String inviteCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("完善用户信息");
        setContentView(R.layout.activity_perfect_userinfo);
        getSupportActionBar().show();
        inviteCode = getIntent().getStringExtra("invite_code");
        if(TextUtils.isEmpty(inviteCode)){
            inviteCode = "";
        }
        initView();
    }

    private void initView(){
        etUser = (EditText)findViewById(R.id.et_username);
        etPass = (EditText)findViewById(R.id.et_pass);
        etPassSecond = (EditText)findViewById(R.id.et_pass_second);
        btnLogin = (Button)findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(this);

        etUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s!=null && s.length()>0){
                    char ch = s.charAt(0);
                    if(ch>='A' && ch<='Z'  ||  ch>='a' && ch<='z'){

                    }else{
                        Toast.makeText(PerfectUserInfoActivity.this,"账户必须字母开头",Toast.LENGTH_SHORT).show();
                        etUser.setText("");
                    }
                }

            }
        });
    }

    private boolean checkLogin(){
        if(TextUtils.isEmpty(etUser.getText().toString())){
            Toast.makeText(this,"用户名不能为空",Toast.LENGTH_SHORT).show();
            return false;
        }

        if(etUser.getText().toString().length()<6 || etUser.getText().toString().length()>20){
            Toast.makeText(this,"用户名长度为6-20位",Toast.LENGTH_SHORT).show();
            return false;
        }

        if(TextUtils.isEmpty(etPass.getText().toString())){
            Toast.makeText(this,"密码不能为空",Toast.LENGTH_SHORT).show();
            return false;
        }

        if(TextUtils.isEmpty(etPassSecond.getText().toString())){
            Toast.makeText(this,"重复密码不能为空",Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!etPass.getText().toString().equals(etPassSecond.getText().toString())){
            Toast.makeText(this,"两次密码输入不一致，请重新输入",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        if(v == btnLogin){
            if(checkLogin()){
                final String user = etUser.getText().toString();
                final String pass = etPass.getText().toString();
                String url = HttpConfig.URL_CODE_REGISTERS;
                Map<String,Object> params = new HashMap<>();
                params.put("invite_code",inviteCode);
                params.put("username",user);
                params.put("password",pass);
                params.put("org_code","481036560705");
                LoadingView.getInstance().show(this);
                BaseService.post(url,params,false,new BaseService.ServiceCallback() {
                    @Override
                    public void onFailure(QSCustomException e) {
                        e.printStackTrace();
                        LoadingView.getInstance().dismiss();
                        Toast.makeText(PerfectUserInfoActivity.this,"注册失败",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(Object obj) {
                        LoadingView.getInstance().dismiss();
                        if(obj!=null){
                            saveUserAccount(user,pass);
                            Intent intent = new Intent(PerfectUserInfoActivity.this,LoginActivity.class);
                            intent.putExtra("user_name",user);
                            intent.putExtra("password",pass);
                            intent.putExtra("from_invate",true);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
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
}

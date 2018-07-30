package com.cheersmind.smartbrain.module.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cheersmind.smartbrain.R;
import com.cheersmind.smartbrain.main.Exception.QSCustomException;
import com.cheersmind.smartbrain.main.activity.BaseActivity;
import com.cheersmind.smartbrain.main.constant.HttpConfig;
import com.cheersmind.smartbrain.main.entity.CommonPostResult;
import com.cheersmind.smartbrain.main.entity.ErrorCodeEntity;
import com.cheersmind.smartbrain.main.service.BaseService;
import com.cheersmind.smartbrain.main.util.InjectionWrapperUtil;
import com.cheersmind.smartbrain.main.util.JsonUtil;
import com.cheersmind.smartbrain.main.util.ToastUtil;
import com.cheersmind.smartbrain.main.view.CommonDialog;
import com.cheersmind.smartbrain.main.view.LoadingView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by gwb on 2017/9/25.
 */

public class InvitationCodeActivity extends BaseActivity implements View.OnClickListener{

    private EditText etInvitation;
    private TextView tvHow;
    private Button btnCheck;

    private CommonDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitation_code);
        getSupportActionBar().show();
        setTitle("邀请码验证");

        initView();
    }

    private void initView(){
        etInvitation = (EditText)findViewById(R.id.et_invitation);
        etInvitation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(TextUtils.isEmpty(etInvitation.getText().toString())){
                    btnCheck.setBackgroundResource(R.mipmap.qs_btn_login_null);
                }else{
                    btnCheck.setBackgroundResource(R.mipmap.qs_btn_login_select);
                }
            }
        });
        tvHow = (TextView)findViewById(R.id.tv_how);
        tvHow.setOnClickListener(this);
        btnCheck = (Button)findViewById(R.id.btn_check);
        btnCheck.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == tvHow){
            if(dialog == null){
                dialog = new CommonDialog(this,"关于邀请码",getResources().getString(R.string.how_to_invite_content),"","确定");
            }
            dialog.show();
        }else if(v == btnCheck){

            final String inviteCode = etInvitation.getText().toString();
            if(TextUtils.isEmpty(inviteCode)){
                return;
            }
            String url = HttpConfig.URL_CODE_INVATE;
            Map<String,Object> params = new HashMap<>();
            params.put("invite_code",inviteCode);
            LoadingView.getInstance().show(this);
            BaseService.post(url, params, false, new BaseService.ServiceCallback() {
                @Override
                public void onFailure(QSCustomException e) {
                    LoadingView.getInstance().dismiss();
                    if(!TextUtils.isEmpty(e.getMessage())){
                        try{
                            String bodyStr = e.getMessage();
                            Map map = JsonUtil.fromJson(bodyStr,Map.class);
                            ErrorCodeEntity errorCodeEntity = InjectionWrapperUtil.injectMap(map,ErrorCodeEntity.class);
                            if(errorCodeEntity!=null){
                                String message = errorCodeEntity.getMessage();
                                if(TextUtils.isEmpty(message)){
                                    ToastUtil.showShort(InvitationCodeActivity.this,getResources().getString(R.string.error_code_common_text));
                                }else{
                                    ToastUtil.showShort(InvitationCodeActivity.this,message);
                                }
                            }else{
                                ToastUtil.showShort(InvitationCodeActivity.this,getResources().getString(R.string.error_code_common_text));
                            }
                        }catch ( Exception err){
                            if(TextUtils.isEmpty(e.getMessage())){
                                ToastUtil.showShort(InvitationCodeActivity.this,getResources().getString(R.string.error_code_common_text));
                            }else{
                                ToastUtil.showShort(InvitationCodeActivity.this,e.getMessage());
                            }
                        }
                    }else{
                        ToastUtil.showShort(InvitationCodeActivity.this,getResources().getString(R.string.error_code_common_text));
                    }
                }

                @Override
                public void onResponse(Object obj) {
                    LoadingView.getInstance().dismiss();
                    Map dataMap = JsonUtil.fromJson(obj.toString(),Map.class);
                    CommonPostResult entity = InjectionWrapperUtil.injectMap(dataMap,CommonPostResult.class);

                    if(entity!=null && entity.isResult()){
                        Toast.makeText(InvitationCodeActivity.this,"验证成功",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(InvitationCodeActivity.this,PerfectUserInfoActivity.class);
                        intent.putExtra("invite_code",inviteCode);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(InvitationCodeActivity.this,"邀请码有误，请重新输入！",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}

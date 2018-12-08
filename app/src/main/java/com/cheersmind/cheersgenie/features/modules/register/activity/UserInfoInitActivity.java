package com.cheersmind.cheersgenie.features.modules.register.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.modules.base.activity.BaseActivity;
import com.cheersmind.cheersgenie.features.utils.DataCheckUtil;
import com.cheersmind.cheersgenie.features.utils.IntegralUtil;
import com.cheersmind.cheersgenie.features.utils.SoftInputUtil;
import com.cheersmind.cheersgenie.features.view.dialog.IntegralTipDialog;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.constant.HttpConfig;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.util.ToastUtil;
import com.cheersmind.cheersgenie.main.view.LoadingView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 用户信息初始化页面（注册）
 */
public class UserInfoInitActivity extends BaseActivity {

    //班级号
    private static final String CLASS_NUM = "class_num";
    //角色
    private static final String PARENT_ROLE = "PARENT_ROLE";

    //子标题
    @BindView(R.id.tv_sub_title)
    TextView tvSubTitle;
    //用户名
    @BindView(R.id.et_username)
    EditText etUsername;
    //用户名的清空按钮
    @BindView(R.id.iv_clear)
    ImageView ivClear;
    //昵称
    @BindView(R.id.et_nickname)
    EditText etNickname;
    //昵称的清空按钮
    @BindView(R.id.iv_clear_nickname)
    ImageView ivClearNickname;
    //生日显示
    @BindView(R.id.et_birthday)
    EditText etBirthday;
    //生日选择器触发图片
    @BindView(R.id.iv_time_select)
    ImageView ivTimeSelect;
    //性别单选框组
    @BindView(R.id.rg_sex)
    RadioGroup rgSex;
    //确认
    @BindView(R.id.btn_confirm)
    Button btnConfirm;

    //选择器的开始日期
    Calendar startDate;

    //班级号
    String classNum;
    //家长角色（默认自己）
    int parentRole = Dictionary.PARENT_ROLE_MYSELF;
    //性别（值）
    private String genderVal = "1";//男

    /**
     * 开启注册环节的用户信息完善页面
     * @param context
     * @param classNum 班级号
     */
    public static void startUserInfoInitActivity(Context context, String classNum, int parentRole) {
        Intent intent = new Intent(context, UserInfoInitActivity.class);
        Bundle extras = new Bundle();
        extras.putString(CLASS_NUM, classNum);
        extras.putInt(PARENT_ROLE, parentRole);
        intent.putExtras(extras);
        context.startActivity(intent);
    }

    @Override
    protected int setContentView() {
        return R.layout.activity_user_info_init;
    }

    @Override
    protected String settingTitle() {
        return "完善信息";
    }

    @Override
    protected void onInitView() {
        //用户名的文本输入监听
        etUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    if (ivClear.getVisibility() == View.INVISIBLE) {
                        ivClear.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (ivClear.getVisibility() == View.VISIBLE) {
                        ivClear.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });

        //昵称的文本输入监听
        etNickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    if (ivClearNickname.getVisibility() == View.INVISIBLE) {
                        ivClearNickname.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (ivClearNickname.getVisibility() == View.VISIBLE) {
                        ivClearNickname.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });

        rgSex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //男
                if(checkedId == R.id.rb_gender_male){
                    genderVal = "1";
                }else{
                    genderVal = "2";
                }
            }
        });
    }

    @Override
    protected void onInitData() {
        //选择器的开始日期
        Date date = getDateFromStr("1900-01-01", "yyyy-MM-dd");
        startDate = Calendar.getInstance();
        startDate.setTime(date);

        //班级号
        classNum = getIntent().getStringExtra(CLASS_NUM);
        if (TextUtils.isEmpty(classNum))  {
            ToastUtil.showShort(getApplicationContext(), "班级号不能为空");
            return;
        }

        //家长角色
        parentRole = getIntent().getIntExtra(PARENT_ROLE, Dictionary.PARENT_ROLE_MYSELF);

        //初始化子标题
        if (parentRole == Dictionary.PARENT_ROLE_MYSELF) {
            tvSubTitle.setText("个人信息");
        } else {
            tvSubTitle.setText("孩子信息");
        }
    }


    @OnClick({R.id.btn_confirm, R.id.iv_time_select, R.id.et_birthday, R.id.iv_clear, R.id.iv_clear_nickname})
    public void onViewClick(View view) {

        Calendar calendar1 = Calendar.getInstance();

        switch (view.getId()) {
            case R.id.iv_time_select:
            case R.id.et_birthday: {
                //隐藏软键盘
                SoftInputUtil.closeSoftInput(UserInfoInitActivity.this);
                Calendar calendar = Calendar.getInstance();
                if (etBirthday.getText().length() > 0) {
                    Date date = getDateFromStr(etBirthday.getText().toString(), "yyyy-MM-dd");
                    calendar.setTime(date);
                }
                showDate(calendar);
                break;
            }
            case R.id.btn_confirm:
                //下一步操作
                doNextDept();
                break;
            //用户名的清空按钮
            case R.id.iv_clear: {
                etUsername.setText("");
                etUsername.requestFocus();
                break;
            }
            //昵称的清空按钮
            case R.id.iv_clear_nickname: {
                etNickname.setText("");
                etNickname.requestFocus();
                break;
            }
        }
    }

    /**
     * 日期时间控件
     * @param selectedDate
     */
    private void showDate(Calendar selectedDate) {
        TimePickerView pvTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                String time = getTime(date);
                etBirthday.setText(time);
            }
        })
                .setType(new boolean[]{true, true, true, false, false, false})// 默认全部显示
                .setCancelText("取消")//取消按钮文字
                .setSubmitText("确定")//确认按钮文字
//                .setContentSize(18)//滚轮文字大小
//                .setTitleSize(20)//标题文字大小
//                //.setTitleText("Title")//标题文字
                .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示
                .isCyclic(true)//是否循环滚动
//                //.setTitleColor(Color.BLACK)//标题文字颜色
//                .setSubmitColor(Color.BLUE)//确定按钮文字颜色
//                .setCancelColor(Color.BLUE)//取消按钮文字颜色
//                //.setTitleBgColor(0xFF666666)//标题背景颜色 Night mode
//                .setBgColor(0xFF333333)//滚轮背景颜色 Night mode
                .setDate(selectedDate)// 如果不设置的话，默认是系统时间*/
                .setRangDate(startDate,Calendar.getInstance())//起始终止年月日设定
//                //.setLabel("年","月","日","时","分","秒")//默认设置为年月日时分秒
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                //.isDialog(true)//是否显示为对话框样式
                .build();

        pvTime.show();
    }

    /**
     * date转str
     * @param date
     * @return
     */
    private String getTime(Date date) {//可根据需要自行截取数据显示
        //"YYYY-MM-DD HH:MM:SS"        "yyyy-MM-dd"
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    /**
     * str转date
     * @param dateStr
     * @param formatStr
     * @return
     */
    private Date getDateFromStr(String dateStr, String formatStr) {
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        Date parse = null;
        try {
            parse = format.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return parse;
    }

    /**
     * 下一步操作：提交用户信息
     */
    private void doNextDept() {
        //提交用户信息
        submitUserInfoWrap();
    }

    /**
     * 提交用户信息
     */
    private void submitUserInfoWrap() {
        //隐藏软键盘
        SoftInputUtil.closeSoftInput(UserInfoInitActivity.this);
        //数据验证
        if (!checkData()) return;
        //提交用户信息
        String username = etUsername.getText().toString();
        String nickName = etNickname.getText().toString();
        String birthday = etBirthday.getText().toString();
        postUserInfo(classNum, genderVal, username, nickName, birthday, parentRole);
    }

    /**
     * 注册环节提交用户信息
     * @param classNum //班级群号
     * @param gender //孩子性别 1-男，2-女
     * @param username //姓名
     * @param nickName //昵称
     * @param birthday //孩子生日
//     * @param role //家长角色：1-父亲，2-母亲，3-爷爷/外公，4-奶奶/外婆  99其他
     */
    private void postUserInfo(String classNum, String gender, String username, String nickName, String birthday, int parentRole) {
        //通信等待提示
        LoadingView.getInstance().show(UserInfoInitActivity.this, httpTag);
        String url = HttpConfig.URL_REGISTER_SUBMIT_USERINFO;
//        {
//            "group_no":"string",        //班级群号
//                "sex":"1",                  //孩子性别 1-男，2-女
//                "name":"string",            //孩子名称
//                "birthday":"data",          //孩子生日
//                "parent_role":"int"         //家长角色：1-父亲，2-母亲，3-爷爷/外公，4-奶奶/外婆  99其他
//        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("group_no", classNum);
        map.put("sex", gender);
        map.put("name", username);
        map.put("nick_name", nickName);
        map.put("birthday", birthday);
        map.put("parent_role", parentRole);
        //注册环节提交用户信息
        BaseService.post(url,map, false, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                onFailureDefault(e);
            }

            @Override
            public void onResponse(Object obj) {
//                LoadingView.getInstance().dismiss();
                //积分提示
                IntegralUtil.showIntegralTipDialog(UserInfoInitActivity.this, obj, new IntegralTipDialog.OnOperationListener() {
                    @Override
                    public void onAnimationEnd() {
                        //提交完善信息成功后，获取孩子列表
                        doGetChildListWrap();
                    }
                });
            }
        }, httpTag);
    }



    /**
     * 验证数据格式
     * @return
     */
    private boolean checkData() {
        String username = etUsername.getText().toString();
        String nickName = etNickname.getText().toString();
        String birthday = etBirthday.getText().toString();

        //用户名非空
        if (TextUtils.isEmpty(username)) {
            ToastUtil.showShort(getApplicationContext(), "请输入姓名");
            return false;
        }

        //昵称非空
        if (TextUtils.isEmpty(nickName)) {
            ToastUtil.showShort(getApplicationContext(), "请输入昵称");
            return false;
        }
        //昵称格式验证
        if (!DataCheckUtil.isNickname(nickName)) {
            ToastUtil.showShort(getApplicationContext(), getResources().getString(R.string.nickname_format_error));
            return false;
        }

        //生日非空
        if (TextUtils.isEmpty(birthday)) {
            ToastUtil.showShort(getApplicationContext(), "请选择出生日期");
            return false;
        }

        return true;
    }



}


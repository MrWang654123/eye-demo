package com.cheersmind.cheersgenie.features.modules.register.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.modules.base.activity.BaseActivity;
import com.cheersmind.cheersgenie.features.utils.DataCheckUtil;
import com.cheersmind.cheersgenie.features.utils.SoftInputUtil;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.entity.ClassInfoEntity;
import com.cheersmind.cheersgenie.main.entity.ErrorCodeEntity;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.ToastUtil;
import com.cheersmind.cheersgenie.main.view.LoadingView;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 班级群号输入页面（注册）
 */
public class ClassNumActivity extends BaseActivity {

    @BindView(R.id.btn_confirm)
    Button btnConfirm;
    //班级号描述
    @BindView(R.id.tv_class_num_desc)
    TextView tvClassNumDesc;
    //班级信息
    @BindView(R.id.tv_class_info)
    TextView tvClassInfo;

    @BindViews({R.id.et_captcha_num1, R.id.et_captcha_num2, R.id.et_captcha_num3, R.id.et_captcha_num4,
            R.id.et_captcha_num5, R.id.et_captcha_num6, R.id.et_captcha_num7, R.id.et_captcha_num8})
    List<EditText> etCaptchaNumList;

    //当前输入索引
    int position = 0;

    //0宽度空格符（在edittext没有内容的时候，按软键盘的删除键,不触发TextWatcher）
    private static final String ZERO_WIDTH_SPACE = "\uFEFF";

    //班级信息
    private ClassInfoEntity classInfo;

    //班级号
    String classNum;

    @Override
    protected int setContentView() {
        return R.layout.activity_classnum;
    }

    @Override
    protected String settingTitle() {
        return "班级号输入";
    }

    @Override
    protected void onInitView() {
        //初始编辑框的焦点
        switchFocus(position);
        //初始化文本监听
        initEditTextListener();
    }

    @Override
    protected void onInitData() {

    }


    /**
     * 初始化数字编辑框监听
     */
    private void initEditTextListener() {
        for (int i = 0; i < etCaptchaNumList.size(); i++) {
            EditText et = etCaptchaNumList.get(i);
            //文本变化监听
            et.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    System.out.println(charSequence.toString());
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    int len = editable.toString().length();
                    //两个字符时（一个0宽度空格，一个数字），跳转到下一个编辑框
                    if (len == 2) {
                        char c = editable.charAt(1);
//                        if (TextUtils.isDigitsOnly(c + "")) {
                            //验证码输入结束的处理
                            if (position == etCaptchaNumList.size() - 1) {
                                //班级号输入完成后的处理
                                classNumInputComplete();
                            } else {
                                //跳转到下一个数字编辑框
                                toNextEdit();
                            }
//                        }

                    } else if (len == 0) {
                        //防止第一个编辑框的0宽度空格符被删除
                        if (position == 0) {
                            etCaptchaNumList.get(position).setText(ZERO_WIDTH_SPACE);
                            etCaptchaNumList.get(position).setSelection(1);
                        } else {
                            //回退到前一个编辑框
                            toBackEdit();
                        }
                    }
                }
            });


            //点击监听，确保光标总是在末尾
            et.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText tempEt = (EditText) v;
//                    tempEt.requestFocus();
                    tempEt.setSelection(tempEt.getText().length());
                }
            });
        }
    }


    /**
     * 跳转到下一个输入框
     */
    private void toNextEdit() {
        if (position < etCaptchaNumList.size() - 1) {
            position++;
        } else {
            return;
        }
        switchFocus(position);
    }

    /**
     * 回退一个输入框
     */
    private void toBackEdit() {
        if (position > 0) {
            position--;
        } else {
            return;
        }
        switchFocus(position);
    }

    /**
     * 切换焦点数字输入框
     *
     * @param position
     */
    private void switchFocus(int position) {

        //要先聚焦，否则下面的setFocusable(false)会把软键盘给收起来
        EditText etTemp = etCaptchaNumList.get(position);
        etTemp.setEnabled(true);
        etTemp.setFocusable(true);
        etTemp.setFocusableInTouchMode(true);
        etTemp.requestFocus();
        //设置编辑框的内容为ZERO_WIDTH_SPACE（用于监听删除键的响应）
        etTemp.setText(ZERO_WIDTH_SPACE);
        etTemp.setSelection(1);

        for (int i = 0; i < etCaptchaNumList.size(); i++) {
            EditText et = etCaptchaNumList.get(i);
            if (i == position) {
                /*et.setEnabled(true);
                et.setFocusable(true);
                et.setFocusableInTouchMode(true);
                et.requestFocus();
                //设置编辑框的内容为ZERO_WIDTH_SPACE（用于监听删除键的响应）
                et.setText(ZERO_WIDTH_SPACE);
                et.setSelection(1);*/

            } else if (i > position) {
                et.setEnabled(false);
                //当前焦点之后的清空（赋值空白符，赋值空串会被监听然后回退）
                et.setFocusable(false);
                et.setFocusableInTouchMode(false);
                et.setText(ZERO_WIDTH_SPACE);

            } else {
                et.setEnabled(false);
                et.setFocusable(false);
                et.setFocusableInTouchMode(false);
            }
        }

        //放到for外面处理是为了保证光标置于ZERO_WIDTH_SPACE之后
        /*EditText et = etCaptchaNumList.get(position);
        et.setFocusable(true);
        et.setFocusableInTouchMode(true);
        et.requestFocus();
        //设置编辑框的内容为ZERO_WIDTH_SPACE（用于监听删除键的响应）
        etCaptchaNumList.get(position).setText(ZERO_WIDTH_SPACE);
        etCaptchaNumList.get(position).setSelection(1);*/
    }


    @OnClick({R.id.btn_confirm, R.id.tv_class_num_desc})
    public void click(View view) {
        switch (view.getId()) {
            //下一步按钮
            case R.id.btn_confirm: {
                //下一步
                doNextDept();
                break;
            }
            case R.id.tv_class_num_desc: {
                //班级号QAQ对话框
                popupClassNumDialog();
                break;
            }
        }
    }

    /**
     * 班级号QAQ对话框
     */
    private void popupClassNumDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
//                .setTitle("班级号")//设置对话框的标题
                .setMessage("班级号是：XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")//设置对话框的内容
                //设置对话框的按钮
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.show();
    }


    /**
     * 班级号输入完成后的处理
     */
    private void classNumInputComplete() {
        //获取班级信息
        queryClassInfoWrapper();
    }


    /**
     * 下一步操作：1、验证是否获取了班级信息；2、没有班级信息则获取；3、已获取了班级信息则跳转到用户信息完善页面
     */
    private void doNextDept() {
        //获取到班级号才能跳转到下一个页面
        if (classInfo == null || TextUtils.isEmpty(classInfo.getSchoolName())) {
            //获取班级信息
            queryClassInfoWrapper();

        } else {
            //获取完整的班级号字符串
//            String classNum = getFullClassNumStr();
            //获取班级信息成功之后就赋值了该次的班级号
            String classNum = ClassNumActivity.this.classNum;
            if (TextUtils.isEmpty(classNum)) {
                ToastUtil.showShort(getApplicationContext(), "请输入班级号");
                return;
            }

            //学段：1-幼儿园，2-小学，3-初中，4-高中
            //目前默认幼儿园和小学跳转到家长角色选择
            if (classInfo.getPeriod() == Dictionary.PERIOD_KINDERGARTEN
                    || classInfo.getPeriod() == Dictionary.PERIOD_PRIMARY_SCHOOL) {
                //跳转到家长角色选择页面
                ParentRoleActivity.startParentRoleActivity(ClassNumActivity.this, classNum);

            } else {
                //跳转到用户信息完善页面
                UserInfoInitActivity.startUserInfoInitActivity(ClassNumActivity.this, classNum, Dictionary.PARENT_ROLE_MYSELF);
            }
        }
    }


    /**
     * 获取班级信息
     */
    private void queryClassInfoWrapper() {
        //隐藏软键盘
        SoftInputUtil.closeSoftInput(ClassNumActivity.this);
        //数据验证
        if (!checkData()) return;
        //获取完整的班级号字符串
        String classNum = getFullClassNumStr();
        //根据班级号请求班级信息
        queryClassInfoByClassNum(classNum);
    }

    /**
     * 根据班级号请求班级信息
     * @param classNum
     */
    private void queryClassInfoByClassNum(final String classNum) {
        //隐藏软键盘
        SoftInputUtil.closeSoftInput(ClassNumActivity.this);
        //开启通信等待提示
        LoadingView.getInstance().show(this);

        DataRequestService.getInstance().getClassInfoByClassNum(classNum, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                onFailureDefault(e);
            }

            @Override
            public void onResponse(Object obj) {
                //关闭通信等待提示
                LoadingView.getInstance().dismiss();

                try {
                    //解析班级信息
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    classInfo = InjectionWrapperUtil.injectMap(dataMap, ClassInfoEntity.class);
                    //学校名称或者班级名称为空，则认为数据异常
                    if (TextUtils.isEmpty(classInfo.getSchoolName())
                            || TextUtils.isEmpty(classInfo.getClassName())) {
                        throw new QSCustomException("班级号无效");
                    }

                    //刷新页面的班级信息
                    refreshClassInfoView(classInfo);
                    //赋值班级号
                    ClassNumActivity.this.classNum = classNum;

                } catch (QSCustomException err) {
                    onFailure(err);
                } catch (Exception e) {
                    onFailure(new QSCustomException("获取班级信息失败"));
                }
            }
        });
    }

    /**
     * 刷新页面的班级信息
     */
    private void refreshClassInfoView(ClassInfoEntity classInfo) {
        String classInfoStr = classInfo.getSchoolName() + "-" + getPeriodName(classInfo.getPeriod()) + "-" + classInfo.getClassName();
        tvClassInfo.setText(classInfoStr);
    }

    /**
     * 获取完整的班级号字符串（正常情况输入完验证码时，每个数字编辑框中包含1个0宽度空格符和一个数字）
     * @return
     */
    private String getFullClassNumStr() {
        String captcha = "";
        for (EditText editText : etCaptchaNumList) {
            if (editText.getText().length() > 1) {
                captcha += (editText.getText().charAt(1) + "");
            }
        }

        int len = captcha.length();

        return captcha;
    }


    /**
     * 验证数据格式
     * @return
     */
    private boolean checkData() {
        for (int i=0; i<etCaptchaNumList.size(); i++) {
            EditText editText = etCaptchaNumList.get(i);
            String num = editText.getText().toString().trim();
            //每个数字编辑框内容为1个ZERO_WIDTH_SPACE和一个数字时说明验证码已经填写完成，否则验证不通过
            if (num.length() == 2 && DataCheckUtil.isNumberOrLetter(num.charAt(1)+"")) {

            } else {
                //验证不通过的处理
                doCheckDataError(i);
                return false;
            }
        }

        return true;
    }

    /**
     * 数据验证不通过的处理
     * @param position
     */
    private void doCheckDataError(int position) {
        //验证不通过
        this.position = position;
        //初始编辑框的焦点
        switchFocus(position);
        SoftInputUtil.openSoftInput(ClassNumActivity.this, etCaptchaNumList.get(position));

        ToastUtil.showShort(getApplicationContext(), "格式不正确，请重新输入");
    }

    /**
     * 获取学段名称
     * @param period
     * @return
     */
    private String getPeriodName(int period) {
        String periodName = "";

        switch (period) {
            case Dictionary.PERIOD_KINDERGARTEN: {
                periodName = "幼儿园";
                break;
            }
            case Dictionary.PERIOD_PRIMARY_SCHOOL: {
                periodName = "小学";
                break;
            }
            case Dictionary.PERIOD_MIDDLE_SCHOOL: {
                periodName = "初中";
                break;
            }
            case Dictionary.PERIOD_HIGH_SCHOOL: {
                periodName = "高中";
                break;
            }
        }

        return periodName;
    }

}

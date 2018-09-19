package com.cheersmind.cheersgenie.features.modules.register.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.modules.base.activity.BaseActivity;
import com.cheersmind.cheersgenie.main.util.ToastUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 角色选择页面
 */
public class ParentRoleActivity extends BaseActivity {

    //班级号
    private static final String CLASS_NUM = "class_num";

    //班级号
    String classNum;

    @BindView(R.id.iv_role_father)
    ImageView ivRoleFather;
    @BindView(R.id.iv_role_mother)
    ImageView ivRoleMother;
    @BindView(R.id.iv_role_grandpa)
    ImageView ivRoleGrandpa;
    @BindView(R.id.iv_role_grandma)
    ImageView ivRoleGrandma;
    @BindView(R.id.iv_role_other)
    ImageView ivRoleOther;


    @BindView(R.id.btn_confirm)
    Button btnConfirm;

    //家长角色（默认自己）
    int parentRole = Dictionary.PARENT_ROLE_MYSELF;


    /**
     * 打开家长角色选择页面
     * @param context
     * @param classNum 班级号
     */
    public static void startParentRoleActivity(Context context, String classNum) {
        Intent intent = new Intent(context, ParentRoleActivity.class);
        Bundle extras = new Bundle();
        extras.putString(CLASS_NUM, classNum);
        intent.putExtras(extras);
        context.startActivity(intent);
    }

    @Override
    protected int setContentView() {
        return R.layout.activity_parent_role;
    }

    @Override
    protected String settingTitle() {
        return "完善信息";
    }

    @Override
    protected void onInitView() {
        //初始角色为父亲
        parentRole = Dictionary.PARENT_ROLE_FATHER;
        ivRoleFather.setBackgroundResource(R.drawable.role_father_selected);
    }

    @Override
    protected void onInitData() {
        classNum = getIntent().getStringExtra(CLASS_NUM);
        if (TextUtils.isEmpty(classNum))  {
            ToastUtil.showShort(getApplicationContext(), "班级号不能为空");
            return;
        }
    }


    @OnClick({R.id.iv_role_father, R.id.iv_role_mother, R.id.iv_role_grandpa, R.id.iv_role_grandma, R.id.iv_role_other, R.id.btn_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //父亲
            case R.id.iv_role_father: {
                parentRole = Dictionary.PARENT_ROLE_FATHER;
                ivRoleFather.setBackgroundResource(R.drawable.role_father_selected);
                ivRoleMother.setBackgroundResource(R.drawable.role_mother_normal);
                ivRoleGrandpa.setBackgroundResource(R.drawable.role_grandpa_normal);
                ivRoleGrandma.setBackgroundResource(R.drawable.role_grandma_normal);
                ivRoleOther.setBackgroundResource(R.drawable.role_other_normal);
                break;
            }
            //母亲
            case R.id.iv_role_mother: {
                parentRole = Dictionary.PARENT_ROLE_MOTHER;
                ivRoleFather.setBackgroundResource(R.drawable.role_father_normal);
                ivRoleMother.setBackgroundResource(R.drawable.role_mother_selected);
                ivRoleGrandpa.setBackgroundResource(R.drawable.role_grandpa_normal);
                ivRoleGrandma.setBackgroundResource(R.drawable.role_grandma_normal);
                ivRoleOther.setBackgroundResource(R.drawable.role_other_normal);
                break;
            }
            //爷爷/外公
            case R.id.iv_role_grandpa: {
                parentRole = Dictionary.PARENT_ROLE_GRANDPA;
                ivRoleFather.setBackgroundResource(R.drawable.role_father_normal);
                ivRoleMother.setBackgroundResource(R.drawable.role_mother_normal);
                ivRoleGrandpa.setBackgroundResource(R.drawable.role_grandpa_selected);
                ivRoleGrandma.setBackgroundResource(R.drawable.role_grandma_normal);
                ivRoleOther.setBackgroundResource(R.drawable.role_other_normal);
                break;
            }
            //奶奶/外婆
            case R.id.iv_role_grandma: {
                parentRole = Dictionary.PARENT_ROLE_GRANDMA;
                ivRoleFather.setBackgroundResource(R.drawable.role_father_normal);
                ivRoleMother.setBackgroundResource(R.drawable.role_mother_normal);
                ivRoleGrandpa.setBackgroundResource(R.drawable.role_grandpa_normal);
                ivRoleGrandma.setBackgroundResource(R.drawable.role_grandma_selected);
                ivRoleOther.setBackgroundResource(R.drawable.role_other_normal);
                break;
            }
            //其他
            case R.id.iv_role_other: {
                parentRole = Dictionary.PARENT_ROLE_OTHER;
                ivRoleFather.setBackgroundResource(R.drawable.role_father_normal);
                ivRoleMother.setBackgroundResource(R.drawable.role_mother_normal);
                ivRoleGrandpa.setBackgroundResource(R.drawable.role_grandpa_normal);
                ivRoleGrandma.setBackgroundResource(R.drawable.role_grandma_normal);
                ivRoleOther.setBackgroundResource(R.drawable.role_other_selected);
                break;
            }
            //下一步
            case R.id.btn_confirm: {
                doNext();
                break;
            }
        }
    }


    /**
     * 下一步
     */
    private void doNext() {
        //家长角色非自己
        if (parentRole == Dictionary.PARENT_ROLE_MYSELF) {
            ToastUtil.showShort(getApplicationContext(), "请选择您的身份");
            return;
        }

        //跳转到用户信息初始化页面
        UserInfoInitActivity.startUserInfoInitActivity(ParentRoleActivity.this, classNum, parentRole);
    }

}


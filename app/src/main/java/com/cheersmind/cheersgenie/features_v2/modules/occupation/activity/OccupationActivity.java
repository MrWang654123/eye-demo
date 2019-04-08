package com.cheersmind.cheersgenie.features_v2.modules.occupation.activity;


import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.modules.base.activity.BaseActivity;
import com.cheersmind.cheersgenie.features.utils.DataCheckUtil;
import com.cheersmind.cheersgenie.features.utils.SoftInputUtil;
import com.cheersmind.cheersgenie.features_v2.interfaces.BackPressedHandler;
import com.cheersmind.cheersgenie.features_v2.modules.occupation.fragment.OccupationFragment;
import com.cheersmind.cheersgenie.main.util.ToastUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 职业
 */
public class OccupationActivity extends BaseActivity implements BackPressedHandler {

    @BindView(R.id.et_search)
    EditText mEtSearch;
    @BindView(R.id.iv_clear)
    ImageView mIvClear;

    private OccupationFragment fragment;

    /**
     * * 启动职业页面
     * @param context 上下文
     */
    public static void startOccupationActivity(Context context) {
        Intent intent = new Intent(context, OccupationActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int setContentView() {
        return R.layout.activity__occupation;
    }

    @Override
    protected String settingTitle() {
        return null;
    }

    @Override
    protected void onInitView() {
        //修改状态栏颜色
        setStatusBarBackgroundColor(OccupationActivity.this, getResources().getColor(R.color.white));

        //清空按钮的显隐
        mEtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    if (mIvClear.getVisibility() == View.GONE) {
                        mIvClear.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (mIvClear.getVisibility() == View.VISIBLE) {
                        mIvClear.setVisibility(View.GONE);
                        fragment.Search("");
                    }
                }
            }
        });

        //监听搜索输入框的软键盘回车键
        mEtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                //当actionId == XX_SEND 或者 XX_DONE时都触发
                //或者event.getKeyCode == ENTER 且 event.getAction == ACTION_DOWN时也触发
                //注意，这是一定要判断event != null。因为在某些输入法上会返回null。
                if (actionId == EditorInfo.IME_ACTION_SEND
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || (keyEvent != null && KeyEvent.KEYCODE_ENTER == keyEvent.getKeyCode() && KeyEvent.ACTION_DOWN == keyEvent.getAction())) {
                    //搜索文章
                    Search(mEtSearch.getText().toString());
                }

                return false;
            }
        });
    }

    @Override
    protected void onInitData() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        String tag = OccupationFragment.class.getSimpleName();
        Fragment fragmentByTag = fragmentManager.findFragmentByTag(tag);
        //空则添加
        if (fragmentByTag == null) {
            //职业
            fragment = new OccupationFragment();
            //添加fragment到容器中
            fragmentManager.beginTransaction().add(R.id.fl_fragment, fragment, tag).commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (!hasHandlerBackPressed()) {
            super.onBackPressed();
        }
    }

    /**
     * 是否处理了回退
     * @return true：回退被处理了
     */
    @Override
    public boolean hasHandlerBackPressed() {
        boolean res = false;
        FragmentManager fragmentManager = getSupportFragmentManager();
        String tag = OccupationFragment.class.getSimpleName();
        Fragment fragmentByTag = fragmentManager.findFragmentByTag(tag);
        //根据fragment的处理结果赋值
        if (fragmentByTag != null && fragmentByTag instanceof BackPressedHandler) {
            res = ((BackPressedHandler) fragmentByTag).hasHandlerBackPressed();
        }
        return res;
    }

    @OnClick({R.id.iv_search, R.id.iv_clear})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_search:
                Search(mEtSearch.getText().toString());
                break;
            case R.id.iv_clear:
                mEtSearch.setText("");
                fragment.Search("");
                break;
            default:
                break;
        }
    }

    private void Search(String context) {
        if (!TextUtils.isEmpty(context)) {

            //去除左右空格
            context = context.trim();

            //包含特殊字符则不搜索
            if (DataCheckUtil.containSpecialCharacter(context)) {
                //通用的搜索文本错误提示
                commonSearchContentErrorTip(R.string.search_error_tip_special_char);
                return;
            }

            //长度只有1的情况
            if (context.length() < 2) {
                //通用的搜索文本错误提示
                commonSearchContentErrorTip(R.string.search_error_tip_length);
                return;
            } else {
                fragment.Search(context);
                //关闭软键盘
                SoftInputUtil.closeSoftInput(OccupationActivity.this);
            }
        }
        else {
            commonSearchContentErrorTip(R.string.search_error_tip_length);
            return;
        }
    }

    private void commonSearchContentErrorTip(int resId) {
        ToastUtil.customToastGravity(getApplication(), getResources().getString(resId), Toast.LENGTH_SHORT, Gravity.CENTER_VERTICAL, 0, 0);
    }

}

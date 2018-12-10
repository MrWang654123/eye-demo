package com.cheersmind.cheersgenie.features.modules.test.activity;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features.utils.DataCheckUtil;
import com.cheersmind.cheersgenie.features.view.PasteEditText;
import com.cheersmind.cheersgenie.main.util.DensityUtil;
import com.cheersmind.cheersgenie.main.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

/**
 * 编辑框黏贴监听
 */
public class EditTextPasteActivity extends Activity {

    @BindViews({R.id.et_captcha_num1, R.id.et_captcha_num2, R.id.et_captcha_num3,
            R.id.et_captcha_num4, R.id.et_captcha_num5, R.id.et_captcha_num6})
    List<PasteEditText> etCaptchaNumList;

    //当前输入索引
    int position = 0;

    //0宽度空格符（在edittext没有内容的时候，按软键盘的删除键,不触发TextWatcher）
    private static final String ZERO_WIDTH_SPACE = "\uFEFF";


    @BindView(R.id.et_use_paste)
    EditText etUsePaste;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edittext_paste);
        ButterKnife.bind(this);

        initView();
    }

    /**
     * 初始化视图
     */
    private void initView() {
        //初始编辑框的焦点
        switchFocus(position);
        //初始化文本监听
        initEditTextListener();
        //初始化粘贴监听
        initEditTextPasteListener();
        //初始化长按监听
        initLongClickListener();
        //注册剪切等事件
        registerClipEvents();

        etUsePaste.setText(System.currentTimeMillis() % 1000000 + "");
    }


    /**
     * 初始化长按监听
     */
    private void initLongClickListener() {
        for (int i = 0; i < etCaptchaNumList.size(); i++) {
            EditText et = etCaptchaNumList.get(i);
            et.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    popupClipboardWindows();

                    return true;
                }
            });
        }
    }


    /**
     * 初始化粘贴监听
     */
    private void initEditTextPasteListener() {
        for (int i = 0; i < etCaptchaNumList.size(); i++) {
            PasteEditText et = etCaptchaNumList.get(i);

            // 粘贴事件监听
            et.setOnPasteCallback(new PasteEditText.OnPasteCallback() {
                @Override
                public void onPaste() {
                    if (checkCode()) {
                        char[] chars = pasteContent.toCharArray();
                        for (int i = 0; i < etCaptchaNumList.size(); i++) {
                            EditText et = etCaptchaNumList.get(i);
                            char aChar = chars[i];
                            String temp = ZERO_WIDTH_SPACE + aChar;
                            et.setText(temp);

                            if (i == etCaptchaNumList.size() - 1) {
                                et.setSelection(et.getText().length());
                            }
                        }

                    } else {
                        ToastUtil.showShort(getApplication(), "您粘贴的内容不符合格式【"+ pasteContent +"】");
                    }
                }
            });

        }

    }


    /**
     * 弹出退出确认对话框
     */
    private void popupClipboardWindows() {
        ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        if (cm != null) {
            ClipData data = cm.getPrimaryClip();
            int itemCount = data.getItemCount();
            if (itemCount > 0) {
//                ClipData.Item item = data.getItemAt(0);
                ClipData.Item item = data.getItemAt(0);
                ArrayList listData = new ArrayList();
                String content = item.getText().toString();

                if (!TextUtils.isEmpty(content)) {
                    if (content.length() > 10) {
                        content = content.substring(0,10) + "…";
                    }

                    AlertDialog dialog = new AlertDialog.Builder(this)
                            .setTitle("是否粘贴剪切板最新内容？")
                            .setMessage(content)
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .create();
                    dialog.getWindow().setWindowAnimations(R.style.WUI_Animation_Dialog);
                    dialog.show();

                } else {

                }

            } else {

            }
        } else {

        }
    }


    private boolean checkCode() {
        if (!TextUtils.isEmpty(pasteContent) && pasteContent.length() == 6 && DataCheckUtil.isNumber(pasteContent)) {
            return true;
        }

        return false;
    }


    ClipboardManager mClipboardManager;
    ClipboardManager.OnPrimaryClipChangedListener mOnPrimaryClipChangedListener;
    String pasteContent;

    /**
     * 注册剪切板复制、剪切事件监听
     */
    private void registerClipEvents() {
        mClipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        mOnPrimaryClipChangedListener = new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                if (mClipboardManager.hasPrimaryClip()
                        && mClipboardManager.getPrimaryClip().getItemCount() > 0) {
                    // 获取复制、剪切的文本内容
                    CharSequence content =
                            mClipboardManager.getPrimaryClip().getItemAt(0).getText();
//                    Log.d("TAG", "复制、剪切的内容为：" + content);
                    ToastUtil.showShort(getApplication(), "内容：" + content);
                    System.out.println("内容：" + content);
                    pasteContent = content.toString();
                }
            }
        };
        mClipboardManager.addPrimaryClipChangedListener(mOnPrimaryClipChangedListener);
    }

    /**
     * 注销监听，避免内存泄漏。
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mClipboardManager != null && mOnPrimaryClipChangedListener != null) {
            mClipboardManager.removePrimaryClipChangedListener(mOnPrimaryClipChangedListener);
        }
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
                    System.out.println(charSequence.toString());

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    int len = editable.toString().length();
                    //两个字符时（一个0宽度空格，一个数字），跳转到下一个编辑框
                    if (len == 2) {
                        char c = editable.charAt(1);
                        if (TextUtils.isDigitsOnly(c + "")) {
                            //验证码输入结束的处理
                            if (position == etCaptchaNumList.size() - 1) {
//                                captchaInputComplete();
                                ToastUtil.showShort(getApplication(), "输入完成");
                            } else {
                                //跳转到下一个数字编辑框
                                toNextEdit();
                            }
                        }

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

        /*EditText et = etCaptchaNumList.get(position);
        et.setEnabled(true);
        et.setFocusable(true);
        et.setFocusableInTouchMode(true);
        et.requestFocus();
        //设置编辑框的内容为ZERO_WIDTH_SPACE（用于监听删除键的响应）
        et.setText(ZERO_WIDTH_SPACE);
        et.setSelection(1);*/

        /*//放到for外面处理是为了保证光标置于ZERO_WIDTH_SPACE之后
        EditText et = etCaptchaNumList.get(position);
        et.setFocusable(true);
        et.setFocusableInTouchMode(true);
        et.requestFocus();
        //设置编辑框的内容为ZERO_WIDTH_SPACE（用于监听删除键的响应）
        etCaptchaNumList.get(position).setText(ZERO_WIDTH_SPACE);
        etCaptchaNumList.get(position).setSelection(1);*/
    }


}

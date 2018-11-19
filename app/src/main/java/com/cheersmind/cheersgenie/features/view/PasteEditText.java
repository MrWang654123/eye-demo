package com.cheersmind.cheersgenie.features.view;

import android.content.Context;
import android.util.AttributeSet;

/**
 * 可监听粘贴的EditText
 */
public class PasteEditText extends android.support.v7.widget.AppCompatEditText {

    private OnPasteCallback mOnPasteCallback;

    public interface OnPasteCallback {
        void onPaste();
    }

    public PasteEditText(Context context) {
        super(context);
    }

    public PasteEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTextContextMenuItem(int id) {
        switch (id) {
            case android.R.id.cut:
                // 剪切
                break;
            case android.R.id.copy:
                // 复制
                break;
            case android.R.id.paste:
                // 粘贴
                if (mOnPasteCallback != null) {
                    mOnPasteCallback.onPaste();
                    return true;
                }
        }
        return super.onTextContextMenuItem(id);
    }

    /**
     * 添加粘贴监听器
     * @param onPasteCallback
     */
    public void setOnPasteCallback(OnPasteCallback onPasteCallback) {
        mOnPasteCallback = onPasteCallback;
    }

}


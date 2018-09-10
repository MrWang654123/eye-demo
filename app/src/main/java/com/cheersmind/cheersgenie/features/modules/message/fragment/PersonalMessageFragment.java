package com.cheersmind.cheersgenie.features.modules.message.fragment;

import android.view.View;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.modules.base.fragment.LazyLoadFragment;

/**
 * 个人消息（提醒）
 */
public class PersonalMessageFragment extends LazyLoadFragment {
    @Override
    protected int setContentView() {
        return R.layout.fragment_message_personal;
    }

    @Override
    protected void onInitView(View contentView) {

    }

    @Override
    protected void lazyLoad() {

    }
}

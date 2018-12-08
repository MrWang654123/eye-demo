package com.cheersmind.cheersgenie.features.modules.base.fragment;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;

import com.cheersmind.cheersgenie.features.interfaces.MessageHandlerCallback;
import com.cheersmind.cheersgenie.features.modules.base.activity.BaseActivity;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.service.BaseService;


/**
 * Fragment基础类
 */
public class BaseFragment extends Fragment implements MessageHandlerCallback {

    //通信tag
    protected String httpTag = System.currentTimeMillis() + "";

    /**
     * 默认的通信失败处理
     * @param e 自定义异常
     */
    protected void onFailureDefault(QSCustomException e) {
        onFailureDefault(e, null);
    }

    /**
     * 默认的通信失败处理
     * @param e
     * @param errorCodeCallBack ErrorCodeEntity对象回调
     */
    protected void onFailureDefault(QSCustomException e, BaseActivity.FailureDefaultErrorCodeCallBack errorCodeCallBack) {
        ((BaseActivity)getActivity()).onFailureDefault(e, errorCodeCallBack);
    }

    //消息处理器
    @SuppressLint("HandlerLeak")
    protected Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //统一回调
            BaseFragment.this.onHandleMessage(msg);
        }
    };

    @Override
    public void onHandleMessage(Message msg) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消当前页面的所有通信
        BaseService.cancelTag(httpTag);
    }

}

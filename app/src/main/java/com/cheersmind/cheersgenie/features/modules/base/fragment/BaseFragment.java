package com.cheersmind.cheersgenie.features.modules.base.fragment;

import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;

import com.cheersmind.cheersgenie.features.interfaces.MessageHandlerCallback;
import com.cheersmind.cheersgenie.features.modules.base.activity.BaseActivity;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.service.BaseService;

import java.lang.ref.WeakReference;


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

    //new Handler对象处理消息
    private Handler mHandler;

    @Override
    public void onHandleMessage(Message msg) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消当前页面的所有通信
        BaseService.cancelTag(httpTag);
        //清空mHandler
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }


    //内部类解决可能存在的内存溢出
    /*
    *1、静态和非静态内部类的区别是非静态内部类持有外部类的引用。
2、内部类实例的持有对象的生命周期大于其外部类对象，那么就有可能导致内存泄露。
比如,要实例化一个超出activity生命周期的内部类对象，
避免使用非静态的内部类。建议使用静态内部类并且在内部类中持有外部类的弱引用。
    *
    * */
    //静态内部类
    private static class MyHandler extends Handler {
        private final WeakReference<BaseFragment> mFragment;
        //构造方法
        MyHandler(BaseFragment fragment) {
            mFragment = new WeakReference<BaseFragment>(fragment);//对外部类的弱引用
        }
        @Override
        public void handleMessage(Message msg) {
            BaseFragment fragment = mFragment.get();
            //统一回调
            if (fragment != null) {
                fragment.onHandleMessage(msg);
            }
        }
    }


    /**
     * 获取handler
     * @return handler
     */
    public Handler getHandler() {
        if (mHandler == null) {
            mHandler = new MyHandler(this);
        }
        return mHandler;
    }


}

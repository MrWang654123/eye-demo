package com.cheersmind.smartbrain.wxapi;

import android.app.Activity;
import android.os.Bundle;

import com.cheersmind.smartbrain.main.constant.Constant;
import com.cheersmind.smartbrain.main.event.WXLoginEvent;
import com.cheersmind.smartbrain.main.util.LogUtils;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by Administrator on 2018/3/8.
 */

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Constant.wx_api.handleIntent(getIntent(), this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(int i) {
    }

    //微信请求相应
    @Override
    public void onReq(BaseReq baseReq) {

    }

    //发送到微信请求的响应结果
    @Override
    public void onResp(BaseResp resp) {
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                LogUtils.w("WXTest", "onResp OK");

                if (resp instanceof SendAuth.Resp) {
                    SendAuth.Resp newResp = (SendAuth.Resp) resp;
                    //获取微信传回的code
                    String code = newResp.code;
                    EventBus.getDefault().post(new WXLoginEvent(code));
                    finish();

                }

                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                LogUtils.w("WXTest", "onResp ERR_USER_CANCEL ");
                finish();
                //发送取消
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                LogUtils.w("WXTest", "onResp ERR_AUTH_DENIED");
                finish();
                //发送被拒绝
                break;
            default:
                LogUtils.w("WXTest", "onResp default errCode " + resp.errCode);
                finish();
                //发送返回
                break;
        }

    }

}

package com.cheersmind.cheersgenie.features.interfaces;

import com.cheersmind.cheersgenie.features.dto.ResponseDto;
import com.zhy.http.okhttp.callback.Callback;

import java.io.IOException;

import okhttp3.Response;

/**
 * 返回Response对象的回调
 */
public abstract class ResponseBaseCallback extends Callback<ResponseDto>
{
    /**
     * if you parse reponse code in parseNetworkResponse, you should make this method return true.
     *
     * @param response
     * @return
     */
    public boolean validateReponse(Response response, int id)
    {
        int code = response.code();
        return code >= 200 && code < 500;
    }
}

package com.cheersmind.cheersgenie.features.interfaces;

import com.cheersmind.cheersgenie.features.dto.ResponseDto;
import com.zhy.http.okhttp.callback.Callback;

import java.io.IOException;

import okhttp3.Response;

/**
 * 返回Response对象的回调
 */
public abstract class ResponseStringCallback extends ResponseBaseCallback
{
    @Override
    public ResponseDto parseNetworkResponse(Response response, int id) throws IOException
    {
        return new ResponseDto(response.code(), response.body().string());
    }

}

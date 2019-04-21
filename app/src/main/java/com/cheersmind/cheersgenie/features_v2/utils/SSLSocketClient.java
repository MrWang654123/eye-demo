package com.cheersmind.cheersgenie.features_v2.utils;

import com.cheersmind.cheersgenie.main.QSApplication;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * SSL(Secure Sockets Layer 安全套接层)工具
 */
public class SSLSocketClient {

    //获取这个SSLSocketFactory
    public static SSLSocketFactory getSSLSocketFactory() {
        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, getTrustManager(), new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //获取TrustManager
    private static TrustManager[] getTrustManager() {
        return new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[]{};
                    }
                }
        };
    }

    //获取HostnameVerifier
    public static HostnameVerifier getHostnameVerifier() {
        return new HostnameVerifier() {
            @Override
            public boolean verify(String s, SSLSession sslSession) {
                return true;
            }
        };
    }

    /*
    第一种方式：

    第一种是绕过https的验证方法

    这种方式是绕过了验证，所以说使用https请求就没什么意义了

    clone = OkHttpUtils.getInstance().getOkHttpClient().newBuilder()
        .readTimeout(readTimeOut, TimeUnit.MILLISECONDS)
        .writeTimeout(writeTimeOut, TimeUnit.MILLISECONDS)
        .connectTimeout(connTimeOut, TimeUnit.MILLISECONDS)
        .sslSocketFactory(SSLSocketClient.getSSLSocketFactory())
        .hostnameVerifier(SSLSocketClient.getHostnameVerifier())
        .build();
     call = clone.newCall(request);

     第二种方式：

    通过读取后台提供的证书文件

    首先把证书放到assets文件夹中

    然后在SSLSocketClient工具类中添加如下方法：
*/

    //获取这个SSLSocketFactory
    public static SSLSocketFactory getSSlSocketFactory(InputStream certificates) {
        SSLContext sslContext = null;
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");

            Certificate ca;
            try {
                ca = certificateFactory.generateCertificate(certificates);

            } finally {
                certificates.close();
            }

            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            // Create an SSLContext that uses our TrustManager
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, tmf.getTrustManagers(), null);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return sslContext != null ? sslContext.getSocketFactory() : null;
    }

    //读取证书文件
    public static InputStream getInputStream(){
        InputStream inputStream = null;
        try {
            inputStream = QSApplication.getContext().getAssets().open("2089084__cheersmind.com.pem");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return inputStream;
    }

    //读取证书文件
    public static InputStream getInputStream2(){
        InputStream inputStream = null;
        try {
            inputStream = QSApplication.getContext().getAssets().open("2089084__cheersmind.com.key");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return inputStream;
    }

    /*然后对okhttp进行配置：

    clone = OkHttpUtils.getInstance().getOkHttpClient().newBuilder()
        .readTimeout(readTimeOut, TimeUnit.MILLISECONDS)
        .writeTimeout(writeTimeOut, TimeUnit.MILLISECONDS)
        .connectTimeout(connTimeOut, TimeUnit.MILLISECONDS)
        .sslSocketFactory(SSLSocketClient.getSSlSocketFactory(SSLSocketClient.getInputStream()))
        .hostnameVerifier(SSLSocketClient.getHostnameVerifier())
        .build();
    call = clone.newCall(request);

    */

}

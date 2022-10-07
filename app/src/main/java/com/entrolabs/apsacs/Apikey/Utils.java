package com.entrolabs.apsacs.Apikey;

import android.util.Log;

import com.entrolabs.apsacs.Common.Helper;

import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class Utils {
    private static boolean isInDebugMode = true;
    public static OkHttpClient getUnsafeOkHttpClientForApiCall() {
        try {
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {

                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain,
                                                       String authType) throws
                                CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain,
                                                       String authType) throws
                                CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            final SSLSocketFactory sslSocketFactory = new NoSSLv3SocketFactory(sslContext.getSocketFactory());


            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);

            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static OkHttpClient getUnsafeOkHttpClient() {
        try {
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {

                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain,
                                                       String authType) throws
                                CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain,
                                                       String authType) throws
                                CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            SSLContext sslcontext = SSLContext.getInstance("TLSv1");
            sslcontext.init(null, trustAllCerts, null);
            SSLSocketFactory noSSLv3Factory = new NoSSLv3SocketFactory(sslcontext.getSocketFactory());

            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);


            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .connectTimeout(100, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.MINUTES)
                    .readTimeout(100000, TimeUnit.SECONDS)
                    .addInterceptor(interceptor);
            builder.sslSocketFactory(noSSLv3Factory, (X509TrustManager) trustAllCerts[0]);

            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static void logd(String TAG, String message) {
        if (isInDebugMode)
            Helper.l(TAG+ String.format("%s", message));
    }

    public static void loge(String TAG, String message) {
        if (isInDebugMode)
            Helper.l(TAG+ String.format("%s", message));
    }

    public static void logv(String TAG, String message) {
        if (isInDebugMode)
            Helper.l(TAG+ String.format("%s", message));
    }

}
